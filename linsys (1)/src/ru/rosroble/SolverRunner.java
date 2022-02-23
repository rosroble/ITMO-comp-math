package ru.rosroble;

import java.io.*;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class SolverRunner {
    public static void main(String[] args) throws IOException {
        Solver solver = new Solver();
        solver.run();
    }
}

class Solver {
    public static final int MAX_ITERATION = 1000000;
    private double eps;
    boolean isFile = false;
    private BufferedReader reader;
    private int n;
    private double[][] matrix;
    private double[] solution;
    private double[] error;

    public void run() throws IOException {
        init();
        matrix = readMatrix(n);
        System.out.println("Считана матрица: ");
        printMatrix();
        System.out.println("Попытка достичь диагонального преобладания.");
        if (!new Kuhn().diagDominance()) {
            System.out.println("Нельзя достигнуть диагонального преобладания. Завершение работы.");
            return;
        }
        System.out.println("Диагональное преобладание достигнуто. Модифицированная матрица: ");
        printMatrix();
        modifyMatrix();
        solution = initSolution(matrix);
        int iterations = iterate();
        if (iterations == MAX_ITERATION) {
            System.out.println("Не удалось достичь требуемой сходимости за допустимое число итераций.");
            return;
        }
        System.out.println("Вектор решения: " + Arrays.toString(solution));
        System.out.println("Вектор погрешностей: " + Arrays.toString(error));
        System.out.println("Количество итераций: " + iterations);
    }

    public void init() throws IOException {
        reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Введите имя файла или введите 0 чтобы считать с клавиатуры: ");
        String input = reader.readLine();
        if (!input.equals("0")) {
            while (true) {
                try {
                    reader = new BufferedReader(new FileReader(input));
                    isFile = true;
                    break;
                } catch (FileNotFoundException e) {
                    System.out.println("Файл с указанным именем не найден. Повторите ввод.");
                }
            }
        }
        printIfConsole("Введите погрешность: ");
        eps = Double.parseDouble(reader.readLine());
        while (!((n > 0) && (n <= 20))) {
            printIfConsole("Введите количество неизвестных системы (<= 20): ");
            n = Integer.parseInt(reader.readLine());
        }
    }

    private void printIfConsole(String msg) {
        if (!isFile) System.out.println(msg);
    }

    private double[][] readMatrix(int n) throws IOException {
        printIfConsole("Введите матрицу: ");
        double[][] matrix = new double[n][n + 1];
        for (int i = 0; i < matrix.length; i++) {
            String[] line = reader.readLine().split(" ");
            matrix[i] = Arrays.stream(line).
                    mapToDouble(Double::parseDouble).
                    toArray();
        }

        return matrix;
    }

    private void printMatrix() {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    private void modifyMatrix() {
        for (int i = 0; i < matrix.length; i++) {
            int currentIndex = i;
            matrix[i] = Arrays.stream(matrix[i]).
                    map(x -> -x / matrix[currentIndex][currentIndex]).
                    toArray();
            matrix[i][i] = 0;
            matrix[i][n] *= -1;
        }
    }

    private double[] initSolution(double[][] matrix) {
        double[] solution = new double[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            solution[i] = matrix[i][matrix.length];
        }
        return solution;
    }

    private int iterate() {
        int currentIter = 0;
        double maxEps = Double.MAX_VALUE;
        error = new double[solution.length];
        while (currentIter < MAX_ITERATION && maxEps >= eps) {
            double currentMaxEps = 0;
            double[] currentSolution = Arrays.copyOf(solution, solution.length);
            for (int i = 0; i < currentSolution.length; i++) {
                double newValue = 0;
                for (int j = 0; j < currentSolution.length; j++) {
                    if (i == j) continue;
                    newValue += matrix[i][j]*currentSolution[j];
                }
                newValue += matrix[i][matrix.length];
                error[i] = Math.abs(newValue - solution[i]);
                if (error[i] > currentMaxEps) currentMaxEps = error[i];
                solution[i] = newValue;
            }
            maxEps = currentMaxEps;
            currentIter++;
        }
        return currentIter;
    }


    // внутренний класс реализующий алгоритм Куна по нахождению максимального паросочетания в двудольном графе
    // используется для достижения диагонального преобладания в матрице
    private class Kuhn {

        List<List<Integer>> g = new ArrayList<>();
        int[] mt = new int[matrix.length];
        boolean[] used = new boolean[matrix.length];

        public boolean diagDominance() {
            IntStream.range(0, mt.length).forEach(i -> mt[i] = -1);
            for (int i = 0; i < matrix.length; i++) {
                List<Integer> possibleIndexes = new ArrayList<>();
                double sum = Arrays.stream(matrix[i]).map(Math::abs).sum() - Math.abs(matrix[i][matrix.length]);
                for (int j = 0; j < matrix.length; j++) {
                    if (sum - 2 * Math.abs(matrix[i][j]) <= 0) possibleIndexes.add(j);
                }
                g.add(possibleIndexes);
            }

            for (int v = 0; v < matrix.length; v++) {
                IntStream.range(0, used.length).forEach(i -> used[i] = false);
                tryKuhn(v);
            }

            double[][] matrixCopy = Arrays.copyOf(matrix, matrix.length);

            for (int i = 0; i < matrix.length; i++) {
                if (mt[i] == -1) {
                    return false;
                }
                matrix[i] = matrixCopy[mt[i]];
            }
            return true;
        }

        private boolean tryKuhn(int v) {
            if (used[v])  return false;
            used[v] = true;
            for (int i = 0; i < g.get(v).size(); ++i) {
                int to = g.get(v).get(i);
                if (mt[to] == -1 || tryKuhn(mt[to])) {
                    mt[to] = v;
                    return true;
                }
            }
            return false;
        }
    }

}

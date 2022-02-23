package ru.rosroble.eqsolver.solvers;

import ru.rosroble.eqsolver.exceptions.DivergenceException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class LinearSystemSolver {
    public static final int MAX_ITERATION = 1000000;
    private double eps;
    private double[] solution;
    private double[] error;

    public LinearSystemSolver(double eps) {
        this.eps = eps;
    }

    public double[] solve(double[][] matrix) throws DivergenceException {
        if (!new Kuhn(matrix).diagDominance()) {
            throw new DivergenceException("Can't achieve diag dominance");
        }
        modifyMatrix(matrix);
        solution = initSolution(matrix);
        int iterations = iterate(matrix);
        if (iterations == MAX_ITERATION) {
            throw new DivergenceException("Can't achieve required accuracy");
        }
        return solution;
    }


    private void modifyMatrix(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            int currentIndex = i;
            matrix[i] = Arrays.stream(matrix[i]).
                    map(x -> -x / matrix[currentIndex][currentIndex]).
                    toArray();
            matrix[i][i] = 0;
            matrix[i][matrix[0].length - 1] *= -1;
        }
    }

    private double[] initSolution(double[][] matrix) {
        double[] solution = new double[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            solution[i] = 10;
        }
        return solution;
    }

    private int iterate(double[][] matrix) {
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

    private class Kuhn {

        double[][] matrix;
        List<List<Integer>> g;
        int[] mt;
        boolean[] used;

        public Kuhn(double[][] matrix) {
            this.matrix = matrix;
            g = new ArrayList<>();
            mt = new int[matrix.length];
            used = new boolean[matrix.length];
        }

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

import auxiliary.FunctionContainer;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;
import interpolation.InterpolationResult;
import interpolation.Interpolator;
import plot.PlotUtils;

import java.io.*;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class InterpolationRunner {


    private final FunctionContainer[] functions;
    private Interpolator interpolator;
    private PlotUtils plotUtils;
    private BufferedReader reader;
    private PrintWriter writer;
    private double[][] functionTable;
    private double pointToInterpolate;
    private boolean outputToFile;

    public static void main(String[] args) throws IOException, PythonExecutionException {
        new InterpolationRunner().run();
    }

    public InterpolationRunner() {
        interpolator = new Interpolator();
        plotUtils = new PlotUtils();
        functions = new FunctionContainer[] {
                new FunctionContainer(Math::sin, "sin (x)"),
                new FunctionContainer(x -> x * x, "x^2"),
                new FunctionContainer(x -> x * x * x, "x^3")
        };
    }

    private void run() throws IOException, PythonExecutionException {
        initInput();
        initOutput();
        initX();
        initFunction();
        InterpolationResult interpolation =  initInterpolation(pointToInterpolate);
        System.out.println(interpolation);

        double minX = Arrays.stream(functionTable).mapToDouble(x -> x[0]).min().getAsDouble();
        double maxX = Arrays.stream(functionTable).mapToDouble(x -> x[0]).max().getAsDouble();
        double minY = Arrays.stream(functionTable).mapToDouble(x -> x[1]).min().getAsDouble();
        double maxY = Arrays.stream(functionTable).mapToDouble(x -> x[1]).max().getAsDouble();
        double[][] newtonPolynom = generateNewtonPolynom(minX, maxX, 50);
        plotUtils.draw(minX - 1, maxX + 1, minY - 1, maxY + 1, functionTable, newtonPolynom);
    }

    private void initX() throws IOException {
        System.out.println("Enter x-coordinate for interpolation.");
        pointToInterpolate = Double.parseDouble(reader.readLine());
    }

    private InterpolationResult initInterpolation(double x) throws IOException {
        System.out.println("Выберите метод.\n1. Метод Лагранжа\n2. Метод Ньютона.");
        int index;
        while (true) {
            try {
                index = Integer.parseInt(reader.readLine());
                switch (index) {
                    case 1:
                        return interpolator.lagrange(x, functionTable);
                    case 2:
                        return interpolator.newton(x, functionTable);
                    default:
                        System.out.println("Choose method from the list above.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Integer value expected. Retry input.");
            }
        }
    }

    private void initFunction() throws IOException {
        System.out.println("Выберите способ ввода функции:\n1.Выбор из списка\n2.Ввод пар (x, y)");
        int choice = Integer.parseInt(reader.readLine());
        switch (choice) {
            case 1:
                functionFromList();
                break;
            case 2:
                functionFromTable();
            default:
                break;
        }
    }

    private void functionFromList() throws IOException {
        System.out.println("Выберите функцию из списка:");
        IntStream.range(1, functions.length + 1)
                .mapToObj(x -> x + ". " + functions[x - 1].toString())
                .forEach(System.out::println);
        int choice = Integer.parseInt(reader.readLine());
        functionTable = IntStream.range((int) pointToInterpolate - 5, (int) pointToInterpolate + 5)
                .asDoubleStream()
                .mapToObj(x -> new double[] {x, functions[choice - 1].getFunction().apply(x)})
                .toArray(double[][]::new);
    }

    private void functionFromTable() throws IOException {
        System.out.println("Введите количество пар (x, y) (не менее 8)");
        int pairs;
        while (true) {
            try {
                pairs = Integer.parseInt(reader.readLine());
                functionTable = new double[pairs][2];
                break;
            } catch (NumberFormatException e) {
                System.err.println("Integer value expected. Try again.");
            }
        }
        initValues(pairs);
    }

    private void initValues(int pairs) throws IOException {
        System.out.println("Введите пары (x, y) через пробел");
        int i = 0;
        while (i < pairs) {
            try {
                double[] pair = Stream.of(reader.readLine().split(" ")).mapToDouble(Double::parseDouble).toArray();
                if (pair.length != 2) {
                    System.err.println("Expected 2 numbers in line.");
                    continue;
                }
                functionTable[i] = pair;
                i++;
            } catch (NumberFormatException e) {
                System.err.println("Invalid input. Try again.");
            }
        }
    }


    private void initInput() throws IOException {
        reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter filename or 0 for keyboard input: ");
        String input = reader.readLine();
        if (!input.equals("0")) {
            while (true) {
                try {
                    reader = new BufferedReader(new FileReader(input));
                    break;
                } catch (FileNotFoundException e) {
                    System.out.println("File cannot be found or created. Retry input. ");
                    input = reader.readLine();
                }
            }
        }
    }

    private void initOutput() throws IOException {
        writer = new PrintWriter(System.out);
        System.out.println("Enter output filename or 0 for console output");
        String output = reader.readLine();
        if (!output.equals("0")) {
            while (true) {
                try {
                    writer = new PrintWriter(new FileWriter(output));
                    break;
                } catch (FileNotFoundException e) {
                    System.out.println("File cannot be found. Retry input. ");
                }
            }
        }
    }

    private double[][] generateNewtonPolynom(double from, double to, int n) {
        double[][] res = new double[n][2];
        double step = (to - from) / n;
        int iter = 0;
        for (double i = from; i <= to; i += step, iter++) {
            res[iter][0] = i;
            res[iter][1] = interpolator.newton(i, functionTable).getY();
        }
        return res;
    }
}

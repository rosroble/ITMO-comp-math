import approximation.ApproximationResult;
import approximation.Approximator;
import plot.GraphFrame;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class Runner {

    private Approximator approximator;
    private BufferedReader reader;
    private PrintWriter writer;
    private double[][] functionTable;
    private GraphFrame frame;
    private boolean outputToFile;

    public Runner() {
        approximator = new Approximator();
        frame = new GraphFrame("Approximation graph");
    }

    public void run() throws IOException {
        init();
        ApproximationResult linear = approximator.linearApproximation(functionTable);
        ApproximationResult quadratic = approximator.squareApproximation(functionTable);
        ApproximationResult exponential = approximator.exponentialApproximation(functionTable);
        ApproximationResult logarithmic = approximator.logarithmicApproximation(functionTable);
        ApproximationResult power = approximator.powerApproximation(functionTable);
        ApproximationResult cubic = approximator.cubicApproximation(functionTable);
        System.out.println(linear);
        System.out.println(quadratic);
        System.out.println(exponential);
        System.out.println(logarithmic);
        System.out.println(power);
        System.out.println(cubic);
        List<ApproximationResult> list = new ArrayList<>(List.of(linear, quadratic, exponential, logarithmic, power, cubic));
        list.sort(Comparator.comparingDouble(ApproximationResult::getDeviation));
        frame.graph(functionTable[0][0] - 2,
                functionTable[functionTable.length - 1][0] + 2,
                list.get(0).getFunction());
    }

    private void init() throws IOException {
        initInput();
        initOutput();
        initFunction();
    }

    private void initFunction() throws IOException {
        System.out.println("Введите количество пар (x, y) (не менее 8)");
        int pairs;
        while (true) {
            try {
                pairs = Integer.parseInt(reader.readLine());
                if (pairs < 6) {
                    System.err.println("Pairs amount >= 6. Try again.");
                    continue;
                }
                functionTable = new double[pairs][2];
                break;
            } catch (NumberFormatException e) {
                System.err.println("Integer value expected. Try again.");
            }
        }
        initValues(pairs);
    }

    private void initValues(int pairs) throws IOException {
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
}

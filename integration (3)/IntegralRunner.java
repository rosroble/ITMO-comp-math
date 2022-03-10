import solver.IntegralSolver;
import solver.simpson.SimpsonMethod;
import solver.rectangle.RectangleMethod;
import solver.rectangle.RectangleMethodType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Function;
import java.util.stream.Stream;

public class IntegralRunner {

    private BufferedReader reader;
    private Function<Double, Double>[] functions;
    private String[] functionDesc;
    private int chosenFunctionIndex;
    private IntegralSolver[] solvers;
    private String[] solversDesc;
    private int chosenSolverIndex;
    private double a;
    private double b;
    private double eps = 1e-4;
    private final int nInitial = 4;
    private final int maxN = 1 << 20;




    public void run() throws IOException {
        init();
        Result result = calculateRunge(solvers[chosenSolverIndex], functions[chosenFunctionIndex], a, b);
        System.out.println(result);
    }

    private void init() throws IOException {
        reader = new BufferedReader(new InputStreamReader(System.in));

        initAccuracy();
        initFunctions();
        functionChoice();
        initSolvers();
        solverChoice();
        initRange();

    }

    private void initRange() throws IOException {
        System.out.println("Enter integration range (two numbers separated with space)");
        double[] values = Stream.of(reader.readLine().split(" ")).mapToDouble(Double::parseDouble).toArray();
        a = values[0];
        b = values[1];
    }


    private void initAccuracy() throws IOException {
        System.out.println("Enter required accuracy (default = 0.0001)");
        String input = reader.readLine();
        if (!input.isEmpty()) {
            eps = Double.parseDouble(input);
        }
    }

    private void initFunctions() {
        Function<Double, Double> f1 = x -> x * x; // x ^ 2
        Function<Double, Double> f2 = x -> x * x * x; // x ^ 3
        Function<Double, Double> f3 = Math::sin; // sin(x)
        Function<Double, Double> f4 = x -> x*x*x + 2 * x * x - 3*x - 12;
        String s1 = "1. x^2";
        String s2 = "2. x^3";
        String s3 = "3. sin(x)";
        String s4 = "4. x^3 + 2x^2 - 3x - 12";
        functions = new Function[] {f1, f2, f3, f4};
        functionDesc = new String[] {s1, s2, s3, s4};


    }

    private void functionChoice() throws IOException {
        System.out.println("Choose a function to integrate:");
        Util.printStringArray(functionDesc);
        chosenFunctionIndex = Util.choice(functions, reader);
    }

    private void solverChoice() throws IOException {
        System.out.println("Choose a solve method:");
        Util.printStringArray(solversDesc);
        chosenSolverIndex = Util.choice(solvers, reader);
    }

    private void initSolvers() {
        IntegralSolver s1 = new RectangleMethod(RectangleMethodType.RECTANGLE_LEFT);
        IntegralSolver s2 = new RectangleMethod(RectangleMethodType.RECTANGLE_MIDDLE);
        IntegralSolver s3 = new RectangleMethod(RectangleMethodType.RECTANGLE_RIGHT);
        IntegralSolver s4 = new SimpsonMethod();
        solvers = new IntegralSolver[] {s1, s2, s3, s4};
        solversDesc = Stream.of(solvers).map(Object::toString).toArray(String[]::new);
    }


    private Result calculateRunge(IntegralSolver solver, Function<Double, Double> f, double a, double b) {
        boolean reversed = false;
        if (a > b) {
            reversed = true;
            double t = a;
            a = b;
            b = t;
        }
        int n = nInitial;
        double prev = solver.calculate(f, a, b, n);
        double next = solver.calculate(f, a, b, n *= 2);
        double err = Math.abs(next - prev);
        while (err >= eps || n < maxN) {
            prev = next;
            next = solver.calculate(f, a, b, n *= 2);
            err = Math.abs(next - prev);
        }
        next = reversed ? -next : next;
        return new Result(next, n, err);
    }
}

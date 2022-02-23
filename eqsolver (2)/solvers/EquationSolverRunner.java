package ru.rosroble.eqsolver.solvers;

import ru.rosroble.eqsolver.result.Result;
import ru.rosroble.eqsolver.result.ResultStatus;
import ru.rosroble.eqsolver.exceptions.DivergenceException;
import ru.rosroble.eqsolver.interfaces.BinaryFunction;
import ru.rosroble.eqsolver.interfaces.Function;
import ru.rosroble.eqsolver.interfaces.EquationSolver;
import ru.rosroble.eqsolver.plot.GraphFrame;

import java.io.*;

public class EquationSolverRunner {

    public static final int MAX_ITERATION = 10000;
    public static final double delta = 1e-6;
    private double eps;
    private double a;
    private double b;
    private int functionIndex;
    private int solverIndex;
    boolean isFile = false;
    private BufferedReader reader;
    private PrintWriter writer;
    private EquationSolver[] solvers;
    private Function[] functions;
    private String[] functionDesc;
    private String[] solversDesc;

    /**
     * Main method of a class. Triggers initializer and outputs a solution for the equation chosen.
     * @throws IOException
     */
    public void run() throws IOException {
        init();
        try {
            Result equationSolution = solvers[solverIndex].solve(functions[functionIndex], a, b);
            writer.println(equationSolution);
            new GraphFrame("Equation plot").graph(a, b, functions[functionIndex]);
            BinaryFunction f1 = (x, y) -> (x*x + y*y - 4);
            BinaryFunction f2 = (x, y) -> (Math.sin(x) - y);
            Result systemSolution = newtonSystem(f1 ,f2, 2, 1);
            writer.println(systemSolution);
            new GraphFrame("System plot").system(systemSolution.getSolution()[0] - 3, systemSolution.getSolution()[0] + 3);
            writer.close();
            reader.close();
            System.out.println("Finished executing.");
        } catch (DivergenceException e) {
            writer.println(e.getMessage());
        }

    }

    /**
     * A user-interactive initializer configuring solve options.
     *
     * @throws IOException
     */
    public void init() throws IOException {

        initInput();
        initOutput();

        printIfConsole("Enter required accuracy: ");
        eps = Double.parseDouble(reader.readLine());

        initFunctions();
        printIfConsole("Choose an equation to solve: ");
        printStringArray(functionDesc);
        functionIndex = choice(functions);
        if (functionIndex == -1) return;


        printIfConsole("Enter the search interval (two numbers separated by space): ");
        String[] interval = reader.readLine().split(" ");
        a = Double.parseDouble(interval[0]);
        b = Double.parseDouble(interval[1]);

        initSolvers();
        printIfConsole("Choose solve method: ");
        printStringArray(solversDesc);
        solverIndex = choice(solvers);
        if (solverIndex == -1) return;
    }

    /**
     * User-interactive method to initialize input format (keyboard or file)
     * @throws IOException
     */
    private void initInput() throws IOException {
        reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter filename or 0 for keyboard input: ");
        String input = reader.readLine();
        if (!input.equals("0")) {
            while (true) {
                try {
                    reader = new BufferedReader(new FileReader(input));
                    isFile = true;
                    break;
                } catch (FileNotFoundException e) {
                    System.out.println("File cannot be found or created. Retry input. ");
                    input = reader.readLine();
                }
            }
        }
    }

    /**
     * User-interactive method to initialize output format (console or file)
     * @throws IOException
     */
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

    /**
     * Creates a basic set of equations to solve and initializes function array and function descriptions
     */
    private void initFunctions() {
        Function f1 = x -> (Math.pow(x, 3) - 4.5 * Math.pow(x, 2) - 9.21 * x - 0.383);
        Function f2 = x -> (Math.pow(x, 3) - x + 4);
        Function f3 = x -> (Math.sin(x) + 0.1);
        String f1s = "1. x^3 - 4.5x^2 - 9.21x - 0.383";
        String f2s = "2. x^3 - x + 4";
        String f3s = "3. sin(x) + 0.1";
        functions = new Function[]{f1, f2, f3};
        functionDesc = new String[]{f1s, f2s, f3s};
    }


    /**
     * Defines solver implementations and initializes solver array.
     */
    private void initSolvers() {
         EquationSolver newton = (f, a, b) -> {
            if (f.calculate(a) * f.calculate(b) >= 0) throw new IllegalArgumentException("f(a) * f(b) should be < 0");
            double x0 = f.calculate(a) * derivativeAtPoint(f, 2, a) > 0 ? a : b;
            int iter = 0;
            double xn = x0;
            while (iter < MAX_ITERATION) {
                double next = xn - f.calculate(xn) / derivativeAtPoint(f, 1, xn);
                if (Math.abs(next - xn) < eps) {
                    return new Result(ResultStatus.SOLUTION_FOUND,
                            new double[] {next},
                            iter,
                            new double[] {Math.abs(next - xn)});
                }
                xn = next;
                iter++;
            }
            return new Result(ResultStatus.DIVERGENCE, null, MAX_ITERATION, null);
        };

         EquationSolver fixedPointIteration = (f, a, b) -> {
            double x0 = derivativeAtPoint(f, 1, a) > derivativeAtPoint(f, 1, b) ? a : b;
            Function phi = phi(f);
            int iter = 0;
            double xn = x0;
            while (iter < MAX_ITERATION) {
                if (Math.abs(derivativeAtPoint(phi, 1, xn)) >= 1) throw new DivergenceException("f'(xn) > 1 => cannot converge");
                double next = phi.calculate(xn);
                if (Math.abs(next - xn) < eps) {
                    return new Result(ResultStatus.SOLUTION_FOUND,
                            new double[] {next},
                            iter,
                            new double[] {Math.abs(next - xn)});
                }
                iter++;
                xn = next;
            }
             return new Result(ResultStatus.DIVERGENCE, null, MAX_ITERATION, null);
        };

         solvers = new EquationSolver[] {newton, fixedPointIteration};
         solversDesc = new String[] {"1. Newton's method.", "2. Fixed-point iteration method."};
    }

    /**
     * Utility method to print all strings in a string array. No output provided in file mode.
     * @param array an array of strings to output
     */
    private void printStringArray(String[] array) {
        if (isFile) return;
        for (String s: array) {
            System.out.println(s);
        }
    }

    /**
     * Utility user-interactive method used to verify user input and match it with corresponding index.
     * @param array an array user chooses one element from
     * @return corresponding index of the element chosen
     * @throws IOException
     */
    private int choice(Object[] array) throws IOException {
        int index = -1;
        while (index == -1) {
            try {
                int input = Integer.parseInt(reader.readLine());
                index = input > 0 && input <= array.length ? input - 1 : -1;
                if ((index == -1) && (isFile)) break;
            } catch (NumberFormatException e) {
                System.out.println("Wrong input.");
                if (isFile) break;
            }
        }
        return index;
    }

    /**
     * A method that calculates the derivative of a function in a certain point
     * @param f a function the derivative is calculated for
     * @param n a derivative order
     * @param x a point the derivative is calculated in
     * @return the derivative of a function in point x
     */
    private double derivativeAtPoint(Function f, int n, double x) {
        if (n <= 0) throw new IllegalArgumentException("n should be at least 1");
        if (n == 1) return (f.calculate(x + delta) - f.calculate(x)) / delta;
        return (derivativeAtPoint(f, n - 1, x + delta) - derivativeAtPoint(f, n - 1, x)) / delta;
    }


    /**
     * Returns a derivative of a binary function given with respect to chosen variable
     * @param f initial function
     * @param withRespectTo 0 for dx and 1 for dy
     * @return derivative function
     */
    private BinaryFunction derivative(BinaryFunction f, int withRespectTo) {
        if (withRespectTo == 0) return (x, y) -> ((f.calculate(x + delta, y) - f.calculate(x, y)) / delta);
        if (withRespectTo == 1) return (x, y) -> ((f.calculate(x, y + delta) - f.calculate(x, y)) / delta);
        throw new IllegalArgumentException("2nd arg should be 0 or 1");
    }

    /**
     * A phi-function required for fixed-point iteration solver.
     * Given an equation f(x) = 0 one needs to transform it into x = phi(x) to start iterative process.
     * @param f a function to generate phi-function for
     * @return phi-function
     */
    private Function phi(Function f) {
        return x -> (x + (-1 / derivativeAtPoint(f, 1, x)) * f.calculate(x));
    }


    /**
     * Utility method to mute unnecessary output while in a file mode.
     * @param s a string to print in a console mode
     */
    private void printIfConsole(String s) {
        if(!isFile) System.out.println(s);
    }

    /**
     * Solves two equations non-linear system using Newton's method
     * @param f1 first equation (f1 = 0)
     * @param f2 second equation (f2 = 0)
     * @param x0 initial approximation for x
     * @param y0 initial approximation for y
     * @return result of computation
     * @throws DivergenceException thrown if the method cannot converge to a solution
     */
    private Result newtonSystem(BinaryFunction f1, BinaryFunction f2, double x0, double y0) throws DivergenceException {
        BinaryFunction f1d_x = derivative(f1, 0);
        BinaryFunction f1d_y = derivative(f1, 1);
        BinaryFunction f2d_x = derivative(f2, 0);
        BinaryFunction f2d_y = derivative(f2, 1);
        LinearSystemSolver solver = new LinearSystemSolver(eps);

        double xn = x0;
        double yn = y0;
        int iter = 0;

        while (iter < MAX_ITERATION) {
            double[][] matrix = new double[][]{
                    {f1d_x.calculate(xn, yn), f1d_y.calculate(xn, yn), -f1.calculate(xn, yn)},
                    {f2d_x.calculate(xn, yn), f2d_y.calculate(xn, yn), -f2.calculate(xn, yn)}
            };
            double[] solution = solver.solve(matrix);
            double xNext = xn + solution[0];
            double yNext = yn + solution[1];
            if ((Math.abs(solution[0]) < eps) && (Math.abs(solution[1]) < eps)) {
                return new Result(ResultStatus.SOLUTION_FOUND, new double[] {xNext, yNext}, iter, solution);
            }
            iter++;
            xn = xNext;
            yn = yNext;
        }
       return new Result(ResultStatus.DIVERGENCE, null, MAX_ITERATION, null);
    }

}



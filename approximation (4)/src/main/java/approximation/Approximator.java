package approximation;

import org.apache.commons.math3.linear.*;

import java.util.Arrays;
import java.util.function.Function;

public class Approximator {

    public ApproximationResult cubicApproximation(double[][] functionTable) {
        double x_sum = 0, x2_sum = 0, x3_sum = 0, x4_sum = 0, x5_sum = 0, x6_sum = 0,
                y_sum = 0, xy_sum = 0, x2y_sum = 0, x3y_sum = 0;
        for (int i = 0; i < functionTable.length; i++) {
            x_sum += functionTable[i][0];
            x2_sum += Math.pow(functionTable[i][0], 2);
            x3_sum += Math.pow(functionTable[i][0], 3);
            x4_sum += Math.pow(functionTable[i][0], 4);
            x5_sum += Math.pow(functionTable[i][0], 5);
            x6_sum += Math.pow(functionTable[i][0], 6);
            y_sum += functionTable[i][1];
            xy_sum += functionTable[i][0] * functionTable[i][1];
            x2y_sum += Math.pow(functionTable[i][0], 2) * functionTable[i][1];
            x3y_sum += Math.pow(functionTable[i][0], 3) * functionTable[i][1];
        }

        double[][] matrix = new double[][] {
                {functionTable.length, x_sum, x2_sum, x3_sum},
                {x_sum, x2_sum, x3_sum, x4_sum},
                {x2_sum, x3_sum, x4_sum, x5_sum},
                {x3_sum, x4_sum, x5_sum, x6_sum}
        };

        double[] constants = new double[] {y_sum, xy_sum, x2y_sum, x3y_sum};
        double[] solution = solveLinearSystem(matrix, constants);
        reverseArray(solution);
        Function<Double, Double> function = coefficientsToCubicFunction(solution);
        double deviation = deviationMeasure(functionTable, function);
        return new ApproximationResult(ApproximationType.CUBIC, solution, function, deviation);
    }

    public ApproximationResult squareApproximation(double[][] functionTable) {
        double x_sum = 0, x2_sum = 0, x3_sum = 0, x4_sum = 0,
                y_sum = 0, xy_sum = 0, x2y_sum = 0;

        for (int i = 0; i < functionTable.length; i++) {
            x_sum += functionTable[i][0];
            x2_sum += Math.pow(functionTable[i][0], 2);
            x3_sum += Math.pow(functionTable[i][0], 3);
            x4_sum += Math.pow(functionTable[i][0], 4);
            y_sum += functionTable[i][1];
            xy_sum += functionTable[i][0] * functionTable[i][1];
            x2y_sum += Math.pow(functionTable[i][0], 2) * functionTable[i][1];
        }

        double[][] matrix = new double[][] {
                {functionTable.length, x_sum, x2_sum},
                {x_sum, x2_sum, x3_sum},
                {x2_sum, x3_sum, x4_sum}
        };

        double[] constants = new double[] {y_sum, xy_sum, x2y_sum};
        double[] solution = solveLinearSystem(matrix, constants);
        reverseArray(solution);
        Function<Double, Double> function = coefficientsToSquareFunction(solution);
        double deviation = deviationMeasure(functionTable, function);
        return new ApproximationResult(ApproximationType.QUADRATIC, solution, function, deviation);
    }

    public ApproximationResult linearApproximation(double[][] functionTable) {
        double x_sum = 0, x2_sum = 0, y_sum = 0, xy_sum = 0;

        for (int i = 0; i < functionTable.length; i++) {
            x_sum += functionTable[i][0];
            x2_sum += Math.pow(functionTable[i][0], 2);
            y_sum += functionTable[i][1];
            xy_sum += functionTable[i][0] * functionTable[i][1];
        }

        double[][] matrix = {
                {x2_sum, x_sum},
                {x_sum, functionTable.length}
        };

        double[] constants = {
                xy_sum, y_sum
        };
        double[] solution = solveLinearSystem(matrix, constants);
        Function<Double, Double> function = coefficientsToLinearFunction(solution);
        double deviation = deviationMeasure(functionTable, function);
        return new ApproximationResult(ApproximationType.LINEAR, solution, function, deviation, linearCorrelation(functionTable));
    }

    public ApproximationResult exponentialApproximation(double[][] functionTable) {
        double[][] modifiedFunctionTable = Arrays.stream(functionTable).map(double[]::clone).toArray(double[][]::new);
        for (double[] xy: modifiedFunctionTable) {
            if (xy[1] <= 0) continue;
            xy[1] = Math.log(xy[1]);
        }
        ApproximationResult linear = linearApproximation(modifiedFunctionTable);
        double[] coefficients = linear.getCoefficients();
        coefficients[1] = Math.exp(coefficients[1]);
        Function<Double, Double> f = coefficientsToExpFunction(coefficients);
        return new ApproximationResult(ApproximationType.EXPONENTIAL, coefficients, f, deviationMeasure(functionTable, f));
    }

    public ApproximationResult logarithmicApproximation(double[][] functionTable) {
        double[][] modifiedFunctionTable = Arrays.stream(functionTable).map(double[]::clone).toArray(double[][]::new);
        for (double[] xy: modifiedFunctionTable) {
            xy[0] = Math.log(xy[0]);
        }
        ApproximationResult linear = linearApproximation(modifiedFunctionTable);
        double[] coefficients = linear.getCoefficients();
        Function<Double, Double> f = coefficientsToLogFunction(coefficients);
        return new ApproximationResult(ApproximationType.LOGARITHMIC, coefficients, f, deviationMeasure(functionTable, f));
    }

    public ApproximationResult powerApproximation(double[][] functionTable) {
        double[][] modifiedFunctionTable = Arrays.stream(functionTable).map(double[]::clone).toArray(double[][]::new);
        for (double[] xy: modifiedFunctionTable) {
            xy[0] = Math.log(xy[0]);
            xy[1] = Math.log(xy[1]);
        }
        ApproximationResult linear = linearApproximation(modifiedFunctionTable);
        double[] coefficients = linear.getCoefficients();
        coefficients[1] = Math.exp(coefficients[1]);
        Function<Double, Double> f = coefficientsToPowerFunction(coefficients);
        return new ApproximationResult(ApproximationType.POWER, coefficients, f, deviationMeasure(functionTable, f));
    }

    private Function<Double, Double> coefficientsToSquareFunction(double[] coefs) {
        return (x -> x * x * coefs[0] + x * coefs[1] + coefs[2]);
    }

    private Function<Double, Double> coefficientsToLinearFunction(double[] coefficients) {
        return (x -> x * coefficients[0] + coefficients[1]);
    }

    private Function<Double, Double> coefficientsToExpFunction(double[] coefficients) {
        return x -> coefficients[1] * Math.exp(coefficients[0] * x);
    }

    private Function<Double, Double> coefficientsToLogFunction(double[] coefficients) {
        return x -> coefficients[1] * Math.log(x) + coefficients[0];
    }

    private Function<Double, Double> coefficientsToPowerFunction(double[] coefficients) {
        return x -> coefficients[1] * Math.pow(x, coefficients[0]);
    }

    private Function<Double, Double> coefficientsToCubicFunction(double[] coefficients) {
        return x -> x * x * x * coefficients[0] + x * x * coefficients[1] + x * coefficients[2] + coefficients[3];
    }

    private double deviationMeasure(double[][] functionTable, Function<Double, Double> function) {
        double s = 0;
        for (double[] xy: functionTable) {
            s += Math.pow(xy[1] - function.apply(xy[0]), 2);
        }
        return s;
    }

    private double[] solveLinearSystem(double[][] coefficients, double[] constants) {
        DecompositionSolver solver = new LUDecomposition(new Array2DRowRealMatrix(coefficients)).getSolver();
        return solver.solve(new ArrayRealVector(constants)).toArray();
    }

    private double linearCorrelation(double[][] functionTable) {
        final double xAvg = Arrays.stream(functionTable).mapToDouble(x -> x[0]).sum() / functionTable.length;
        final double yAvg = Arrays.stream(functionTable).mapToDouble(x -> x[1]).sum() / functionTable.length;
        double top = Arrays.stream(functionTable).map(x -> new double[]{x[0] - xAvg, x[1] - yAvg}).mapToDouble(x -> x[0] * x[1]).sum();
        double bottomXSum = Arrays.stream(functionTable).mapToDouble(x -> Math.pow(x[0] - xAvg, 2)).sum();
        double bottomYSum = Arrays.stream(functionTable).mapToDouble(x -> Math.pow(x[1] - yAvg, 2)).sum();
        return top / Math.sqrt(bottomXSum * bottomYSum);
    }

    private void reverseArray(double[] array) {
        for (int i = 0; i < array.length / 2; i++) {
            double temp = array[i];
            array[i] = array[array.length - i - 1];
            array[array.length - i - 1] = temp;
        }
    }
}

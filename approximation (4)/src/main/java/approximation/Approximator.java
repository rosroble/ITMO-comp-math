package approximation;

import org.apache.commons.math3.linear.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class Approximator {

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

    private Function<Double, Double> coefficientsToSquareFunction(double[] coefs) {
        return (x -> x * x * coefs[0] + x * coefs[1] + coefs[2]);
    }

    private Function<Double, Double> coefficientsToLinearFunction(double[] coefficients) {
        return (x -> x * coefficients[0] + coefficients[1]);
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

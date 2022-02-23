package ru.rosroble.eqsolver.result;

import java.util.Arrays;

public class Result {
    private ResultStatus status;
    private double[] solution;
    private int iterations;
    private double[] error;

    public Result(ResultStatus status, double[] solution, int iterations, double[] error) {
        this.status = status;
        this.solution = solution;
        this.iterations = iterations;
        this.error = error;
    }

    public ResultStatus getStatus() {
        return status;
    }

    public double[] getSolution() {
        return solution;
    }

    public int getIterations() {
        return iterations;
    }

    public double[] getError() {
        return error;
    }

    @Override
    public String toString() {
        if (status == ResultStatus.DIVERGENCE) {
            return "Divergence: no solution found within required accuracy";
        }
        return "Found a solution: " + Arrays.toString(solution) + "\n" +
                "Iterations made: " + iterations + "\n" +
                "Error: " + Arrays.toString(error) + "\n" +
                "---------------------";
    }
}

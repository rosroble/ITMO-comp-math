package approximation;

import java.util.Arrays;
import java.util.function.Function;

public class ApproximationResult {
    private ApproximationType type;
    private double[] coefficients;
    private Function<Double, Double> function;
    private double deviation;
    private String functionToString;
    private double correlation;

    public ApproximationResult(ApproximationType type, double[] coefficients, Function<Double, Double> function, double deviation) {
        this.type = type;
        this.coefficients = coefficients;
        this.function = function;
        this.deviation = deviation;
        functionToString = coefficientsToString();
    }

    public ApproximationResult(ApproximationType type, double[] coefficients, Function<Double, Double> function, double deviation, double correlation) {
        this(type, coefficients, function, deviation);
        this.correlation = correlation;

    }

    public ApproximationType getType() {
        return type;
    }

    public double[] getCoefficients() {
        return coefficients;
    }

    public Function<Double, Double> getFunction() {
        return function;
    }

    public double getDeviation() {
        return deviation;
    }

    private String coefficientsToString() {
        String toStr;
        switch(type) {
            case LINEAR -> toStr = String.format("%fx +%f", coefficients[0], coefficients[1]);
            case QUADRATIC -> toStr = String.format("%fx^2 + %fx + %f", coefficients[0], coefficients[1], coefficients[2]);
            case EXPONENTIAL -> toStr = String.format("%fe^(%fx)", coefficients[1], coefficients[0]);
            case LOGARITHMIC -> toStr = String.format("%flnx + %f", coefficients[0], coefficients[1]);
            case POWER -> toStr = String.format("%fx^(%f)", coefficients[1], coefficients[0]);
            case CUBIC -> toStr = String.format("%fx^3 + %fx^2 + %fx + %f", coefficients[0], coefficients[1], coefficients[2], coefficients[3]);
            default -> toStr = null;
        }
        return toStr;
    }

    @Override
    public String toString() {
        return String.format("Approximation result.\n" +
                "Type: %s\n" +
                "Function: %s\n" +
                "Deviation: %f\n", type.name(), functionToString, deviation)
                + (type == ApproximationType.LINEAR ? "Correlation: " + correlation + "\n"
                : "");
    }
}

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
        functionToString = type == ApproximationType.LINEAR
                ? String.format("%fx +%f", coefficients[0], coefficients[1])
                : String.format("%fx^2 + %fx + %f", coefficients[0], coefficients[1], coefficients[2]);

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

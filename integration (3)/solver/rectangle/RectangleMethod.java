package solver.rectangle;

import solver.IntegralSolver;

import java.util.function.Function;

public class RectangleMethod implements IntegralSolver {

    private final RectangleMethodType type;
    private RectangleYValueFunction yValueFunction;

    public RectangleMethod(RectangleMethodType type) {
        this.type = type;
        switch (type) {
            case RECTANGLE_LEFT -> yValueFunction = (f, xStart, xEnd) -> (f.apply(xStart));
            case RECTANGLE_MIDDLE -> yValueFunction = (f, xStart, xEnd) -> (f.apply((xEnd + xStart) * 0.5));
            case RECTANGLE_RIGHT -> yValueFunction = (f, xStart, xEnd) -> (f.apply(xEnd));
        }
    }

    @Override
    public double calculate(Function<Double, Double> f, double a, double b, int n) {
        if (b < a) throw new IllegalArgumentException("b < a");
        final double h = (b - a) / n;
        double xCurrent = a;
        double xNext = a + h;
        double sum = 0;

        for (int i = 0; i < n; i++) {
            double y_n = yValueFunction.y_n(f, xCurrent, xNext);
            sum += h * y_n;
            xCurrent = xNext;
            xNext += h;
        }
        return sum;
    }

    @Override
    public String toString() {
        return "Rectangle method (" + type.name() + ")";
    }

    @FunctionalInterface
    interface RectangleYValueFunction {
        double y_n(Function<Double, Double> f, double xStart, double xEnd);
    }
}


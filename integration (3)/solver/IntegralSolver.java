package solver;

import java.util.function.Function;

public interface IntegralSolver {
    double calculate(Function<Double, Double> f, double a, double b, int n);
}

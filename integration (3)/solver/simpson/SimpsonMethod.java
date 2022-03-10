package solver.simpson;

import solver.IntegralSolver;

import java.util.function.Function;
import java.util.stream.IntStream;

public class SimpsonMethod implements IntegralSolver {
    @Override
    public double calculate(Function<Double, Double> f, double a, double b, int n) {
        if (b < a) throw new IllegalArgumentException("b < a");
        final double h = (b - a) / n;
        double xLeft = a;
        double xRight = xLeft + h;
        double xMid = (xLeft + xRight) / 2;
        double[] yValues = new double[2 * n + 1];
        yValues[0] = f.apply(xLeft);
        yValues[1] = f.apply(xMid);
        yValues[2] = f.apply(xRight);
        for (int i = 1; i < n; i++) {
            yValues[i * 2 + 1] = f.apply(xMid);
            yValues[i * 2 + 2] = f.apply(xRight);
            xLeft = xRight;
            xRight += h;
            xMid = (xLeft + xRight) / 2;
        }
        double sumOdds = IntStream.range(1, n*2).filter(x -> x % 2 == 1).mapToDouble(x -> yValues[x]).sum();
        double sumEvens = IntStream.range(2, n*2 - 1).filter(x -> x % 2 == 0).mapToDouble(x -> yValues[x]).sum();

        return (h / 6) * (yValues[0] + 4 * sumOdds + 2 * sumEvens + yValues[n*2]);

    }

    @Override
    public String toString() {
        return "Simpson Method";
    }
}

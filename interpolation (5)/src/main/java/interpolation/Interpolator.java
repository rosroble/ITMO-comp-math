package interpolation;

import java.util.stream.IntStream;

public class Interpolator {


    public InterpolationResult lagrange(double x, double[][] functionTable) {
        double sum = 0;
        for (int i = 0; i < functionTable.length; i++) {
            final int currentIndex = i;
            double numerator = IntStream.range(0, functionTable.length)
                    .filter(a -> a != currentIndex)
                    .mapToDouble(a -> x - functionTable[a][0])
                    .reduce(1, (a, b) -> a * b);
            double denominator = IntStream.range(0, functionTable.length)
                    .filter(a -> a != currentIndex)
                    .mapToDouble(a -> functionTable[currentIndex][0] - functionTable[a][0])
                    .reduce(1, (a, b) -> a * b);

            sum += functionTable[currentIndex][1] * numerator / denominator;
        }
        return new InterpolationResult(x, sum);
    }

    public InterpolationResult newton(double x, double[][] functionTable) {
        double res = 0;
        double product = 1;
        for (int i = 0; i < functionTable.length; i++) {
            res += dividedDifference(i, 0, functionTable) * product;
            product *= (x - functionTable[i][0]);
        }
        return new InterpolationResult(x, res);
    }

    private double dividedDifference(int diffDegree, int i, double[][] functionTable) {
        if (diffDegree == 0) {
            return functionTable[i][1];
        }
        return (dividedDifference(diffDegree - 1, i + 1, functionTable) - dividedDifference(diffDegree - 1, i, functionTable))
                / (functionTable[i + diffDegree][0] - functionTable[i][0]);
    }
}

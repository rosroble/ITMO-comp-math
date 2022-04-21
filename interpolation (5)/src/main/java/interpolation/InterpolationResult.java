package interpolation;

public class InterpolationResult {
    private double x;
    private double y;

    public InterpolationResult(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return "interpolation.InterpolationResult{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}

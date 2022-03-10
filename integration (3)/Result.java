public class Result {
    private final double answer;
    private final int n;
    private final double error;

    public Result(double answer, int n, double error) {
        this.answer = answer;
        this.n = n;
        this.error = error;
    }

    @Override
    public String toString() {
        return "Result info:\n" +
                "\tanswer: " + answer +
                "\tsectors: " + n +
                "\terror: " + error;
    }
}

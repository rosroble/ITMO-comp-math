package auxiliary;

import java.util.function.Function;

public class FunctionContainer {
    private final Function<Double, Double> function;
    private final String description;

    public FunctionContainer(Function<Double, Double> function, String description) {
        this.function = function;
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public Function<Double, Double> getFunction() {
        return function;
    }
}

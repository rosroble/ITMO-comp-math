package plot;

import com.github.sh0nk.matplotlib4j.NumpyUtils;
import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class PlotUtils {

    private final Plot plt;

    public PlotUtils() {
        plt = Plot.create();
    }
    
    public void draw(double fromX, double toX, double fromY, double toY, double[][]... functionTables) throws PythonExecutionException, IOException {
        plt.xlim(fromX, toX);
        plt.ylim(fromY, toY);
        for (double[][] functionTable: functionTables) {
            List<Double> x = Arrays.stream(functionTable).mapToDouble(doubles -> doubles[0])
                    .boxed().toList();
            List<Double> y = Arrays.stream(functionTable).mapToDouble(doubles -> doubles[1])
                    .boxed().toList();
            plt.plot().add(x, y);
        }
        plt.show();
    }


}

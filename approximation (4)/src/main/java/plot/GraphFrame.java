package plot;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.util.function.Function;


public class GraphFrame extends ApplicationFrame {
    public static final double DEFAULT_STEP = 0.05;

    public GraphFrame(String title) {
        super(title);
    }

    public void graph(double a, double b, Function<Double, Double>... functions) {
        XYDataset dataset = generateDataset(a, b, DEFAULT_STEP, functions);
        JFreeChart chart = ChartFactory.createXYAreaChart(
                "Graph",
                "X",
                "Y",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );
        ChartPanel panel = new ChartPanel(chart);
        panel.setFillZoomRectangle(false);
        panel.setDomainZoomable(true);
        panel.setRangeZoomable(true);
        pack();
        setSize(800, 600);
        setLocationRelativeTo(null);
        setContentPane(panel);
        setVisible(true);
    }

    private XYDataset generateDataset(double from, double to, double step, Function<Double, Double>... functions) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        for (Function<Double, Double> f: functions) {
            XYSeries series = new XYSeries(f.hashCode());
            for (double x = from; x < to + step; x += step) {
                series.add(x, f.apply(x));
            }
            dataset.addSeries(series);
        }
        return dataset;
    }
}

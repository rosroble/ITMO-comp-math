package ru.rosroble.eqsolver.plot;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import ru.rosroble.eqsolver.interfaces.Function;



public class GraphFrame extends ApplicationFrame {
    public static final double DEFAULT_STEP = 0.05;

    public GraphFrame(String title) {
        super(title);
    }

    public void graph(double a, double b, Function... functions) {
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

    private XYDataset generateDataset(double from, double to, double step, Function... functions) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        for (Function f: functions) {
            XYSeries series = new XYSeries(f.hashCode());
            for (double x = from; x < to + step; x += step) {
                series.add(x, f.calculate(x));
            }
            dataset.addSeries(series);
        }
        return dataset;
    }

    public void system(double from, double to) {
        Function f1 = x -> (Math.sqrt(4 - x*x));
        Function f1_ = x -> (-Math.sqrt(4 - x*x));
        Function f2 = Math::sin;
        graph(from, to, f1, f1_, f2);
    }
}

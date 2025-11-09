import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import javax.swing.*;

public class TestChart {
    public static void main(String[] args) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(1, "Series1", "A");
        dataset.addValue(4, "Series1", "B");
        dataset.addValue(3, "Series1", "C");

        JFreeChart chart = ChartFactory.createBarChart(
                "Test Chart",
                "Category",
                "Value",
                dataset
        );

        JFrame frame = new JFrame("Chart Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ChartPanel(chart));
        frame.setSize(600, 400);
        frame.setVisible(true);
    }
}

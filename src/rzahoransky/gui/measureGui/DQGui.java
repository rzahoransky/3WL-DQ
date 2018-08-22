package rzahoransky.gui.measureGui;

import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import calculation.MieList;
import charts.MieChartPanels;
import rzahoransky.dqpipeline.DQPipeline;
import rzahoransky.gui.measureSetup.MeasureSetUp;
import rzahoransky.utils.Charts;

public class DQGui extends JPanel{
	
	JFreeChart chart;
	private DQAnnotator dqAnnotator;
	MeasureSetUp setup = MeasureSetUp.getInstance();

	public DQGui(DQPipeline pipeline) {
		chart = MieChartPanels.getDQFieldDataset(setup.getMieList(0), setup.getMieList(1), setup.getMieList(2), true);
		dqAnnotator = new DQAnnotator();
		chart.getXYPlot().addAnnotation(dqAnnotator);
		pipeline.addNewSignalListener(dqAnnotator);
		ChartPanel panel = Charts.getChartPanel("DQ", chart);
		add(panel);
	}
	
	public JFreeChart getChart() {
		return chart;
	}

}

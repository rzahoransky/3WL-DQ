package rzahoransky.gui.measureGui;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYAnnotation;

import calculation.CalculationAssignment;
import calculation.MieList;
import charts.MieChartPanels;

public class DQGui {
	
	JFreeChart chart;
	private DQAnnotator dqAnnotator;

	public DQGui(MieList wl1, MieList wl2, MieList wl3) {
		chart = MieChartPanels.getDQFieldDataset(wl1, wl2, wl3, true);
		dqAnnotator = new DQAnnotator();
		chart.getXYPlot().addAnnotation(dqAnnotator);
	}

}

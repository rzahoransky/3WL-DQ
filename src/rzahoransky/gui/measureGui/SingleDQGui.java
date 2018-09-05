package rzahoransky.gui.measureGui;

import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;

import calculation.MieList;
import charts.MieChartPanels;
import rzahoransky.gui.measureSetup.MeasureSetUp;
import rzahoransky.utils.Charts;
import rzahoransky.utils.DQtype;

public class SingleDQGui extends JTabbedPane{
	
	private HashMap<DQtype, JFreeChart> dqPanels = new HashMap<>();

	public SingleDQGui() {
		
		int qlIndex = 0;
		
		for (DQtype dq: DQtype.values()) {
			MieList wl1 = null;
			MieList wl2 = null;
			switch (dq) {
			case DQ1:
				wl1 = MeasureSetUp.getInstance().getMieList(0);
				wl2 = MeasureSetUp.getInstance().getMieList(1);
				break;
			case DQ2:
				wl1 = MeasureSetUp.getInstance().getMieList(1);
				wl2 = MeasureSetUp.getInstance().getMieList(2);
				break;
			case DQ3:
				wl1 = MeasureSetUp.getInstance().getMieList(0);
				wl2 = MeasureSetUp.getInstance().getMieList(2);
			default:
				break;
			}
			

			JFreeChart dqChart = MieChartPanels.getDQDataset(wl1, wl2, true);
			dqChart.getXYPlot().addAnnotation(new SingleDQAnnotator(dq));
			dqChart.getXYPlot().setDomainAxis(new LogarithmicAxis("Diameter"));
			dqPanels.put(dq, dqChart);
			addTab(dq.toString(),Charts.getChartPanel(dq.toString(), dqChart));
			qlIndex++;
		}

	}

}

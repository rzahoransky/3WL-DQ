package rzahoransky.gui.measureGui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;

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
		chart.getTitle().setFont(new Font("Arial", Font.BOLD, 14));
		dqAnnotator = new DQAnnotator();
		chart.getXYPlot().addAnnotation(dqAnnotator);
		pipeline.addNewSignalListener(dqAnnotator);
		ChartPanel panel = Charts.getChartPanel("DQ", chart);
		setLayout(new GridBagLayout());
		
		//panel.setSize(100, 100);
		//panel.setPreferredSize(new Dimension(100, 100));
		GridBagConstraints c = new GridBagConstraints();
		c.fill=GridBagConstraints.BOTH;
		c.weightx=1;
		c.weighty=1;
		add(panel,c);
	}
	
	public JFreeChart getChart() {
		return chart;
	}

}

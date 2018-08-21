package rzahoransky.gui.measureGui;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYDrawableAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.AnnotationChangeListener;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.Drawable;

import calculation.MieList;
import charts.ChartType;
import charts.Charts;
import errors.WavelengthMismatchException;
import storage.dqMeas.read.DQReader;

@Deprecated
public class DQChart extends JPanel{

	private Charts chart;
	Annotator annotator;

	public DQChart(MieList wl1, MieList wl2, MieList wl3) throws IOException, WavelengthMismatchException {
		this.chart = new Charts(ChartType.DQField, wl1, wl2, wl3, true);
		add(chart.getChartPanel());
		annotator = new Annotator(chart.getChart().getXYPlot());
	}
	
	public ChartPanel getChartPanel() {
		return chart.getChartPanel();
	}
	
	public void addDQ(double dq1, double dq2) {
		annotator.add(dq1, dq2);
		
	}
	
	class Annotator {
		LinkedList<XYAnnotation> annotations = new LinkedList<>();
		int count = 10;
		private XYPlot plot;
		
		public Annotator(XYPlot plot) {
			this.plot = plot;
		}
		
		public void add(double dq1, double dq2) {
			XYAnnotation annotation = new XYTextAnnotation("x", dq1, dq2);
			annotations.add(annotation);
			plot.addAnnotation(annotation);
			
			if (annotations.size()>count) {
				plot.removeAnnotation(annotations.removeFirst());
			}
		}
	}

}

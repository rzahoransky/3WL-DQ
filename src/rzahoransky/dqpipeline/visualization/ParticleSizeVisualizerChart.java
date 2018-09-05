package rzahoransky.dqpipeline.visualization;

import java.awt.BasicStroke;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.swing.JFrame;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.renderer.xy.DeviationRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYIntervalSeries;
import org.jfree.data.xy.XYIntervalSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;

import javafx.scene.chart.XYChart.Series;
import rzahoransky.dqpipeline.dataExtraction.DiameterComperator;
import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.interfaces.AbstractDQPipelineElement;
import rzahoransky.dqpipeline.interfaces.DQPipelineElement;
import rzahoransky.dqpipeline.interfaces.ParticleDiameterVisualizer;
import rzahoransky.utils.Charts;
import rzahoransky.utils.RawSignalType;

public class ParticleSizeVisualizerChart extends AbstractDQPipelineElement implements ParticleDiameterVisualizer {

	ChartPanel chartPanel;
	JFrame frame;
	private int maxAge = 3000;
	private long start;

	public ParticleSizeVisualizerChart(boolean showAsFrame) {
		start = System.currentTimeMillis();

		YIntervalSeriesCollection dataset = Charts.getParticleIntervalCollection();
		JFreeChart chart = Charts.getXYChart("Particle Information", "Time", "Diameter in µm", dataset);
		DeviationRenderer r = getRenderer();
		chart.getXYPlot().setRenderer(r);
		chart.getXYPlot().setDomainAxis(getDateAxis());
		chartPanel = Charts.getChartPanel("Measurment", chart);

		if (showAsFrame) {
			frame = new JFrame("AD-Samples");
			frame.setSize(500, 300);
			frame.add(chartPanel);
			frame.setVisible(true);
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}
	}

	private DeviationRenderer getRenderer() {
		DeviationRenderer r = new DeviationRenderer();
		r.setBaseShapesVisible(false);
		r.setSeriesFillPaint(0, Color.ORANGE);
		r.setAlpha(0.3f);
		r.setBaseStroke(new BasicStroke(2));
		r.setSeriesStroke(0, new BasicStroke(3));
		return r;
	}
	
	private DateAxis getDateAxis() {
		DateAxis dateAxis = new DateAxis();
		dateAxis.setDateFormatOverride(new SimpleDateFormat("HH:mm:ss.SSS")); 
		return dateAxis;
	}

	public ChartPanel getChartPanel() {
		return chartPanel;
	}

	public JFrame getFrame() {
		return frame;
	}

	public void visualizeDQMeasurement(DQSignal measurement) {
		YIntervalSeriesCollection collection = (YIntervalSeriesCollection) chartPanel.getChart().getXYPlot().getDataset();
		updateSeries(measurement, collection);
	}

	private void updateSeries(DQSignal measurement, YIntervalSeriesCollection collection) {
		long now = System.currentTimeMillis();
		//double seconds = (now - start)/1000d;
		YIntervalSeries series = collection.getSeries(0);
		// series.clear();
		//double d = measurement.getDiameter().getAverageDiameter();
		//RegularTimePeriod period = new Millisecond(new Date(measurement.getTimeStamp()));
		DiameterComperator d = measurement.getDiameter();
		series.add(now, d.getAverageDiameter(),d.getLowesetDiameter(), d.getHighestDiameter());
		//xyIntervalSeries.add(seconds, measurement.getDiameter().getLowesetDiameter(), measurement.getDiameter().getHighestDiameter(), y, yLow, yHigh);
		//xyIntervalSeries.setMaximumItemAge(maxAge);
		series.setMaximumItemCount(maxAge);
		series.fireSeriesChanged();
		
		//series = collection.getSeries(1);
		//series.a
		
		
		// series.notify();
	}

	@Override
	public DQSignal processDQElement(DQSignal in) {
		DQSignal element = in;
		if (element != null)
			try {
			visualizeDQMeasurement(element);
			} catch (Exception e) {
				e.printStackTrace();
			}
		// out.put(element); for debug purpose
		return element;
	}

	@Override
	public String description() {
		return "Visualizes Particle Diameter";
	}
	
	public void setMaximumAge(int entries) {
		this.maxAge = entries;
	}

}

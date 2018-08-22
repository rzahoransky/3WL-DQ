package rzahoransky.dqpipeline.visualization;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.swing.JFrame;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import rzahoransky.dqpipeline.AbstractDQPipelineElement;
import rzahoransky.dqpipeline.DQSignal;
import rzahoransky.dqpipeline.DQPipelineElement;
import rzahoransky.utils.Charts;
import rzahoransky.utils.RawSignalType;

public class ParticleSizeVisualizer extends AbstractDQPipelineElement {

	ChartPanel chartPanel;
	JFrame frame;
	RawSignalType[] signalTypes = { RawSignalType.ref, RawSignalType.meas, RawSignalType.mode, RawSignalType.trigger };

	public ParticleSizeVisualizer(boolean showAsFrame) {

		TimeSeriesCollection dataset = Charts.getParticleDataSet();
		JFreeChart chart = Charts.getXYChart("Particle Size", "Time", "Diameter in µm", dataset);
		chartPanel = Charts.getChartPanel("Measurment", chart);

		if (showAsFrame) {
			frame = new JFrame("AD-Samples");
			frame.setSize(500, 300);
			frame.add(chartPanel);
			frame.setVisible(true);
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}
	}

	public ChartPanel getChartPanel() {
		return chartPanel;
	}

	public JFrame getFrame() {
		return frame;
	}

	public void visualizeDQMeasurement(DQSignal measurement) {
		TimeSeriesCollection collection = (TimeSeriesCollection) chartPanel.getChart().getXYPlot().getDataset();

		TimeSeries series = (TimeSeries) collection.getSeries().get(0);

		updateSeries(measurement, series);
	}

	private void updateSeries(DQSignal measurement, TimeSeries series) {
		// series.clear();
		double d = measurement.getDiameter();
		RegularTimePeriod period = new Millisecond(new Date(measurement.getTimeStamp()));
		series.add(period, d);
		//series.setMaximumItemAge(100);
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
		return "Visualizes Particle Sizes";
	}

}

package rzahoransky.dqpipeline.visualization;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.swing.JFrame;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import rzahoransky.dqpipeline.AbstractDQPipelineElement;
import rzahoransky.dqpipeline.DQSignal;
import rzahoransky.dqpipeline.DQPipelineElement;
import rzahoransky.utils.Charts;
import rzahoransky.utils.RawSignalType;

public class DQSinglePeriodMeasurementVisualizer extends AbstractDQPipelineElement{
	
	ChartPanel chartPanel;
	JFrame frame;
	RawSignalType[] signalTypes = {RawSignalType.ref, RawSignalType.meas, RawSignalType.mode, RawSignalType.trigger};

	public DQSinglePeriodMeasurementVisualizer(boolean showAsFrame) {
		
		XYSeriesCollection dataset = Charts.getDataSet(signalTypes);
		JFreeChart chart = Charts.getXYChart("Raw Signal", "Time", "Voltage", dataset);
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
	
	public void visualizeDQMeasurement (DQSignal measurement) {
		XYSeriesCollection collection = (XYSeriesCollection) chartPanel.getChart().getXYPlot().getDataset();
		
		for (RawSignalType type: signalTypes) {
			XYSeries series = collection.getSeries(type);
			if (measurement.contains(type)) {
				updateSeries(measurement, series);
			}
		}
	}
	
	private void updateSeries(DQSignal measurement, XYSeries series) {
		if (measurement.getPeriodMarker().size() > 1) {
			series.setNotify(false);
			series.clear();
			RawSignalType type = (RawSignalType) series.getKey();
			int start = measurement.getPeriodMarker().get(0);
			int end = measurement.getPeriodMarker().get(1);

			for (int i = start; i < end; i++) {
				series.add(i - start, measurement.get(type).get(i));
			}
			series.setNotify(true);
			// series.notify();
		}
	}

	@Override
	public DQSignal processDQElement(DQSignal in) {
		DQSignal element = in;
		if (element!=null)
			visualizeDQMeasurement(element);
		// out.put(element); for debug purpose
		return element;
	}

	@Override
	public String description() {
		return "Visualizes a single raw data period";
	}


	
	

}

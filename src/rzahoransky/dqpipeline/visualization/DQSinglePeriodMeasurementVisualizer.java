package rzahoransky.dqpipeline.visualization;

import java.awt.BasicStroke;
import java.awt.Font;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.swing.JFrame;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.interfaces.AbstractDQPipelineElement;
import rzahoransky.dqpipeline.interfaces.DQPipelineElement;
import rzahoransky.utils.Charts;
import rzahoransky.utils.RawSignalType;
import rzahoransky.utils.TimeCounter;

public class DQSinglePeriodMeasurementVisualizer extends AbstractDQPipelineElement{
	
	volatile ChartPanel chartPanel;
	JFrame frame;
	RawSignalType[] signalTypes = {RawSignalType.ref, RawSignalType.meas, RawSignalType.mode, RawSignalType.trigger};
	TimeCounter refresh = new TimeCounter(250);

	public DQSinglePeriodMeasurementVisualizer(boolean showAsFrame) {
		
		XYSeriesCollection dataset = Charts.getDataSet(signalTypes);
		JFreeChart chart = Charts.getXYChart("Raw Signal", null, "Voltage", dataset);
		for (int i = 0; i<chart.getXYPlot().getSeriesCount();i++) {
			chart.getXYPlot().getRenderer().setSeriesStroke(i, new BasicStroke(2.0f));
		}
		chartPanel = Charts.getChartPanel("Measurment", chart);
		chart.getTitle().setFont(new Font("Arial", Font.BOLD, 14));
		
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
	
	public void setUpdateIntervallInMs(long ms) {
		refresh = new TimeCounter(ms);
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
		series.setNotify(false);

		
		if (measurement.getPeriodMarker().size() > 1) {
			series.clear();
			RawSignalType type = (RawSignalType) series.getKey();

			 int start = measurement.getPeriodMarker().get(0);
			 int end = measurement.getPeriodMarker().get(1);
			
			for (int i = start; i < end; i++) {
				series.add(i - start, measurement.get(type).get(i));
			}
			
		} else {
			try {
				int start = 0;
				int end = 80;
				RawSignalType type = (RawSignalType) series.getKey();
				series.clear();
				for (int i = start; i < end; i++) {
					series.add(i - start, measurement.get(type).get(i));
				}
			} catch (Exception e) {}
		}
		series.setNotify(true);
	}

	@Override
	public DQSignal processDQElement(DQSignal in) {
		DQSignal element = in;
		if (element!=null && refresh.timeForUpdate())
			visualizeDQMeasurement(element);
		// out.put(element); for debug purpose
		return element;
	}

	@Override
	public String description() {
		return "Visualizes a single raw data period";
	}


	
	

}

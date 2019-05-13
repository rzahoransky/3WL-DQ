package rzahoransky.dqpipeline.visualization;

import javax.swing.JFrame;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.interfaces.AbstractDQPipelineElement;
import rzahoransky.utils.Charts;
import rzahoransky.utils.RawSignalType;

public class DQMeasurementVisualizer extends AbstractDQPipelineElement{
	
	ChartPanel chartPanel;
	JFrame frame;
	RawSignalType[] signalTypes = {RawSignalType.ref, RawSignalType.meas, RawSignalType.mode, RawSignalType.trigger};

	public DQMeasurementVisualizer() {
		frame = new JFrame("AD-Samples");
		XYSeriesCollection dataset = Charts.getDataSet(signalTypes);
		JFreeChart chart = Charts.getXYChart("Values", "Time", "Voltage", dataset);
		chartPanel = Charts.getChartPanel("Measurment", chart);
		frame.setSize(500, 300);
		frame.add(chartPanel);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	public ChartPanel getChartPanel() {
		return chartPanel;
	}

	public JFrame getFrame() {
		return frame;
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
	
	private void updateSeries (DQSignal measurement, XYSeries series) {
		series.setNotify(false);
		series.clear();
		RawSignalType type = (RawSignalType) series.getKey();
		 int i = 0;
		 for (double value: measurement.get(type)) {
			 //series.addOrUpdate(i, value);
			 series.add(i, value);
			 i++;
		 }
		 series.setNotify(true);
		 //series.notify();
	}

	@Override
	public DQSignal processDQElement(DQSignal in) {
		if(!in.isValid)
			return in;
		
		DQSignal element = in;
		if (element!=null)
			visualizeDQMeasurement(element);
		// out.put(element); for debug purpose
		return element;
	}

	@Override
	public String description() {
		return "Visualizes the DQPipeline";
	}


	
	

}

package rzahoransky.dqpipeline.visualization;

import java.util.Date;

import javax.swing.JFrame;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.interfaces.AbstractDQPipelineElement;
import rzahoransky.utils.Charts;
import rzahoransky.utils.DQtype;
import rzahoransky.utils.RefreshTimeCounter;

public class DQVisualizer extends AbstractDQPipelineElement{
	
	ChartPanel chartPanel;
	JFrame frame;
	RefreshTimeCounter timer = new RefreshTimeCounter();
	//ExtractedSignalType[] types = {ExtractedSignalType.wl1wOffset, ExtractedSignalType.wl2wOffset, ExtractedSignalType.wl3wOffset, ExtractedSignalType.offset};
	//ExtractedSignalType[] typesRef = {ExtractedSignalType.wl1wOffset, ExtractedSignalType.wl2wOffset, ExtractedSignalType.wl3wOffset, ExtractedSignalType.offset};

	public DQVisualizer() {
		frame = new JFrame("AD-Samples");
		TimeSeriesCollection dataset = Charts.getDataSet(DQtype.values());
		JFreeChart chart = Charts.getXYChart("Values", "Time", "DQ", dataset);
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
		TimeSeriesCollection collection = (TimeSeriesCollection) chartPanel.getChart().getXYPlot().getDataset();
		
		for (DQtype type: DQtype.values()) {
			TimeSeries series = collection.getSeries(type);
				updateSeries(measurement, series);
		}
	}
	
	private void updateSeries(DQSignal measurement, TimeSeries series) {
			series.setNotify(false);
			//series.clear();
			DQtype type = (DQtype) series.getKey();
			
			Second second = new Second(new Date(measurement.getTimeStamp()));
			Millisecond milliSecond = new Millisecond(new Date(measurement.getTimeStamp()));
			//series.addOrUpdate(milliSecond, measurement.getAveragedValues(RawSignalType.meas, type));
			//series.addOrUpdate(milliSecond, measurement.getAveragedValues(RawSignalType.meas, type));
			series.addOrUpdate(milliSecond, measurement.getDQ(type).getDqValue());


//			for (int i = measurement.getsin; i < end; i++) {
//				series.add(i - start, measurement.get(type).get(i));
//			}
			series.setNotify(true);
			// series.notify();
	}

	@Override
	public DQSignal processDQElement(DQSignal in) {
		DQSignal element = in;
		if (element!=null && timer.timeForUpdate())
			visualizeDQMeasurement(element);
		// out.put(element); for debug purpose
		return element;
	}

	@Override
	public String description() {
		return "Visualizes a single dq";
	}


	
	

}

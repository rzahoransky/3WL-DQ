package rzahoransky.dqpipeline.visualization;

import java.awt.BasicStroke;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFrame;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.interfaces.AbstractDQPipelineElement;
import rzahoransky.utils.Charts;
import rzahoransky.utils.ExtractedSignalType;
import rzahoransky.utils.RawSignalType;
import rzahoransky.utils.TransmissionType;

public class LaserVoltageVisualizer extends AbstractDQPipelineElement{
	
	ChartPanel chartPanel;
	JFrame frame;
	ExtractedSignalType[] types = {ExtractedSignalType.wl1wOffset, ExtractedSignalType.wl2wOffset, ExtractedSignalType.wl3wOffset, ExtractedSignalType.offset};
	ExtractedSignalType[] typesRef = {ExtractedSignalType.wl1wOffset, ExtractedSignalType.wl2wOffset, ExtractedSignalType.wl3wOffset, ExtractedSignalType.offset};

	public LaserVoltageVisualizer(boolean showAsFrame) {
		TimeSeriesCollection dataset = Charts.getDataSet(types);
		JFreeChart chart = Charts.getXYChart("Values", "Time", "Voltage", dataset);
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

	public ChartPanel getChartPanel() {
		return chartPanel;
	}

	public JFrame getFrame() {
		return frame;
	}
	
	private DateAxis getDateAxis() {
		DateAxis dateAxis = new DateAxis();
		dateAxis.setDateFormatOverride(new SimpleDateFormat("HH:mm:ss.SSS")); 
		return dateAxis;
	}
	
	public void visualizeDQMeasurement (DQSignal measurement) {
		TimeSeriesCollection collection = (TimeSeriesCollection) chartPanel.getChart().getXYPlot().getDataset();
		ExtractedSignalType[] typesToShow = {ExtractedSignalType.wl1wOffset, ExtractedSignalType.wl2wOffset, ExtractedSignalType.wl3wOffset};
		for (ExtractedSignalType type: typesToShow) {
			TimeSeries series = collection.getSeries(type);
				updateSeries(measurement, series);
		}
	}
	
	private void updateSeries(DQSignal measurement, TimeSeries series) {
			series.setNotify(false);
			//series.clear();
			ExtractedSignalType type = (ExtractedSignalType) series.getKey();
			
			//Second second = new Second(new Date(measurement.getTimeStamp()));
			Millisecond milliSecond = new Millisecond(new Date(measurement.getTimeStamp()));
			//series.addOrUpdate(milliSecond, measurement.getAveragedValues(RawSignalType.meas, type));
			series.addOrUpdate(milliSecond, measurement.getAveragedValues(RawSignalType.meas, type));
			


//			for (int i = measurement.getsin; i < end; i++) {
//				series.add(i - start, measurement.get(type).get(i));
//			}
			series.setNotify(true);
			// series.notify();
	}

	@Override
	public DQSignal processDQElement(DQSignal in) {
		DQSignal element = in;
		if (element!=null) {
			visualizeDQMeasurement(element);
		}
		// out.put(element); for debug purpose
		return element;
	}

	@Override
	public String description() {
		return "Visualizes raw laser measurement";
	}
	
	public void setStroke(BasicStroke stroke) {
		JFreeChart chart = chartPanel.getChart();
		for (int i = 0; i<chart.getXYPlot().getSeriesCount();i++) {
			chart.getXYPlot().getRenderer().setSeriesStroke(i, stroke);
		}
	}
	
	public void setMaxAge(long age) {
		TimeSeriesCollection collection = (TimeSeriesCollection) chartPanel.getChart().getXYPlot().getDataset();
		for(Object series: collection.getSeries()) {
			((TimeSeries)series).setMaximumItemCount((int) age);
		}
	}
	
	public void clearSeries() {
		XYSeriesCollection collection = (XYSeriesCollection) chartPanel.getChart().getXYPlot().getDataset();
		
		for (TransmissionType type: TransmissionType.values()) {
			XYSeries series = collection.getSeries(type);
				series.clear();
		}
	}


	
	

}

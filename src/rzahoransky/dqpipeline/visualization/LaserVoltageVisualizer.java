package rzahoransky.dqpipeline.visualization;

import java.awt.BasicStroke;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFrame;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimePeriodFormatException;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.interfaces.AbstractDQPipelineElement;
import rzahoransky.utils.Charts;
import rzahoransky.utils.ExtractedSignalType;
import rzahoransky.utils.RawSignalType;
import rzahoransky.utils.RefreshTimeCounter;
import rzahoransky.utils.TransmissionType;

public class LaserVoltageVisualizer extends AbstractDQPipelineElement{
	
	ChartPanel chartPanel;
	JFrame frame;
	RefreshTimeCounter refresh = new RefreshTimeCounter(100);
	ExtractedSignalType[] typesToShow = {ExtractedSignalType.wl1wOffset, ExtractedSignalType.wl2wOffset, ExtractedSignalType.wl3wOffset, ExtractedSignalType.offset};
	
	//enums for measurement and reference
	ExtractedSignalType[] types = {ExtractedSignalType.wl1wOffset, ExtractedSignalType.wl2wOffset, ExtractedSignalType.wl3wOffset, ExtractedSignalType.offset};
	ExtractedSignalType[] typesRef = {ExtractedSignalType.wl1wOffset, ExtractedSignalType.wl2wOffset, ExtractedSignalType.wl3wOffset, ExtractedSignalType.offset};

	public LaserVoltageVisualizer(boolean showAsFrame) {
		
		TimeSeriesCollection dataset = Charts.getDataSet(types,1000);
		JFreeChart chart = Charts.getXYChart("Values", "Time", "Voltage", dataset);
		chart.getXYPlot().setDomainAxis(getDateAxis());
		chartPanel = Charts.getChartPanel("Measurment", chart);
		
		for (int i = 0; i<chart.getXYPlot().getSeriesCount();i++) {
			chart.getXYPlot().getRenderer().setSeriesStroke(i, new BasicStroke(2.0f));
		}
		
		frame = new JFrame("AD-Samples");
		frame.setSize(500, 300);
		frame.add(chartPanel);
		frame.setVisible(showAsFrame);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

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
		for (ExtractedSignalType type: typesToShow) {
			TimeSeries series = collection.getSeries(type);
				updateSeries(measurement, series);
		}
	}
	
	private void updateSeries(DQSignal measurement, TimeSeries series) {
		series.setNotify(false);
		// series.clear();
		ExtractedSignalType type = (ExtractedSignalType) series.getKey();

		// Second second = new Second(new Date(measurement.getTimeStamp()));
		Millisecond milliSecond = new Millisecond(new Date(measurement.getTimeStamp()));
		// series.addOrUpdate(milliSecond,
		// measurement.getAveragedValues(RawSignalType.ref, type));
		double offset = measurement.getAveragedValues(RawSignalType.meas, ExtractedSignalType.offset);
		double signalWithOffset = measurement.getAveragedValues(RawSignalType.meas, type);

		if (type == ExtractedSignalType.offset)
			series.addOrUpdate(milliSecond, signalWithOffset);
		else
			series.addOrUpdate(milliSecond, signalWithOffset + offset);
			


//			for (int i = measurement.getsin; i < end; i++) {
//				series.add(i - start, measurement.get(type).get(i));
//			}
			series.setNotify(true);
			// series.notify();
	}

	@Override
	public DQSignal processDQElement(DQSignal in) {
		DQSignal element = in;
		if (frame.isVisible() && element!=null && refresh.timeForUpdate()) {
			visualizeDQMeasurement(element);
		}
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

package rzahoransky.dqpipeline.visualization;

import java.awt.BasicStroke;
import java.awt.Font;
import java.text.SimpleDateFormat;

import javax.swing.JFrame;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.interfaces.AbstractDQPipelineElement;
import rzahoransky.utils.Charts;
import rzahoransky.utils.RefreshTimeCounter;
import rzahoransky.utils.TransmissionType;

public class TransmissionVisualizer extends AbstractDQPipelineElement{
	
	ChartPanel chartPanel;
	JFrame frame;
	protected RefreshTimeCounter refresh = new RefreshTimeCounter(33);
	//ExtractedSignalType[] types = {ExtractedSignalType.wl1wOffset, ExtractedSignalType.wl2wOffset, ExtractedSignalType.wl3wOffset, ExtractedSignalType.offset};
	//ExtractedSignalType[] typesRef = {ExtractedSignalType.wl1wOffset, ExtractedSignalType.wl2wOffset, ExtractedSignalType.wl3wOffset, ExtractedSignalType.offset};

	public TransmissionVisualizer(boolean showAsFrame) {

		//TimeSeriesCollection dataset = Charts.getTimeSeries(TransmissionType.values());
		XYSeriesCollection dataset = Charts.getDataSet(TransmissionType.values(),1000);
		JFreeChart chart = Charts.getXYChart("Transmission", "Time", "I/I0", dataset);
		chart.getXYPlot().setDomainAxis(getDateAxis());
		for (int i = 0; i<chart.getXYPlot().getSeriesCount();i++) {
			chart.getXYPlot().getRenderer().setSeriesStroke(i, new BasicStroke(2.0f));
		}
		chart.getTitle().setFont(new Font("Arial", Font.BOLD, 14));
		chartPanel = Charts.getChartPanel("Measurment", chart);

		
		if(showAsFrame) {
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
		dateAxis.setDateFormatOverride(new SimpleDateFormat("HH:mm:ss")); 
		return dateAxis;
	}
	
	public void visualizeDQMeasurement (DQSignal measurement) {
		XYSeriesCollection collection = (XYSeriesCollection) chartPanel.getChart().getXYPlot().getDataset();
		
		for (TransmissionType type: TransmissionType.values()) {
			XYSeries series = collection.getSeries(type);
				updateSeries(measurement, series);
		}
	}
	
	private void updateSeries(DQSignal measurement, XYSeries series) {
			series.setNotify(false);
			//series.clear();
			TransmissionType type = (TransmissionType) series.getKey();
			
			//Second second = new Second(new Date(measurement.getTimeStamp()));
			//Millisecond milliSecond = new Millisecond(new Date(measurement.getTimeStamp()));
			//series.addOrUpdate(milliSecond, measurement.getAveragedValues(RawSignalType.meas, type));
			//series.addOrUpdate(milliSecond, measurement.getAveragedValues(RawSignalType.meas, type));
			//series.addOrUpdate(milliSecond, measurement.getTransmission(type));
			//series.addo
			series.addOrUpdate(measurement.getTimeStamp(),measurement.getTransmission(type));


//			for (int i = measurement.getsin; i < end; i++) {
//				series.add(i - start, measurement.get(type).get(i));
//			}
			series.setNotify(true);
			// series.notify();
	}

	@Override
	public DQSignal processDQElement(DQSignal in) {
		if (!in.isValid)
			return in;
		
		try {
		if (in!=null && refresh.timeForUpdate())
			visualizeDQMeasurement(in);
		} catch (Exception e) {} //just continue
		// out.put(element); for debug purpose
		return in;
	}
	
	public void setStroke(BasicStroke stroke) {
		JFreeChart chart = chartPanel.getChart();
		for (int i = 0; i<chart.getXYPlot().getSeriesCount();i++) {
			chart.getXYPlot().getRenderer().setSeriesStroke(i, stroke);
		}
	}
	
	public void setMaxAge(long age) {
		XYSeriesCollection collection = (XYSeriesCollection) chartPanel.getChart().getXYPlot().getDataset();
		for(Object series: collection.getSeries()) {
			((XYSeries)series).setMaximumItemCount((int) age);
		}
	}

	@Override
	public String description() {
		return "Visualizes transmissions";
	}
	
	public void clearSeries() {
		XYSeriesCollection collection = (XYSeriesCollection) chartPanel.getChart().getXYPlot().getDataset();
		
		for (TransmissionType type: TransmissionType.values()) {
			XYSeries series = collection.getSeries(type);
				series.clear();
		}
	}


	
	

}

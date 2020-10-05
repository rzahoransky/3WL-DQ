package rzahoransky.dqpipeline.visualization;

import java.awt.BasicStroke;
import java.awt.Font;

import javax.swing.JFrame;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import rzahoransky.dqpipeline.dataExtraction.rawDataExtraction.RawVoltageExtractorFactory;
import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.interfaces.AbstractDQPipelineElement;
import rzahoransky.dqpipeline.interfaces.IMeasurePoints;
import rzahoransky.gui.measureGui.TriggerAnnotator;
import rzahoransky.utils.Charts;
import rzahoransky.utils.MeasureSetUp;
import rzahoransky.utils.RawSignalType;
import rzahoransky.utils.RefreshTimeCounter;

public class DQSinglePeriodMeasurementVisualizer extends AbstractDQPipelineElement{
	
	volatile ChartPanel chartPanel;
	JFrame frame;
	RawSignalType[] signalTypes = {RawSignalType.ref, RawSignalType.meas, RawSignalType.mode, RawSignalType.trigger};
	RefreshTimeCounter refresh = new RefreshTimeCounter(250);
	protected static final IMeasurePoints measurePoints = RawVoltageExtractorFactory.getRawVoltageExtractor().getMeasurePoints();
	boolean annotatorAdded = false;

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
		refresh = new RefreshTimeCounter(ms);
	}
	
	public void visualizeDQMeasurement (DQSignal measurement) {
		//chartPanel.getChart().getXYPlot().setNotify(false);
		XYSeriesCollection collection = (XYSeriesCollection) chartPanel.getChart().getXYPlot().getDataset();
		
		for (RawSignalType type: signalTypes) {
			XYSeries series = collection.getSeries(type);
			if (measurement.contains(type)) {
					updateSeries(measurement, series);
			}
		}
		
//		chartPanel.getChart().getXYPlot().setNotify(false);
//		for (Object annot: chartPanel.getChart().getXYPlot().getAnnotations()) 
//			chartPanel.getChart().getXYPlot().removeAnnotation((XYAnnotation) annot);
//		
//		for (XYBoxAnnotation annotation: getMarkerAnnotations(measurement)) {
//			chartPanel.getChart().getXYPlot().addAnnotation(annotation);
//		}
//		chartPanel.getChart().getXYPlot().setNotify(true);
	}
	
//	private LinkedList<XYBoxAnnotation> getMarkerAnnotations(DQSignal measurement) {
//		LinkedList<XYBoxAnnotation> result = new LinkedList<>();
//		for (ExtractedSignalType type: ExtractedSignalType.values()) {
//			double periodLength = measurement.getSinglePeriods().get(0).getPeriodLength();
//			int arrayLength = measurePoints.getRelativeMeasurePoint(type).length;
//			double start = measurePoints.getRelativeMeasurePoint(type)[0]*periodLength;
//			double end = measurePoints.getRelativeMeasurePoint(type)[arrayLength-1]*periodLength;
//			end = Math.max(end, start+1); //if there is only one marker
//			double y0 = measurement.getReference().get((int)start)-1;
//			double y1 = measurement.getReference().get((int)start)+1;
//			XYBoxAnnotation annotation = new XYBoxAnnotation(start, y0, end, y1);
//			result.add(annotation);
//		}
//		return result;
//	}
	
	private void updateSeries(DQSignal measurement, XYSeries series) {
		if(!annotatorAdded)
			addAnnotator();
		series.setNotify(false);

		
		if (measurement.getPeriodMarker().size() > 1) { //enough markers are present
			series.clear();
			RawSignalType type = (RawSignalType) series.getKey();

			 int start = measurement.getPeriodMarker().get(0);
			 int end = measurement.getPeriodMarker().get(1);
			
			for (int i = start; i < end; i++) {
				series.add(i - start, measurement.get(type).get(i));
			}
			
		} else { //not enough markers present. Show signal from index 0 .. 80
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



	private void addAnnotator() {
		TriggerAnnotator triggerAnnotator = new TriggerAnnotator();
		MeasureSetUp.getInstance().getPipeline().addNewSignalListener(triggerAnnotator);
		chartPanel.getChart().getXYPlot().addAnnotation(triggerAnnotator);
		annotatorAdded = true;
		
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

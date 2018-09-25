package rzahoransky.utils;

import java.awt.BasicStroke;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.Dataset;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYIntervalSeries;
import org.jfree.data.xy.XYIntervalSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;

public class Charts {

	private Charts() {
		// TODO Auto-generated constructor stub
	}
	
	public static JFreeChart getXYChart(String title, String xAxis, String yAxis, XYDataset dataset) {
	      JFreeChart xylineChart = ChartFactory.createXYLineChart(
	    	         title ,
	    	         xAxis ,
	    	         yAxis,
	    	         dataset ,
	    	         PlotOrientation.VERTICAL ,
	    	         true , true , false);
			XYPlot plot = (XYPlot) xylineChart.getPlot();
			XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( );
			renderer.setBaseShapesVisible(false);
			renderer.setBaseStroke(new BasicStroke(2f));
			plot.setRenderer(renderer);
	      
		return xylineChart;
		
	}
	
	public static ChartPanel getChartPanel(String title, JFreeChart chart) {
		ChartPanel panel = new ChartPanel(chart);
		return panel;
	}
	
	public static XYSeriesCollection getDataSet(RawSignalType... names) {
		XYSeriesCollection collection = new XYSeriesCollection();
		for (RawSignalType s: names) {
			collection.addSeries(new XYSeries(s));
		}
		
		return collection;
	}
	
	public static YIntervalSeriesCollection getParticleIntervalCollection() {
		YIntervalSeriesCollection collection = new YIntervalSeriesCollection();
		collection.addSeries(new YIntervalSeries("Particle Diameter"));
		collection.addSeries(new YIntervalSeries("Sigma"));
		return collection;
	}
	
	public static TimeSeriesCollection getParticleDataSet() {
		TimeSeriesCollection collection = new TimeSeriesCollection(); 
		
		collection.addSeries(new TimeSeries("Particle Size"));
		return collection;
	}
	
	public static TimeSeriesCollection getDataSet(TransmissionType... names) {
		TimeSeriesCollection collection = new TimeSeriesCollection();
		for (TransmissionType s: names) {
			collection.addSeries(new TimeSeries(s));
		}
		
		return collection;
	}
	
	public static TimeSeriesCollection getDataSet(DQtype... names) {
		TimeSeriesCollection collection = new TimeSeriesCollection();
		for (DQtype s: names) {
			collection.addSeries(new TimeSeries(s));
		}
		
		return collection;
	}
	
	public static TimeSeriesCollection getDataSet(ExtractedSignalType... names) {
		TimeSeriesCollection collection = new TimeSeriesCollection();
		for (ExtractedSignalType s: names) {
			collection.addSeries(new TimeSeries(s));
		}
		
		return collection;
	}
	
	public static XYSeries getListAsSeries(List<Double> list, String name) {
		XYSeries series = new XYSeries(name);
		int i = 0;
		
		for(Double d: list) {
			series.add(i, d);
			i++;
		}
		return series;
	}
	
	

}

package rzahoransky.utils;

import java.awt.BasicStroke;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
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
			XYSeries series = new XYSeries(s,false);
			collection.addSeries(series);
		}
		
		return collection;
	}
	
	public static YIntervalSeriesCollection getDiameterIntervalCollection() {
		YIntervalSeriesCollection collection = new YIntervalSeriesCollection();
		YIntervalSeries diameter = new YIntervalSeries("Particle Diameter",false,false);
		//YIntervalSeries concentration = new YIntervalSeries("Concentration",false,false);
		collection.addSeries(diameter);
		//collection.addSeries(concentration);
		return collection;
	}
	
	public static TimeSeriesCollection getParticleDataSet() {
		TimeSeriesCollection collection = new TimeSeriesCollection(); 
		
		collection.addSeries(new TimeSeries("Particle Size"));
		return collection;
	}
	
	public static TimeSeriesCollection getTimeSeries(TransmissionType... names) {
		TimeSeriesCollection collection = new TimeSeriesCollection();
		for (TransmissionType s: names) {
			TimeSeries series = new TimeSeries(s);
			series.setMaximumItemAge(100000);
			collection.addSeries(series);
		}
		
		return collection;
	}
	
	public static XYSeriesCollection getDataSet(TransmissionType[] names, int maxItemCount) {
		XYSeriesCollection collection = new XYSeriesCollection();
		for (TransmissionType s: names) {
			XYSeries series = new XYSeries(s,false,false);
			series.setMaximumItemCount(maxItemCount);
			collection.addSeries(series);
		}
		return collection;
	}
	
	public static XYSeriesCollection getDataSet(TransmissionType... names) {
		XYSeriesCollection collection = new XYSeriesCollection();
		for (TransmissionType s: names) {
			XYSeries series = new XYSeries(s,false,false);
			//series.setMaximumItemCount(maxItemCount);
			collection.addSeries(series);
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
	
	/**
	 * Shortcut to create {@link TimeSeriesCollection} with given {@link ExtractedSignalType} 
	 * (simply converted to converted to String) as name
	 * @param names
	 * @return
	 */
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

	public static YIntervalSeriesCollection getConcentrationIntervalCollection() {
		YIntervalSeriesCollection collection = new YIntervalSeriesCollection();
		YIntervalSeries diameter = new YIntervalSeries("Particle Concentration",false,false);
		//YIntervalSeries concentration = new YIntervalSeries("Concentration",false,false);
		collection.addSeries(diameter);
		//collection.addSeries(concentration);
		return collection;
	}
	
	

}

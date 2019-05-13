package rzahoransky.dqpipeline.visualization;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.renderer.xy.DeviationRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;

import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.interfaces.AbstractDQPipelineElement;
import rzahoransky.dqpipeline.interfaces.ParticleDiameterVisualizer;
import rzahoransky.utils.Charts;
import rzahoransky.utils.RingBuffer;
import rzahoransky.utils.TimeCounter;

public class ParticleSizeVisualizerChart extends AbstractDQPipelineElement implements ParticleDiameterVisualizer {

	JPanel visualizerPanel = new JPanel(new GridBagLayout());
	GridBagConstraints c = new GridBagConstraints();
	ChartPanel chartPanel;
	JFrame frame;
	private int maxAge = 300000; // in milliseconds
	// private int maxAge = 10000;
	protected TimeCounter refresh = new TimeCounter(66);
	protected RingBuffer<DQSignal> buffer = new RingBuffer<>(3);

	public static void main(String[] args) {
		NumberFormat logarithmicFormatter = new DecimalFormat("0.0E0");
		System.out.println(logarithmicFormatter.format(0.000001));
		System.out.println(logarithmicFormatter.format(0.00000154));
	}

	public ParticleSizeVisualizerChart(boolean showAsFrame) {

		YIntervalSeriesCollection diameterDataSet = Charts.getDiameterIntervalCollection();
		YIntervalSeriesCollection concentrationDataSet = Charts.getConcentrationIntervalCollection();

		JFreeChart chart = Charts.getXYChart("Particle Information", "Time", "Diameter in µm", diameterDataSet);
		chart.getTitle().setFont(new Font("Arial", Font.BOLD, 14));
		// DeviationRenderer r = getRenderer();

		chart.getXYPlot().setDataset(1, concentrationDataSet);

		// chart.getXYPlot().setRenderer(getRenderer());
		chart.getXYPlot().setRenderer(0, getRenderer());
		chart.getXYPlot().setRenderer(1, getRenderer());
		chart.getXYPlot().setDomainAxis(getDateAxis());
		chart.getXYPlot().setRangeAxis(0, new NumberAxis("Diameter in µm"));

		LogAxis concentrationAxis = new LogAxis("Vol. Concentration");
		NumberFormat logarithmicFormatter = new DecimalFormat("0.0E0");
		concentrationAxis.setNumberFormatOverride(logarithmicFormatter);
		// concentrationAxis.setMinorTickCount(0);

		chart.getXYPlot().setRangeAxis(1, concentrationAxis);

		chart.getXYPlot().mapDatasetToRangeAxis(0, 0);
		chart.getXYPlot().mapDatasetToRangeAxis(1, 1);

		chartPanel = Charts.getChartPanel("Measurment", chart);

		if (showAsFrame) {
			frame = new JFrame("AD-Samples");
			frame.setSize(500, 300);
			frame.add(chartPanel);
			frame.setVisible(true);
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}
	}

	private XYItemRenderer getRenderer() {
		DeviationRenderer r = new DeviationRenderer();
		// SamplingXYLineRenderer r = new SamplingXYLineRenderer();
		// Shape shape1 = new XYLineAndShapeRenderer().getLegendShape(0);
		// Shape shape2 = new XYLineAndShapeRenderer().getLegendShape(1);

		r.setBaseShapesVisible(false);
		r.setSeriesFillPaint(0, Color.ORANGE);
		r.setAlpha(0.3f);
		r.setBaseStroke(new BasicStroke(2));
		r.setSeriesStroke(0, new BasicStroke(2.5f));
		r.setSeriesStroke(1, new BasicStroke(2.0f));
		// r.getBaseLegendShape(new BasicStroke(2.0f));
		// r.setLegendShape(0, shape1);
		// r.setLegendShape(1, shape2);
		return r;
	}

	private DateAxis getDateAxis() {
		DateAxis dateAxis = new DateAxis();
		dateAxis.setDateFormatOverride(new SimpleDateFormat("HH:mm:ss"));
		return dateAxis;
	}

	public ChartPanel getChartPanel() {
		return chartPanel;
	}

	public JFrame getFrame() {
		return frame;
	}

	public void visualizeDQMeasurement(DQSignal measurement) {
		try {
			YIntervalSeriesCollection diameterDataset = (YIntervalSeriesCollection) chartPanel.getChart().getXYPlot().getDataset(0);
			YIntervalSeriesCollection concentrationDataset = (YIntervalSeriesCollection) chartPanel.getChart().getXYPlot().getDataset(1);
			diameterDataset.setNotify(false);
			concentrationDataset.setNotify(false);
			updateDiameter(measurement, diameterDataset);
			updateConcentration(measurement, concentrationDataset);
			long now = System.currentTimeMillis();
			processOldElements(diameterDataset, now);
			processOldElements(concentrationDataset, now);
			diameterDataset.setNotify(true);
			// concentrationDataset.setNotify(true);
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		// removeOldEntries(collection);

	}

	private void updateConcentration(DQSignal measurement, YIntervalSeriesCollection concentrationDataset) {
		double concentration = Math.max(measurement.getVolumeConcentration(), Double.MIN_NORMAL);
		concentrationDataset.getSeries(0).add(System.currentTimeMillis(), concentration, concentration, concentration);
	}

	private void updateDiameter(DQSignal measurement, YIntervalSeriesCollection collection) {
		long now = System.currentTimeMillis();

		YIntervalSeries diameterSeries = collection.getSeries(0);

		double d = measurement.getGeometricalDiameter();
		if (measurement.hasMinAndMaxDiameter()) {
			diameterSeries.add(now, d, measurement.getMinGeometricalDiameter(),
					measurement.getMaxGeometricalDiameter());
		} else {
			double sigma = measurement.getSigma();
			diameterSeries.add(now, d, d - sigma, d + sigma);
		}
	}

	private void processOldElements(YIntervalSeriesCollection collection, long now) {
		double history = collection.getXValue(0, 0);
		long age = (long) (now - history);

		if (age > maxAge) { // remove old elements
			for (int i = 0; i < collection.getSeriesCount(); i++) {
				collection.getSeries(i).remove(history);
			}
		}
	}

	@Override
	public DQSignal processDQElement(DQSignal in) {
		if(in != null && in.isValid && in.checkTransmission(0.02, 0.98))
			buffer.add(in);
		
		if (in != null && in.isValid && in.checkTransmission(0.02, 0.98) && refresh.timeForUpdate()) {
			try {
				DQSignal element = buffer.getAverage();
				visualizeDQMeasurement(element);
				// buffer.clear();
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
		return in;
	}

	@Override
	public String description() {
		return "Visualizes Particle Diameter";
	}

	public void setMaximumAge(int entries) {
		this.maxAge = entries;
	}

	@Override
	public void setMaxAge(int ageOrCount) {
		this.maxAge = ageOrCount;

	}

}

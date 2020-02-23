package rzahoransky.gui.measureGui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.swing.JFrame;

import org.jfree.chart.annotations.AbstractXYAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.RectangleEdge;

import charts.Charts;
import errors.WavelengthMismatchException;
import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.listener.DQSignalListener;
import rzahoransky.utils.DQtype;
import rzahoransky.utils.RefreshTimeCounter;
import storage.dqMeas.read.DQReader;

public class DQAnnotator extends AbstractXYAnnotation implements DQSignalListener {

	private Paint paint;
	private Stroke stroke;
	private volatile LinkedList<Annotations> annotators = new LinkedList<>();
	private int size = 15;
	RefreshTimeCounter timer = new RefreshTimeCounter();

	public static void main(String[] args) throws IOException, WavelengthMismatchException, InterruptedException {
		JFrame test = new JFrame("DQ Annotator test");
		test.setSize(400, 400);
		Charts dqChart = new DQReader(new File("D:/mietemp/2018-08-08.miezip")).getChart();
		dqChart.setVisible(true);
		DQAnnotator annotator = new DQAnnotator();
		dqChart.getChart().getXYPlot().addAnnotation(annotator);
		// annotator.setHistorySize(3);
		annotator.addDQ(1, 1);
		Thread.sleep(1000);
		annotator.addDQ(1.5, 1.5);
		Thread.sleep(1000);
		annotator.addDQ(2, 2);
		Thread.sleep(1000);
		annotator.addDQ(1.8, 3.5);
		Thread.sleep(1000);
	}

	public DQAnnotator() {
	}

	public void setHistorySize(int size) {
		this.size = size;
	}

	public int getHistorySize() {
		return size;
	}

	public Color getGreyColor(double fractionOfGrey) {
		fractionOfGrey = 1 - fractionOfGrey;
		if (fractionOfGrey == 0) {
			fractionOfGrey = Double.MIN_NORMAL;
		}
		float r = (float) (0.5 * fractionOfGrey);
		float g = (float) (0.5 * fractionOfGrey);
		float b = (float) (0.5 * fractionOfGrey);
		return new Color(r, g, b);
	}

	public void addDQ(double dq1, double dq2) {
		Annotations annotator = new Annotations();
		annotator.x = dq1;
		annotator.y = dq2;
		synchronized (annotators) {
			annotators.add(annotator);
			if (annotators.size() > size) {
				annotators.removeFirst();
			}
		}
		fireAnnotationChanged();

	}

	@Override
	public void draw(Graphics2D g2, XYPlot plot, Rectangle2D dataArea, ValueAxis domainAxis, ValueAxis rangeAxis,
			int rendererIndex, PlotRenderingInfo info) {

		PlotOrientation orientation = plot.getOrientation();
		RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(plot.getDomainAxisLocation(), orientation);
		RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(plot.getRangeAxisLocation(), orientation);

		int color = 0;
		synchronized (annotators) {
			for (Annotations not : annotators) {
				double x = domainAxis.valueToJava2D(not.x, dataArea, domainEdge);
				double y = rangeAxis.valueToJava2D(not.y, dataArea, rangeEdge);

				if (orientation == PlotOrientation.HORIZONTAL) {
					double tempX = x;
					x = y;
					y = tempX;
				}
				g2.setColor(getGreyColor((double) color / annotators.size()));
				g2.drawOval((int) x - 3, (int) y - 3, 6, 6);
				color++;
			}
		}

	}

	class Annotations {
		double x;
		double y;
	}

	@Override
	public void newSignal(DQSignal currentSignal) {
		if (timer.timeForUpdate()) //check if it is time for an update
			addDQ(currentSignal.getDQ(DQtype.DQ1).getDqValue(), currentSignal.getDQ(DQtype.DQ2).getDqValue());
	}

	@Override
	public void closing() {
	}

}

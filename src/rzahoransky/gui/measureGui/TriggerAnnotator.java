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
import org.jfree.chart.annotations.XYBoxAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.RectangleEdge;

import charts.Charts;
import errors.WavelengthMismatchException;
import rzahoransky.dqpipeline.dataExtraction.rawDataExtraction.RawDataExtractorFactory;
import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.interfaces.IMeasurePoints;
import rzahoransky.dqpipeline.listener.DQSignalListener;
import rzahoransky.utils.DQtype;
import rzahoransky.utils.ExtractedSignalType;
import storage.dqMeas.read.DQReader;

/** draws the currently used triggerpoints**/
public class TriggerAnnotator extends AbstractXYAnnotation implements DQSignalListener {

	private volatile LinkedList<Annotations> annotators = new LinkedList<>();
	private int size = 15;
	protected static final IMeasurePoints measurePoints = RawDataExtractorFactory.getRawDataExtractor().getMeasurePoints();


	public TriggerAnnotator() {
		// TODO Auto-generated constructor stub
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
		double yMax = plot.getDomainAxis().getUpperBound();
		double yMin = plot.getDomainAxis().getLowerBound();

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
		
		fireAnnotationChanged();
	}

	@Override
	public void closing() {
		// TODO Auto-generated method stub
		
	}
	
	private LinkedList<Coordinates> getMarkerAnnotations(DQSignal measurement) {
		LinkedList<Coordinates> result = new LinkedList<>();
		for (ExtractedSignalType type: ExtractedSignalType.values()) {
			Coordinates coordinates = new Coordinates();
			double periodLength = measurement.getSinglePeriods().get(0).getPeriodLength();
			int arrayLength = measurePoints.getRelativeMeasurePoint(type).length;
			double start = measurePoints.getRelativeMeasurePoint(type)[0]*periodLength;
			double end = measurePoints.getRelativeMeasurePoint(type)[arrayLength-1]*periodLength;
			end = Math.max(end, start+1); //if there is only one marker
			double y0 = measurement.getReference().get((int)start)-1;
			double y1 = measurement.getReference().get((int)start)+1;
			coordinates.x0=start;
			coordinates.x1=end;

			result.add(coordinates);
		}
		return result;
	}

}

class Coordinates {
	double x0,x1;
}

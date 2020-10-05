package rzahoransky.gui.measureGui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;

import org.jfree.chart.annotations.AbstractXYAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.RectangleEdge;

import rzahoransky.dqpipeline.dataExtraction.rawDataExtraction.RawVoltageExtractorFactory;
import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.interfaces.IMeasurePoints;
import rzahoransky.dqpipeline.listener.DQSignalListener;
import rzahoransky.utils.ExtractedSignalType;
import rzahoransky.utils.RawSignalType;

/** draws the currently used triggerpoints**/
public class TriggerAnnotator extends AbstractXYAnnotation implements DQSignalListener {
	private static final long serialVersionUID = 1L;
	private volatile LinkedList<Coordinates> annotators = new LinkedList<>();
	private int size = 15;
	protected static final IMeasurePoints measurePoints = RawVoltageExtractorFactory.getRawVoltageExtractor().getMeasurePoints();


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

	@Override
	public void draw(Graphics2D g2, XYPlot plot, Rectangle2D dataArea, ValueAxis domainAxis, ValueAxis rangeAxis,
			int rendererIndex, PlotRenderingInfo info) {

		PlotOrientation orientation = plot.getOrientation();
		RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(plot.getDomainAxisLocation(), orientation);
		RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(plot.getRangeAxisLocation(), orientation);
		//double yMax = plot.getDomainAxis().getUpperBound();
		//double yMin = plot.getDomainAxis().getLowerBound();

		synchronized (annotators) {
			for (Coordinates triggerCoordinate : annotators) {
				double x0 = domainAxis.valueToJava2D(triggerCoordinate.x0, dataArea, domainEdge);
				double x1 = domainAxis.valueToJava2D(triggerCoordinate.x1, dataArea, domainEdge);
				double y0 = rangeAxis.valueToJava2D(triggerCoordinate.y0, dataArea, rangeEdge);
				double y1 = rangeAxis.valueToJava2D(triggerCoordinate.y1, dataArea, rangeEdge);

				if (orientation == PlotOrientation.HORIZONTAL) {
					double tempX0 = x0;
					double tempX1 = x1;
					x0 = y0;
					x1 = y1;
					y0 = tempX0;
					y1= tempX1;
				}
				g2.setColor(Color.YELLOW);
				//g2.drawOval((int) x - 3, (int) y - 3, 6, 6);
				int height = 10;
				g2.drawRoundRect((int)x0, (int)y0-height/2, (int) (x1-x0), height, 2, 2);
				//g2.drawLine((int)x0, (int)y0, (int)x1, (int)y1);
			}
		}

	}

	@Override
	public void newSignal(DQSignal currentSignal) {
		try {
			annotators = getMarkerAnnotations(currentSignal);
			fireAnnotationChanged();
		} catch (IndexOutOfBoundsException e) {
		} //this is thrown if no trigger was found. Ignore it and do not fire an annotation change event
	}

	@Override
	public void closing() {
		// nothing else to do
	}
	
	private LinkedList<Coordinates> getMarkerAnnotations(DQSignal measurement) {
		LinkedList<Coordinates> result = new LinkedList<>();
		RawSignalType[] refOrMeasArray = { RawSignalType.ref, RawSignalType.meas };
		for (ExtractedSignalType type : ExtractedSignalType.values()) {
			double periodLength = measurement.getSinglePeriods().get(0).getPeriodLength();
			int arrayLength = measurePoints.getRelativeMeasurePoint(type).length;
			double start = measurePoints.getRelativeMeasurePoint(type)[0] * periodLength;
			double end = measurePoints.getRelativeMeasurePoint(type)[arrayLength - 1] * periodLength;
			end = Math.max(end, start + 1); // if there is only one marker
			for (RawSignalType refOrMeas : refOrMeasArray) { //add for Ref and Meas signal
				Coordinates coordinates = new Coordinates();
				double y0 = measurement.getSinglePeriods().get(0).getRawSignal(refOrMeas).get((int) Math.round(start));
				// double y0 = measurement.getReference().get((int) Math.round(start));
				double y1 = measurement.getSinglePeriods().get(0).getRawSignal(refOrMeas).get((int) Math.round(end));
				coordinates.x0 = start;
				coordinates.x1 = end;
				coordinates.y0 = y0;
				coordinates.y1 = y1;

				result.add(coordinates);
			}
		}
		return result;
	}

}

class Coordinates {
	double x0,x1,y0,y1;
}

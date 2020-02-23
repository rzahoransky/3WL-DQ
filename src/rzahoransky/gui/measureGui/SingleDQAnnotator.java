package rzahoransky.gui.measureGui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.annotations.AbstractXYAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.RectangleEdge;

import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.dqSignal.DQSignalEntry;
import rzahoransky.dqpipeline.listener.DQSignalListener;
import rzahoransky.utils.DQtype;
import rzahoransky.utils.MeasureSetUp;
import rzahoransky.utils.RefreshTimeCounter;

public class SingleDQAnnotator extends AbstractXYAnnotation implements DQSignalListener{

	private DQtype dq;
	private DQSignalEntry currentDQ;
	private double maxDiameter = MeasureSetUp.getInstance().getMieList(0).getMaxDiameter();
	private double minDiameter = MeasureSetUp.getInstance().getMieList(0).getMinDiameter();
	protected final RefreshTimeCounter refresh = new RefreshTimeCounter(33);

	public SingleDQAnnotator(DQtype dq) {
		this.dq = dq;
		MeasureSetUp.getInstance().getPipeline().addNewSignalListener(this);
	}

	@Override
	public void newSignal(DQSignal currentSignal) {
		currentDQ = currentSignal.getDQ(dq);
		if(refresh.timeForUpdate())
			fireAnnotationChanged();
		
	}

	@Override
	public void draw(Graphics2D g2, XYPlot plot, Rectangle2D dataArea, ValueAxis domainAxis, ValueAxis rangeAxis,
			int rendererIndex, PlotRenderingInfo info) {
		
		try {
		
        PlotOrientation orientation = plot.getOrientation();
        RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(plot.getDomainAxisLocation(), orientation);
        RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(plot.getRangeAxisLocation(), orientation);
        
        	//double x = domainAxis.valueToJava2D(not.x, dataArea, domainEdge);
        	//double y = rangeAxis.valueToJava2D(not.y, dataArea, rangeEdge);
        
        double y = rangeAxis.valueToJava2D(currentDQ.getDqValue(), dataArea, rangeEdge); //line
        double x1 = domainAxis.valueToJava2D(minDiameter, dataArea, domainEdge); //beginning of line
        double x2 = domainAxis.valueToJava2D(maxDiameter, dataArea, domainEdge); //end of line
        	g2.setColor(Color.BLACK);
//            if (orientation == PlotOrientation.HORIZONTAL) {
//                double tempX = x;
//                x = y;
//                y = tempX;
//            }

        	g2.drawLine((int)x1, (int)y, (int)x2, (int)y);
        	
		} catch (Exception e ) {} //draw nothing
		
	}

	@Override
	public void closing() {
		// TODO Auto-generated method stub
		
	}
	

}

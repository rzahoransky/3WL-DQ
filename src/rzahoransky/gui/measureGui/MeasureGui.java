package rzahoransky.gui.measureGui;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.JFreeChart;

import rzahoransky.dqpipeline.DQPipeline;
import rzahoransky.gui.measureSetup.MeasureSetUp;

public class MeasureGui extends JFrame {

MeasureSetUp setup = MeasureSetUp.getInstance();
GridBagConstraints c = new GridBagConstraints();
private DQPipeline pipeline;

	public MeasureGui(DQPipeline pipeline) {
		this.pipeline = pipeline;
		setupFrame();
		positionElements();
		pipeline.start();
		setVisible(true);
	}
	
	private void setupFrame() {
		setSize(new Dimension(600, 500));
		setTitle("3WL DQ Particle Size Measurement");
		setLayout(new GridBagLayout());
	}
	
	private void positionElements() {
		//c.fill = GridBagConstraints.BOTH;
		//add(setup.getSizeVisualizer().getChartPanel(),c);
		//c.gridx++;
		//add(setup.getSinglePeriodVisualizer().getChartPanel(),c);
		
		c.gridy++;
		c.gridx=0;
		add(new DQGui(pipeline),c);
	}
}

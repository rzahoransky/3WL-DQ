package rzahoransky.gui.measureGui;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
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
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent evt) {
				System.out.println("Stopping...");
				pipeline.stop();
			}
		});
	}
	
	private void positionElements() {
		
		//Particle Diameter History
		c.fill = GridBagConstraints.BOTH;
		c.gridx=0;
		c.gridy=0;
		c.gridwidth=GridBagConstraints.RELATIVE;
		c.weightx=0.5;
		c.weighty=0.5;
		
		add(setup.getSizeVisualizer().getChartPanel(),c);
		
		//add(setup.getSizeVisualizer().getChartPanel(),c);
		//c.gridx++;
		//add(setup.getSinglePeriodVisualizer().getChartPanel(),c);

		
		//Single DQs
		c.gridx++;
		c.gridwidth=1;
		c.gridheight=2;
		add(new SingleDQGui(),c);
		
		//DQ Field
		c.gridwidth=1;
		c.gridheight=3;
		c.weightx=1;
		c.weighty=1;
		c.gridy++;
		c.gridx=0;
		add(new DQGui(pipeline),c);
		
		//Transmission
		c.gridx=1;
		c.gridy++;
		c.gridheight=1;
		c.weightx=0.5;
		c.weighty=0.5;
		add(setup.getTransmissionVis().getChartPanel(),c);
		
		//Single Period
		c.gridy++;
		add(setup.getSinglePeriodVisualizer().getChartPanel(),c);
		
		//add I0 Btn
		c.gridy++;
		add(setup.getTransmissionExtractor().getI0Btn(),c);
		
		//add numeric representation
		//c.gridy++;
		c.gridx=0;
		c.gridwidth = GridBagConstraints.RELATIVE;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(new NumericDiameterGui(pipeline),c);
	}
}

package rzahoransky.gui.measureGui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JToolBar;

import rzahoransky.dqpipeline.DQPipeline;
import rzahoransky.utils.MeasureSetUp;

public class MeasureGui extends JFrame {

MeasureSetUp setup = MeasureSetUp.getInstance();
GridBagConstraints c = new GridBagConstraints();
private DQPipeline pipeline;
protected JToolBar toolbar;

	public MeasureGui(DQPipeline pipeline) {
		this.pipeline = pipeline;
		setupFrame();
		toolbar = new MeasureToolbar();
		positionElements();
		pipeline.start();
		setLocationByPlatform(true);
		setVisible(true);
	}

	private void setupFrame() {
		setSize(new Dimension(700, 600));
		setTitle("3WL DQ Particle Size Measurement");
		setLayout(new GridBagLayout());
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent evt) {
				System.out.println("Stopping...");
				pipeline.stop();
				//MeasureSetUp.getInstance().getOutputWriter().close();
			}
		});
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
	private void positionElements() {
		
		c.gridx=0;
		c.gridy=0;
		c.weightx=1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(toolbar, c);
		
		//add numeric representation
		c.gridy=1;
		c.gridx=0;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.weightx=0;
		c.weighty=0;
		c.fill = GridBagConstraints.HORIZONTAL;
		//c.fill = GridBagConstraints.NONE;
		NumericDiameterGui numericDiameter = new NumericDiameterGui(pipeline);
		numericDiameter.setPreferredSize(new Dimension(600, 60));
		add(numericDiameter,c);
		
		//Particle Diameter History (0/0)
		c.fill = GridBagConstraints.BOTH;
		c.gridx=0;
		c.gridy=2;
		c.gridwidth=1;
		c.weightx=1;
		c.weighty=1;
		c.gridheight=1;
		
		add(setup.getSizeVisualizer().getChartPanel(),c);
		
		//add(setup.getSizeVisualizer().getChartPanel(),c);
		//c.gridx++;
		//add(setup.getSinglePeriodVisualizer().getChartPanel(),c);

		//Transmission
		c.gridx++;
		//c.gridy+=2;
		c.gridheight=1;
		c.weightx=.5;
		c.weighty=1;
		add(setup.getTransmissionVis().getChartPanel(),c);
		
		//Single DQs 
		//c.gridx++;
		c.gridy++;
		c.gridwidth=1;
		c.gridheight=2;
		c.weightx=.5;
		c.weighty=1;
		add(new SingleDQGui(),c);
		
		//Single Period
		c.gridy+=2;
		add(setup.getSinglePeriodVisualizer().getChartPanel(),c);

		

		
		//DQ Field
		c.gridwidth=1;
		c.gridheight=4;
		c.fill=GridBagConstraints.BOTH;
		c.weightx=1;
		c.weighty=1;
		c.gridy=3;
		c.gridx=0;
		add(new DQGui(pipeline),c);
		
		

		//statistics
		c.gridy = 10;
		c.gridx = 0;
		c.weighty=0;
		c.gridwidth=GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(new StatisticsGui(pipeline),c);
		

		
		//add I0 Btn
		c.gridy=20;
		c.gridx=0;
		c.weighty=0;
		c.fill=GridBagConstraints.HORIZONTAL;
		c.gridwidth=GridBagConstraints.REMAINDER;
		add(setup.getTransmissionExtractor().getI0Btn(),c);
	}
}

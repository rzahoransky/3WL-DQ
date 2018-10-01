package rzahoransky.gui.adjustmentGui;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.DefaultButtonModel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import rzahoransky.dqpipeline.DQPipeline;
import rzahoransky.dqpipeline.analogueAdapter.FiveWLNIDaqAdapter;
import rzahoransky.dqpipeline.dataExtraction.FiveWLExtractor;
import rzahoransky.dqpipeline.dataExtraction.FiveWLMeasurePoints;
import rzahoransky.dqpipeline.dataExtraction.TransmissionExtractor;
import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.interfaces.AdapterInterface;
import rzahoransky.dqpipeline.listener.DQSignalListener;
import rzahoransky.dqpipeline.periodMarker.FiveWLMarker;
import rzahoransky.dqpipeline.simulation.FiveWLOneHeadSimulator;
import rzahoransky.dqpipeline.visualization.TransmissionVisualizer;
import rzahoransky.utils.TransmissionType;

public class Adjustment extends JFrame implements DQSignalListener {
	GridBagConstraints c;
	DQPipeline pipeline;
	AdapterInterface adapter = new FiveWLNIDaqAdapter();
	private FiveWLMarker triggerMarker;
	private FiveWLExtractor valueExtractor;
	private TransmissionExtractor transmissionExtractor;
	private int maxPitch = 10;
	private TransmissionVisualizer vis;
	private AudioOutput sound = new PlayTheDQSoundWithMidi();
	protected ButtonGroupGui btnGroup;


	public static void main(String[] args) {
		Adjustment test = new Adjustment();
		test.setVisible(true);
	}

	public Adjustment() {
		//setupPipeline();
		//setupFrame();
		
		//setVisible(true);
	}
	
	
	public void setVisible(boolean visible) {
		if (visible) {
			setupPipeline();
			setupRadioButton();
			setupFrame();
			pipeline.addNewSignalListener(sound);
			super.setVisible(visible);
		} else {
			try {
			pipeline.stop();
			} catch (Exception e) {}
			try {
				adapter.clearTask();
			} catch (Exception e) {}
			sound.close();
			pipeline = null;
			super.setVisible(visible);
		}
	}


	private void setupRadioButton() {
		btnGroup = new ButtonGroupGui();
		for (TransmissionType type : TransmissionType.values()) {
			JRadioButton btn = new JRadioButton(type.toString());
			btn.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					sound.setTransmissionType(type);
				}
			});
			btnGroup.add(btn);
		}
	}

	private void setupPipeline() {
		pipeline = new DQPipeline();
		
		adapter = new FiveWLNIDaqAdapter();
		adapter = new FiveWLOneHeadSimulator();
		//Look for triggers
		triggerMarker = new FiveWLMarker();
		//extract single periods
		valueExtractor = new FiveWLExtractor(new FiveWLMeasurePoints());
		//extract transmissions
		transmissionExtractor = new TransmissionExtractor(false);
		vis = new TransmissionVisualizer(false);
		vis.setStroke(new BasicStroke(5.0f));
		vis.setMaxAge(20000);
		
		pipeline.addPipelineElement(adapter);
		pipeline.addPipelineElement(triggerMarker);
		pipeline.addPipelineElement(valueExtractor);
		pipeline.addPipelineElement(transmissionExtractor);
		pipeline.addPipelineElement(vis);
		pipeline.addNewSignalListener(this);
		
		pipeline.start();
	}

	private void setupFrame() {
		c = new GridBagConstraints();
		setSize(new Dimension(450, 400));
		setLayout(new GridBagLayout());
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				pipeline.stop();
				adapter.clearTask();
				adapter = null;
				pipeline = null;
				sound.close();
			}
		});
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		c.gridx=1;
		c.gridwidth=GridBagConstraints.REMAINDER;
		c.gridy=1;
		c.fill=c.HORIZONTAL;
		c.weightx=1;
		add(btnGroup,c);
		c.gridx=10;
		c.gridy=10;
		c.fill=c.BOTH;
		c.weightx=1;
		c.weighty=1;
		c.gridwidth=1;
		add(vis.getChartPanel(),c);
		c.gridy++;
		c.fill=c.HORIZONTAL;
		c.weighty=0;
		add(transmissionExtractor.getI0Btn(),c);
		
	}

	@Override
	public void newSignal(DQSignal currentSignal) {

	}

}

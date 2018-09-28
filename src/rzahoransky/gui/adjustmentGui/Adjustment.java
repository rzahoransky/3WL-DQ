package rzahoransky.gui.adjustmentGui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JFrame;
import javax.swing.JPanel;

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

public class Adjustment extends JFrame implements DQSignalListener {
	GridBagConstraints c;
	DQPipeline pipeline;
	AdapterInterface adapter = new FiveWLNIDaqAdapter();
	private FiveWLMarker triggerMarker;
	private FiveWLExtractor valueExtractor;
	private TransmissionExtractor transmissionExtractor;
	private int maxPitch = 10;
	private TransmissionVisualizer vis;
	private PlayTheDQSound sound = new PlayTheDQSound();


	public static void main(String[] args) {
		Adjustment test = new Adjustment();
	}

	public Adjustment() {
		setupPipeline();
		setupFrame();
		
		setVisible(true);
		pipeline.addNewSignalListener(sound);
		Thread soundThread = new Thread(sound);
		soundThread.start();
	}


	private void setupPipeline() {
		pipeline = new DQPipeline();
		
		//adapter = new FiveWLNIDaqAdapter();
		adapter = new FiveWLOneHeadSimulator();
		//Look for triggers
		triggerMarker = new FiveWLMarker();
		//extract single periods
		valueExtractor = new FiveWLExtractor(new FiveWLMeasurePoints());
		//extract transmissions
		transmissionExtractor = new TransmissionExtractor(false);
		vis = new TransmissionVisualizer(false);
		
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
		setSize(new Dimension(400, 400));
		setLayout(new GridBagLayout());
		addWindowStateListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				pipeline.stop();
				adapter.clearTask();
				adapter = null;
			}
			
		});
		
		c.gridx=0;
		c.gridy=0;
		c.fill=c.BOTH;
		c.weightx=1;
		c.weighty=1;
		add(vis.getChartPanel(),c);
		c.gridx++;
		c.fill=c.HORIZONTAL;
		c.weighty=0;
		add(transmissionExtractor.getI0Btn(),c);
		
	}

	@Override
	public void newSignal(DQSignal currentSignal) {

	}

}

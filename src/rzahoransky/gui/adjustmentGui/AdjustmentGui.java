package rzahoransky.gui.adjustmentGui;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JRadioButton;

import rzahoransky.dqpipeline.DQPipeline;
import rzahoransky.dqpipeline.analogueAdapter.GenericNIDaqAdapter;
import rzahoransky.dqpipeline.dataExtraction.TransmissionExtractor;
import rzahoransky.dqpipeline.dataExtraction.rawDataExtraction.FiveWLExtractor;
import rzahoransky.dqpipeline.dataExtraction.rawDataExtraction.RawDataExtractorFactory;
import rzahoransky.dqpipeline.dataExtraction.rawDataExtraction.ThreeWLMeasurePoints;
import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.interfaces.AbstractDQPipelineElement;
import rzahoransky.dqpipeline.listener.DQSignalListener;
import rzahoransky.dqpipeline.periodMarker.FiveWLMarker;
import rzahoransky.dqpipeline.periodMarker.MarkerFactory;
import rzahoransky.dqpipeline.visualization.TransmissionVisualizer;
import rzahoransky.gui.measureSetup.MeasureSetupEntry;
import rzahoransky.utils.MeasureSetUp;
import rzahoransky.utils.TransmissionType;

public class AdjustmentGui extends JFrame implements DQSignalListener {
	GridBagConstraints c;
	DQPipeline pipeline;
	GenericNIDaqAdapter adapter = new GenericNIDaqAdapter();
	private AbstractDQPipelineElement triggerMarker;
	private AbstractDQPipelineElement valueExtractor;
	private TransmissionExtractor transmissionExtractor;
	private int maxPitch = 10;
	private TransmissionVisualizer vis;
	TransmissionType type = TransmissionType.TRANSMISSIONWL3;
	private AudioOutput sound = new PlayTheDQSoundWithMidi(type);
	protected ButtonGroupGui btnGroup;
	String device = "";
	int samplesPerChannel = 300;


	public static void main(String[] args) {
		AdjustmentGui test = new AdjustmentGui();
		test.setVisible(true);
	}

	public AdjustmentGui() {
		device = MeasureSetUp.getInstance().getProperty(MeasureSetupEntry.NIADAPTER);
	}
	
	
	public AdjustmentGui(String selectedDevice) {
		device = selectedDevice;
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
		btnGroup.setActive(type);
	}

	private void setupPipeline() {
		pipeline = new DQPipeline();
		
		adapter = new GenericNIDaqAdapter();
		adapter.setSamplesPerChannel(samplesPerChannel);
		adapter.setADCardOrConfigParameter(device);
		
		//adapter = new FiveWLOneHeadSimulator();
		//Look for triggers
		triggerMarker = MarkerFactory.getPeriodMarker();
		//extract single periods
		valueExtractor = RawDataExtractorFactory.getRawDataExtractor();
		//valueExtractor.useOffset(true);
		//extract transmissions
		transmissionExtractor = new TransmissionExtractor(false);
		vis = new TransmissionVisualizer(false);
		vis.setStroke(new BasicStroke(5.0f));
		vis.setMaxAge(1000);
		
		pipeline.addPipelineElement(adapter);
		pipeline.addPipelineElement(triggerMarker);
		pipeline.addPipelineElement(valueExtractor);
		pipeline.addPipelineElement(transmissionExtractor);
		pipeline.addPipelineElement(vis);
		pipeline.addNewSignalListener(this); //inform this class every new element
		
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
		add(getI0Button(),c);
		
	}
	
	protected JButton getI0Button() {
		JButton i0 = new JButton("Set I0");
		for (ActionListener l: transmissionExtractor.getI0Btn().getActionListeners())
			i0.addActionListener(l);
		i0.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				vis.clearSeries();
				
			}
		});
		
		return i0;
	}

	@Override
	public void newSignal(DQSignal currentSignal) {

	}

	@Override
	public void closing() {
		// TODO Auto-generated method stub
		
	}

}

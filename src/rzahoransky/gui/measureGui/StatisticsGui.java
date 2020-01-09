package rzahoransky.gui.measureGui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.Timer;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import rzahoransky.dqpipeline.DQPipeline;
import rzahoransky.gui.measureSetup.MeasureSetUp;
import rzahoransky.utils.DQTimer;
import rzahoransky.utils.DQTimerListener;
import rzahoransky.utils.Measurement;

public class StatisticsGui extends JPanel implements DQTimerListener {
	
	GridBagConstraints c = new GridBagConstraints();
	private DQPipeline pipeline;
	protected int align = SwingConstants.LEFT;
	protected JLabel frequencyValue = new JLabel("",align);
	protected JLabel transmissionValue = new JLabel("",align);
	protected JLabel transmissionLabel = new JLabel("Transmission: ");
	protected JLabel frequencyLabel = new JLabel("Measurement frequency: ");
	protected JLabel wavelengthValue = new JLabel("",align);
	protected JLabel wavelengthLabel = new JLabel("Wavelengths: ");
	DecimalFormat perSecondFormat = new DecimalFormat("");
	
	public StatisticsGui(DQPipeline pipeline) {
		this.pipeline = pipeline;
		DQTimer timer = new DQTimer(pipeline);
		timer.addTimeListener(this);
		setupPanel();
	}

	private void setupPanel() {
		Dimension d = new Dimension(330, 30);
		setPreferredSize(d);
		setMinimumSize(d);
		setMaximumSize(d);
		setLayout(new GridBagLayout());
		
		//add frequency
		c.gridx=0;
		c.gridy=0;
		c.weightx=1;
		c.weighty=1;
		add(frequencyLabel,c);
		c.gridx++;
		add(frequencyValue);
		
		//add transmission check
		c.gridx++;
		add(transmissionLabel,c);
		c.gridx++;
		add(transmissionValue,c);
		
		//add wavelength info
		c.gridx++;
		add(wavelengthLabel);
		c.gridx++;
		add(wavelengthValue);
	}

	@Override
	public void newTimeStatistics(DQTimer timer) {
		frequencyValue.setText(new Measurement(timer.getMeasurementsPerSecond()).toString()+"Hz");
		transmissionValue.setText(pipeline.getCurrentSignal().checkTransmission()? "valid": "INVALID");
		double wl1 = pipeline.getCurrentSignal().getWL1();
		double wl2 = pipeline.getCurrentSignal().getWL2();
		double wl3 = pipeline.getCurrentSignal().getWL3();
		wavelengthValue.setText(wl1+"/"+wl2+"/"+wl3+"µm");
	}

}

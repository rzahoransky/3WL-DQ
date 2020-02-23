package rzahoransky.gui.measureSetup;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import rzahoransky.utils.MeasureSetUp;

public class MeasureLengthGui extends JPanel implements ChangeListener{

	private static final long serialVersionUID = -3666131497499433865L;
	JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 200, 1);
	JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, 0, Double.MAX_VALUE, 0.1));
	double length = 1;
	GridBagConstraints c = new GridBagConstraints();

	public static void main(String[] args) {
		JFrame test = new JFrame("test");
		test.setSize(300, 170);
		test.add(new MeasureLengthGui());
		test.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		test.setVisible(true);
	}
	
	public MeasureLengthGui() {

		placeComponents();

		try {
			length = Double.parseDouble(MeasureSetUp.getInstance().getProperty(MeasureSetupEntry.MEASURELENGTH_IN_CM));
			slider.setValue((int) length);
			spinner.setValue(length);
		} catch (Exception e) {
			System.out.println("Cannot parse");
		}
		
		slider.addChangeListener(this);
		spinner.addChangeListener(this);

	}

	private void placeComponents() {
		setLayout(new GridBagLayout());
		c.anchor=GridBagConstraints.NORTH;
		c.gridx=0;
		c.weightx=1;
		c.fill=GridBagConstraints.HORIZONTAL;
		c.gridy=0;
		JLabel text = new JLabel("Set measure length in cm");
		//text.setMinimumSize(new Dimension(20, 60));
		//add(new JLabel("<html><b>Set measure length in cm</b></html>"),c);
		add(text, c);
		
		c.gridy++;
		c.insets = new Insets(0, 0, 0, 5);
		c.fill=GridBagConstraints.BOTH;
		c.weightx=1;
		c.weighty=0;
		slider.setMinimumSize(new Dimension(100, 43));
		slider.setMajorTickSpacing(50);
		slider.setMinorTickSpacing(10);
		//slider.createStandardLabels(5);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		add(slider,c);
		
		c.insets=new Insets(0, 0, 0, 0);
		c.fill=GridBagConstraints.NONE;

		c.weightx=0;
		c.gridx=1;
		//c.gridy=0;
		c.ipadx=10;
		spinner.setMinimumSize(new Dimension(70, 30));
		
		add(spinner, c);
		
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		if (arg0.getSource() instanceof JSpinner) {
			length = (double) spinner.getValue();
		} else if (arg0.getSource() instanceof JSlider) {
			length = slider.getValue();
		}
		
		
		slider.removeChangeListener(this);
		spinner.removeChangeListener(this);
		slider.setValue((int) length);
		spinner.setValue(length);
		slider.addChangeListener(this);
		spinner.addChangeListener(this);
		MeasureSetUp.getInstance().setProperty(MeasureSetupEntry.MEASURELENGTH_IN_CM, Double.toString(length));
	}
	
	public double getLength() {
		return length;
	}
	
	public void setLength(double length) {
		this.length=length;
		slider.removeChangeListener(this);
		spinner.removeChangeListener(this);
		slider.setValue((int) length);
		spinner.setValue(length);
		slider.addChangeListener(this);
		spinner.addChangeListener(this);
		MeasureSetUp.getInstance().setProperty(MeasureSetupEntry.MEASURELENGTH_IN_CM, Double.toString(length));
	}

}

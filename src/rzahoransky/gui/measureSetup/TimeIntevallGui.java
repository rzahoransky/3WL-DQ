package rzahoransky.gui.measureSetup;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.NumberFormat;

import javax.swing.BoxLayout;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;

public class TimeIntevallGui extends JPanel{
	
	JSpinner intervalField = new JSpinner(new SpinnerNumberModel(1, 0.0, 7200, 0.1));
	
	public static void main(String[] args) {
		TimeIntevallGui test = new TimeIntevallGui();
		JFrame testFrame = new JFrame("Time Interval Test");
		testFrame.setSize(300, 300);
		testFrame.add(test);
		testFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		testFrame.setVisible(true);
	}

	public TimeIntevallGui() {
		JLabel text = new JLabel("Storage interval in s");
		text.setHorizontalAlignment(SwingConstants.LEFT);
		//text.setMinimumSize(new Dimension(100, 100));
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		add(text,c);
		c.gridx++;
		c.weighty=1;
		//c.fill=GridBagConstraints.BOTH;
		//intervalField.setMaximumSize(new Dimension(65, 50));
		//intervalField.size
		
		try {
			intervalField.setValue(MeasureSetUp.getInstance().getStorageIntervall());
		} catch (Exception e) {
			intervalField.setValue(1);
		}
		
		intervalField.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				try {
					System.out.println(intervalField.getValue());
					MeasureSetUp.getInstance().setStorageIntervall((double) intervalField.getValue());
				} catch (Exception e1) {} 
			}
		});
		
		add(intervalField,c);
	}
	
	public double getValue() {
		return (double) intervalField.getValue();
	}

}

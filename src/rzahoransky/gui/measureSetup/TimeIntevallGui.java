package rzahoransky.gui.measureSetup;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.NumberFormat;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
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
	
	JSpinner intervalField;
	JCheckBox averageOverTime;
	MeasureSetUp setup = MeasureSetUp.getInstance();
	
	public static void main(String[] args) {
		TimeIntevallGui test = new TimeIntevallGui();
		JFrame testFrame = new JFrame("Time Interval Test");
		testFrame.setSize(300, 300);
		testFrame.add(test);
		testFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		testFrame.setVisible(true);
	}

	public TimeIntevallGui() {
		intervalField = new JSpinner(new SpinnerNumberModel(1, 0.0, 7200, 0.1));
		averageOverTime = new JCheckBox("average over time");
		JLabel text = new JLabel("<html><body>Storage interval in s<br>0 means as fast as possible</body></html>");
		text.setHorizontalAlignment(SwingConstants.LEFT);
		//text.setMinimumSize(new Dimension(100, 100));
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill=GridBagConstraints.NONE;
		c.gridx=0;
		c.gridy=0;
		add(text,c);
		//c.gridx++;
		c.gridy++;
		c.weighty=1;
		//c.fill=GridBagConstraints.HORIZONTAL;
		//c.fill=GridBagConstraints.BOTH;
		//intervalField.setMaximumSize(new Dimension(65, 50));
		//intervalField.size
		
		try {
			intervalField.setValue(setup.getStorageIntervall());
		} catch (Exception e) {
			intervalField.setValue(1);
		}
		
		try {
			averageOverTime.setSelected(Boolean.parseBoolean(setup.getProperty(MeasureSetupEntry.AVERAGE_OVER_TIME)));
		} catch (Exception e) {
			averageOverTime.setSelected(false);
		}
		
		intervalField.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				try {
					System.out.println(intervalField.getValue());
					setup.setStorageIntervall((double) intervalField.getValue());
				} catch (Exception e1) {} 
			}
		});
		
		add(intervalField,c);
		
		averageOverTime.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setup.setProperty(MeasureSetupEntry.AVERAGE_OVER_TIME, Boolean.toString(averageOverTime.isSelected()));
			}
		});
		
		c.gridy++;
		add(averageOverTime,c);
	}
	
	public double getValue() {
		return Double.parseDouble(intervalField.getValue().toString());
	}

}

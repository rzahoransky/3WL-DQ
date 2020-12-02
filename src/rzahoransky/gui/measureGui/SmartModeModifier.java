package rzahoransky.gui.measureGui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JFrame;

import rzahoransky.utils.properties.MeasureSetUp;
import rzahoransky.utils.properties.MeasureSetupChangeListener;
import rzahoransky.utils.properties.MeasureSetupEntry;

public class SmartModeModifier extends JCheckBox{
	MeasureSetUp setup = MeasureSetUp.getInstance();

	public static void main(String[] args) {
		JFrame frame = new JFrame("Smart Mode Modifier Test");
		frame.setSize(new Dimension(100, 50));
		frame.getContentPane().add(new SmartModeModifier());
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public SmartModeModifier() {
		setText("Use Smart Timing");
		boolean smartModeIsActive = Boolean.parseBoolean(setup.getProperty(MeasureSetupEntry.USE_ADAPTIVE_OUTPUT_WRITER));
		setEnabled(smartModeIsActive);
		setSelected(true);
		addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setup.setSmartModeEnabled(isSelected());
			}
		});
	}

}

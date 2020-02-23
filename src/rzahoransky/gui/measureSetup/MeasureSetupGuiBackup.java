package rzahoransky.gui.measureSetup;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;

import gui.FileGui;
import rzahoransky.utils.MeasureSetUp;

public class MeasureSetupGuiBackup extends JFrame{
	
	MeasureSetUp setup = MeasureSetUp.getInstance();
	GridBagConstraints c = new GridBagConstraints();
	FileGui measureFile;
	TimeIntevallGui time;
	MeasureLengthGui length;
	MieGUI mieGui;

	Border test;
	
	public static void main (String args[]) {
		MeasureSetupGuiBackup gui = new MeasureSetupGuiBackup();
		gui.setVisible(true);
	}

	public MeasureSetupGuiBackup() {
		setLookAndFeel();
		setupComponents();
		setupFrame();
		addBorders();
		
		//select measure file
		c.gridy=0;
		c.gridx=0;
		c.weightx=1;
		c.weighty=0;
		c.anchor=GridBagConstraints.BASELINE;
		c.fill=GridBagConstraints.HORIZONTAL;
		c.gridwidth=2;
		getContentPane().add(measureFile,c);
		c.gridwidth=1;
		
		
		//add spacer
//		c.gridy++;
//		c.gridx=0;
//		c.gridwidth=GridBagConstraints.REMAINDER;
//		add(new JSeparator(),c);
		
		
		//choose length
		c.gridy++;
		c.gridwidth=1;
		Border test = BorderFactory.createEtchedBorder();
		length.setBorder(test);
		add(length,c);

		
		//choose interval
		c.anchor=GridBagConstraints.NORTH;
		//c.weighty=1;
		c.fill=GridBagConstraints.BOTH;
		c.gridx++;
		c.insets=new Insets(0, 30, 0, 0);
		c.weightx=0;
		time.setBorder(test);
		getContentPane().add(time,c);
		
		c.gridx=0;
		c.gridy++;
		c.insets=new Insets(0, 0, 0, 0);
		c.weightx=1;
		c.gridwidth=GridBagConstraints.REMAINDER;
		mieGui=new MieGUI();
		mieGui.setBorder(test);
		add(mieGui, c);
	}

	private void setupComponents() {
		setup = MeasureSetUp.getInstance();
		measureFile = new FileGui("Store Measurement: ", new FileNameExtensionFilter("CSV-Files (*.csv)","csv"));
		time = new TimeIntevallGui();
		length = new MeasureLengthGui();
		measureFile.setDialogType(FileGui.DialogType.OPEN);
		test = BorderFactory.createEtchedBorder();
		
		
	}

	private void addBorders() {
		time.setBorder(test);
		length.setBorder(test);
		measureFile.setBorder(test);
		
	}

	private void nextLine() {
		c.gridy++;
		c.gridx=0;
	}
	
	private void nextColumn() {
		c.gridx++;
	}

	private void setupFrame() {
		setSize(700, 500);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		//setLayout(new GridBagLayout());
		getContentPane().setLayout(new GridBagLayout());
		setTitle("DQ Measure setup");
	}
	
	private void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
		}
	}
	
	private JPanel getMieFileChooser(String description) {
		JPanel result = new JPanel();
		result.setLayout(new GridBagLayout());
		c.gridy=0;
		c.gridx=0;
		c.weightx=0;
		c.fill=GridBagConstraints.NONE;
		c.ipadx=3;
		result.add(new JLabel("Choose Mie-File"), c);
		
		JTextField textField = new JTextField();
		c.gridx=1;
		c.fill=GridBagConstraints.HORIZONTAL;
		c.weightx=1;
		result.add(textField, c);
		
		JButton chooseFile = new JButton("Choose...");
		c.gridx=2;
		c.weightx=0;
		c.fill=GridBagConstraints.NONE;
		result.add(chooseFile);
		
		return result;
	}

}

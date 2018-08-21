package rzahoransky.gui.measureSetup;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.sun.glass.ui.Window;
import com.sun.java.swing.plaf.windows.resources.windows;

import gui.FileGui;
import gui.JMieCalcGuiGridBagLayout;
import javafx.scene.control.Separator;
import presets.Wavelengths;
import rzahoransky.dqpipeline.DQPipeline;
import rzahoransky.dqpipeline.DQPipelineElement;
import rzahoransky.dqpipeline.DQSignal;
import rzahoransky.dqpipeline.analogueAdapter.AdapterInformation;
import rzahoransky.dqpipeline.analogueAdapter.FiveWLNIDaqAdapter;
import rzahoransky.dqpipeline.dataExtraction.FiveWLExtractor;
import rzahoransky.dqpipeline.dataExtraction.FiveWLMeasurePoints;
import rzahoransky.dqpipeline.periodMarker.FiveWLMarker;

public class MeasureSetupGui extends JFrame{
	
	MeasureSetUp setup = MeasureSetUp.getInstance();
	GridBagConstraints c = new GridBagConstraints();
	FileGui measureFile;
	TimeIntevallGui time;
	MeasureLengthGui length;
	MieGUI mieGUI;
	Border test;
	DQPipeline pipeline = new DQPipeline();
	
	public static void main (String args[]) {
		MeasureSetupGui gui = new MeasureSetupGui();
		gui.setVisible(true);
	}

	public MeasureSetupGui() {
		try {
		getWavelengths();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		c.weighty=1;
		c.fill=GridBagConstraints.BOTH;
		c.gridwidth=GridBagConstraints.REMAINDER;
		mieGUI.setBorder(test);
		add(mieGUI, c);
	}

	private void getWavelengths() {
		FiveWLNIDaqAdapter adapter = new FiveWLNIDaqAdapter();
		adapter.setADCardOrConfigParameter(AdapterInformation.getAvailableDevices().get(0));
		//pipeline.addPipelineElement(adapter);
		
		FiveWLMarker triggerMarker = new FiveWLMarker();
		//pipeline.addPipelineElement(triggerMarker);
		
		FiveWLExtractor valueExtractor = new FiveWLExtractor(new FiveWLMeasurePoints());
		//pipeline.addPipelineElement(valueExtractor);
		
		DQSignal element = valueExtractor.processDQElement(triggerMarker.processDQElement(adapter.processDQElement(null)));
		
		
		//pipeline.start();
		
		//DQSignal element = pipeline.getElement();
		
		//pipeline.stop();
		
		//System.out.println("Got element with "+element.getWL1());
		
		Wavelengths.WL1.setValue(element.getWL1());
		Wavelengths.WL2.setValue(element.getWL2());
		Wavelengths.WL3.setValue(element.getWL3());
	}

	private void setupComponents() {
		setup = MeasureSetUp.getInstance();
		measureFile = new FileGui("Store Measurement: ", new FileNameExtensionFilter("CSV-Files (*.csv)","csv"));
		time = new TimeIntevallGui();
		length = new MeasureLengthGui();
		mieGUI = new MieGUI();
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
		setSize(550, 600);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationByPlatform(true);
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

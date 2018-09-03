package rzahoransky.gui.measureSetup;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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

import calculation.MieList;
import errors.WavelengthMismatchException;
import gui.FileGui;
import gui.JMieCalcGuiGridBagLayout;
import javafx.scene.control.Separator;
import kirkwood.nidaq.access.NiDaq;
import kirkwood.nidaq.access.NiDaqException;
import presets.Wavelengths;
import rzahoransky.dqpipeline.DQPipeline;
import rzahoransky.dqpipeline.analogueAdapter.AdapterInformation;
import rzahoransky.dqpipeline.analogueAdapter.FiveWLNIDaqAdapter;
import rzahoransky.dqpipeline.dataExtraction.ConcentrationExtractor;
import rzahoransky.dqpipeline.dataExtraction.DQExtractor;
import rzahoransky.dqpipeline.dataExtraction.FiveWLExtractor;
import rzahoransky.dqpipeline.dataExtraction.FiveWLMeasurePoints;
import rzahoransky.dqpipeline.dataExtraction.ParticleSizeExtractor;
import rzahoransky.dqpipeline.dataExtraction.TransmissionExtractor;
import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.interfaces.AdapterInterface;
import rzahoransky.dqpipeline.interfaces.DQPipelineElement;
import rzahoransky.dqpipeline.periodMarker.FiveWLMarker;
import rzahoransky.dqpipeline.simulation.FiveWLOneHeadSimulator;
import rzahoransky.dqpipeline.visualization.DQMeasurementVisualizer;
import rzahoransky.dqpipeline.visualization.DQSinglePeriodMeasurementVisualizer;
import rzahoransky.dqpipeline.visualization.DQVisualizer;
import rzahoransky.dqpipeline.visualization.LaserVoltageVisualizer;
import rzahoransky.dqpipeline.visualization.ParticleSizeVisualizer;
import rzahoransky.dqpipeline.visualization.TransmissionVisualizer;
import rzahoransky.gui.measureGui.MeasureGui;
import storage.dqMeas.read.DQReader;

public class MeasureSetupGui extends JFrame{
	
	MeasureSetUp setup = MeasureSetUp.getInstance();
	GridBagConstraints c = new GridBagConstraints();
	FileGui measureFile;
	TimeIntevallGui time;
	MeasureLengthGui length;
	MieGUI mieGUI;
	Border test;
	DQPipeline pipeline = new DQPipeline();
	JButton startBtn;
	
	public static void main (String args[]) {
		MeasureSetupGui gui = new MeasureSetupGui();
		gui.setVisible(true);
	}

	public MeasureSetupGui() {
		try {
		getWavelengths();
		} catch (Exception e) {
			//e.printStackTrace();
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
		
		//choose and set up mie File
		c.gridx=0;
		c.gridy++;
		c.insets=new Insets(0, 0, 0, 0);
		c.weightx=1;
		c.weighty=1;
		c.fill=GridBagConstraints.BOTH;
		c.gridwidth=GridBagConstraints.REMAINDER;
		mieGUI.setBorder(test);
		add(mieGUI, c);
		
		//OK Button
		
		c.gridy++;
		c.gridwidth=GridBagConstraints.REMAINDER;
		c.anchor=GridBagConstraints.LAST_LINE_END;
		c.fill=GridBagConstraints.NONE;
		c.weighty=0;
		add(getStartBtn(),c);
		
	}

	private void getWavelengths() {
		FiveWLNIDaqAdapter adapter = new FiveWLNIDaqAdapter();
		adapter.setADCardOrConfigParameter(AdapterInformation.getAvailableDevices().get(0));
		
		FiveWLMarker triggerMarker = new FiveWLMarker();
		
		FiveWLExtractor valueExtractor = new FiveWLExtractor(new FiveWLMeasurePoints());
		
		DQSignal element = valueExtractor.processDQElement(triggerMarker.processDQElement(adapter.processDQElement(null)));
		
		
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
		mieGUI.setEditable(false);
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
		setSize(600, 650);
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
	
	private void setupPipeline(MieList wl1, MieList wl2, MieList wl3) throws NiDaqException {
		//NI Adapter
		//AdapterInterface adapter = new FiveWLNIDaqAdapter();
		//adapter.setADCardOrConfigParameter(NiDaq.getDeviceNames().get(0));
		
		AdapterInterface adapter = new FiveWLOneHeadSimulator();
		
		//Look for triggers
		FiveWLMarker triggerMarker = new FiveWLMarker();
		
		//extract single periods
		FiveWLExtractor valueExtractor = new FiveWLExtractor(new FiveWLMeasurePoints());
		
		//extract transmissions
		TransmissionExtractor transmissionExtractor = new TransmissionExtractor(false);
		
		//calculate DQ
		DQExtractor dqExtractor = new DQExtractor();
		
		//Calculate particles from DQ
		ParticleSizeExtractor sizeExtractor = new ParticleSizeExtractor(wl1, wl2, wl3);
		
		//Calulate particle concentration
		ConcentrationExtractor concentrationExtractor = new ConcentrationExtractor(wl1, wl2, wl3);
		
		
		//Graphical Elements
		DQSinglePeriodMeasurementVisualizer singelPeriodVisualizer = new DQSinglePeriodMeasurementVisualizer(false);
		TransmissionVisualizer transmissionVisualizer = new TransmissionVisualizer(false);
		LaserVoltageVisualizer laserVoltage = new LaserVoltageVisualizer();
		ParticleSizeVisualizer sizeVisualizer = new ParticleSizeVisualizer(true);
		
		//create Pipeline
		pipeline = new DQPipeline();
		
		setup.addTransmissionVisualizer(transmissionVisualizer);
		setup.addSinglePeriodVisualizer(singelPeriodVisualizer);
		setup.addParticleVisualizer(sizeVisualizer);
		setup.addTransmissionExtractor(transmissionExtractor);
		
		MieList[] list = {wl1, wl2, wl3};
		
		setup.setMieList(list);
		
		
		
		

		//DQPipelineElement concenentrationExtractor = new ConcentrationExtractor(measureLengthInCm, wl1, wl2, wl3)
		
		//DQPipelineElement writer = new RawDataWriter("testInfared.txt");
		
		pipeline.addPipelineElement(adapter);
		pipeline.addPipelineElement(triggerMarker);
		pipeline.addPipelineElement(singelPeriodVisualizer);
		pipeline.addPipelineElement(valueExtractor);
		pipeline.addPipelineElement(laserVoltage);
		pipeline.addPipelineElement(transmissionExtractor);
		pipeline.addPipelineElement(transmissionVisualizer);
		pipeline.addPipelineElement(dqExtractor);
		//pipeline.addPipelineElement(new DQVisualizer());
		pipeline.addPipelineElement(sizeExtractor);
		pipeline.addPipelineElement(sizeVisualizer);
		pipeline.addPipelineElement(concentrationExtractor);
		
		pipeline.addPipelineElement(new DQVisualizer());
		//pipeline.addPipelineElement(writer);
		//pipeline.start();
	}
	
	private JButton getStartBtn() {
		startBtn = new JButton("Start");
		startBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					DQReader mieReader = new DQReader(mieGUI.getChoosenFile());
					setupPipeline(mieReader.getWl1(), mieReader.getWl2(), mieReader.getWl3());
					MeasureGui gui = new MeasureGui(pipeline);
					//pipeline.start();
					
				} catch (IOException e1) {
					JOptionPane.showMessageDialog((Component) e.getSource(), "Cannot read MIE-File", "I/O error", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				} catch (WavelengthMismatchException e1) {
					JOptionPane.showMessageDialog((Component) e.getSource(), "MIE-File does not include correct wavelengths", "Wavelength mismatch", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				} catch (NiDaqException e1) {
					JOptionPane.showMessageDialog((Component) e.getSource(), "Cannot read from A/D converter", "I/O error", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
				
			}
		});
		//startBtn.setBorder(BorderFactory.createEtchedBorder());
		return startBtn;
	}

}

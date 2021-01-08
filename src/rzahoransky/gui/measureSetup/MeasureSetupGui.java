package rzahoransky.gui.measureSetup;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;

import calculation.MieList;
import errors.WavelengthMismatchException;
import gui.FileGui;
import kirkwood.nidaq.access.NiDaq;
import kirkwood.nidaq.access.NiDaqException;
import presets.Wavelengths;
import rzahoransky.dqpipeline.DQPipeline;
import rzahoransky.dqpipeline.analogueAdapter.GenericNIDaqAdapter;
import rzahoransky.dqpipeline.dataExtraction.ConcentrationExtractor;
import rzahoransky.dqpipeline.dataExtraction.DQExtractor;
import rzahoransky.dqpipeline.dataExtraction.IndividualDQSizeAndConcentrationExtractor;
import rzahoransky.dqpipeline.dataExtraction.SimpleDQLookupDiameterExtractor;
import rzahoransky.dqpipeline.dataExtraction.TransmissionExtractor;
import rzahoransky.dqpipeline.dataExtraction.rawDataExtraction.FiveWLExtractor;
import rzahoransky.dqpipeline.dataExtraction.rawDataExtraction.RawVoltageExtractorFactory;
import rzahoransky.dqpipeline.dataExtraction.rawDataExtraction.ThreeWLMeasurePoints;
import rzahoransky.dqpipeline.dataWriter.AdaptiveOutptWriter;
import rzahoransky.dqpipeline.dataWriter.OutputWriter;
import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.interfaces.AbstractDQPipelineElement;
import rzahoransky.dqpipeline.interfaces.AdapterInterface;
import rzahoransky.dqpipeline.interfaces.DQPipelineElement;
import rzahoransky.dqpipeline.periodMarker.FiveWLMarker;
import rzahoransky.dqpipeline.periodMarker.MarkerFactory;
import rzahoransky.dqpipeline.simulation.FiveWLOneHeadSimulator;
import rzahoransky.dqpipeline.simulation.ThreeWLOneHeadSimulator;
import rzahoransky.dqpipeline.visualization.DQSinglePeriodMeasurementVisualizer;
import rzahoransky.dqpipeline.visualization.LaserVoltageVisualizer;
import rzahoransky.dqpipeline.visualization.ParticleSizeVisualizationChartIndividualDQ;
import rzahoransky.dqpipeline.visualization.ParticleSizeVisualizerChart;
import rzahoransky.dqpipeline.visualization.TransmissionVisualizer;
import rzahoransky.gui.adjustmentGui.AdjustmentGui;
import rzahoransky.gui.measureGui.MeasureGui;
import rzahoransky.utils.DQTimer;
import rzahoransky.utils.properties.MeasureSetUp;
import rzahoransky.utils.properties.MeasureSetupEntry;
import storage.dqMeas.read.DQReader;

public class MeasureSetupGui extends JFrame {

	MeasureSetUp setup = MeasureSetUp.getInstance();
	GridBagConstraints c = new GridBagConstraints();
	FileGui measureFile;
	TimeIntevallGui time;
	MeasureLengthGui length;
	MieGUI mieGUI;
	Border border;
	DQPipeline pipeline = new DQPipeline();
	JButton startBtn;
	private AdapterSelectGui adapterSelectGUI;

	public static void main(String args[]) {
		MeasureSetupGui gui = new MeasureSetupGui();
		gui.setVisible(true);
	}

	public MeasureSetupGui() {

		setLookAndFeel();
		setupComponents();
		setupFrame();
		addBorders();

		// select measure file
		c.gridy = 0;
		c.gridx = 0;
		c.weightx = 1;
		c.weighty = 0;
		c.anchor = GridBagConstraints.BASELINE;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = GridBagConstraints.REMAINDER;
		getContentPane().add(measureFile, c);
		c.gridwidth = 1;

		// choose length
		c.gridy++;
		c.gridx = 0;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		Border test = BorderFactory.createEtchedBorder();
		length.setBorder(test);
		add(length, c);

		// choose interval
		c.anchor = GridBagConstraints.NORTH;
		//c.gridwidth = 1;
		// c.weighty=1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx += 2;
		c.insets = new Insets(0, 20, 0, 0);
		c.weightx = 1;
		time.setBorder(test);
		getContentPane().add(time, c);
		
		//add reference usage checkboxes
		c.insets = new Insets(0, 0, 0, 0);
		c.gridy++;
		c.gridx=0;
		c.weightx=0;
		c.anchor = GridBagConstraints.BASELINE;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth=GridBagConstraints.REMAINDER;
		UseRefGui refGui = new UseRefGui();
		refGui.setBorder(test);
		getContentPane().add(refGui,c);

		// choose and set up mie File
		c.gridx = 0;
		c.gridy++;
		c.insets = new Insets(0, 0, 0, 0);
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = GridBagConstraints.REMAINDER;
		mieGUI.setBorder(test);
		add(mieGUI, c);

		// Adapter Select
		c.gridy++;
		// c.anchor=GridBagConstraints.LAST_LINE_END;
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.NONE;
		adapterSelectGUI = new AdapterSelectGui();
		add(adapterSelectGUI, c);

		c.gridx++;
		// c.anchor=GridBagConstraints.LAST_LINE_END;
		JButton adjust = new JButton("Adjustment...");
		adjust.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new AdjustmentGui(adapterSelectGUI.getSelectedDevice()).setVisible(true);
			}
		});
		add(adjust, c);
		
		c.gridx++;
		add(getAboutBtn(),c);

		// START Button

		// c.gridy++;
		c.gridx = GridBagConstraints.RELATIVE;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.anchor = GridBagConstraints.LINE_END;
		c.fill = GridBagConstraints.NONE;
		c.weighty = 0;
		add(getStartBtn(), c);

		try {
			getWavelengths();
		} catch (Exception e) {
			Wavelengths.WL1.setValue(0.673);
			setup.setProperty(MeasureSetupEntry.DEVICEWL1, Double.toString(0.673));
			Wavelengths.WL2.setValue(0.812);
			setup.setProperty(MeasureSetupEntry.DEVICEWL2, Double.toString(0.812));
			Wavelengths.WL3.setValue(1.313);
			setup.setProperty(MeasureSetupEntry.DEVICEWL3, Double.toString(1.313));
		}

	}

	private void getWavelengths() throws NiDaqException {
		GenericNIDaqAdapter adapter = new GenericNIDaqAdapter();
		//adapter.setADCardOrConfigParameter(setup.getProperty(MeasureSetupEntry.NIADAPTER));
		//adapter.setADCardOrConfigParameter(AdapterInformation.getAvailableDevices().get(0));

		AbstractDQPipelineElement triggerMarker = MarkerFactory.getPeriodMarker();

		AbstractDQPipelineElement rawValueExtractor = RawVoltageExtractorFactory.getRawVoltageExtractor();

		DQSignal element = rawValueExtractor
				.processDQElement(triggerMarker.processDQElement(adapter.processDQElement(null)));

		if (element != null) {
			Wavelengths.WL1.setValue(element.getWL1());
			setup.setProperty(MeasureSetupEntry.DEVICEWL1, Double.toString(element.getWL1()));
			Wavelengths.WL2.setValue(element.getWL2());
			setup.setProperty(MeasureSetupEntry.DEVICEWL2, Double.toString(element.getWL2()));
			Wavelengths.WL3.setValue(element.getWL3());
			setup.setProperty(MeasureSetupEntry.DEVICEWL3, Double.toString(element.getWL3()));

			System.out.println("Wavelengths: " + element.getWL1() + ", " + element.getWL2() + ", " + element.getWL3());
		}

		setup.setDeviceIsConnected(true);

		adapter.reset();
	}

	private void setupComponents() {
		setup = MeasureSetUp.getInstance();
		measureFile = new FileGui("Store Measurement: ", new FileNameExtensionFilter("CSV-Files (*.csv)", "csv"));
		measureFile.getTextField().setText(setup.getProperty(MeasureSetupEntry.OUTPUTFILE));
		measureFile.getTextField().addTextListener(new TextListener() {
			public void textValueChanged(TextEvent e) {
				setup.setProperty(MeasureSetupEntry.OUTPUTFILE, measureFile.getChoosenFile().getAbsolutePath());
			}
		});
		time = new TimeIntevallGui();
		length = new MeasureLengthGui();
		mieGUI = new MieGUI();
		mieGUI.setEditable(false);
		border = BorderFactory.createEtchedBorder();

	}

	private void addBorders() {
		time.setBorder(border);
		length.setBorder(border);
		measureFile.setBorder(border);

	}

	private void setupFrame() {
		setSize(600, 750);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationByPlatform(true);
		// setLayout(new GridBagLayout());
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
		// NI Adapter
		AdapterInterface adapter;
		if (adapterSelectGUI.hasDevices()) {
			adapter = new GenericNIDaqAdapter(); //TODO
			adapter.setADCardOrConfigParameter(adapterSelectGUI.getSelectedDevice());
			setup.setProperty(MeasureSetupEntry.NIADAPTER, adapterSelectGUI.getSelectedDevice());
		} else {
			JOptionPane.showMessageDialog(this, "No NiDAQ adapter. Will start in simulation mode!");
			//adapter = new FiveWLOneHeadSimulator();
			adapter = new ThreeWLOneHeadSimulator();
		}

		MieList[] list = { wl1, wl2, wl3 };

		setup.setMieList(list);

		// Look for triggers
		AbstractDQPipelineElement triggerMarker = MarkerFactory.getPeriodMarker();
		// extract single periods
		AbstractDQPipelineElement valueExtractor = RawVoltageExtractorFactory.getRawVoltageExtractor();

		// extract transmissions
		TransmissionExtractor transmissionExtractor = new TransmissionExtractor(false);

		// calculate DQ
		DQExtractor dqExtractor = new DQExtractor();
		
		//get particle sizes for individualDQ
		AbstractDQPipelineElement individualDQParticleSizeExtractor = new IndividualDQSizeAndConcentrationExtractor(wl1, wl2, wl3);

		// Calculate particles from DQ
		// DQPipelineElement sizeExtractor = new ProbabilityBasedDiameterExtractor(wl1,wl2, wl3);
		DQPipelineElement sizeExtractor = new SimpleDQLookupDiameterExtractor(wl1, wl2, wl3);

		// Calculate particle concentration
		ConcentrationExtractor concentrationExtractor = new ConcentrationExtractor(wl1, wl2, wl3);

		// Graphical Elements
		DQSinglePeriodMeasurementVisualizer singelPeriodVisualizer = new DQSinglePeriodMeasurementVisualizer(false);
		TransmissionVisualizer transmissionVisualizer = new TransmissionVisualizer(false);
		LaserVoltageVisualizer laserVoltage = new LaserVoltageVisualizer(false);
		
		//"single DQ1/DQ2" Visualization
		//ParticleSizeVisualizerChart sizeVisualizer = new ParticleSizeVisualizerChart(false);
		
		//Visualization with possibility to show diameter based on DQ1, DQ2 or DQ3 only
		ParticleSizeVisualizerChart sizeVisualizer = new ParticleSizeVisualizationChartIndividualDQ(false);

		setup.addTransmissionVisualizer(transmissionVisualizer);
		setup.addSinglePeriodVisualizer(singelPeriodVisualizer);
		setup.addParticleVisualizer(sizeVisualizer);
		setup.addTransmissionExtractor(transmissionExtractor);
		setup.addLaserVoltageVisualizer(laserVoltage);
		
		// create Pipeline
		pipeline = new DQPipeline();



		// DQPipelineElement concenentrationExtractor = new
		// ConcentrationExtractor(measureLengthInCm, wl1, wl2, wl3)

		// DQPipelineElement writer = new RawDataWriter("testInfared.txt");

		pipeline.addPipelineElement(adapter);
		pipeline.addPipelineElement(triggerMarker);
		pipeline.addPipelineElement(singelPeriodVisualizer);
		pipeline.addPipelineElement(valueExtractor);
		pipeline.addPipelineElement(laserVoltage);
		pipeline.addPipelineElement(transmissionExtractor);
		pipeline.addPipelineElement(transmissionVisualizer);
		pipeline.addPipelineElement(dqExtractor);
		pipeline.addPipelineElement(individualDQParticleSizeExtractor); //required for single DQ1, DQ2 or DQ3 particle size only
		// pipeline.addPipelineElement(new DQVisualizer());
		pipeline.addPipelineElement(sizeExtractor);
		pipeline.addPipelineElement(concentrationExtractor);
		pipeline.addPipelineElement(sizeVisualizer);
		

		// pipeline.addPipelineElement(new SimpleDQEntryDiameterExtractor());
		// pipeline.addPipelineElement(new DQVisualizer());
		// pipeline.addPipelineElement(writer);

		// pipeline.addPipelineElement(laserVoltage);

		// store measurement
		if (measureFile.hasChoosenFile()) {
			OutputWriter outWriter;
			boolean adaptiveOutputWriter = time.getSmartMode();
			if (adaptiveOutputWriter) 
				outWriter = new AdaptiveOutptWriter(measureFile.getChoosenFile());
			else
				outWriter = new OutputWriter(measureFile.getChoosenFile());
			setup.addOutputWriter(outWriter);
			pipeline.addPipelineElement(outWriter);
			setup.setProperty(MeasureSetupEntry.OUTPUTFILE, measureFile.getChoosenFile().getAbsolutePath());
		}
		
		//add Timer
		//DQTimer timer = new DQTimer(pipeline);
		//setup.addTimer(timer);

		setup.setPipeline(pipeline);
		

	}

	private JButton getStartBtn() {
		startBtn = new JButton("Start");
		startBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					DQReader mieReader = new DQReader(mieGUI.getChoosenFile());
					setup.setStorageIntervall(time.getTimeIntervall());
					setup.setAverageOverTime(time.averageOverTime.isSelected());
					setup.setProperty(MeasureSetupEntry.NIADAPTER, adapterSelectGUI.getSelectedDevice());
					setupPipeline(mieReader.getWl1(), mieReader.getWl2(), mieReader.getWl3());
					
					@SuppressWarnings("unused")
					MeasureGui gui = new MeasureGui(pipeline);
					// pipeline.start();

				} catch (IOException e1) {
					JOptionPane.showMessageDialog((Component) e.getSource(), "Cannot read MIE-File", "I/O error",
							JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				} catch (WavelengthMismatchException e1) {
					JOptionPane.showMessageDialog((Component) e.getSource(),
							"MIE-File does not include correct wavelengths", "Wavelength mismatch",
							JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				} catch (NiDaqException e1) {
					JOptionPane.showMessageDialog((Component) e.getSource(), "Cannot read from A/D converter",
							"I/O error", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				} catch (Exception e2) {
					JOptionPane.showMessageDialog((Component) e.getSource(),
							"Exception occured. Cannot start measurement", "Error", JOptionPane.ERROR_MESSAGE);
					e2.printStackTrace();
				}

			}
		});
		// startBtn.setBorder(BorderFactory.createEtchedBorder());
		return startBtn;
	}
	
	private JButton getAboutBtn() {
		JButton abtbtn = new JButton("about...");
		JFrame about = new JFrame("About");
		about.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JLabel text = new JLabel("<html>3WL-DQ 2021-01<b> by Richard Markus Zahoransky <br> r.zahoransky@gmx.de </b> for Wizard DQ "
				+ "<br><br> this software contains the apache commons libraries and the JFreeChart library"
				+ "</html>");
		about.setSize(300, 200);
		about.add(text);
		
		abtbtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				about.setVisible(true);
			}
		});
		return abtbtn;
	}

}

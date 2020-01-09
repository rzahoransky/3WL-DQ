package rzahoransky.gui.measureSetup;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import calculation.CalculationAssignment;
import calculation.CalculationAssignmentListener;
import errors.WavelengthMismatchException;
import gui.FileGui;
import gui.JMieCalcGuiGridBagLayout;
import gui.MieParameterGui;
import presets.Wavelengths;
import storage.dqMeas.read.DQReader;
import storage.dqMeas.read.RefIndexReader;

public class MieGUI extends JPanel implements CalculationAssignmentListener {
	
	protected FileGui mieFile = new FileGui("Mie-File: ");
	//MieParameterGui mieParams = new Mie
	protected GridBagConstraints c = new GridBagConstraints();
	private HashMap<Wavelengths, MieParameterGui> mieParams = new HashMap<>();
	protected JPanel dqField = new JPanel();
	
	public static void main(String[] args) {
		MieGUI test = new MieGUI();
		JFrame testFrame = new JFrame("Mie Params Test");
		testFrame.setSize(300, 300);
		testFrame.add(test);
		testFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		testFrame.setVisible(true);
	}

	public MieGUI() {
		setLayout(new GridBagLayout());
		c.gridwidth=GridBagConstraints.RELATIVE;
		c.weightx=1;
		c.fill=GridBagConstraints.HORIZONTAL;
		mieFile.setDialogType(FileGui.DialogType.OPEN);
		add(mieFile, c);
		c.gridwidth=1;
		c.fill=GridBagConstraints.NONE;
		c.weightx=0;
		c.gridx=3;
		c.insets=new Insets(0, 5, 0, 0);
		add(calcBtn(),c);
		addMieParameters();
		addDQPlot();
		addMieFileListener();
		try {
		mieFile.getTextField().setText(MeasureSetUp.getInstance().getMieFile().getAbsolutePath());
		for (TextListener listener: mieFile.getTextField().getTextListeners()) {
			listener.textValueChanged(new TextEvent(mieFile.getTextField(), 0));
		}
		} catch (Exception e) {
			//Could not read last MIE-File. Ignore
		}
		//mieFile.getTextField().addActionListener(new Action);
	}
	
	public void setEditable(boolean editable) {
		for (MieParameterGui parameterGui: mieParams.values()) {
			parameterGui.setEditable(editable);
		}
	}
	
	private void addDQPlot() {
		dqField.setMinimumSize(new Dimension(150, 150));
		dqField.setSize(new Dimension(150, 150));
		//dqField.setSize(new Dimension(150, 140));
		dqField.setLayout(new BorderLayout());
		dqField.setBorder(BorderFactory.createEtchedBorder());
		c.gridx=0;
		c.gridwidth=GridBagConstraints.REMAINDER;
		c.gridheight=GridBagConstraints.REMAINDER;
		c.fill=GridBagConstraints.BOTH;
		c.weighty=1;
		c.gridy++;
		add(dqField, c);
	}
	
	private JButton calcBtn() {
		JButton calc = new JButton("create...");
		calc.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				MeasureSetUp setup = MeasureSetUp.getInstance();
				if (setup.getDeviceIsConnected()) {
					Wavelengths.WL1.setValue(Double.parseDouble(setup.getProperty(MeasureSetupEntry.DEVICEWL1)));
					Wavelengths.WL2.setValue(Double.parseDouble(setup.getProperty(MeasureSetupEntry.DEVICEWL2)));
					Wavelengths.WL3.setValue(Double.parseDouble(setup.getProperty(MeasureSetupEntry.DEVICEWL3)));
				}
				new JMieCalcGuiGridBagLayout(getChoosenFile().getAbsolutePath()).setVisible(true);	
				addAsListener();
			}
		});
		
		return calc;
	}

	private void addMieFileListener() {

		mieFile.getTextField().addTextListener(new TextListener() {

			@Override
			public void textValueChanged(TextEvent e) {
				new Thread() {
					public void run() {
						if (mieFile.getChoosenFile().exists()) {
							try {
								DQReader reader = new DQReader(mieFile.getChoosenFile());
								RefIndexReader refIndex = reader.getrefIndexReader();
								CalculationAssignment.getInstance().setParticles(refIndex.getRefIndices());
								dqField.removeAll();
								Container plot = reader.getPlot();
								dqField.add(plot, BorderLayout.CENTER);
								revalidate();
								repaint();
								MeasureSetUp.getInstance().setMieFile(mieFile.getChoosenFile());
							} catch (IOException | WavelengthMismatchException e) {
								e.printStackTrace();
							}
						}
					}
				}.start();
				MeasureSetUp.getInstance().setProperty(MeasureSetupEntry.MIEFILE, mieFile.getChoosenFile().getAbsolutePath());

			}
		});
	}

	public void addMieParameters() {
		int x = 0;
		for(Wavelengths wl:Wavelengths.values()) {
			MieParameterGui gui = new MieParameterGui(wl);
			c.gridwidth=1;
			c.gridx=x;
			c.gridy=2;
			c.weightx=1;
			c.fill=GridBagConstraints.HORIZONTAL;
			add(gui,c);
			mieParams.put(wl, gui);
			x++;
		}
	}
	
	public File getChoosenFile() {
		return mieFile.getChoosenFile();
	}

	@Override
	public void mieParticleChanged() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void wavelengthsChanged() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void calculationFinished() {
		// TODO Auto-generated method stub
	}

	@Override
	public void progress(double fraction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void diametersChanged() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sigmaChanged() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void outputFileChanged() {

		
	}
	
	protected void addAsListener() {
		CalculationAssignment.getInstance().removeListener(this);
		CalculationAssignment.getInstance().addListener(this);
	}

	@Override
	public void fileWritten() {
		mieFile.getTextField().setText(CalculationAssignment.getInstance().getOutputFile().getAbsolutePath());
		//for (TextListener listener: mieFile.getTextField().getTextListeners()) {
		//	listener.textValueChanged(new TextEvent(mieFile.getTextField(), 0));
		//}
		
	}

}

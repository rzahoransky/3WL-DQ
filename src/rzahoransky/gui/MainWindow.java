package rzahoransky.gui;

import java.io.File;
import java.io.IOException;

import javax.swing.JPanel;

import calculation.CalculationAssignment;
import errors.WavelengthMismatchException;
import gui.JMieCalcGuiGridBagLayout;
import gui.OldMieCalculator;
import presets.IMieParticlePreset;
import presets.Wavelengths;
import storage.dqMeas.read.DQReader;

public class MainWindow extends JPanel{
	
	OldMieCalculator mieCalculator;
	CalculationAssignment assignment;

	public MainWindow(File mieFile) {
		try {
			DQReader reader = new DQReader(mieFile);
			reader.plot();
		} catch (IOException | WavelengthMismatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void getMieField() {
		assignment = CalculationAssignment.getInstance();
		assignment.getParticles().setName("Test");
		
		JMieCalcGuiGridBagLayout gui = new JMieCalcGuiGridBagLayout();
		
	}

}

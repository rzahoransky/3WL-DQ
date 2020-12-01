package rzahoransky.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import actions.CalculationAction;
import buildInPresets.LatexInWaterPreset;
import calculation.CalculationAssignment;
import calculation.MieList;
import calculation.original.Mie;
import charts.ChartType;
import charts.Charts;
import errors.WavelengthMismatchException;
import presets.FixedSigmaParameter;
import presets.StandardDiameterParameters;
import presets.Wavelengths;
import rzahoransky.dqpipeline.dataExtraction.ConcentrationExtractor;
import rzahoransky.dqpipeline.dataExtraction.SimpleDQLookupDiameterExtractor;
import rzahoransky.dqpipeline.dataExtraction.TransmissionExtractor;
import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.dqSignal.DQSignalEntry;
import rzahoransky.dqpipeline.interfaces.DQPipelineElement;
import rzahoransky.utils.DQtype;
import rzahoransky.utils.MeasureSetUp;
import rzahoransky.utils.TransmissionType;
import storage.dqMeas.read.DQReader;

/**
 * Test Diameter and concentration within DQPipeline. Values are for latex
 * particle in water. Density, particle size, wavelength and refractive index
 * set by variables
 * 
 * @author richard
 *
 */
class EndToEndTest {
	static double wavelength1 = 0.673;
	static double wavelength2 = 0.818;
	static double wavelength3 = 1.313;
	double refractiveIndexMedium = 1.33;
	double refractiveIndexRealSphere = 1.59;
	double refractiveIndexImagSphere = 0;
	static double diameterMicrometer = 0.750;
	static double concentration = 1*Math.pow(10, 14); //particles per m³
	static double length = 0.01; //length in m
	static double sigma = 0.003; //almost monodisperse
	DQSignal signal;
	static File tempfile;

	@BeforeAll
	static void setUp() throws Exception {
		System.out.println("Preapring test...");
		tempfile = File.createTempFile("testMieFile", "miezip");
		// File tempFile = File.createTempFile("MyAppName-", ".tmp");
		 tempfile.deleteOnExit();

		System.out.println(tempfile.getAbsolutePath());

		createMieFile();
	}

	@Test
	void mieFileIsCreated() {
		assertTrue(tempfile.exists() && tempfile.isFile() && tempfile.length() > 0);
	}

	@Test
	void readingOfMieFile() {
		try {
			simulateDQPipeline(tempfile);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void getDiameter() throws IOException, WavelengthMismatchException {
		simulateDQPipeline(tempfile);
		System.out.println("Calculated Diameter: " + signal.getGeometricalDiameter());
		assertEquals(diameterMicrometer, signal.getGeometricalDiameter(), 0.1);
	}
	
	@Test
	void getTransmission() throws IOException, WavelengthMismatchException {
		simulateDQPipeline(tempfile);
		System.out.println("Calculated concentration: " + signal.getNumberConcentration());
		System.out.println("Expected concentration: "+concentration);
		assertEquals(concentration, signal.getNumberConcentration(), concentration*0.1);
	}

	void simulateDQPipeline(File mieField) throws IOException, WavelengthMismatchException {
		signal = generateDQSignalFor(diameterMicrometer);
		DQReader reader = getDQReader(mieField);

		MieList[] list = { reader.getWl1(), reader.getWl2(), reader.getWl3() };

		MeasureSetUp.getInstance().setMieList(list);

		SimpleDQLookupDiameterExtractor sizeExtractor = new SimpleDQLookupDiameterExtractor(list[0], list[1], list[2]);
		sizeExtractor.processDQElement(signal);
		ConcentrationExtractor concentrationExtractor = new ConcentrationExtractor(list[0], list[1], list[2]);
		concentrationExtractor.setMeasureLengthInCentimeters(length*100);
		concentrationExtractor.processDQElement(signal);
	}

	DQSignal generateDQSignalFor(double diameterInMicrometer) {
		DQSignal signal = new DQSignal();
		// Take Mie as ground truth
		Mie wl1 = new Mie();
		wl1.setRadiusWavelength(diameterInMicrometer / 2, wavelength1);
		wl1.setHostRefractiveIndex(refractiveIndexMedium);
		wl1.setRefractiveIndex(refractiveIndexRealSphere, refractiveIndexImagSphere);

		Mie wl2 = new Mie();
		wl2.setRadiusWavelength(diameterInMicrometer / 2, wavelength2);
		wl2.setHostRefractiveIndex(refractiveIndexMedium);
		wl2.setRefractiveIndex(refractiveIndexRealSphere, refractiveIndexImagSphere);

		Mie wl3 = new Mie();
		wl3.setRadiusWavelength(diameterInMicrometer / 2, wavelength3);
		wl3.setHostRefractiveIndex(refractiveIndexMedium);
		wl3.setRefractiveIndex(refractiveIndexRealSphere, refractiveIndexImagSphere);

		wl1.calcScattCoeffs();
		wl2.calcScattCoeffs();
		wl3.calcScattCoeffs();
		
		double qext1 = wl1.qext();
		double qext2 = wl2.qext();
		double qext3 = wl3.qext();

		System.out.println("Qext r=" + diameterInMicrometer / 2 + ",wl=" + wavelength1 + "n=" + refractiveIndexMedium + "/("
				+ refractiveIndexRealSphere + "-" + refractiveIndexImagSphere + "i=" + qext1);
		System.out.println("Qext r=" + diameterInMicrometer / 2 + ",wl=" + wavelength2 + "n=" + refractiveIndexMedium + "/("
				+ refractiveIndexRealSphere + "-" + refractiveIndexImagSphere + "i=" + qext2);
		System.out.println("Qext r=" + diameterInMicrometer / 2 + ",wl=" + wavelength3 + "n=" + refractiveIndexMedium + "/("
				+ refractiveIndexRealSphere + "-" + refractiveIndexImagSphere + "i=" + qext3);
		System.out.println("DQ 1 is: " + wl1.qext() / wl2.qext() + ", DQ 2 is: " + wl2.qext() / wl3.qext());
	

		signal.setDQ(new DQSignalEntry(DQtype.DQ1, wavelength1, wavelength2, wl1.qext() / wl2.qext()));
		signal.setDQ(new DQSignalEntry(DQtype.DQ2, wavelength2, wavelength3, wl2.qext() / wl3.qext()));

		signal.setWL1(wavelength1);
		signal.setWL2(wavelength2);
		signal.setWL3(wavelength3);
		
		//append transmission
		double radiusInMeter = (diameterInMicrometer/1000000d)/2d;
		double concentrationInCubicMicrometer = concentration / Math.pow(10, 18);
		double lengthInMicrometer = length * Math.pow(10, 6);
		double transmissionWL1 = Math.exp(-1d * concentrationInCubicMicrometer * lengthInMicrometer * Math.PI * Math.pow(diameterInMicrometer/2, 2) * qext1);
		double transmissionWL2 = Math.exp(-1d * concentrationInCubicMicrometer * lengthInMicrometer * Math.PI * Math.pow(diameterInMicrometer/2, 2) * qext2);
		double transmissionWL3 = Math.exp(-1d * concentrationInCubicMicrometer * lengthInMicrometer * Math.PI * Math.pow(diameterInMicrometer/2, 2) * qext3);
		
		System.out.println("Transmission WL1: "+transmissionWL1+", WL2: "+transmissionWL2+", WL3: "+transmissionWL3);
		
		signal.addTransmission(TransmissionType.TRANSMISSIONWL1, transmissionWL1);
		signal.addTransmission(TransmissionType.TRANSMISSIONWL2, transmissionWL2);
		signal.addTransmission(TransmissionType.TRANSMISSIONWL3, transmissionWL3);
		return signal;
	}

	static void createMieFile() throws WavelengthMismatchException {
		CalculationAssignment mieCalc = CalculationAssignment.getInstance();
		mieCalc.changeWavelength(Wavelengths.WL1, wavelength1);
		mieCalc.changeWavelength(Wavelengths.WL2, wavelength2);
		mieCalc.changeWavelength(Wavelengths.WL3, wavelength3);

		// set calculation for sizes 0.001 to 2 um as logarithmic scale
		mieCalc.setDiameters(new StandardDiameterParameters(0.01, 2d, 1000, false));

		// set ref index. Medium: 1.33, particle: 1.59-0i
		mieCalc.setParticles(new LatexInWaterPreset());

		// set sigma to a low value
		mieCalc.setSigmas(new FixedSigmaParameter(sigma));

		mieCalc.setOutputFile(tempfile);

		CalculationAction startCalculation = new CalculationAction();
		startCalculation.actionPerformed(new ActionEvent(new JButton(), 1, "test"));

		// wait for caluclation to finish
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		Charts dq = new Charts(ChartType.DQField, CalculationAssignment.getInstance());
//		dq.setVisible(true);
//		try {
//			Thread.sleep(4000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		System.out.println("MieFile is generated!");
	}

	DQReader getDQReader(File file) throws IOException, WavelengthMismatchException {
		DQReader mieReader = new DQReader(file);
		return mieReader;
	}

}

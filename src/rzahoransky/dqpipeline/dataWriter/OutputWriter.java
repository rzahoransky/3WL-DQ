package rzahoransky.dqpipeline.dataWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import calculation.CalculationAssignment;
import presets.Wavelengths;
import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.interfaces.DQPipelineElement;
import rzahoransky.gui.measureSetup.MeasureSetUp;
import rzahoransky.gui.measureSetup.MeasureSetupEntry;
import storage.dqMeas.write.MieInfoWriter;

public class OutputWriter implements DQPipelineElement {

	public static void main(String[] args) {
		System.out.println(new OutputWriter(new File("bla")).getCurrentDateAsString());
	}

	private File file;
	private FileWriter fw;
	protected boolean headerWritten = false;
	protected boolean integrateOverTime = false;
	protected MeasureSetUp setup = MeasureSetUp.getInstance();
	protected int storageInterval;

	public OutputWriter(File file) {
		this.file = file;
		storageInterval = Integer.parseInt(setup.getProperty(MeasureSetupEntry.STOREINTERVAL));
		try {
			this.fw = openFile(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private FileWriter openFile(File file2) throws IOException {
		FileWriter fw = new FileWriter(file2);
		fw.write(generateHeader());
		return fw;
	}

	private String generateHeader() {
		String header = "# File Generated: " + getCurrentDateAsString();
		String mieFile = "# MIE-File: " + MeasureSetUp.getInstance().getMieFile().getAbsolutePath();
		String mieInfo = "# Mie-Info: " + MieInfoWriter.getInfoString(MeasureSetUp.getInstance().getMieList(0),
				MeasureSetUp.getInstance().getMieList(1), MeasureSetUp.getInstance().getMieList(3));
		String length = "# Measure length: "
				+ MeasureSetUp.getInstance().getProperty(MeasureSetupEntry.MEASURELENGTH_IN_CM) + "cm";
		String deviceWL = "# Device wavelength: "+Wavelengths.WL1.toString()+" "+Wavelengths.WL2.toString()+" "+Wavelengths.WL3.toString();
		return header+"\n\r"+mieFile+"\n\r"+mieInfo+"\n\r"+length+"\n\r"+deviceWL;
	}
	
	private String generateColumns() {
		TabbedStringBuilder s = new TabbedStringBuilder();
		s.append("System Time in ms");
		s.append("Time");
		s.append("Particle Diameter in μm");
		s.append("Sigma Log-Normal");
		s.append("Particles per cm³");
		s.append("Confidence");
		s.append("Transmission WL1");
		s.append("Transmission WL2");
		s.append("Transmission WL3");
		s.append("Voltage Meas WL1");
		s.append("Voltage Meas WL2");
		s.append("Voltage Meas WL3");
		s.append("Voltage Meas Offset");
		s.append("Voltage Ref WL1");
		s.append("Voltage Ref WL2");
		s.append("Voltage Ref WL3");
		s.append("Voltage Ref Offset");
		s.append("Factor WL1");
		s.append("Factor WL2");
		s.append("Factor WL3");
		return s.toString();
	}

	public String getCurrentDateAsString() {
		SimpleDateFormat df = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
		return df.format(new Date().getTime());
	}

	@Override
	public DQSignal processDQElement(DQSignal in) {
		if(!integrateOverTime && storageInterval == 0)
			System.out.println("Writing...");
		return in;
	}

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return "Stores Measurement Data";
	}

}

class TabbedStringBuilder {
	
	private StringBuilder builder = new StringBuilder();
	private String append = "\t";
	boolean first = true;
	
	public void setAppendCharacter(String s) {
		this.append = s;
	}
	
	public void append(String s) {
		if (first) {
			first=false;
			builder.append(s);
		} else {
			builder.append(append);
			builder.append(s);
		}
	}
	
	public String toString() {
		return builder.toString();
	}
	
}

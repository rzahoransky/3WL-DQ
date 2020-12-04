package rzahoransky.dqpipeline.dataWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;

import presets.Wavelengths;
import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.interfaces.AbstractDQPipelineElement;
import rzahoransky.utils.DQListUtils;
import rzahoransky.utils.DQTimer;
import rzahoransky.utils.DQtype;
import rzahoransky.utils.ExtractedSignalType;
import rzahoransky.utils.RawSignalType;
import rzahoransky.utils.TransmissionType;
import rzahoransky.utils.properties.MeasureSetUp;
import rzahoransky.utils.properties.MeasureSetupEntry;
import storage.dqMeas.write.MieInfoWriter;

public class OutputWriter extends AbstractDQPipelineElement implements Runnable{

	public static void main(String[] args) {
		System.out.println(getCurrentDateAsGermanString());
	}

	protected File file;
	protected FileWriter fw;
	protected boolean headerWritten = false;
	protected boolean integrateOverTime = false;
	protected MeasureSetUp setup = MeasureSetUp.getInstance();
	protected double storageInterval; //in seconds
	protected NumberFormat formatter = NumberFormat.getInstance(Locale.US);
	//protectes String scientificFormat = "";
	protected ArrayList<DQSignal> signals = new ArrayList<>(100);
	protected long lastWriteTime;
	protected long lastSaveTime;
	protected long saveIntervallTime = 5000;
	protected boolean errorMessageShown = false;
	protected long nanoTimeReference = 0; 
	

	
	public OutputWriter(File file) {
		lastWriteTime = System.currentTimeMillis();
		lastSaveTime = System.currentTimeMillis();
		nanoTimeReference = System.nanoTime(); //get nanosecond reference as it starts with arbitrary time
		this.file = new File(file.getParentFile(), getFileNameSaveDateString() +" "+ file.getName());
		// this.file = file;
		storageInterval = Double.parseDouble(setup.getProperty(MeasureSetupEntry.STOREINTERVAL));
		integrateOverTime = Boolean.parseBoolean(setup.getProperty(MeasureSetupEntry.AVERAGE_OVER_TIME));
		formatter.setMaximumFractionDigits(8);
		try {
			this.fw = openFile(this.file);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage(), "Cannot write output file", JOptionPane.ERROR_MESSAGE);
		}
	}

	protected FileWriter openFile(File file2) throws IOException {
		formatter.setGroupingUsed(false);
		FileWriter fw = new FileWriter(file2);
		fw.write(generateHeader());
		fw.write("\r\n");
		fw.write(generateColumns());
		fw.write("\r\n");
		return fw;
	}

	protected String generateHeader() {
		String header = "# File Generated: " + getCurrentDateAsString();
		String mieFile = "# MIE-File: " + MeasureSetUp.getInstance().getMieFile().getAbsolutePath();
		String mieInfo = "# Mie-Info: " + MieInfoWriter.getOneLineInfoString(MeasureSetUp.getInstance().getMieList(0),
				MeasureSetUp.getInstance().getMieList(1), MeasureSetUp.getInstance().getMieList(2));
		String length = "# Measure length: "
				+ MeasureSetUp.getInstance().getProperty(MeasureSetupEntry.MEASURELENGTH_IN_CM) + "cm";
		String deviceWL = "# Device wavelength: " + Wavelengths.WL1.toString() + " " + Wavelengths.WL2.toString() + " "
				+ Wavelengths.WL3.toString();
		return header + "\r\n" + mieFile + "\r\n"+mieInfo +"\r\n" + length + "\r\n" + deviceWL;
	}

	protected String generateColumns() {
		TabbedStringBuilder s = new TabbedStringBuilder("\t");
		s.append("System Time in us");
		s.append("Date");
		s.append("Time");
		s.append("Particle Diameter in um");
		s.append("Sigma Log-Normal");
		s.append("Particles per m³");
		s.append("Particle Number Concentration");
		s.append("Confidence");
		s.append("Measure length in cm");
		s.append("DQ1");
		s.append("DQ2");
		s.append("Transmission WL1");
		s.append("Transmission WL2");
		s.append("Transmission WL3");
		s.append("Voltage Meas WL1 w offset");
		s.append("Voltage Meas WL2 w offset");
		s.append("Voltage Meas WL3 w offset");
		s.append("Voltage Meas Offset");
		s.append("Voltage Ref WL1 w offset");
		s.append("Voltage Ref WL2 w offset");
		s.append("Voltage Ref WL3 w offset");
		s.append("Voltage Ref Offset");
		s.append("Factor WL1");
		s.append("Factor WL2");
		s.append("Factor WL3");
		return s.toString();
	}

	public String getCurrentDateAsString() {
		SimpleDateFormat df = new SimpleDateFormat("yyy-MM-dd HH:mm:ss.SSS");
		return df.format(new Date().getTime());
	}
	
	public String getDateAsString(long timestamp) {
		SimpleDateFormat df = new SimpleDateFormat("yyy-MM-dd");
		return df.format(new Date(timestamp).getTime());
	}
	
	public static String getCurrentDateAsGermanString() {
		//SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyy HH:mm:ss.SSS");
		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
		return df.format(new Date().getTime());
	}
	
	public static String getCurrentTimeAsGermanString(long timestamp) {
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
		//DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
		return df.format(new Date(timestamp).getTime());
	}
	
	public String getFileNameSaveDateString() {
		SimpleDateFormat df = new SimpleDateFormat("yyy-MM-dd HH.mm.ss");
		return df.format(new Date().getTime());
	}

	@Override
	public DQSignal processDQElement(DQSignal in) {
		if (storageInterval == 0) {
			write(in);
		} else if (!integrateOverTime) {
			if (in.getTimeStamp()-lastWriteTime>=storageInterval*1000) {
				write(in);
			}
		}
		else { //integrate over time
			signals.add(in);
			if(in.getTimeStamp()-signals.get(0).getTimeStamp()>storageInterval*1000) {
				write(DQListUtils.getAverageDQSignal(signals));
				signals.clear();
			}
		}
		return in;
	}

	protected void write(DQSignal in) {
		if (MeasureSetUp.getInstance().getPause())
			return; //do not save is paused is pressed
		try {
			fw.write(getLineString(in));
			lastWriteTime = System.currentTimeMillis();
			
			if(System.currentTimeMillis() - lastSaveTime > saveIntervallTime) {
				//flush file writer
				fw.flush();
				lastSaveTime = System.currentTimeMillis();
			}
			
		} catch (IOException e) {
			if(!errorMessageShown)
				JOptionPane.showMessageDialog(null, e.getMessage(), "Cannot write to disk", JOptionPane.ERROR_MESSAGE);
			errorMessageShown=true;
			e.printStackTrace();
		}
	}

	protected String getLineString(DQSignal in) {
		
		TabbedStringBuilder b = new TabbedStringBuilder("\t");
		
		b.append(Long.toString((in.getNanoSecondTimestamp()-nanoTimeReference)/1000));
		b.append(getDateAsString(in.getTimeStamp()));
		b.append(getCurrentTimeAsGermanString(in.getTimeStamp()));
		b.append(getAsLocale(in.getGeometricalDiameter()));
		b.append(getAsLocale(in.getSigma()));
		b.append((in.getNumberConcentration()));
		b.append((in.getVolumeConcentration()));
		b.append("NaN");
		b.append(getAsLocale(in.getMeasureLength()));
		b.append(in.getDQ(DQtype.DQ1).getDqValue());
		b.append(in.getDQ(DQtype.DQ2).getDqValue());
		b.append(getAsLocale(in.getTransmission(TransmissionType.TRANSMISSIONWL1)));
		b.append(getAsLocale(in.getTransmission(TransmissionType.TRANSMISSIONWL2)));
		b.append(getAsLocale(in.getTransmission(TransmissionType.TRANSMISSIONWL3)));
		b.append(getAsLocale(in.getAveragedValues(RawSignalType.meas, ExtractedSignalType.wl1wOffset)));
		b.append(getAsLocale(in.getAveragedValues(RawSignalType.meas, ExtractedSignalType.wl2wOffset)));
		b.append(getAsLocale(in.getAveragedValues(RawSignalType.meas, ExtractedSignalType.wl3wOffset)));
		b.append(getAsLocale(in.getAveragedValues(RawSignalType.meas, ExtractedSignalType.offset)));
		b.append(getAsLocale(in.getAveragedValues(RawSignalType.ref, ExtractedSignalType.wl1wOffset)));
		b.append(getAsLocale(in.getAveragedValues(RawSignalType.ref, ExtractedSignalType.wl2wOffset)));
		b.append(getAsLocale(in.getAveragedValues(RawSignalType.ref, ExtractedSignalType.wl3wOffset)));
		b.append(getAsLocale(in.getAveragedValues(RawSignalType.ref, ExtractedSignalType.offset)));
		b.append(getAsLocale(in.getFactor(TransmissionType.TRANSMISSIONWL1)));
		b.append(getAsLocale(in.getFactor(TransmissionType.TRANSMISSIONWL2)));
		b.append(getAsLocale(in.getFactor(TransmissionType.TRANSMISSIONWL3)));
		return b.toString() + "\r\n";
	}

	public void close() {
		try {
			fw.flush();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void endProcessing() {
		close();
	}

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return "Stores Measurement Data";
	}
	
	protected String getAsLocale(double d) {
		return formatter.format(d);
	}

	@Override
	public void run() {
		if (fw!=null)
			try {
				fw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
	}

}

class TabbedStringBuilder {

	private StringBuilder builder = new StringBuilder();
	private String sep = "\t";
	boolean first = true;

	public TabbedStringBuilder(String string) {
		sep = string;
	}
	
	public TabbedStringBuilder() {
		
	}

	public void setAppendCharacter(String s) {
		this.sep = s;
	}

	public void append(Object s) {
		if (first) {
			first = false;
			builder.append(s);
		} else {
			builder.append(sep);
			builder.append(s);
		}
	}

	public String toString() {
		return builder.toString();
	}

}

package rzahoransky.gui.measureSetup;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import calculation.MieList;
import rzahoransky.dqpipeline.DQPipelineElement;
import rzahoransky.dqpipeline.visualization.DQSinglePeriodMeasurementVisualizer;
import rzahoransky.dqpipeline.visualization.ParticleSizeVisualizer;
import rzahoransky.dqpipeline.visualization.TransmissionVisualizer;

public class MeasureSetUp extends Properties{
	
	private static File propertyFile = new File("measureSetupProperties.properties");
	private static MeasureSetUp myInstance = new MeasureSetUp();
	private DQSinglePeriodMeasurementVisualizer singlePeriodVisualizer;
	private TransmissionVisualizer transmissionVis;
	private DQPipelineElement periodVis;
	private MieList[] mieList;
	private ParticleSizeVisualizer sizeVisualizer;
	

	private MeasureSetUp() {
		preparePropertiesFile();
	}
	
	public static MeasureSetUp getInstance() {
		return myInstance;
	}
	
	private void preparePropertiesFile() {
		if (propertyFile.exists()) {
			try {
				BufferedInputStream is = new BufferedInputStream(new FileInputStream(propertyFile));
				load(is);
				is.close();
			} catch (IOException e) {
				// Property file not present
			}
		}
		
	}

	public File getMieFile() {
		return new File(getProperty(MeasureSetupEntry.MIEFILE.toString()));
	}

	public void setMieFile(File mieFile) {
		setProperty(MeasureSetupEntry.MIEFILE, mieFile.getAbsolutePath());
	}

	public File getOutputFile() {
		return new File(getProperty(MeasureSetupEntry.OUTPUTFILE.toString()));
	}

	public void setOutputFile(File outputFile) {
		setProperty(MeasureSetupEntry.OUTPUTFILE, outputFile.getAbsolutePath());
	}
	
	public String getProperty(MeasureSetupEntry entry) {
		return getProperty(entry.toString());
	}
	
	public void setProperty(MeasureSetupEntry entry, String value) {
		setProperty(entry.toString(), value);
		save();
	}
	
	public DQSinglePeriodMeasurementVisualizer getSinglePeriodVisualizer() {
		return singlePeriodVisualizer;
	}
	
	public void addSinglePeriodVisualizer(DQSinglePeriodMeasurementVisualizer vis) {
		singlePeriodVisualizer = vis;
	}
	
	public boolean save() {
		try {
			FileWriter fw = new FileWriter(propertyFile);
			DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
			store(fw, "File generated "+dateFormat.format(new Date()));
			fw.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public void addTransmissionVisualizer(TransmissionVisualizer transmissionVisualizer) {
		this.transmissionVis = transmissionVisualizer;
		
	}

	
	public TransmissionVisualizer getTransmissionVis() {
		return transmissionVis;
	}

	public void setMieList(MieList... list) {
		this.mieList = list;
	}
	
	public MieList getMieList(int index) {
		return mieList[index];
	}

	public void addParticleVisualizer(ParticleSizeVisualizer sizeVisualizer) {
		this.sizeVisualizer = sizeVisualizer;
		
	}

	public ParticleSizeVisualizer getSizeVisualizer() {
		return sizeVisualizer;
	}
	
	

}

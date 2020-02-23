package rzahoransky.gui.measureSetup;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import calculation.MieList;
import rzahoransky.dqpipeline.DQPipeline;
import rzahoransky.dqpipeline.dataExtraction.ConcentrationExtractor;
import rzahoransky.dqpipeline.dataExtraction.TransmissionExtractor;
import rzahoransky.dqpipeline.dataExtraction.rawDataExtraction.RawDataExtractorType;
import rzahoransky.dqpipeline.dataWriter.OutputWriter;
import rzahoransky.dqpipeline.interfaces.DQPipelineElement;
import rzahoransky.dqpipeline.periodMarker.MarkerType;
import rzahoransky.dqpipeline.visualization.DQSinglePeriodMeasurementVisualizer;
import rzahoransky.dqpipeline.visualization.ParticleSizeVisualizerChart;
import rzahoransky.dqpipeline.visualization.TransmissionVisualizer;
import rzahoransky.utils.DQTimer;

public class MeasureSetUp extends Properties{
	
	private static File propertyFile = new File("measureSetupProperties.properties");
	private static MeasureSetUp myInstance = new MeasureSetUp();
	private DQSinglePeriodMeasurementVisualizer singlePeriodVisualizer;
	private TransmissionVisualizer transmissionVis;
	private DQPipelineElement periodVis;
	private MieList[] mieList;
	private ParticleSizeVisualizerChart sizeVisualizer;
	private TransmissionExtractor transmissionExtractor;
	private ConcentrationExtractor concentrationExtractor;
	private DQPipeline pipeline;
	private boolean deviceIsConnected = false;
	private OutputWriter addOutputWriter;
	private DQTimer timer;
	private boolean pauseSignalWriting = false;
	

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
	
	public long getRefreshTime() {
		return Long.parseLong(getProperty(MeasureSetupEntry.OVERALL_REFRESH_RATE));
	}
	
	public void setRefreshTime(long refreshTime) {
		setProperty(MeasureSetupEntry.OVERALL_REFRESH_RATE, Long.toString(refreshTime));
	}

	public void setOutputFile(File outputFile) {
		setProperty(MeasureSetupEntry.OUTPUTFILE, outputFile.getAbsolutePath());
	}
	
	public String getProperty(MeasureSetupEntry entry) {
		String property = getProperty(entry.toString());
		if (property == null) { //fallback if no property file found
			switch (entry) {
			case AVERAGE_OVER_TIME:
				return Boolean.toString(false);
			case DEVICEWL1:
				return Double.toString(0.635);
			case DEVICEWL2:
				return Double.toString(0.818);
			case DEVICEWL3:
				return Double.toString(1.313);
			case MEASURELENGTH_IN_CM:
				return Integer.toString(1);
			case MIEFILE:
				return "";
			case NIADAPTER:
				return "Dev1";
			case OUTPUTFILE:
				return "";
			case STOREINTERVAL:
				return Integer.toString(0);
			case SAMPLES_PER_CHANNEL:
				return Integer.toString(6000);
			case MARKER_TYPE:
				return MarkerType.FiveWLMarker.toString();
			case RAW_DATA_EXTRACTOR:
				return RawDataExtractorType.FiveWlExtractor.toString();
			case NI_ADAPTER_CHANNEL:
				return "ai0:3";
			case OVERALL_REFRESH_RATE:
				return "30";
			case MEASUREMENT_DIFFERENCE_THRESHOLD:
				return "0.1";
			default:
				return Integer.toString(1);
			}
		}
		return property;
	}
	
	public double getMeasurementDifferenceThreshold() {
		String value = getProperty(MeasureSetupEntry.MEASUREMENT_DIFFERENCE_THRESHOLD);
		try {
			return Double.parseDouble(value);
		} catch (Exception e) {
			return 0.1;
		}
	}
	
	public void setMeasurementDifferenceThreshold(double threshold) {
		setProperty(MeasureSetupEntry.MEASUREMENT_DIFFERENCE_THRESHOLD, Double.toString(threshold));
	}
	
	
	public void setProperty(MeasureSetupEntry entry, String value) {
		setProperty(entry.toString(), value);
		save();
	}
	
	public void setStorageIntervall(double seconds) {
		setProperty(MeasureSetupEntry.STOREINTERVAL, Double.toString(seconds));
	}
	
	public double getStorageIntervall() {
		return Double.parseDouble(getProperty(MeasureSetupEntry.STOREINTERVAL));
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
	
	public MieList getMieListForWavelength(double wavelength) {
		for (MieList list: mieList) {
			if(list.getWavelength() == wavelength)
				return list;
		}
		
		return null;
	}

	public void addParticleVisualizer(ParticleSizeVisualizerChart sizeVisualizer2) {
		this.sizeVisualizer = sizeVisualizer2;
		
	}

	public ParticleSizeVisualizerChart getSizeVisualizer() {
		return sizeVisualizer;
	}

	public void addTransmissionExtractor(TransmissionExtractor transmissionExtractor) {
		this.transmissionExtractor = transmissionExtractor;
	}
	
	public TransmissionExtractor getTransmissionExtractor() {
		return this.transmissionExtractor;
	}
	
	public void setConcentrationExtractor(ConcentrationExtractor concentrationExtractor) {
		this.concentrationExtractor = concentrationExtractor;
	}
	
	public ConcentrationExtractor getConcentrationExtractor() {
		return concentrationExtractor;
	}

	public void setPipeline(DQPipeline pipeline) {
		this.pipeline = pipeline;
	}
	
	public DQPipeline getPipeline() {
		return this.pipeline;
	}

	public void setDeviceIsConnected(boolean b) {
		deviceIsConnected = b;
	}
	
	public boolean getDeviceIsConnected() {
		return deviceIsConnected;
	}

	public void addOutputWriter(OutputWriter outWriter) {
		this.addOutputWriter = outWriter;
	}
	
	public OutputWriter getOutputWriter() {
		return this.addOutputWriter;
	}

	public void setAverageOverTime(boolean averageOverTime) {
		setProperty(MeasureSetupEntry.AVERAGE_OVER_TIME, Boolean.toString(averageOverTime));
		
	}

	public void addTimer(DQTimer timer) {
		this.timer = timer;
	}
	
	public DQTimer getTimer() {
		return this.timer;
	}

	public boolean getPause() {
		return pauseSignalWriting ;
	}

	public void setPause(boolean b) {
		pauseSignalWriting = b;
		
	}
	

}

package rzahoransky.dqpipeline.analogueAdapter;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import com.sun.jna.Pointer;

import kirkwood.nidaq.access.NiDaq;
import kirkwood.nidaq.access.NiDaqException;
import kirkwood.nidaq.jna.Nicaiu;
import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.interfaces.AbstractDQPipelineElement;
import rzahoransky.dqpipeline.interfaces.AdapterInterface;
import rzahoransky.gui.measureSetup.MeasureSetUp;
import rzahoransky.gui.measureSetup.MeasureSetupEntry;

public class FiveWLNIDaqAdapter extends AbstractDQPipelineElement implements AdapterInterface {

	private String adCard = "Dev2";
	private NiDaq daq;
	private Pointer task;
	private DoubleBuffer db;
	private IntBuffer reads = IntBuffer.allocate(1);
	private double minVoltage = -7.0;
	private double maxVoltage = 7.1;
	private boolean isInitialized = false;
	private int samplesPerChannel = 6000; //6000
	protected boolean errorMessageShown = false;
	protected boolean doReset = true;
	
	//DEVICE WAVELENGTHS: 0.405, 0.532, 0.635, 0.635, 0.818, 1.313
	//ACCORDING TO SHIPPING LIST: 405, 532, 670, 850, 1305
	
	public static void main (String args[]) throws NiDaqException {
		String adCard = MeasureSetUp.getInstance().getProperty(MeasureSetupEntry.NIADAPTER);
		System.out.println("Creating NiDaq with device: "+adCard);
		FiveWLNIDaqAdapter test = new FiveWLNIDaqAdapter();
		DQSignal dqSample = test.getNextMeasurement();
	}

	public FiveWLNIDaqAdapter() {
		daq = new NiDaq();
		adCard = MeasureSetUp.getInstance().getProperty(MeasureSetupEntry.NIADAPTER);
		try {
			samplesPerChannel = Integer.parseInt(MeasureSetUp.getInstance().getProperty(MeasureSetupEntry.SAMPLES_PER_CHANNEL));
		} catch (Exception e) {
			samplesPerChannel = 6000;
			MeasureSetUp.getInstance().setProperty(MeasureSetupEntry.SAMPLES_PER_CHANNEL, Integer.toString(samplesPerChannel));
		}
		System.out.println("Samples: "+samplesPerChannel);
	}

	public void setSamplesPerChannel(int samples) {
		this.samplesPerChannel = samples;
		clearTask();
	}

	public void clearTask() {
		errorMessageShown=false;
		
		try {
			daq.stopTask(task);
		} catch (Exception e) {}
		
		try {
			daq.clearTask(task);
		} catch (Exception e) {
			// Only try to clear the task. It may be null. Ignore
		}
		isInitialized = false;
		daq = new NiDaq();
		sleep(60);
	}
	
	@Override
	public void endProcessing() {
		clearTask();
		reset();
	}

	@Override
	public String description() {
		return "NI-DAQ device for 5WL";
	}

	public DQSignal getNextMeasurement() throws NiDaqException {
		if (!isInitialized)
			initDAQ();

		if (isInitialized) {
			daq.readAnalogF64(task, samplesPerChannel, -1, Nicaiu.DAQmx_Val_GroupByChannel, db, db.capacity(), reads);
			if (checkDataAcquisition()) {
				return readMeasurement();
			}
		}
		return null;
	}

	private boolean checkDataAcquisition() {
		return reads.get(0) == samplesPerChannel;
	}
	
	protected void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e1) {}
	}

	public void initDAQ() {
		
//		sleep(60);
//		
//		try {
//			daq.clearTask(task);
//		} catch (Exception e) {
//			// ignore
//		}
		//reset();

		sleep(60);
		
		for (int tries = 1; tries>0;tries --) {

		try {
			daq = new NiDaq();
			task = daq.createTask("");
			sleep(30);

			daq.createAIVoltageChannel(task, adCard + "/" + "ai0:3", "", Nicaiu.DAQmx_Val_Cfg_Default, minVoltage,
					maxVoltage, Nicaiu.DAQmx_Val_Volts, null);

			daq.cfgSampClkTiming(task, "", 100000.0, Nicaiu.DAQmx_Val_Rising, Nicaiu.DAQmx_Val_ContSamps, samplesPerChannel);
			
			// init Buffer
			db = DoubleBuffer.allocate(samplesPerChannel * 4);
			daq.startTask(task);
			isInitialized = true;
			return;
		} catch (Exception e) {
			if (!errorMessageShown) {
				//JOptionPane.showMessageDialog(null, "Cannot read from device", "I/O error", JOptionPane.ERROR_MESSAGE);
				errorMessageShown = true;
			}
			e.printStackTrace();
			isInitialized = false;
			reset();
		}
		sleep(30);
		}
		
		
	}
	
	public void reset() {
		if (!doReset)
			return;
		try {
			System.out.println("Resetting "+adCard);
			daq.resetDevice(adCard);
			sleep(30);
		} catch (NiDaqException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private DQSignal readMeasurement() {
		// System.out.println("convertToRawData");
		int size = db.capacity();
		db.rewind();
		ArrayList<Double> reference = new ArrayList<>();
		ArrayList<Double> measurement = new ArrayList<>();
		ArrayList<Double> mode = new ArrayList<>();
		ArrayList<Double> trigger = new ArrayList<>();

		for (int i = 0; i < size / 4; i++)
			reference.add(db.get());

		for (int i = size / 4; i < size / 2; i++)
			measurement.add(db.get());

		// channel 3 is wavelength selector
		for (int i = size / 2; i < (3 * size) / 4; i++)
			mode.add(db.get());

		for (int i = (3 * size) / 4; i < size; i++)
			trigger.add(db.get());

		DQSignal current = new DQSignal(reference, measurement, mode, trigger);

		db.clear();
		return current;
	}

	@Override
	public void setADCardOrConfigParameter(String device) {
		if(!adCard.equals(device)) {
			adCard = device;
			isInitialized = false;
		}

	}

	@Override
	public DQSignal processDQElement(DQSignal in) {
		DQSignal measurement = null;
		for (int tries = 3; tries>0; tries--) {
			try {
				// System.out.println("Trying... "+System.currentTimeMillis());
				measurement = getNextMeasurement();
				if(measurement !=null)
					return measurement;

			} catch (NiDaqException e) {
				reset();
				e.printStackTrace();
			}
		}
		return null;
	}
}

package rzahoransky.dqpipeline.analogueAdapter;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

import javax.swing.JPanel;

import com.sun.jna.Pointer;

import kirkwood.nidaq.access.NiDaq;
import kirkwood.nidaq.access.NiDaqException;
import kirkwood.nidaq.jna.Nicaiu;
import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.interfaces.AbstractDQPipelineElement;
import rzahoransky.dqpipeline.interfaces.AdapterInterface;
import rzahoransky.gui.measureSetup.OldAdapterConfigPanel;

public class FiveWLNIDaqAdapter extends AbstractDQPipelineElement implements AdapterInterface {

	private String adCard = "Dev2";
	private static NiDaq daq;
	private static Pointer task;
	private static DoubleBuffer db;
	private static IntBuffer reads = IntBuffer.allocate(1);
	private double minVoltage = -7.0;
	private double maxVoltage = 7.1;
	private boolean isInitialized = false;
	private int samplesPerChannel = 6000;

	public FiveWLNIDaqAdapter() {

	}
	public void setSamplesPerChannel(int samples) {
		this.samplesPerChannel = samples;
		clearTask();
	}

	public void clearTask() {
		try {
			daq.clearTask(task);
		} catch (Exception e) {
			// Only try to clear the task. It may be null. Ignore
		}
		isInitialized = false;
	}

	@Override
	public String description() {
		return "NI-DAQ device for 5WL";
	}

	public DQSignal getNextMeasurement() throws NiDaqException {

		if (!isInitialized)
			initDAQ();
		

		daq.readAnalogF64(task, samplesPerChannel, -1, Nicaiu.DAQmx_Val_GroupByChannel, db, db.capacity(), reads);
		
		if (checkDataAcquisition()) {
			return readMeasurement();
		}

		return null;
	}
	
	private boolean checkDataAcquisition() {
		return reads.get(0)==samplesPerChannel;
	}

	public void initDAQ() throws NiDaqException {
		try {
			Thread.sleep(30);
		} catch (InterruptedException e1) {}

		try {
			daq.clearTask(task);
		} catch (Exception e) {
			// ignore
		}

		daq = new NiDaq();
		task = daq.createTask("");

		// reference channel
		daq.createAIVoltageChannel(task, adCard + "/" + "ai0:3", "", Nicaiu.DAQmx_Val_Cfg_Default, minVoltage,
				maxVoltage, Nicaiu.DAQmx_Val_Volts, null);
		/**
		 * 
		 * // measurement channel daq.createAIVoltageChannel(task, adCard + "/" +
		 * "ai1:ai1", "", Nicaiu.DAQmx_Val_Cfg_Default, minVoltage, maxVoltage,
		 * Nicaiu.DAQmx_Val_Volts, null);
		 * 
		 * // mode channel daq.createAIVoltageChannel(task, adCard + "/" + "ai2:ai2",
		 * "", Nicaiu.DAQmx_Val_Cfg_Default, minVoltage, maxVoltage,
		 * Nicaiu.DAQmx_Val_Volts, null);
		 * 
		 * // trigger channel daq.createAIVoltageChannel(task, adCard + "/" + "ai3:ai3",
		 * "", Nicaiu.DAQmx_Val_Cfg_Default, minVoltage, maxVoltage,
		 * Nicaiu.DAQmx_Val_Volts, null);
		 * 
		 **/
		//daq.cfgSampClkTiming(task, "", 100000.0, Nicaiu.DAQmx_Val_Rising, Nicaiu.DAQmx_Val_FiniteSamps, samplesPerChannel);
		daq.cfgSampClkTiming(task, "", 100000.0, Nicaiu.DAQmx_Val_Rising, Nicaiu.DAQmx_Val_ContSamps, samplesPerChannel);
		// init Buffer
		db = DoubleBuffer.allocate(samplesPerChannel * 4);
		daq.startTask(task);

		isInitialized = true;
	}
	
	private DQSignal readMeasurement() {
		//System.out.println("convertToRawData");
		int size = db.capacity();
		db.rewind();
		ArrayList<Double> reference = new ArrayList<>();
		ArrayList<Double> measurement = new ArrayList<>();
		ArrayList<Double> mode = new ArrayList<>();
		ArrayList<Double> trigger = new ArrayList<>();
		
		for (int i = 0; i<size/4;i++)
			reference.add(db.get());
		
		for (int i = size/4; i<size/2;i++)
			measurement.add(db.get());
		
		//channel 3 is wavelength selector
		for (int i = size/2;i<(3*size)/4;i++)
			mode.add(db.get());
		
		for (int i = (3*size)/4;i<size;i++)
			trigger.add(db.get());
		
		DQSignal current = new DQSignal(reference, measurement, mode, trigger);
		
		db.clear();
		return current;
	}
	@Override
	public void setADCardOrConfigParameter(String device) {
		adCard=device;
		isInitialized=false;
		
	}
	@Override
	public DQSignal processDQElement(DQSignal in) {
		try {
			DQSignal measurement = getNextMeasurement();
			return measurement;
			
		} catch (NiDaqException e) {
			e.printStackTrace();
			return null;
		}
	}
}

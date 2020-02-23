package rzahoransky.utils;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.List;

import rzahoransky.dqpipeline.DQPipeline;
import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.listener.DQSignalListener;

/**
 * Counts measurements per second. Registers as listener with DQPipeline to count measurements.
 * @author richard
 *
 */
public class DQTimer extends TimerTask implements DQSignalListener {
	
	int numberOfMeasurements = 0;
	int measurementsPerTimeUnit = 0;
	List<DQTimerListener> listeners = new LinkedList<>();
	
	public DQTimer(DQPipeline pipeline) {
		pipeline.addNewSignalListener(this);
		Timer timer = new Timer(true);
		timer.schedule(this, 1000, 1000);
	}
	
	public double getMeasurementsPerSecond() {
		return measurementsPerTimeUnit;
	}
	
	public double getMeasurementIntervallInMs() {
		return 1000d / getMeasurementsPerSecond();
	}
	


	@Override
	public void run() {
		//run as timer: Each timer the accumulated measurements will be set to zero again.
		measurementsPerTimeUnit = numberOfMeasurements;
		numberOfMeasurements = 0;
		//System.out.println("Number of Measurements per Second: "+measurementsPerTimeUnit +"(each "+1000d/measurementsPerTimeUnit+"ms)");
		informListeners();
	}

	private void informListeners() {
		for (DQTimerListener l: listeners)
			l.newTimeStatistics(this);
	}
	
	public void addTimeListener(DQTimerListener listener) {
		listeners.add(listener);
	}

	@Override
	public void newSignal(DQSignal currentSignal) {
		numberOfMeasurements++;
		
	}

	@Override
	public void closing() {
		System.out.println("Timer closing...");
		this.cancel();
	}
	
	public int getMeasurementsPerTimeUnit() {
		return measurementsPerTimeUnit;
	}
	

}

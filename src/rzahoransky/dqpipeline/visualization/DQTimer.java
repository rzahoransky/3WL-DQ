package rzahoransky.dqpipeline.visualization;

import java.util.Timer;
import java.util.TimerTask;

import rzahoransky.dqpipeline.DQPipeline;
import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.listener.DQSignalListener;
import rzahoransky.gui.measureSetup.MeasureSetUp;

public class DQTimer {
	TimeCounter counter;
	
	public DQTimer(DQPipeline pipeline) {
		counter = new TimeCounter();
		pipeline.addNewSignalListener(counter);
		Timer timer = new Timer(true);
		timer.schedule(counter, 1000, 1000);
	}
	
	public double getMeasurementsPerSecond() {
		return counter.measurementsPerTimeUnit;
	}
	

}

class TimeCounter extends TimerTask implements DQSignalListener{
	
	int numberOfMeasurements = 0;
	int measurementsPerTimeUnit = 0;

	@Override
	public void run() {
		measurementsPerTimeUnit = numberOfMeasurements;
		numberOfMeasurements = 0;
		//System.out.println("Number of Measurements per Second: "+measurementsPerTimeUnit);
		
	}

	@Override
	public void newSignal(DQSignal currentSignal) {
		numberOfMeasurements++;
		
	}

	@Override
	public void closing() {
		// nothing to do here
	}
	
	public int getMeasurementsPerTimeUnit() {
		return measurementsPerTimeUnit;
	}
	
}

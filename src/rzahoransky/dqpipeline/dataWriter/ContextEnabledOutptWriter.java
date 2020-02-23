package rzahoransky.dqpipeline.dataWriter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.gui.measureSetup.MeasureSetUp;
import rzahoransky.utils.DQListUtils;

public class ContextEnabledOutptWriter extends OutputWriter {
	
	protected DQSignal lastWrittenDQMeasurement = null;
	protected double threshold = MeasureSetUp.getInstance().getMeasurementDifferenceThreshold();

	public ContextEnabledOutptWriter(File file) {
		super(file);
	}
	
	@Override
	public DQSignal processDQElement(DQSignal in) {
		if (storageInterval == 0) {
			write(in);
		} 
		else { //integrate over time and react to sudden changes in measurement
			signals.add(in);
			if(in.getTimeStamp()-signals.get(0).getTimeStamp()>storageInterval*1000) {
				evaluateAndWriteMeasurements(signals);
				signals.clear();
			}
		}
		return in;
	}
	
	private void evaluateAndWriteMeasurements(ArrayList<DQSignal> measurements) {
		
		
		//get last saved element to not miss any change in measurements
		if (lastWrittenDQMeasurement != null)
			measurements.add(lastWrittenDQMeasurement);

		
		// evaluate if there is a rapid change in measurements.
		if (DQListUtils.containsDifferenceInMeasurement(measurements, threshold)) {
			// write individual measurements
			measurements.remove(lastWrittenDQMeasurement);
			for (DQSignal signal : measurements) {
				write(signal);
			}
		} else {
			//write average for this time period
			measurements.remove(lastWrittenDQMeasurement);
			write(DQListUtils.getAverageDQSignal(measurements));
		}
		lastWrittenDQMeasurement = measurements.get(measurements.size());
	}

}
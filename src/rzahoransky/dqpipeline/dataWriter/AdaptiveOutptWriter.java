package rzahoransky.dqpipeline.dataWriter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.utils.DQListUtils;
import rzahoransky.utils.MeasureSetUp;

/**
 * Extends {@link OutputWriter}. This writer behaves the same except that it will
 * store all measurement readings from an interval if a change in particle size or concentration
 * was detected
 * @author richard
 *
 */
public class AdaptiveOutptWriter extends OutputWriter {
	
	protected DQSignal lastWrittenDQMeasurement = null;
	protected double threshold = MeasureSetUp.getInstance().getMeasurementDifferenceThreshold();

	public AdaptiveOutptWriter(File file) {
		super(file);
		System.out.println("Enabled smart mode file writer with "+threshold+" threshold");
	}
	
	@Override
	public DQSignal processDQElement(DQSignal in) {
		if (storageInterval == 0) { //smart mode is enabled. However, no time intervall is set
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
		
		
		//append last saved element to not miss any change in measurements
		if (lastWrittenDQMeasurement != null)
			measurements.add(0, lastWrittenDQMeasurement);

		
		// evaluate if there is a rapid change in measurements.
		boolean measurementIsValid = DQListUtils.measurementsAreValid(measurements);
		boolean containsDifference = DQListUtils.containsDifferenceInMeasurement(measurements, threshold);
		boolean factorIsSet = MeasureSetUp.getInstance().getTransmissionExtractor().getFactorIsSet();
		if (factorIsSet && measurementIsValid && containsDifference) {
			// write individual measurements. Remove appended measurement
			measurements.remove(lastWrittenDQMeasurement);
			for (DQSignal signal : measurements) {
				write(signal);
			}
		} else {
			//write average for this time period
			measurements.remove(lastWrittenDQMeasurement);
			write(DQListUtils.getAverageDQSignal(measurements));
		}
		lastWrittenDQMeasurement = measurements.get(measurements.size()-1);
	}

}

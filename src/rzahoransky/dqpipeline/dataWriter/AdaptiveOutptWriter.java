package rzahoransky.dqpipeline.dataWriter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.utils.DQListUtils;
import rzahoransky.utils.properties.MeasureSetUp;
import rzahoransky.utils.properties.MeasureSetupChangeListener;
import rzahoransky.utils.properties.MeasureSetupEntry;

/**
 * Extends {@link OutputWriter}. This writer behaves the same except that it will
 * store all measurement readings from an interval if a change in particle size or concentration
 * was detected and the Property USE_SMART_MODE in {@link MeasureSetupEntry} is true.
 * @author richard
 *
 */
public class AdaptiveOutptWriter extends OutputWriter implements MeasureSetupChangeListener{
	
	protected DQSignal lastWrittenDQMeasurement = null;
	protected double threshold = MeasureSetUp.getInstance().getMeasurementDifferenceThreshold();
	protected static boolean smartMode = MeasureSetUp.getInstance().getSmartModeEnabled();

	public AdaptiveOutptWriter(File file) {
		super(file);
		MeasureSetUp.getInstance().addListener(this);
		System.out.println("Enabled smart mode file writer with "+threshold+" threshold");
	}
	
	@Override
	public DQSignal processDQElement(DQSignal in) {
		if (storageInterval == 0) { //smart mode is enabled. However, no time intervall is set
			write(in); //in this case: Just write all measurement individual
		} 
		else { //integrate over time and react to sudden changes in measurement
			signals.add(in);
			if(in.getTimeStamp()-signals.get(0).getTimeStamp()>storageInterval*1000) {
				evaluateAndWriteMeasurements(signals); //measurement interval reached. Evaluate signals list
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
		if (useSmartMode() && factorIsSet && measurementIsValid && containsDifference) {
			// write individual measurements. 
			//Remove appended measurement first to not store it twice
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
	//static synchronization monitor lock is on Class object of AdaptiveOutputWriter.
	//There ist only one Class object for this in the JVM. So this is synchronized
	//also with the method setSmartModeEnabled.
	protected static synchronized boolean useSmartMode() {
		return smartMode;
	}
	
	protected static synchronized void setSmartModeEnabled(boolean enabled) {
		smartMode = enabled;
	}
	

	@Override
	public void propertyChanged(MeasureSetupEntry changedProperty) {
		if (changedProperty.equals(MeasureSetupEntry.SMART_MODE_ENABLED)) {
			boolean isEnabled = MeasureSetUp.getInstance().getSmartModeEnabled();
			//System.out.println("Smart mode enabled?: "+isEnabled);
			smartMode = isEnabled;
		}
	}
}

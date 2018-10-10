package rzahoransky.dqpipeline.dqSignal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.OptionalDouble;

import rzahoransky.dqpipeline.dataExtraction.MeasurePointDescriptor;
import rzahoransky.dqpipeline.interfaces.IMeasurePoints;
import rzahoransky.utils.ExtractedSignalType;
import rzahoransky.utils.RawSignalType;


public class DQSignalSinglePeriod {
	
	HashMap<RawSignalType, List<Double>> singlePeriod = new HashMap<>(); //Signal type -> Signal
	HashMap<RawSignalType, DQSinglePeriodValues> measuredValues = new HashMap<>(); //Signal type -> extracted Raw Values
	
	
	public DQSignalSinglePeriod (DQSignal measurement, int start, int end) {
		for (RawSignalType type: RawSignalType.values()) {
			if(measurement.get(type).size()>=end) {
			 singlePeriod.put(type, measurement.get(type).subList(start, end));
			}
		}
	}
	
	public int getPeriodLength() {
		return singlePeriod.get(RawSignalType.ref).size();
	}
	
	public DQSinglePeriodValues getValues(RawSignalType type) {		
		return measuredValues.get(type);
	}
	
	public void add(RawSignalType refOrMeas, ExtractedSignalType wavelength, double value) {
		if(!measuredValues.containsKey(refOrMeas) || measuredValues.get(refOrMeas)==null) {
			measuredValues.put(refOrMeas, new DQSinglePeriodValues());
		}
		measuredValues.get(refOrMeas).add(wavelength, value);
	}
	
	public List<Double> getRawSignal(RawSignalType type) {
		return singlePeriod.get(type);
	}
	
	public String toString() {
		return measuredValues.get(RawSignalType.ref).toString()+" / "+measuredValues.get(RawSignalType.meas).toString();
	}
	
	public class DQSinglePeriodValues {
		HashMap<ExtractedSignalType, ArrayList<Double>> singleValues = new HashMap<>();
		
		public void add(ExtractedSignalType type, double value) {
			checkEntry(type);
			singleValues.get(type).add(value);
		}
		
		public ArrayList<Double> get(ExtractedSignalType type) {
			return singleValues.get(type);
		}
		
		private void checkEntry(ExtractedSignalType type) {
			if(singleValues.get(type)==null) {
				singleValues.put(type, new ArrayList<>());
			}
		}
		
		public String toString() {
			String result = "";
			for (ExtractedSignalType key: singleValues.keySet()) {
				result+=key+": "+getAverage(singleValues.get(key))+" ";
			}
			return result;
		}
		
		private double getAverage(ArrayList<Double> list) {
			OptionalDouble average = list
		            .stream()
		            .mapToDouble(a -> a)
		            .average();
			return average.isPresent() ? average.getAsDouble() : 0; 
		}
		
	}

	public void addExtractedValues(RawSignalType type, ExtractedSignalType wave, List<Double> extractValue) {
		for (Double d: extractValue) {
			add(type, wave, d);
		}
	}

}

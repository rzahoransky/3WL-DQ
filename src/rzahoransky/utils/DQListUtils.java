package rzahoransky.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalLong;

import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.dqSignal.DQSignalEntry;
import rzahoransky.dqpipeline.interfaces.DQPipelineElement;

public class DQListUtils {
	
	public static void main(String[] args) {
		ArrayList<Double> test = new ArrayList<>();
		Double[] numbers = {1d,2d,3d,4d, 5d};
		for (double n: numbers)
			test.add(n);
		System.out.println(test + "Avg: "+getAverage(test));
	}

	public DQListUtils() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * returns if the given list contains fractional differences greater than the
	 * limit. E.g. two measurements [100, 75] have a fractional difference of 0,33
	 * 
	 * @param measurements
	 *            the given measurements
	 * @param limit
	 *            the limit as fractional proportion (0.1 is 10%)
	 * @return true, if the given measurements contains differences so that
	 *         (max(measurements)-min(measurement))/min(measurement) > limit.
	 *         Diameter, concentration and variation are compared
	 */
	public static boolean containsDifferenceInMeasurement(List<DQSignal> measurements, double limit) {
		ArrayList<Double> diameters = new ArrayList<>(measurements.size());
		ArrayList<Double> concentration = new ArrayList<>(measurements.size());
		ArrayList<Double> variation = new ArrayList<>(measurements.size());

		for (DQSignal element : measurements) {
			diameters.add(element.getGeometricalDiameter());
			concentration.add(element.getVolumeConcentration());
			variation.add(element.getSigma());
		}
		Collections.sort(diameters);
		Collections.sort(concentration);
		Collections.sort(variation);

		if (Math.abs(getFractionalChange(diameters.get(0), diameters.get(diameters.size()-1))) > limit)
			return true;
		if (Math.abs(getFractionalChange(concentration.get(0), concentration.get(concentration.size()-1))) > limit)
			return true;
		if (Math.abs(getFractionalChange(variation.get(0), variation.get(variation.size()-1))) > limit)
			return true;
		

		return false;

	}
	
	public static double getFractionalChange(double min, double max) {
		return (max - min)/min;
	}
	
	public double getMin(List<Double> list) {
		double result = Double.MAX_VALUE;
		for (Double d: list) {
			if (d<result) {
				result = d;
			}
		}
		return result;
	}
	
	public double getMax(List<Double> list) {
		double result = Double.MIN_VALUE;
		for (Double d: list) {
			if (d>result) {
				result = d;
			}
		}
		return result;
	}
	
	public static double getAverage(List<Double> list) {
		OptionalDouble average = list
	            .stream()
	            .mapToDouble(a -> a)
	            .average();
		return average.isPresent() ? average.getAsDouble() : 0; 
	}
	
	public static long getAverageLong(List<Long> list) {
		if (list == null || list.isEmpty())
			return 0;
		long sum = 0;
		for (long l:list)
			sum+=l;
		return Math.floorDiv(sum, list.size());
	}
	
	public static DQSignal getAverageDQSignal(List<DQSignal> list) {
		DQSignal out = new DQSignal();
		int size = list.size();
		ArrayList<Long> nanoSeconds = new ArrayList<>(size);
		ArrayList<Long> timestamp = new ArrayList<>(size);
		ArrayList<Double> diameters = new ArrayList<>(size);
		ArrayList<Double> sigmas = new ArrayList<>(size);
		ArrayList<Double> numberConcentrations = new ArrayList<>(size);
		ArrayList<Double> dq1s = new ArrayList<>(size);
		ArrayList<Double> dq2s = new ArrayList<>(size);
		ArrayList<Double> TransmissionWL1s = new ArrayList<>(size);
		ArrayList<Double> TransmissionWL2s = new ArrayList<>(size);
		ArrayList<Double> TransmissionWL3s = new ArrayList<>(size);
		ArrayList<Double> MeasWOffset1s = new ArrayList<>(size);
		ArrayList<Double> MeasWOffset2s = new ArrayList<>(size);
		ArrayList<Double> MeasWOffset3s = new ArrayList<>(size);
		ArrayList<Double> MeasOffsets = new ArrayList<>(size);
		ArrayList<Double> RefWOffset1s = new ArrayList<>(size);
		ArrayList<Double> RefWOffset2s = new ArrayList<>(size);
		ArrayList<Double> RefWOffset3s = new ArrayList<>(size);
		ArrayList<Double> RefOffsets = new ArrayList<>(size);
		ArrayList<Double> lengths = new ArrayList<>(size);
		//ArrayList<Double> FactorWl1s = new ArrayList<>(size);
		//ArrayList<Double> FactorWl2s = new ArrayList<>(size);
		//ArrayList<Double> FactorWl3s = new ArrayList<>(size);

		
		for (DQSignal in: list) {
			nanoSeconds.add(in.getNanoSecondTimestamp());
			timestamp.add(in.getTimeStamp());
			diameters.add(in.getGeometricalDiameter());
			sigmas.add(in.getSigma());
			numberConcentrations.add(in.getNumberConcentration());
			dq1s.add(in.getDQ(DQtype.DQ1).getDqValue());
			dq2s.add(in.getDQ(DQtype.DQ2).getDqValue());
			TransmissionWL1s.add(in.getTransmission(TransmissionType.TRANSMISSIONWL1));
			TransmissionWL2s.add(in.getTransmission(TransmissionType.TRANSMISSIONWL2));
			TransmissionWL3s.add(in.getTransmission(TransmissionType.TRANSMISSIONWL3));
			MeasWOffset1s.add(in.getAveragedValues(RawSignalType.meas, ExtractedSignalType.wl1wOffset));
			MeasWOffset2s.add(in.getAveragedValues(RawSignalType.meas, ExtractedSignalType.wl2wOffset));
			MeasWOffset3s.add(in.getAveragedValues(RawSignalType.meas, ExtractedSignalType.wl3wOffset));
			MeasOffsets.add(in.getAveragedValues(RawSignalType.meas, ExtractedSignalType.offset));
			RefWOffset1s.add(in.getAveragedValues(RawSignalType.ref, ExtractedSignalType.wl1wOffset));
			RefWOffset2s.add(in.getAveragedValues(RawSignalType.ref, ExtractedSignalType.wl2wOffset));
			RefWOffset3s.add(in.getAveragedValues(RawSignalType.ref, ExtractedSignalType.wl3wOffset));
			RefOffsets.add(in.getAveragedValues(RawSignalType.ref, ExtractedSignalType.offset));
			lengths.add(in.getMeasureLength());
			//FactorWl1s.add(in.getFactor(TransmissionType.TRANSMISSIONWL1));
			//FactorWl2s.add(in.getFactor(TransmissionType.TRANSMISSIONWL2));
			//FactorWl3s.add(in.getFactor(TransmissionType.TRANSMISSIONWL3));
		}
		
		DQSignal last = list.get(list.size()-1);
		
		out.setNanoSecondTimestamp(getAverageLong(nanoSeconds));
		out.setTimestamp(getAverageLong(timestamp));
		out.setGeometricalDiameter(getAverage(diameters));
		out.setSigma(getAverage(sigmas));
		out.setNumberConcentration(getAverage(numberConcentrations));
		out.setDQ(new DQSignalEntry(DQtype.DQ1, list.get(0).getWL1(), list.get(0).getWL2(), getAverage(dq1s)));
		out.setDQ(new DQSignalEntry(DQtype.DQ2, list.get(0).getWL2(), list.get(0).getWL3(), getAverage(dq2s)));
		out.addTransmission(TransmissionType.TRANSMISSIONWL1, getAverage(TransmissionWL1s));
		out.addTransmission(TransmissionType.TRANSMISSIONWL2, getAverage(TransmissionWL2s));
		out.addTransmission(TransmissionType.TRANSMISSIONWL3, getAverage(TransmissionWL3s));
		out.setAveragedValue(RawSignalType.meas, ExtractedSignalType.wl1wOffset, getAverage(MeasWOffset1s));
		out.setAveragedValue(RawSignalType.meas, ExtractedSignalType.wl2wOffset, getAverage(MeasWOffset2s));
		out.setAveragedValue(RawSignalType.meas, ExtractedSignalType.wl3wOffset, getAverage(MeasWOffset3s));
		out.setAveragedValue(RawSignalType.meas, ExtractedSignalType.offset, getAverage(MeasOffsets));
		out.setAveragedValue(RawSignalType.ref, ExtractedSignalType.wl1wOffset, getAverage(RefWOffset1s));
		out.setAveragedValue(RawSignalType.ref, ExtractedSignalType.wl2wOffset, getAverage(RefWOffset2s));
		out.setAveragedValue(RawSignalType.ref, ExtractedSignalType.wl3wOffset, getAverage(RefWOffset3s));
		out.setAveragedValue(RawSignalType.ref, ExtractedSignalType.offset, getAverage(RefOffsets));
		out.setMeasureLength(getAverage(lengths));
		
		for (TransmissionType transmission : TransmissionType.values())
			out.setFactor(transmission, last.getFactor(transmission));
		
		out.setTimestamp(last.getTimeStamp());

		
		return out;
	}

	public static boolean measurementsAreValid(ArrayList<DQSignal> measurements) {
		long validReadings = 0;
		long invalidReadings = 0;
		for (DQSignal signal: measurements) {
			if (signal.isValid && signal.checkTransmission())
				validReadings++;
			else
				invalidReadings++;
		}
		if (validReadings > invalidReadings)
			return true;
		else
			return false;
	}

}


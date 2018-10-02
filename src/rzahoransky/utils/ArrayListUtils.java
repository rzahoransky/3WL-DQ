package rzahoransky.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.dqSignal.DQSignalEntry;
import rzahoransky.dqpipeline.dqSignal.DQSignalSinglePeriod;

public class ArrayListUtils {

	public ArrayListUtils() {
		// TODO Auto-generated constructor stub
	}
	
	public static double getAverage(List<Double> list) {
		OptionalDouble average = list
	            .stream()
	            .mapToDouble(a -> a)
	            .average();
		return average.isPresent() ? average.getAsDouble() : 0; 
	}
	
	public static DQSignal getAverageDQSignal(List<DQSignal> list) {
		DQSignal out = new DQSignal();
		int size = list.size();
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
		//ArrayList<Double> FactorWl1s = new ArrayList<>(size);
		//ArrayList<Double> FactorWl2s = new ArrayList<>(size);
		//ArrayList<Double> FactorWl3s = new ArrayList<>(size);

		
		for (DQSignal in: list) {
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
			//FactorWl1s.add(in.getFactor(TransmissionType.TRANSMISSIONWL1));
			//FactorWl2s.add(in.getFactor(TransmissionType.TRANSMISSIONWL2));
			//FactorWl3s.add(in.getFactor(TransmissionType.TRANSMISSIONWL3));
		}
		
		DQSignal last = list.get(list.size()-1);
		
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
		
		for (TransmissionType transmission : TransmissionType.values())
			out.setFactor(transmission, last.getFactor(transmission));
		
		out.setTimestamp(last.getTimeStamp());

		
		return out;
	}

}


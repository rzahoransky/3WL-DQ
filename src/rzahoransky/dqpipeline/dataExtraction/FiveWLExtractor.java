package rzahoransky.dqpipeline.dataExtraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.jfree.chart.util.RelativeDateFormat;

import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.dqSignal.DQSignalSinglePeriod;
import rzahoransky.dqpipeline.interfaces.AbstractDQPipelineElement;
import rzahoransky.dqpipeline.interfaces.IMeasurePoints;
import rzahoransky.utils.ExtractedSignalType;
import rzahoransky.utils.RawSignalType;

public class FiveWLExtractor extends AbstractDQPipelineElement {

	IMeasurePoints measurePoints;
	RawSignalType[] refMeas = { RawSignalType.ref, RawSignalType.meas };

	public FiveWLExtractor(IMeasurePoints measurePoints) {
		this.measurePoints = measurePoints;
	}

	@Override
	public DQSignal processDQElement(DQSignal in) {

		for (DQSignalSinglePeriod singlePeriod : in.getSinglePeriods()) {
			extractValues(singlePeriod);
		}
		
		getMode(in);
		
		
		
		//System.out.println("Extracted: "+element.getSinglePeriods().get(0).toString());

		return in;
	}

//	private void extractValues(DQSinglePeriodMeasurement singlePeriod) {
//		double refOffset = getOffset(singlePeriod, RawSignalType.ref);
//		double measOffset = getOffset(singlePeriod, RawSignalType.meas);
//		for (MeasurePointDescriptor descriptor : MeasurePointDescriptor.values()) { //wl1, wl2, wl3
//			if (!descriptor.equals(MeasurePointDescriptor.offset)) {
//				for (double measurePoint : measurePoints.getMeasurePoint(descriptor)) {
//					int periodLength = singlePeriod.getPeriodLength();
//					double refValue = singlePeriod.get(RawSignalType.ref).get((int) (periodLength * measurePoint))-refOffset;
//					double measValue = singlePeriod.get(RawSignalType.meas).get((int) (periodLength * measurePoint))-measOffset;
//
//					switch (descriptor) {
//					case wl1:
//						singlePeriod.add(ProcessedSignalType.wl1wOffset, refValue);
//						singlePeriod.add(ProcessedSignalType.wl1MeasureWithOffset, measValue);
//						break;
//					case wl2:
//						singlePeriod.add(ProcessedSignalType.wl2wOffset, refValue);
//						singlePeriod.add(ProcessedSignalType.wl2MeasureWithOffset, measValue);
//						break;
//					case wl3:
//						singlePeriod.add(ProcessedSignalType.wl3wOffset, refValue);
//						singlePeriod.add(ProcessedSignalType.wl3MeasureWithOffset, measValue);
//						break;
//					default:
//						break;
//					}
//
//				}
//			}
//		}
//
//	}
	
	private void getMode(DQSignal element) {
		double mode = element.get(RawSignalType.mode).get(0);
		if (mode < 5) {
			element.setWL1(0.405);
			element.setWL2(0.532);
			element.setWL3(0.635);
		} else {
			element.setWL1(0.635);
			element.setWL2(0.818);
			element.setWL3(1.313);
		}
		
	}

	private void extractValues(DQSignalSinglePeriod singlePeriod) {
		
		for (RawSignalType type: RawSignalType.values()) {
			switch (type) {
			case ref:
			case meas:
				for (ExtractedSignalType wave: ExtractedSignalType.values()) {
					switch (wave) {
					case wl1wOffset:
					case wl2wOffset:
					case wl3wOffset:
						singlePeriod.add(type, wave, extractValue(singlePeriod, type, wave));
						break;
					case offset:
						singlePeriod.add(type, wave, getOffset(singlePeriod, type));

					default:
						break;
					}
				}
				break;

			default:
				break;
			}
		}
		
	}
	
	private List<Double> extractValue(DQSignalSinglePeriod singlePeriod, RawSignalType refMeas, ExtractedSignalType wavelength) {
		ArrayList<Double> result = new ArrayList<>();
		double offset = getOffset(singlePeriod, refMeas);
		for (double measurePoint: measurePoints.getRelativeMeasurePoint(wavelength)) {
			List<Double> rawSignal = singlePeriod.getRawSignal(refMeas);
			int signalPeriodLength = singlePeriod.getPeriodLength();
			double signalValue = rawSignal.get((int) (measurePoint * signalPeriodLength)); //signal value without offset
			result.add(signalValue - offset); //signal value WITH OFFSET
		}
		return result;
		
	}
	
	private double getOffset(DQSignalSinglePeriod singlePeriod, RawSignalType type) {
		double[] measures = measurePoints.getRelativeMeasurePoint(ExtractedSignalType.offset);
		int numberOfMeasures = measures.length;
		double offsetValue = 0.0;
		for (double d: measures)
			offsetValue+=singlePeriod.getRawSignal(type).get((int) (d*singlePeriod.getPeriodLength()));
		
		return offsetValue/numberOfMeasures;
	}


//	private void readValues(DQSinglePeriodMeasurement singlePeriod) {
//		
//		for (MeasurePointDescriptor descriptor: MeasurePointDescriptor.values()) {
//			for (double d: measurePoints.getMeasurePoint(descriptor)) {
//				int periodLength = singlePeriod.getPeriodLength();
//				double measurePoint = periodLength * d;
//				singlePeriod.add(descriptor, singlePeriod.getRawMeasurement(type).get(1));
//			}
//		}
//		
//	}

	@Override
	public String description() {
		return "Reads raw voltages from 5WL device";
	}

}

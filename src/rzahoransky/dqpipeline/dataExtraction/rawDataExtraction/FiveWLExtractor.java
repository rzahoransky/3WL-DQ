package rzahoransky.dqpipeline.dataExtraction.rawDataExtraction;

import java.util.ArrayList;
import java.util.List;

import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.dqSignal.DQSignalSinglePeriod;
import rzahoransky.dqpipeline.interfaces.AbstractDQPipelineElement;
import rzahoransky.dqpipeline.interfaces.IMeasurePoints;
import rzahoransky.utils.ExtractedSignalType;
import rzahoransky.utils.RawSignalType;

public class FiveWLExtractor extends AbstractRawDataExtractor {

	
	RawSignalType[] refMeas = { RawSignalType.ref, RawSignalType.meas };
	private boolean useOffset = true;
	boolean visibleWavelengths = false;

	public FiveWLExtractor(IMeasurePoints measurePoints) {
		this.measurePoints = measurePoints;
	}

	@Override
	public DQSignal processDQElement(DQSignal in) {
		
		visibleWavelengths = isVisibleWavelengths(in);
		
		if(!in.isValid)
			return in;

		for (DQSignalSinglePeriod singlePeriod : in.getSinglePeriods()) {
			extractValues(singlePeriod);
		}
		
		
		
		
		//System.out.println("Extracted: "+element.getSinglePeriods().get(0).toString());

		return in;
	}


	private boolean isVisibleWavelengths(DQSignal element) {
		try {
			double mode = element.get(RawSignalType.mode).get(0);
			if (mode < 5) {
				element.setWL1(0.405); //blue
				element.setWL2(0.532); //green
				element.setWL3(0.670); //red
				return true; //for visible light: reverser Wavelength associatioen
			} else {
				element.setWL1(0.670);
				element.setWL2(0.850);
				element.setWL3(1.305);
				return false;
			}
		} catch (Exception e) {
			element.setWL1(0.670);
			element.setWL2(0.850);
			element.setWL3(1.305);
			return false;
		}
	}

	private void extractValues(DQSignalSinglePeriod singlePeriod) {
		
		for (RawSignalType refOrMeas: RawSignalType.values()) {
			switch (refOrMeas) {
			case ref: //no break
			case meas:
				for (ExtractedSignalType wavelength: ExtractedSignalType.values()) {
					switch (wavelength) {
					case wl1wOffset:
					case wl2wOffset:
					case wl3wOffset:
						singlePeriod.addExtractedValues(refOrMeas, wavelength, extractValue(singlePeriod, refOrMeas, wavelength));
						break;
					case offset:
						singlePeriod.add(refOrMeas, wavelength, getOffset(singlePeriod, refOrMeas));

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
	
	private List<Double> extractValue(DQSignalSinglePeriod singlePeriod, RawSignalType refOrMeas, ExtractedSignalType wavelength) {
		ArrayList<Double> result = new ArrayList<>();
		double offset = getOffset(singlePeriod, refOrMeas);
		for (double measurePoint: measurePoints.getRelativeMeasurePoint(wavelength, visibleWavelengths)) {
			List<Double> rawSignal = singlePeriod.getRawSignal(refOrMeas);
			int signalPeriodLength = singlePeriod.getPeriodLength();
			double signalValue = rawSignal.get((int) (measurePoint * signalPeriodLength)); //signal value without offset
			result.add(signalValue - offset); //signal value WITH OFFSET
		}
		return result;	
	}
	
	private double getOffset(DQSignalSinglePeriod singlePeriod, RawSignalType type) {
		
		if(!useOffset)
			return 0;
		
		double[] measures = measurePoints.getRelativeMeasurePoint(ExtractedSignalType.offset, visibleWavelengths);
		int numberOfMeasures = measures.length;
		double offsetValue = 0.0;
		for (double d: measures)
			offsetValue+=singlePeriod.getRawSignal(type).get((int) (d*singlePeriod.getPeriodLength()));
		
		return offsetValue/numberOfMeasures;
	}
	
	public void useOffset(boolean useOffset) {
		this.useOffset  = useOffset;
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

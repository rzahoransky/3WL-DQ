package rzahoransky.dqpipeline.dataExtraction.rawDataExtraction;

import java.util.ArrayList;
import java.util.List;

import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.dqSignal.DQSignalSinglePeriod;
import rzahoransky.dqpipeline.interfaces.AbstractDQPipelineElement;
import rzahoransky.dqpipeline.interfaces.IMeasurePoints;
import rzahoransky.gui.measureSetup.MeasureSetupEntry;
import rzahoransky.utils.ExtractedSignalType;
import rzahoransky.utils.MeasureSetUp;
import rzahoransky.utils.RawSignalType;

public class ThreeWLExtracor extends AbstractRawVoltageExtractor {
	
	protected boolean useOffset = true;
	static final double wl1 = Double.parseDouble(MeasureSetUp.getInstance().getProperty(MeasureSetupEntry.DEVICEWL1));
	static final double wl2 = Double.parseDouble(MeasureSetUp.getInstance().getProperty(MeasureSetupEntry.DEVICEWL2));
	static final double wl3 = Double.parseDouble(MeasureSetUp.getInstance().getProperty(MeasureSetupEntry.DEVICEWL3));
	
	public ThreeWLExtracor(IMeasurePoints measurePoints) {
		this.measurePoints = measurePoints;
	}

	@Override
	public String description() {
		return "Extract raw signal for 3-WL device";
	}

	@Override
	public DQSignal processDQElement(DQSignal in) {
		
		in.setWL1(wl1);
		in.setWL2(wl2);
		in.setWL3(wl3);

		if (!in.isValid)
			return in;

		for (DQSignalSinglePeriod singlePeriod : in.getSinglePeriods()) {
			extractValues(singlePeriod);
		}
		
		return in;

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
		for (double measurePoint: measurePoints.getRelativeMeasurePoint(wavelength)) {
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
		
		double[] measures = measurePoints.getRelativeMeasurePoint(ExtractedSignalType.offset);
		int numberOfMeasures = measures.length;
		double offsetValue = 0.0;
		for (double d: measures)
			offsetValue+=singlePeriod.getRawSignal(type).get((int) (d*singlePeriod.getPeriodLength()));
		
		return offsetValue/numberOfMeasures;
	}
}

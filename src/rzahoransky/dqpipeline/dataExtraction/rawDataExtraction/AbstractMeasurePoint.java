package rzahoransky.dqpipeline.dataExtraction.rawDataExtraction;

import java.util.HashMap;

import rzahoransky.dqpipeline.interfaces.IMeasurePoints;
import rzahoransky.utils.ExtractedSignalType;

public abstract class AbstractMeasurePoint implements IMeasurePoints {

	protected HashMap<ExtractedSignalType, double[]> map = new HashMap<>();

	@Override
	public double[] getRelativeMeasurePoint(ExtractedSignalType measureType, boolean visibleWavelengths) {
		if (visibleWavelengths) {
			switch (measureType) {
			case offset:
			case wl2wOffset:
				return map.get(measureType);
			case wl1wOffset:
				return map.get(ExtractedSignalType.wl3wOffset); // switch for visible laser diodes
			case wl3wOffset:
				return map.get(ExtractedSignalType.wl1wOffset); // switch for visible laser diodes
			default:
				return map.get(measureType);
			}

		} else {
			return map.get(measureType);
		}
	}
	
	@Override
	public double[] getRelativeMeasurePoint(ExtractedSignalType measureType) {
			return map.get(measureType);
	}

}

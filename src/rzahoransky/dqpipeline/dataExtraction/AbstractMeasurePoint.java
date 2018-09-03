package rzahoransky.dqpipeline.dataExtraction;

import java.util.HashMap;
import java.util.List;

import rzahoransky.dqpipeline.dqSignal.DQSignalSinglePeriod;
import rzahoransky.dqpipeline.interfaces.IMeasurePoints;
import rzahoransky.utils.ExtractedSignalType;

public abstract class AbstractMeasurePoint implements IMeasurePoints {
	
	protected HashMap<ExtractedSignalType, double[]> map = new HashMap<>();

	@Override
	public double[] getRelativeMeasurePoint(ExtractedSignalType measureType) {
		return map.get(measureType);
	}

}

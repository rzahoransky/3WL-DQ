package rzahoransky.dqpipeline.interfaces;

import rzahoransky.utils.ExtractedSignalType;

public interface IMeasurePoints {
	
	public double[] getRelativeMeasurePoint(ExtractedSignalType measureType, boolean visibleWavelengths);

}

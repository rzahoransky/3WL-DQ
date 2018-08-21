package rzahoransky.dqpipeline.dataExtraction;

import java.util.List;

import rzahoransky.dqpipeline.DQSignalSinglePeriod;
import rzahoransky.utils.ExtractedSignalType;

public interface IMeasurePoints {
	
	public double[] getRelativeMeasurePoint(ExtractedSignalType measureType);

}

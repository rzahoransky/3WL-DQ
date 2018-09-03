package rzahoransky.dqpipeline.interfaces;

import java.util.List;

import rzahoransky.dqpipeline.dqSignal.DQSignalSinglePeriod;
import rzahoransky.utils.ExtractedSignalType;

public interface IMeasurePoints {
	
	public double[] getRelativeMeasurePoint(ExtractedSignalType measureType);

}

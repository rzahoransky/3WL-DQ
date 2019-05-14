package rzahoransky.dqpipeline.dataExtraction.rawDataExtraction;

import rzahoransky.utils.ExtractedSignalType;

public class FiveWLMeasurePoints extends AbstractMeasurePoint {
	
	public FiveWLMeasurePoints() {
		double[] offset = {10d/85d, 11d/85d, 12d/85d, 13d/85d, 14d/85d};
//		double[] wl1 = {34d/85d, 35d/85d, 36d/85d};
//		double[] wl2 = {55d/85d, 56d/85d};
//		double[] wl3 = {73d/85d, 74d/85d, 75d/85d};
		
		double[] wl1 = {35d/85d};
		double[] wl2 = {55d/85d};
		double[] wl3 = {74d/85d};
		
		map.put(ExtractedSignalType.offset, offset);
		map.put(ExtractedSignalType.wl1wOffset, wl1);
		map.put(ExtractedSignalType.wl2wOffset, wl2);
		map.put(ExtractedSignalType.wl3wOffset, wl3);
	}

}

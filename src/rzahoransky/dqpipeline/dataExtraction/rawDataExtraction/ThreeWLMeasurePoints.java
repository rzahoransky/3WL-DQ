package rzahoransky.dqpipeline.dataExtraction.rawDataExtraction;

import rzahoransky.utils.ExtractedSignalType;

/**
 * contains measure points for three wl device
 * @author richard
 *
 */
public class ThreeWLMeasurePoints extends AbstractMeasurePoint {
	
	public ThreeWLMeasurePoints() {
		double[] offset = {52d/64d, 53d/64d, 54d/64d, 55d/64d, 56d/64d};
		double[] wl1 = {5d/64d, 6d/64d, 7d/64d};
		double[] wl2 = {20d/64d, 21d/64d, 22d/64d, 23d/64d};
		double[] wl3 = {37d/64d, 38d/64d, 39d/64d};
		
//		double[] wl1 = {35d/85d};
//		double[] wl2 = {55d/85d};
//		double[] wl3 = {74d/85d};
		
		map.put(ExtractedSignalType.offset, offset);
		map.put(ExtractedSignalType.wl1wOffset, wl1);
		map.put(ExtractedSignalType.wl2wOffset, wl2);
		map.put(ExtractedSignalType.wl3wOffset, wl3);
	}

}

package rzahoransky.dqpipeline.dataExtraction.rawDataExtraction;

import rzahoransky.dqpipeline.interfaces.AbstractDQPipelineElement;
import rzahoransky.gui.measureSetup.MeasureSetupEntry;
import rzahoransky.utils.MeasureSetUp;

public class RawVoltageExtractorFactory {
	
	public static AbstractRawDataExtractor getRawDataExtractor(RawDataExtractorType type) {
		switch (type) {
		case FiveWlExtractor:
			return new FiveWLExtractor(new FiveWLMeasurePoints());
		case ThreeWlExtractor:
			return new ThreeWLExtracor(new ThreeWLMeasurePoints());
		default:
			return new FiveWLExtractor(new FiveWLMeasurePoints());
		}
	}
	
	public static AbstractRawDataExtractor getRawVoltageExtractor() {
		try {
		String extractorType = MeasureSetUp.getInstance().getProperty(MeasureSetupEntry.RAW_DATA_EXTRACTOR);
		RawDataExtractorType rawExtractorToUse = RawDataExtractorType.valueOf(extractorType);
		System.out.println("Raw extractor type is: "+rawExtractorToUse.toString());
		return getRawDataExtractor(rawExtractorToUse);
		} catch (Exception e) {
			System.out.println("Raw extractor FALLBACK to "+RawDataExtractorType.FiveWlExtractor.toString());
			return new FiveWLExtractor(new FiveWLMeasurePoints());
		}
	}

}

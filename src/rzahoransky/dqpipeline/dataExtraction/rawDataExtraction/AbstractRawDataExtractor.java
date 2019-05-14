package rzahoransky.dqpipeline.dataExtraction.rawDataExtraction;

import rzahoransky.dqpipeline.interfaces.AbstractDQPipelineElement;
import rzahoransky.dqpipeline.interfaces.IMeasurePoints;

public abstract class AbstractRawDataExtractor extends AbstractDQPipelineElement {
	IMeasurePoints measurePoints;
	
	public IMeasurePoints getMeasurePoints() {
		return measurePoints;
	}

}

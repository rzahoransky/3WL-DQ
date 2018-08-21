package rzahoransky.dqpipeline.periodMarker;

import java.util.concurrent.BlockingQueue;

import rzahoransky.dqpipeline.AbstractDQPipelineElement;
import rzahoransky.dqpipeline.DQSignal;
import rzahoransky.dqpipeline.DQPipelineElement;
import rzahoransky.utils.RawSignalType;

public class FiveWLMarker extends AbstractDQPipelineElement {


	@Override
	public DQSignal processDQElement(DQSignal in) {
		DQSignal element = in;
		
		for (int i = 1;i<element.get(RawSignalType.trigger).size();i++) {
			double first = element.get(RawSignalType.trigger).get(i-1);
			double second = element.get(RawSignalType.trigger).get(i);
			
			if (second>3 && first<3) {
				element.addPeriodMark(i);
			}
		}
		return element;

	}

	@Override
	public String description() {
		return "Marks periods for 5WL device";
	}

}

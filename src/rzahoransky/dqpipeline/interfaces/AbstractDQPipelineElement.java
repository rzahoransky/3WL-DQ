package rzahoransky.dqpipeline.interfaces;

import rzahoransky.dqpipeline.dqSignal.DQSignal;

public abstract class AbstractDQPipelineElement implements DQPipelineElement {
	
	public String toString() {
		return description();
	}

	@Override
	public DQSignal processDQElement(DQSignal in) {
		if (in == null) {
			return null;
		}
		else
			try {
				return processDQElement(in);
			} catch (Exception e) {
				return null;
			}
	}
	
	@Override
	public void endProcessing() {
		
	}
	
	

}

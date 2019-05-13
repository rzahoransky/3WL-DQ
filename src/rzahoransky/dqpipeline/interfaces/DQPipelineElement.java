package rzahoransky.dqpipeline.interfaces;

import rzahoransky.dqpipeline.dqSignal.DQSignal;

public interface DQPipelineElement {
	
	public DQSignal processDQElement(DQSignal in);
	
	public String description();
	
	public void endProcessing();
	

}

package rzahoransky.dqpipeline.interfaces;

import rzahoransky.dqpipeline.DQPipeline;
import rzahoransky.dqpipeline.dqSignal.DQSignal;

public interface DQPipelineElement {
	
	/** Next {@link DQSignal} to process **/
	public DQSignal processDQElement(DQSignal in);
	
	/** Return a brief descriptions of this {@link DQPipelineElement}. **/
	public String description();
	
	/**
	 * Inform of immediate closing of the {@link DQPipeline}. All Elements must stop and terminate
	 */
	public void endProcessing();
	

}

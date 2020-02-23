package rzahoransky.dqpipeline.listener;

import rzahoransky.dqpipeline.dqSignal.DQSignal;

public interface DQSignalListener {
	
	/**
	 * A new {@link DQSignal} has been processed
	 * @param currentSignal
	 */
	public void newSignal(DQSignal currentSignal);
	
	/** inform the listener of the immediate closing and flushing of the pipeline.
	 * Time to close streams, etc...
	 * **/
	public void closing();

}

package rzahoransky.dqpipeline.listener;

import rzahoransky.dqpipeline.dqSignal.DQSignal;

public interface DQSignalListener {
	
	public void newSignal(DQSignal currentSignal);
	
	public void closing();

}

package rzahoransky.dqpipeline.listener;

import rzahoransky.dqpipeline.DQSignal;

public interface DQSignalListener {
	
	public void newSignal(DQSignal currentSignal);

}

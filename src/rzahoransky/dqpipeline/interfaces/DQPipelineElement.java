package rzahoransky.dqpipeline.interfaces;

import java.util.concurrent.BlockingQueue;

import javax.swing.JPanel;

import rzahoransky.dqpipeline.dqSignal.DQSignal;

public interface DQPipelineElement {
	
	public DQSignal processDQElement(DQSignal in);
	
	public String description();
	

}

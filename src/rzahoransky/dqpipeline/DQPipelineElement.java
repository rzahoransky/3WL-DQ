package rzahoransky.dqpipeline;

import java.util.concurrent.BlockingQueue;

import javax.swing.JPanel;

public interface DQPipelineElement {
	
	public DQSignal processDQElement(DQSignal in);
	
	public String description();
	

}

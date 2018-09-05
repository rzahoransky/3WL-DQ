package rzahoransky.dqpipeline.interfaces;

import java.util.concurrent.BlockingQueue;

import javax.swing.JPanel;

import rzahoransky.dqpipeline.dqSignal.DQSignal;

public abstract class AbstractDQPipelineElement implements DQPipelineElement {
	
	public String toString() {
		return description();
	}

	@Override
	public DQSignal processDQElement(DQSignal in) {
		if (in == null) {
			System.out.println("taken null");
			return null;
		}
		else
			try {
				return processDQElement(in);
			} catch (Exception e) {
				return null;
			}
			 //processDQElement(in);
	}
	
	

}
package rzahoransky.gui.adjustmentGui;

import rzahoransky.dqpipeline.interfaces.DQPipelineElement;
import rzahoransky.dqpipeline.listener.DQSignalListener;
import rzahoransky.utils.TransmissionType;

public interface AudioOutput extends DQSignalListener{

	public void setTransmissionType(TransmissionType type);
	
	public void close();


}

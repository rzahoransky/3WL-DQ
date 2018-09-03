package rzahoransky.dqpipeline.interfaces;


import javax.swing.JPanel;

import kirkwood.nidaq.access.NiDaqException;
import rzahoransky.dqpipeline.dqSignal.DQSignal;

public interface AdapterInterface extends DQPipelineElement{
	
	public void setADCardOrConfigParameter(String config);

}

package rzahoransky.dqpipeline.analogueAdapter;


import javax.swing.JPanel;

import kirkwood.nidaq.access.NiDaqException;
import rzahoransky.dqpipeline.DQSignal;
import rzahoransky.dqpipeline.DQPipelineElement;

public interface AdapterInterface extends DQPipelineElement{
	
	public void setADCardOrConfigParameter(String config);

}

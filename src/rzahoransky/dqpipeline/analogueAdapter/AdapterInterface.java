package rzahoransky.dqpipeline.analogueAdapter;


import javax.swing.JPanel;

import kirkwood.nidaq.access.NiDaqException;
import rzahoransky.dqpipeline.DQPipelineElement;
import rzahoransky.dqpipeline.DQSignal;

public interface AdapterInterface extends DQPipelineElement{
	
	public void setADCardOrConfigParameter(String config);

}

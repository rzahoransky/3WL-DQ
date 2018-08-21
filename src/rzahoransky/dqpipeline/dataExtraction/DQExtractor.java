package rzahoransky.dqpipeline.dataExtraction;

import java.util.HashMap;

import rzahoransky.dqpipeline.AbstractDQPipelineElement;
import rzahoransky.dqpipeline.DQSignal;
import rzahoransky.utils.TransmissionType;
import rzahoransky.utils.DQtype;
import rzahoransky.utils.RawSignalType;

public class DQExtractor extends AbstractDQPipelineElement {

	public DQExtractor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String description() {
		return "Calculates DQ values from transmission";
	}

	@Override
	public DQSignal processDQElement(DQSignal in) {
		//HashMap<MeasureValues, Double> transmissions = new HashMap<>();
		
		double transmissionWl1=in.getTransmission(TransmissionType.TRANSMISSIONWL1);
		double transmissionWl2=in.getTransmission(TransmissionType.TRANSMISSIONWL2);
		double transmissionWl3=in.getTransmission(TransmissionType.TRANSMISSIONWL3);
		
		
		double dq1 = Math.log(transmissionWl1/transmissionWl2);
		double dq2 = Math.log(transmissionWl2/transmissionWl3);
		
		in.setDQ(DQtype.DQ1, dq1);
		in.setDQ(DQtype.DQ2, dq2);
		
		return in;
	}
	
	

}

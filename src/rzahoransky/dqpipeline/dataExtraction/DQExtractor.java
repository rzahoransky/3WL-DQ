package rzahoransky.dqpipeline.dataExtraction;

import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.dqSignal.DQSignalEntry;
import rzahoransky.dqpipeline.interfaces.AbstractDQPipelineElement;
import rzahoransky.utils.DQtype;
import rzahoransky.utils.TransmissionType;

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
		// HashMap<MeasureValues, Double> transmissions = new HashMap<>();
		try {
			double transmissionWl1 = in.getTransmission(TransmissionType.TRANSMISSIONWL1);
			double transmissionWl2 = in.getTransmission(TransmissionType.TRANSMISSIONWL2);
			double transmissionWl3 = in.getTransmission(TransmissionType.TRANSMISSIONWL3);

			double dq1 = Math.log(transmissionWl1) / Math.log(transmissionWl2);
			double dq2 = Math.log(transmissionWl2) / Math.log(transmissionWl3);
			double dq3 = Math.log(transmissionWl1) / Math.log(transmissionWl3);

			DQSignalEntry dq1Entry = new DQSignalEntry(DQtype.DQ1, in.getWL1(), in.getWL2(), dq1);
			DQSignalEntry dq2Entry = new DQSignalEntry(DQtype.DQ2, in.getWL2(), in.getWL3(), dq2);
			DQSignalEntry dq3Entry = new DQSignalEntry(DQtype.DQ3, in.getWL1(), in.getWL3(), dq3);

			in.setDQ(dq1Entry);
			in.setDQ(dq2Entry);
			in.setDQ(dq3Entry);
		} catch (Exception e) {
			//e.printStackTrace();
		}

		return in;
	}

}

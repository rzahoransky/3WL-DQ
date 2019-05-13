package rzahoransky.dqpipeline.periodMarker;

import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.interfaces.AbstractDQPipelineElement;
import rzahoransky.utils.RawSignalType;

/**
 * Processes raw input from three wavelength device without trigger signal.
 * Sets period markers according to rising edge in reference signal.
 * All three laser diods must be turned ON for this period marker to work
 * @author richard
 *
 */
public class ThreeWLMarker extends AbstractDQPipelineElement {


	@Override
	public DQSignal processDQElement(DQSignal in) {
		DQSignal element = in;
		
		//search for 6V mark in reference signal
		for (int i = 1;i<element.get(RawSignalType.ref).size();i++) {
			double first = element.get(RawSignalType.ref).get(i-1);
			double second = element.get(RawSignalType.ref).get(i);
			
			if (second>6 && first<6) {
				element.addPeriodMark(i);
			}
		}
		element.isValid = !element.getPeriodMarker().isEmpty();
		return element;

	}

	@Override
	public String description() {
		return "Marks periods for 5WL device";
	}

}

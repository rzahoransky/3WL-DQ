package rzahoransky.dqpipeline.dataExtraction;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;

import presets.Wavelengths;
import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.dqSignal.DQSignalSinglePeriod;
import rzahoransky.dqpipeline.interfaces.AbstractDQPipelineElement;
import rzahoransky.utils.ExtractedSignalType;
import rzahoransky.utils.MeasureSetUp;
import rzahoransky.utils.RawSignalType;
import rzahoransky.utils.TransmissionType;

public class TransmissionExtractor extends AbstractDQPipelineElement {

	private boolean setI0 = true; //set factor if started
	private HashMap<ExtractedSignalType, Double> factors = new HashMap<>(); //factors for wl1, wl2, wl3
	private ExtractedSignalType[] wlSignals = { ExtractedSignalType.wl1wOffset, ExtractedSignalType.wl2wOffset,
			ExtractedSignalType.wl3wOffset };
	private boolean factorIsSet = false;
	private boolean useOffsetWL1 = MeasureSetUp.getInstance().getUseReference(Wavelengths.WL1);
	private boolean useOffsetWL2 = MeasureSetUp.getInstance().getUseReference(Wavelengths.WL2);
	private boolean useOffsetWL3 = MeasureSetUp.getInstance().getUseReference(Wavelengths.WL3);

	public TransmissionExtractor(boolean showAsFrame) {
		if (showAsFrame)
			showI0Button();
	}
	
	/**
	 * Change the factor from external (e.g. the factor GUI to jump back to a previous factor
	 * @param type which wavelength
	 * @param factor what is the factor for this wavelength
	 */
	public void setFactor(TransmissionType type, double factor) {
		switch (type) {
		case TRANSMISSIONWL1:
			factors.put(ExtractedSignalType.wl1wOffset, factor);
			break;
		case TRANSMISSIONWL2:
			factors.put(ExtractedSignalType.wl2wOffset, factor);
			break;
		case TRANSMISSIONWL3:
			factors.put(ExtractedSignalType.wl3wOffset, factor);
			break;
		default:
			break;
		}
	}

	//Factors are to be set from the next measurement
	@Override
	public DQSignal processDQElement(DQSignal in) {

		if (setI0) {
			setFactors(in); //new factor set request
			setI0 = false;
			factorIsSet  = true;
		}

		for (DQSignalSinglePeriod period : in.getSinglePeriods()) { //calculate each individual period
			getTransmission(period, in);
		}
		
		//save the factor into the measurement element
		in.setFactor(TransmissionType.TRANSMISSIONWL1, factors.get(ExtractedSignalType.wl1wOffset));
		in.setFactor(TransmissionType.TRANSMISSIONWL2, factors.get(ExtractedSignalType.wl2wOffset));
		in.setFactor(TransmissionType.TRANSMISSIONWL3, factors.get(ExtractedSignalType.wl3wOffset));

		return in;
	}

	private void getTransmission(DQSignalSinglePeriod period, DQSignal element) {
		for (ExtractedSignalType wl: wlSignals) { //get ref and meas for each wavelength individual.
			ArrayList<Double> ref = period.getValues(RawSignalType.ref).get(wl);
			ArrayList<Double> meas = period.getValues(RawSignalType.meas).get(wl);
			double transmissionValue;
			
			if (useReferenceFor(wl))
				transmissionValue = calculateSingleTransmissionWithReference(ref, meas, wl);
			else
				transmissionValue = calculateSingleTransmissionWithoutReference(ref, meas, wl);
				
			TransmissionType type;
			switch (wl) {
			case wl1wOffset:
				type = TransmissionType.TRANSMISSIONWL1;
				element.addTransmission(type, transmissionValue); //add the extracted transmission
				break;
			case wl2wOffset:
				type = TransmissionType.TRANSMISSIONWL2;
				element.addTransmission(type, transmissionValue); //add the extracted transmission
				break;
			case wl3wOffset:
				type = TransmissionType.TRANSMISSIONWL3;
				element.addTransmission(type, transmissionValue); //add the extracted transmission
				break;
			default:
				break;
			}
		}

		
		//period.getValues(RawSignalType.meas).get(ProcessedSignalType.wl1wOffset)
		
	}
	
	private boolean useReferenceFor(ExtractedSignalType wl) {
		switch (wl) {
		case offset:
			return false; //should never be reached
		case wl1wOffset:
			return useOffsetWL1;
		case wl2wOffset:
			return useOffsetWL2;
		case wl3wOffset:
			return useOffsetWL3;
		default:
			return true; //default: use reference for this wavelength (should never be reached)
		}
	}

	/**
	 * calculate the average transmission from given measurement. Use the reference signal
	 **/
	private double calculateSingleTransmissionWithReference(ArrayList<Double> ref, ArrayList<Double> meas, ExtractedSignalType wl) {
		double transmission = 0;
		for (int i = 0; i<ref.size(); i++) {
			double i0 = ref.get(i)*factors.get(wl); //use reference to calculate i0 for this measurement
			transmission += meas.get(i)/i0;
		}
		return transmission/ref.size();
	}
	
	/**
	 * calculate the average transmission from given measurement. Do NOT use the reference signal
	 **/
	private double calculateSingleTransmissionWithoutReference(ArrayList<Double> ref, ArrayList<Double> meas, ExtractedSignalType wl) {
		double transmission = 0;
		double i0 = factors.get(wl); //factor is the absolute io value
		for (int i = 0; i<ref.size(); i++) {
			transmission += meas.get(i)/i0; //same i0 for all measurement periods
		}
		return transmission/ref.size();
	}

	/**
	 * Extract the factor or the absolute transmission from the given element
	 * @param element the current measurement including reference and measurement
	 */
	private void setFactors(DQSignal element) {

		for (ExtractedSignalType wl : wlSignals) { //this is also computed for offset but ignored later
			double refValue = element.getAveragedValues(RawSignalType.ref, wl);
			double measValue = element.getAveragedValues(RawSignalType.meas, wl);
			
			if (useReferenceFor(wl))
				factors.put(wl, Math.abs(measValue / refValue)); //use reference to compute factor
			else
				factors.put(wl, Math.abs(measValue)); //use absolute value only as "factor"
			
			System.out.println("Setting factors for "+wl+" to "+Math.abs(measValue / refValue));
		}

	}

	@Override
	public String description() {
		return "reads extracted raw values and calculates transmission from reference detector";
	}

	public void setI0() {
		setI0 = true;
	}
	
	public JButton getI0Btn() {
		JButton iobtn = new JButton("set Io");
		iobtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setI0 = true;
				
			}
		});
		return iobtn;
	}
	
	public void showI0Button() {
		JFrame io = new JFrame("I0 control");
		io.setSize(200, 200);
		io.add(getI0Btn());
		io.setVisible(true);
	}
	
	public boolean getFactorIsSet() {
		return factorIsSet;
	}

}

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
import rzahoransky.utils.TransmissionType;
import rzahoransky.utils.RawSignalType;

public class TransmissionExtractor extends AbstractDQPipelineElement {

	private boolean setI0 = true;
	private HashMap<ExtractedSignalType, Double> factors = new HashMap<>();
	private ExtractedSignalType[] wlSignals = { ExtractedSignalType.wl1wOffset, ExtractedSignalType.wl2wOffset,
			ExtractedSignalType.wl3wOffset };

	public TransmissionExtractor(boolean showAsFrame) {
		if (showAsFrame)
			showI0Button();
	}

	@Override
	public DQSignal processDQElement(DQSignal in) {

		if (setI0) {
			setFactors(in);
			setI0 = false;
		}

		for (DQSignalSinglePeriod period : in.getSinglePeriods()) {
			getTransmission(period, in);
		}

		return in;
	}

	private void getTransmission(DQSignalSinglePeriod period, DQSignal element) {
		for (ExtractedSignalType wl: wlSignals) { //get ref and meas for each wavelength
			ArrayList<Double> ref = period.getValues(RawSignalType.ref).get(wl);
			ArrayList<Double> meas = period.getValues(RawSignalType.meas).get(wl);
			double transmissionValue = calculateSingleTransmission(ref, meas, wl);
			TransmissionType type;
			switch (wl) {
			case wl1wOffset:
				type = TransmissionType.TRANSMISSIONWL1;
				element.addTransmission(type, transmissionValue);
				break;
			case wl2wOffset:
				type = TransmissionType.TRANSMISSIONWL2;
				element.addTransmission(type, transmissionValue);
				break;
			case wl3wOffset:
				type = TransmissionType.TRANSMISSIONWL3;
				element.addTransmission(type, transmissionValue);
				break;
			default:
				break;
			}
		}

		
		//period.getValues(RawSignalType.meas).get(ProcessedSignalType.wl1wOffset)
		
	}

	private double calculateSingleTransmission(ArrayList<Double> ref, ArrayList<Double> meas, ExtractedSignalType wl) {
		double transmission = 0;
		for (int i = 0; i<ref.size(); i++) {
			double i0 = ref.get(i)*factors.get(wl);
			transmission += meas.get(i)/i0;
		}
		return transmission/ref.size();
		
	}

	private void setFactors(DQSignal element) {

		for (ExtractedSignalType wl : wlSignals) {
			double refValue = element.getAveragedValues(RawSignalType.ref, wl);
			double measValue = element.getAveragedValues(RawSignalType.meas, wl);
			factors.put(wl, measValue / refValue);
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

}

package rzahoransky.dqpipeline.dataExtraction;

import java.util.ArrayList;

import calculation.MieList;
import calculation.MieWrapper;
import dq.DQField;
import rzahoransky.dqpipeline.AbstractDQPipelineElement;
import rzahoransky.dqpipeline.DQSignal;
import rzahoransky.utils.ArrayListUtils;
import rzahoransky.utils.TransmissionType;

public class ConcentrationExtractor extends AbstractDQPipelineElement {
	
	double length;
	private MieList wl1;
	private MieList wl2;
	private MieList wl3;
	private DQField dq1;
	private DQField dq2;

	public ConcentrationExtractor(double measureLengthInCm, MieList wl1, MieList wl2, MieList wl3) {
		length = measureLengthInCm;
		this.wl1 = wl1;
		this.wl2 = wl2;
		this.wl3 = wl3;
		this.dq1 = new DQField(wl1, wl2);
		this.dq2 = new DQField(wl2, wl3);
	}
	
	public ConcentrationExtractor() {
		length = 1;
	}
	
	

	@Override
	public DQSignal processDQElement(DQSignal in) {
		in.setLength(length);
		
		double transmissionWl1 = in.getTransmission(TransmissionType.TRANSMISSIONWL1);
		double transmissionWl2 = in.getTransmission(TransmissionType.TRANSMISSIONWL2);
		double transmissionWl3 = in.getTransmission(TransmissionType.TRANSMISSIONWL3);
		
		ArrayList<Double> volumeConcentrations = new ArrayList<>();
		
		volumeConcentrations.add(getVolumeConcentration(transmissionWl1, in.getSigma(), wl1.getClosesElementForDiameter(in.getDiameter())));
		volumeConcentrations.add(getVolumeConcentration(transmissionWl2, in.getSigma(), wl2.getClosesElementForDiameter(in.getDiameter())));
		volumeConcentrations.add(getVolumeConcentration(transmissionWl3, in.getSigma(), wl3.getClosesElementForDiameter(in.getDiameter())));
		
		in.setVolumeConcentration(ArrayListUtils.getAverage(volumeConcentrations));
		
		return in;

	}



	private double getVolumeConcentration(double transmission, double sigma, MieWrapper element) {
		double n = (-1d) * Math.log(transmission) / ((length/100)*Math.PI*element.getIntegratedQext().get(sigma));
		return n;
	}

	@Override
	public String description() {
		return "Calculates particle concentration";
	}

}

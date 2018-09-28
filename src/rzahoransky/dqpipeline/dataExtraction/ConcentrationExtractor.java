package rzahoransky.dqpipeline.dataExtraction;

import java.util.ArrayList;

import calculation.MieList;
import calculation.MieWrapper;
import dq.ReverseDQ;
import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.interfaces.AbstractDQPipelineElement;
import rzahoransky.gui.measureSetup.MeasureSetUp;
import rzahoransky.gui.measureSetup.MeasureSetupEntry;
import rzahoransky.utils.ArrayListUtils;
import rzahoransky.utils.TransmissionType;

public class ConcentrationExtractor extends AbstractDQPipelineElement {
	
	double length;
	private MieList wl1;
	private MieList wl2;
	private MieList wl3;
	private ReverseDQ dq1;
	private ReverseDQ dq2;

	public ConcentrationExtractor(MieList wl1, MieList wl2, MieList wl3) {
		length = Double.parseDouble(MeasureSetUp.getInstance().getProperty(MeasureSetupEntry.MEASURELENGTH_IN_CM));
		this.wl1 = wl1;
		this.wl2 = wl2;
		this.wl3 = wl3;
		this.dq1 = new ReverseDQ(wl1, wl2);
		this.dq2 = new ReverseDQ(wl2, wl3);
	}
	
	@Override
	public DQSignal processDQElement(DQSignal in) {
		try {
		in.setLength(length);
		
		double transmissionWl1 = in.getTransmission(TransmissionType.TRANSMISSIONWL1);
		double transmissionWl2 = in.getTransmission(TransmissionType.TRANSMISSIONWL2);
		double transmissionWl3 = in.getTransmission(TransmissionType.TRANSMISSIONWL3);
		
		ArrayList<Double> numberConcentration = new ArrayList<>();
		
		numberConcentration.add(getParticleConcentration(transmissionWl1, in.getSigma(), wl1.getClosesElementForDiameter(in.getGeometricalDiameter())));
		numberConcentration.add(getParticleConcentration(transmissionWl2, in.getSigma(), wl2.getClosesElementForDiameter(in.getGeometricalDiameter())));
		numberConcentration.add(getParticleConcentration(transmissionWl3, in.getSigma(), wl3.getClosesElementForDiameter(in.getGeometricalDiameter())));
		
		
		in.setNumberConcentration(ArrayListUtils.getAverage(numberConcentration));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return in;

	}



	/** concentration for m³. All to convert to micrometer **/
	private double getParticleConcentration(double transmission, double sigma, MieWrapper element) {
		double n = (-1d) * Math.log(transmission) / ((length*10000)*(Math.PI/4)*element.getIntegratedQext().get(sigma));
		return n*Math.pow(10, 18);
	}

	@Override
	public String description() {
		return "Calculates particle concentration";
	}

}

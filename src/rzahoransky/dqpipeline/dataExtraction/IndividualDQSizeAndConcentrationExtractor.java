package rzahoransky.dqpipeline.dataExtraction;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.event.CellEditorListener;

import java.util.TreeMap;

import calculation.MieList;
import calculation.MieWrapper;
import dq.ReverseDQ;
import dq.ReverseDQEntry;
import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.dqSignal.RadiusConcentrationElement;
import rzahoransky.dqpipeline.interfaces.AbstractDQPipelineElement;
import rzahoransky.utils.DQtype;
import rzahoransky.utils.TransmissionType;
import rzahoransky.utils.properties.MeasureSetUp;
import rzahoransky.utils.properties.MeasureSetupEntry;

public class IndividualDQSizeAndConcentrationExtractor extends AbstractDQPipelineElement{
	
	private HashMap<DQtype, DQTree> dqs = new HashMap<>();
	MieList wl1;
	MieList wl2;
	MieList wl3;
	double lowestSigma;
	double lengthInCm = Double.parseDouble(MeasureSetUp.getInstance().getProperty(MeasureSetupEntry.MEASURELENGTH_IN_CM));
	

	public IndividualDQSizeAndConcentrationExtractor(MieList wl1, MieList wl2, MieList wl3) {
		
		dqs.put(DQtype.DQ1, new DQTree(wl1, wl2, DQtype.DQ1));
		dqs.put(DQtype.DQ2, new DQTree(wl2, wl3, DQtype.DQ2));
		dqs.put(DQtype.DQ3, new DQTree(wl1, wl3, DQtype.DQ3));
		
		this.wl1 = wl1;
		this.wl2 = wl2;
		this.wl3 = wl3;
		lowestSigma = wl1.get(0).getSortedSigmas().get(0); //get the smalles sigma. It will be used to calculate particle concentration
	}

	@Override
	public DQSignal processDQElement(DQSignal in) {
		for (DQtype dqType: dqs.keySet()) { //for each possible dq
			double measuredDQ = in.getDQ(dqType).getDqValue();
			double possibleParticleRadius = dqs.get(dqType).getDQHit(measuredDQ);
			RadiusConcentrationElement particleProperties = new RadiusConcentrationElement();
			particleProperties.radius = possibleParticleRadius;
			double concentration = 0.0;
			concentration += getParticleConcentration(in.getTransmission(TransmissionType.TRANSMISSIONWL1), lowestSigma, wl1.getClosestElementForDiameter(possibleParticleRadius));
			concentration += getParticleConcentration(in.getTransmission(TransmissionType.TRANSMISSIONWL2), lowestSigma, wl2.getClosestElementForDiameter(possibleParticleRadius));
			concentration += getParticleConcentration(in.getTransmission(TransmissionType.TRANSMISSIONWL3), lowestSigma, wl3.getClosestElementForDiameter(possibleParticleRadius));
			concentration = concentration / 3.0;
			particleProperties.concentrationPerm3 = concentration;
			in.setParticlePropertiesForDQ(dqType, particleProperties);
		}
		return in;
	}
	

	@Override
	public String description() {
		return "Extract possible particle sizes for each DQ";
	}
	
	/** concentration for m³. All to convert to micrometer **/
	private double getParticleConcentration(double transmission, double sigma, MieWrapper element) {
		//element.getIntegratedQext contains r²*Qext already!
		//1cm = 10000 micrometer
		//System.out.println("Qext for "+element.getWavelength()+": "+element.getIntegratedQext().get(sigma)+"Radius: "+element.getRadius());
		double n = (-1d) * Math.log(transmission) / ((lengthInCm*10000)*(Math.PI)*element.getIntegratedQext().get(sigma));
		//old: the term included (Math.PI/4) to compensate for diameter instead of radius
		return n*Math.pow(10, 18); //um³ to m³ conversion
	}
	
	class DQTree {
		DQtype dqType;
		TreeMap<Double, Double> dqToParticleSize = new TreeMap<>(); //DQ-Value / Particle Radius
		
		public DQTree(MieList wl1, MieList wl2, DQtype dqType) {
			this.dqType = dqType;
			double lowestSigma = wl1.get(0).getSortedSigmas().get(0);
			for (int i = 0; i<wl1.size(); i++) {
				//use first sigma only
				double dq = wl1.get(i).getIntegratedQext().get(lowestSigma) / wl2.get(i).getIntegratedQext().get(lowestSigma);
				double radius = wl1.get(i).getRadius();
				dqToParticleSize.put(dq, radius);
			}
		}
		
		/** returns the possible particle radius for the given DQ **/
		public double getDQHit(double dq) {
			Entry<Double, Double> floor = dqToParticleSize.floorEntry(dq);
			Entry<Double, Double> celing = dqToParticleSize.ceilingEntry(dq);
			
			if(floor == null || celing == null) {
				return 0.0; //none found
			}
			
			if (Math.abs(floor.getKey()-dq) <= Math.abs(celing.getKey()-dq)) {
				return floor.getValue();
			} else {
				return celing.getValue();
			}
		}
	}

}

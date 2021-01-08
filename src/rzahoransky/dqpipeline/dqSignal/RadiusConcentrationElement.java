package rzahoransky.dqpipeline.dqSignal;

public class RadiusConcentrationElement {
	public double concentrationPerm3;
	public double radius;
	
	public RadiusConcentrationElement(double radius, double concentration) {
		this.radius = radius;
		this.concentrationPerm3 = concentration;
	}
	
	public RadiusConcentrationElement() {
		
	}
	
	public double getVolumeConcentration() {
		return concentrationPerm3*(Math.PI/6)*Math.pow((radius*2)/(1e6),3);
	}

}

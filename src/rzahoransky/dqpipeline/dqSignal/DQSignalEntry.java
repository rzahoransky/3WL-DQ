package rzahoransky.dqpipeline.dqSignal;

import rzahoransky.utils.DQtype;

public class DQSignalEntry {
	
	DQtype type;
	double wl1;
	double wl2;
	double dqValue;

	public DQSignalEntry(DQtype type, double wl1, double wl2, double dqValue) {
		this.type=type;
		this.wl1=wl1;
		this.wl2=wl2;
		this.dqValue=dqValue;
	}
	
	public DQtype getType() {
		return type;
	}

	public void setType(DQtype type) {
		this.type = type;
	}

	public double getWl1() {
		return wl1;
	}

	public void setWl1(double wl1) {
		this.wl1 = wl1;
	}

	public double getWl2() {
		return wl2;
	}

	public void setWl2(double wl2) {
		this.wl2 = wl2;
	}

	public double getDqValue() {
		return dqValue;
	}

	public void setDqValue(double dqValue) {
		this.dqValue = dqValue;
	}

}

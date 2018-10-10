package rzahoransky.dqpipeline.dataExtraction;

import javax.swing.JOptionPane;

import calculation.MieList;
import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.interfaces.AbstractDQPipelineElement;
import rzahoransky.dqpipeline.interfaces.DQPipelineElement;
import rzahoransky.gui.measureSetup.MeasureSetUp;
import rzahoransky.utils.DQtype;

public class SimpleDQLookupDiameterExtractor extends AbstractDQPipelineElement {
	
	MeasureSetUp setup = MeasureSetUp.getInstance();
	SimpleDQLookup lookup = new SimpleDQLookup(setup.getMieList(0), setup.getMieList(1), setup.getMieList(2));
	boolean warningShown = false;
	private MieList wl1;
	private MieList wl2;
	private MieList wl3;

	public SimpleDQLookupDiameterExtractor() {
		// TODO Auto-generated constructor stub
	}
	
	public SimpleDQLookupDiameterExtractor(MieList wl1, MieList wl2, MieList wl3) {
		lookup = new SimpleDQLookup(wl1, wl2, wl3);
		this.wl1 = wl1;
		this.wl2 = wl2;
		this.wl3 = wl3;
	}

	@Override
	public DQSignal processDQElement(DQSignal in) {
		if (!warningShown)
			checkWavelengths(in);
		try {
			double now = System.currentTimeMillis();
			double diameter = lookup.getDiameterFor(in.getDQ(DQtype.DQ1).getDqValue(), in.getDQ(DQtype.DQ2).getDqValue());
			double sigma = lookup.getSigmaFor(in.getDQ(DQtype.DQ1).getDqValue(), in.getDQ(DQtype.DQ2).getDqValue());
			//System.out.println("SimpleLookup: d: "+diameter+" sigma: "+sigma+" time: "+Double.toString(System.currentTimeMillis()-now));
			in.setSigma(sigma);
			in.setGeometricalDiameter(diameter);
		} catch (Exception e) {
			
		}
		
		return in;
	}

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return "Calculated Diameter based on single 2-point DQ Entry";
	}
	
	private void checkWavelengths(DQSignal in) {
		if (Math.abs(in.getWL1() - wl1.getWavelength()) > 0.01 || Math.abs(in.getWL2() - wl2.getWavelength()) > 0.01
				|| Math.abs(in.getWL3() - wl3.getWavelength()) > 0.01) {
			JOptionPane.showMessageDialog(null, "Mie File wavelength does not match the current Measurement Device: \r\n"
					+ "Device: "+in.getWL1()+", "+in.getWL2()+", "+ in.getWL3()+
					". MIE-File: "+wl1.getWavelength()+", "+wl2.getWavelength()+", "+wl3.getWavelength());
		}
		warningShown = true;

	}

}

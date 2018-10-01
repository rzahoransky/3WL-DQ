package rzahoransky.dqpipeline.dataExtraction;

import calculation.MieList;
import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.interfaces.DQPipelineElement;
import rzahoransky.gui.measureSetup.MeasureSetUp;
import rzahoransky.utils.DQtype;

public class SimpleDQLookupDiameterExtractor implements DQPipelineElement {
	
	MeasureSetUp setup = MeasureSetUp.getInstance();
	SimpleDQLookup lookup = new SimpleDQLookup(setup.getMieList(0), setup.getMieList(1), setup.getMieList(2));

	public SimpleDQLookupDiameterExtractor() {
		// TODO Auto-generated constructor stub
	}
	
	public SimpleDQLookupDiameterExtractor(MieList wl1, MieList wl2, MieList wl3) {
		lookup = new SimpleDQLookup(wl1, wl2, wl3);
	}

	@Override
	public DQSignal processDQElement(DQSignal in) {
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

}

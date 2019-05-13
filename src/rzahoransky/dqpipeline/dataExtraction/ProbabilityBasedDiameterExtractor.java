package rzahoransky.dqpipeline.dataExtraction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import calculation.MieList;
import dq.ReverseDQ;
import dq.ReverseDQEntry;
import errors.WavelengthMismatchException;
import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.dqSignal.DQSignalEntry;
import rzahoransky.dqpipeline.interfaces.AbstractDQPipelineElement;
import rzahoransky.utils.DQtype;
import storage.dqMeas.read.DQReader;

public class ProbabilityBasedDiameterExtractor extends AbstractDQPipelineElement {

	private MieList wl1;
	private MieList wl3;
	private MieList wl2;
	private HashMap<DQtype, ReverseDQ> dqs = new HashMap<>();
	boolean wavelengthCheck = true;

	public static void main(String[] args) {
		double dq1 = 3;
		double dq2 = 4;

		ProbabilityBasedDiameterExtractor extractor = new ProbabilityBasedDiameterExtractor(new File("D:/mietemp/2018-08-08.miezip"));
		DQSignal in = new DQSignal();
		in.setDQ(new DQSignalEntry(DQtype.DQ1, 1, 2, 1.8));
		in.setDQ(new DQSignalEntry(DQtype.DQ2, 1, 2, 3.4));
		in.setDQ(new DQSignalEntry(DQtype.DQ3, 1, 2, 6.12));
		extractor.processDQElement(in);
	}

	public ProbabilityBasedDiameterExtractor(MieList wl1, MieList wl2, MieList wl3) {
		this.wl1 = wl1;
		this.wl2 = wl2;
		this.wl3 = wl3;
		dqs.put(DQtype.DQ1, new ReverseDQ(wl1, wl2));
		dqs.put(DQtype.DQ2, new ReverseDQ(wl2, wl3));
		//dqs.put(DQtype.DQ3, new ReverseDQ(wl1, wl3));
	}

	public ProbabilityBasedDiameterExtractor(File zippedMie) {
		DQReader reader;
		try {
			reader = new DQReader(zippedMie);
			this.wl1 = reader.getWl1();
			this.wl2 = reader.getWl2();
			this.wl3 = reader.getWl3();
			dqs.put(DQtype.DQ1, new ReverseDQ(wl1, wl2));
			dqs.put(DQtype.DQ2, new ReverseDQ(wl2, wl3));
			//dqs.put(DQtype.DQ3, new ReverseDQ(wl1, wl3));

		} catch (IOException | WavelengthMismatchException e) {
			e.printStackTrace();
		}

	}

	@Override
	public DQSignal processDQElement(DQSignal in) {
		if (wavelengthCheck) {
			checkWavelengths(in);
			wavelengthCheck = false;
		}
		//double now = System.currentTimeMillis();

		try {
			extractDiameterAndSigma(in);
			//System.out.println("Probability Lookup: d: "+in.getDiameter().getAverageDiameter()+" sigma: "+in.getSigma()+" time: "+Double.toString(System.currentTimeMillis()-now));
		} catch (Exception e) {
			//e.printStackTrace();
		}
		


		return in;
	}

	public void extractDiameterAndSigma(DQSignal signal) {
		HashMap<DQtype, List<ReverseDQEntry>> dqHits = new HashMap<>();

		for (DQtype dqType : dqs.keySet()) {
			dqHits.put(dqType, getDQHits(signal.getDQ(dqType))); // get all possible diameter matches from dqs
		}

		ArrayList<DiameterComperator> comperators = new ArrayList<>();

		DQtype mostHits = getBiggestList(dqHits); // get the DQ with the most particle diameter matches as starting
													// point

		for (ReverseDQEntry entry : dqHits.get(mostHits)) {
			comperators.add(new DiameterComperator(entry)); // take every single possible diameter as starting point
		}

		//LinkedList<DQtype> remainingDQs = new LinkedList<>(Arrays.asList(dqs.keySet()));
		Set<DQtype> remainingDQs = new HashSet<>(dqs.keySet());
		remainingDQs.remove(mostHits);

		for (DQtype remaining : remainingDQs) {
			for (DiameterComperator comperator : comperators) {
				//comperator.filterForBestMatchWithAbsoluteDistance(dqHits.get(remaining)); // match best diameters from remaining DQs
				comperator.filterForBestMatchWithProbabilityFunction(dqHits.get(remaining));
			}
		}

		DiameterComperator result = getBestMatchingComperatorElement(comperators);
		signal.setGeometricalDiameter(result);
		signal.setSigma(result.getSigma());
	}

	private DiameterComperator getBestMatchingComperatorElement(ArrayList<DiameterComperator> comperators) {
		LinkedList<DiameterComperator> result = new LinkedList<>();
		for (DiameterComperator comp : comperators) { //filkter for null elements and element with only one match
			if (comp != null && comp.getSize() > 1)
				result.add(comp);
		}
		Collections.sort(result);
		return result.get(0);

	}

	private DQtype getBiggestList(HashMap<DQtype, List<ReverseDQEntry>> dqHits) {
		DQtype result = null;
		for (DQtype type : dqHits.keySet()) {
			if (result == null || dqHits.get(result).size() < dqHits.get(type).size())
				result = type;
		}
		return result;
	}

	private HashSet<Double> getSigmas(HashMap<DQtype, List<ReverseDQEntry>> dqHits) {
		HashSet<Double> set = new HashSet<>();
		for (DQtype type : dqHits.keySet()) {
			for (ReverseDQEntry entry : dqHits.get(type)) {
				set.add(entry.getSigma());
			}
		}
		return set;
	}

	private List<ReverseDQEntry> getDQHits(DQSignalEntry dq) {
		return dqs.get(dq.getType()).getDQHits(dq.getDqValue());
	}

	private void checkWavelengths(DQSignal in) {
		if (Math.abs(in.getWL1() - wl1.getWavelength()) > 0.01 || Math.abs(in.getWL2() - wl2.getWavelength()) > 0.01
				|| Math.abs(in.getWL3() - wl3.getWavelength()) > 0.01) {
			JOptionPane.showMessageDialog(null, "Mie File wavelength does not match the current Measurement Device: \r\n"
					+ "Device: "+in.getWL1()+", "+in.getWL2()+", "+ in.getWL3()+
					". MIE-File: "+wl1.getWavelength()+", "+wl2.getWavelength()+", "+wl3.getWavelength());
		}

	}

	@Override
	public String description() {
		return "Reads particles sizes from DQ values and MIE field";
	}

	private class SigmaSize {
		double sigma;
		double diameter;
	}

}

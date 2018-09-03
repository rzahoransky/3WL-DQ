package rzahoransky.dqpipeline.dataExtraction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NavigableSet;

import javax.crypto.spec.DHGenParameterSpec;
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

public class ParticleSizeExtractor extends AbstractDQPipelineElement {

	private MieList wl1;
	private MieList wl3;
	private MieList wl2;
	private HashMap<DQtype, ReverseDQ> dqs = new HashMap<>();
	boolean wavelengthCheck = true;

	public static void main(String[] args) {
		double dq1 = 3;
		double dq2 = 4;

		ParticleSizeExtractor extractor = new ParticleSizeExtractor(new File("D:/mietemp/2018-08-08.miezip"));
		DQSignal in = new DQSignal();
		in.setDQ(new DQSignalEntry(DQtype.DQ1, 1, 2, 1.8));
		in.setDQ(new DQSignalEntry(DQtype.DQ2, 1, 2, 3.4));
		in.setDQ(new DQSignalEntry(DQtype.DQ3, 1, 2, 6.12));
		extractor.processDQElement(in);

	}

	public ParticleSizeExtractor(MieList wl1, MieList wl2, MieList wl3) {
		this.wl1 = wl1;
		this.wl2 = wl2;
		this.wl3 = wl3;
		dqs.put(DQtype.DQ1, new ReverseDQ(wl1, wl2));
		dqs.put(DQtype.DQ2, new ReverseDQ(wl2, wl3));
		dqs.put(DQtype.DQ3, new ReverseDQ(wl1, wl3));
	}

	public ParticleSizeExtractor(File zippedMie) {
		DQReader reader;
		try {
			reader = new DQReader(zippedMie);
			this.wl1 = reader.getWl1();
			this.wl2 = reader.getWl2();
			this.wl3 = reader.getWl3();
			dqs.put(DQtype.DQ1, new ReverseDQ(wl1, wl2));
			dqs.put(DQtype.DQ2, new ReverseDQ(wl2, wl3));
			dqs.put(DQtype.DQ3, new ReverseDQ(wl1, wl3));

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

		try {
			extractDiameterAndSigma(in);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return in;
	}

	public void extractDiameterAndSigma(DQSignal signal) {
		HashMap<DQtype, List<ReverseDQEntry>> dqHits = new HashMap<>();

		for (DQtype dqType : DQtype.values()) {
			dqHits.put(dqType, getDQHits(signal.getDQ(dqType))); // get all possible diameter matches from dqs
		}

		ArrayList<DiameterComperator> comperators = new ArrayList<>();

		DQtype mostHits = getBiggestList(dqHits); // get the DQ hits with the most particle diameter matches as starting
													// point

		for (ReverseDQEntry entry : dqHits.get(mostHits)) {
			comperators.add(new DiameterComperator(entry)); // take every single possible diameter as starting point
		}

		LinkedList<DQtype> remainingDQs = new LinkedList<>(Arrays.asList(DQtype.values()));
		remainingDQs.remove(mostHits);

		for (DQtype remaining : remainingDQs) {
			for (DiameterComperator comperator : comperators) {
				comperator.filterForBestMatch(dqHits.get(remaining)); // match best diameters from remaining DQs
			}
		}

		DiameterComperator result = getBestMatchingComperatorElement(comperators);
		signal.setDiameter(result.getMedianDiameter());
		signal.setSigma(result.getSigma());
	}

	private DiameterComperator getBestMatchingComperatorElement(ArrayList<DiameterComperator> comperators) {
		LinkedList<DiameterComperator> result = new LinkedList<>();
		for (DiameterComperator comp : comperators) {
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

	/**
	 * returns the minimum distance between two elements in the sepcified sets.
	 * Stores minimum found diameter in SigmaSize
	 * 
	 * @param sizesForSigma
	 * @param sizesForSigma2
	 * @return
	 */
	private double bestMatch(NavigableSet<Double> sizesForSigma, NavigableSet<Double> sizesForSigma2, SigmaSize ss) {
		double distance = Double.MAX_VALUE;
		for (double diameter : sizesForSigma) {
			double floor = sizesForSigma2.floor(diameter);
			double ceiling = sizesForSigma2.ceiling(diameter);
			if (Math.abs(floor - diameter) < distance) {
				distance = Math.abs(floor - diameter);
				ss.diameter = (diameter + floor) / 2;
			}
			if (Math.abs(ceiling - diameter) < diameter) {
				distance = Math.abs(ceiling - diameter);
				ss.diameter = (diameter + ceiling) / 2;
			}
		}

		return distance;
	}

	private void checkWavelengths(DQSignal in) {
		if (Math.abs(in.getWL1() - wl1.getWavelength()) > 0.01 || Math.abs(in.getWL2() - wl2.getWavelength()) > 0.01
				|| Math.abs(in.getWL3() - wl3.getWavelength()) > 0.01) {
			JOptionPane.showMessageDialog(null, "Mie File wavelength does not match the current Measurement Device");
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

package rzahoransky.dqpipeline.dataExtraction;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.NavigableSet;

import javax.swing.JOptionPane;

import calculation.MieList;
import dq.DQField;
import dq.DQEntry;
import errors.WavelengthMismatchException;
import rzahoransky.dqpipeline.AbstractDQPipelineElement;
import rzahoransky.dqpipeline.DQSignal;
import rzahoransky.utils.DQtype;
import storage.dqMeas.read.DQReader;

public class ParticleSizeExtractor extends AbstractDQPipelineElement{

	private MieList wl1;
	private MieList wl3;
	private MieList wl2;
	DQField dq1;
	DQField dq2;
	boolean wavelengthCheck = true;
	
	public static void main(String[] args) {
		double dq1 = 3;
		double dq2 = 4;
		
	}

	public ParticleSizeExtractor(MieList wl1, MieList wl2, MieList wl3) {
		this.wl1 = wl1;
		this.wl2 = wl2;
		this.wl3 = wl3;
		dq1 = new DQField(wl1, wl2);
		dq2 = new DQField(wl2, wl3);
	}
	
	public ParticleSizeExtractor(File zippedMie) {
		DQReader reader;
		try {
			reader = new DQReader(zippedMie);
			this.wl1=reader.getWl1();
			this.wl2=reader.getWl2();
			this.wl3=reader.getWl3();
			dq1 = new DQField(wl1, wl2);
			dq2 = new DQField(wl2, wl3);
			
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
		DQEntry dq1Hits = dq1.getEntryFor(in.getDQ(DQtype.DQ1));
		DQEntry dq2Hits = dq2.getEntryFor(in.getDQ(DQtype.DQ2));
		
		SigmaSize sigmaSize = getMatchingEntries(dq1Hits, dq2Hits);
		
		in.setDiameter(sigmaSize.diameter);
		in.setSigma(sigmaSize.sigma);
		} catch (Exception e) {
			//e.printStackTrace(); 
			//Cannot find a matching DQ
		}

		return in;
	}

	private SigmaSize getMatchingEntries(DQEntry dq1Hits, DQEntry dq2Hits) {
		double distance = Double.MAX_VALUE;
		SigmaSize ss = new SigmaSize();
		ss.diameter=Double.MAX_VALUE;
		ss.sigma=Double.MAX_VALUE;
		for (double cur_sigma: dq1Hits.getSigmas()) {
			double cur_distance =  bestMatch(dq1Hits.getPossibleDiameterForSigma(cur_sigma), dq2Hits.getPossibleDiameterForSigma(cur_sigma), ss);
			if (cur_distance < distance) {
				distance = cur_distance;
				ss.sigma = cur_sigma;
			}
		}
		return ss;
	}

	/**
	 * returns the minimum distance between two elements in the sepcified sets. Stores minimum found diameter in SigmaSize
	 * @param sizesForSigma
	 * @param sizesForSigma2
	 * @return
	 */
	private double bestMatch(NavigableSet<Double> sizesForSigma, NavigableSet<Double> sizesForSigma2, SigmaSize ss) {
		double distance = Double.MAX_VALUE;
		for (double diameter: sizesForSigma) {
			double floor = sizesForSigma2.floor(diameter);
			double ceiling = sizesForSigma2.ceiling(diameter);
			if (Math.abs(floor - diameter) < distance) {
				distance = Math.abs(floor - diameter);
				ss.diameter=(diameter+floor)/2;
			}
			if (Math.abs(ceiling - diameter) < diameter) {
				distance = Math.abs(ceiling - diameter);
				ss.diameter=(diameter+ceiling)/2;
			}
		}
		
		return distance;
	}

	private void checkWavelengths(DQSignal in) {
		if (Math.abs(in.getWL1() - wl1.getWavelength()) > 0.01 || Math.abs(in.getWL2() - wl2.getWavelength()) > 0.01 || Math.abs(in.getWL3() - wl3.getWavelength()) > 0.01) {
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

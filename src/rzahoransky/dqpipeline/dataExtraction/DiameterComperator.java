package rzahoransky.dqpipeline.dataExtraction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import calculation.MieIntegratorThreat;
import dq.ReverseDQEntry;
import jdistlib.LogNormal;
import rzahoransky.gui.measureSetup.MeasureSetUp;

/**
 * Compare possible diameters according to their error range (max possible found
 * diameter - min possible found diameter
 * 
 * @author richard
 *
 */
public class DiameterComperator implements Comparable<DiameterComperator> {

	private ArrayList<ReverseDQEntry> foundDiameters = new ArrayList<>();
	private double medianDiameter;
	private double sigma;
	private double averageDiameter;
	private double probability;
	private LogNormal log;
	private static double minDiameter = MeasureSetUp.getInstance().getMieList(0).getMinDiameter();
	private static double maxDiameter = MeasureSetUp.getInstance().getMieList(0).getMaxDiameter();
	//private static int steps = 400;
	private static double start = MeasureSetUp.getInstance().getMieList(0).getMinDiameter();
	private static double end = MeasureSetUp.getInstance().getMieList(0).getMaxDiameter();
	private static double step = (end - start) / 700;

	public DiameterComperator(ReverseDQEntry initialEntry) {
		sigma = initialEntry.getSigma();
		add(initialEntry);
	}

	public void filterForBestMatchWithProbabilityFunction(List<ReverseDQEntry> list) {
		double probability = 0;
		ReverseDQEntry bestMatch = null;
		for (ReverseDQEntry entry : list) {
			double current_probability = getProbabilityOfMatch(entry);
			if (current_probability >= probability) { // filter for best probability match
				bestMatch = entry;
				probability = current_probability;
			}
		// addWithSigmaUpdate(bestMatch);
		add(bestMatch);
		this.probability = probability;
		}
	}

	private void addWithSigmaUpdate(ReverseDQEntry bestMatch) {
		add(bestMatch);
		sigma = (sigma + bestMatch.getSigma()) / 2;
	}

	protected double getProbabilityOfMatch(ReverseDQEntry entry) {
		
		//consider only matches with same Sigma value
		if (entry.getSigma() != sigma)
			return 0;
		
		LogNormal two = new LogNormal(Math.log(entry.getDiameter()), entry.getSigma());
		double area = 0;
		//double start = Math.max(minDiameter, entry.getDiameter()/4);
		//double end = Math.min(maxDiameter, entry.getDiameter()*4);
		//double step = (end-start) / steps;
		for (double x = start; x < end; x += step) {
			double y1Min = Math.min(log.density(x, false), two.density(x, false));
			double y2Min = Math.min(log.density(x + step, false), two.density(x + step, false));
			area += MieIntegratorThreat.simpsonRule(x, x + step, y1Min, y2Min);
		}
		return area;
	}

	private void add(ReverseDQEntry entry) {
		foundDiameters.add(entry);
//		this.medianDiameter = calcMedianDiameter();
		this.averageDiameter = calcAverageDiameter();
		log = new LogNormal(Math.log(averageDiameter), sigma);
	}

	private double calcAverageDiameter() {
		double average = 0;
		for (ReverseDQEntry entry : foundDiameters) {
			average += entry.getDiameter();
		}
		return average / foundDiameters.size();
	}

	public double getAverageDiameter() {

		// printDebugInfo();
		return averageDiameter;

	}

	private void printDebugInfo() {
		String s = "Diameters: ";
		for (ReverseDQEntry entry : foundDiameters) {
			s += entry.getDiameter() + " / ";
		}
		System.out.println(s);

	}

	public double getErrorRangeAbsolute() {
		return Math.abs(Collections.min(foundDiameters).getDiameter() - Collections.max(foundDiameters).getDiameter());
	}

	public double getErrorRangeAsFraction() {
		return Collections.min(foundDiameters).getDiameter() / Collections.max(foundDiameters).getDiameter();
	}

	@Override
	public int compareTo(DiameterComperator o) {
		// return Double.compare(getErrorRangeAsFraction(),
		// o.getErrorRangeAsFraction()); //return according to error in diameter
		// distance
		return Double.compare(1 - probability, 1 - o.probability);
	}

	public int getSize() {
		return foundDiameters.size();
	}

	public double getSigma() {
		return sigma;
	}

	public double getLowesetDiameter() {
		return Collections.min(foundDiameters).getDiameter();
	}

	public double getHighestDiameter() {
		return Collections.max(foundDiameters).getDiameter();
	}
	
	public double getProbability() {
		return probability;
	}

}

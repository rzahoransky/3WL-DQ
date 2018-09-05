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
	private double start;
	private double end;
	private double step;

	public DiameterComperator(ReverseDQEntry initialEntry) {
		sigma = initialEntry.getSigma();
		add(initialEntry);

		start = MeasureSetUp.getInstance().getMieList(0).getMinDiameter();
		end = MeasureSetUp.getInstance().getMieList(0).getMaxDiameter();
		step = (end - start) / 750;
	}

	public void filterForBestMatchWithAbsoluteDistance(List<ReverseDQEntry> list) {
		double distance = Double.MAX_VALUE;
		ReverseDQEntry closestElement = null;
		for (ReverseDQEntry entry : list) {
			if (entry.getSigma() == sigma && Math.abs(getMedianDiameter() - entry.getDiameter()) < distance) { // filter
																												// for
																												// absolute
																												// distance
				closestElement = entry;
				distance = Math.abs(getMedianDiameter() - entry.getDiameter());
			}
		}
		add(closestElement);
	}

	public void filterForBestMatchWithProbabilityFunction(List<ReverseDQEntry> list) {
		double probability = 0;
		ReverseDQEntry bestMatch = null;
		for (ReverseDQEntry entry : list) {
			double current_probability = getProbabilityOfMatch(entry);
			if (current_probability > probability) { // filter for best probability match
				bestMatch = entry;
				probability = current_probability;
			}
		}
		// addWithSigmaUpdate(bestMatch);
		add(bestMatch);
		this.probability = probability;
	}

	private void addWithSigmaUpdate(ReverseDQEntry bestMatch) {
		add(bestMatch);
		sigma = (sigma + bestMatch.getSigma()) / 2;
	}

	protected double getProbabilityOfMatch(ReverseDQEntry entry) {
		LogNormal two = new LogNormal(entry.getDiameter(), entry.getSigma());
		double area = 0;
		for (double x = start; x < end; x += step) {
			double y1Min = Math.min(log.density(x, false), two.density(x, false));
			double y2Min = Math.min(log.density(x + step, false), two.density(x + step, false));
			area += MieIntegratorThreat.simpsonRule(x, x + step, y1Min, y2Min);
		}
		// System.out.println(area);
		return area;
	}

	private void add(ReverseDQEntry entry) {
		foundDiameters.add(entry);
		this.medianDiameter = calcMedianDiameter();
		this.averageDiameter = calcAverageDiameter();
		log = new LogNormal(averageDiameter, sigma);
	}

	private double calcAverageDiameter() {
		double average = 0;
		for (ReverseDQEntry entry : foundDiameters) {
			average += entry.getDiameter();
		}
		return average / foundDiameters.size();
	}

	public double getMedianDiameter() {
		return medianDiameter;
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

	protected double calcMedianDiameter() {
		Collections.sort(foundDiameters);
		if (foundDiameters.size() % 2 == 0) {
			return foundDiameters.get(foundDiameters.size() / 2).getDiameter();
		} else if (foundDiameters.size() == 1) {
			return foundDiameters.get(0).getDiameter();
		} else {
			return (foundDiameters.get(foundDiameters.size() / 2).getDiameter()
					+ foundDiameters.get(foundDiameters.size() / 2 - 1).getDiameter()) / 2;
		}
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

}

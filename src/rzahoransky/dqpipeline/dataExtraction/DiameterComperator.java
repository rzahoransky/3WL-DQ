package rzahoransky.dqpipeline.dataExtraction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dq.ReverseDQEntry;

/**
 * Compare possible diameters according to their error range (max possible found diameter - min possible found diameter
 * @author richard
 *
 */
public class DiameterComperator implements Comparable<DiameterComperator>{

	private ArrayList<ReverseDQEntry> foundDiameters = new ArrayList<>();
	private double medianDiameter;
	private double sigma;

	public DiameterComperator(ReverseDQEntry initialEntry) {
		add(initialEntry);
		sigma = initialEntry.getSigma();
	}

	public void filterForBestMatch(List<ReverseDQEntry> list) {
		double distance = Double.MAX_VALUE;
		ReverseDQEntry closestElement =  null;
		for (ReverseDQEntry entry : list) {
			if(entry.getSigma() == sigma && Math.abs(getMedianDiameter()-entry.getDiameter())<distance) {
				closestElement = entry;
				distance=Math.abs(getMedianDiameter()-entry.getDiameter());
			}
		}
		foundDiameters.add(closestElement);
	}
	
	private void add(ReverseDQEntry entry) {
		foundDiameters.add(entry);
		this.medianDiameter = calcMedianDiameter();
	}
	
	public double getMedianDiameter() {
		return medianDiameter;
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
	
	public double getErrorRange() {
		return Math.abs(Collections.min(foundDiameters).getDiameter() - Collections.max(foundDiameters).getDiameter());
	}

	@Override
	public int compareTo(DiameterComperator o) {
		return Double.compare(getErrorRange(), o.getErrorRange());
	}
	
	public int getSize() {
		return foundDiameters.size();
	}
	
	public double getSigma() {
		return sigma;
	}

}

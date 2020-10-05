package rzahoransky.dqpipeline.dataExtraction;

import calculation.MieList;
import rzahoransky.dqpipeline.dataExtraction.KdTree.XYZPoint;

public class SimpleDQLookup {

	KdTree<XYZPoint> tree = new KdTree<>();

	public SimpleDQLookup(MieList wl1, MieList wl2, MieList wl3) {

		for (int i = 0; i < wl1.size(); i++) {
			for (double sigma : wl1.get(i).getSortedSigmas()) {
				double dq1 = wl1.get(i).getIntegratedQext().get(sigma) / wl2.get(i).getIntegratedQext().get(sigma);
				double dq2 = wl2.get(i).getIntegratedQext().get(sigma) / wl3.get(i).getIntegratedQext().get(sigma);
				XYZPoint p = new XYZPoint(dq1, dq2);
				//use diameter as lookup key here instead of radius. 
				//Diameter is used to display results. 
				//This makes printing the result straight forward
				p.diameter=wl1.get(i).getRadius()*2; 
				p.sigma=sigma;
				tree.add(p);
			}
		}

	}

	public double getDiameterFor(double dq1, double dq2) {
		for (XYZPoint p: tree.nearestNeighbourSearch(1, new XYZPoint(dq1, dq2))) {
			return p.diameter; //return first found point
		}
		return 0;
	}

	public double getSigmaFor(double dq1, double dq2) {
		for (XYZPoint p: tree.nearestNeighbourSearch(1, new XYZPoint(dq1, dq2))) {
			return p.sigma; //return first found point
		}
		return 0;
	}


}

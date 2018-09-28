package rzahoransky.dqpipeline.dqSignal;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.OptionalDouble;

import calculation.DistributionFactory;
import javafx.scene.chart.PieChart.Data;
import presets.Wavelengths;
import rzahoransky.dqpipeline.dataExtraction.DiameterComperator;
import rzahoransky.utils.ArrayListUtils;
import rzahoransky.utils.DQtype;
import rzahoransky.utils.ExtractedSignalType;
import rzahoransky.utils.TransmissionType;
import rzahoransky.utils.RawSignalType;

/**
 * A DQMeasurement object holds the produced data from the Wizard-DQ Measurement
 * device. It is first populated with raw voltage values and further
 * processed to hold transmission, DQ and particle information
 * 
 * @author richard
 *
 */
public class DQSignal {


	private long timeStamp;

	private HashMap<RawSignalType, ArrayList<Double>> values = new HashMap<>();
	private ArrayList<Integer> periodMarker = new ArrayList<>();
	private ArrayList<DQSignalSinglePeriod> singlePeriods = new ArrayList<>();
	private double wl1 = 0;
	private double wl2 = 0;
	private double wl3 = 0;

	private double geometricalDiameter = 0;
	private double sigma = 0;
	private double minFoundGeometricalDiameter = 0;
	private double maxFoundGeometricalDiameter = 0;
	private boolean hasMinAndMaxDiameter = false;
	
	private HashMap<TransmissionType, ArrayList<Double>> measuredValues = new HashMap<>();
	private HashMap<DQtype, DQSignalEntry> dq = new HashMap<>();
	
	protected HashMap<TransmissionType, Double> factors = new HashMap<>();

	private double length;
	private double numberConcentration;
	private int numberOfParticlesPerCubicMeter;
	

	public ArrayList<DQSignalSinglePeriod> getSinglePeriods() {
		return singlePeriods;
	}
	
	public long getTimeStamp() {
		return timeStamp;
	}

	public ArrayList<Integer> getPeriodMarker() {
		return periodMarker;
	}
	
	public void addPeriodMark(int mark) {
		
		if (!periodMarker.isEmpty()) {
			//create single period entries
			singlePeriods.add(new DQSignalSinglePeriod(this, periodMarker.get(periodMarker.size()-1), mark));
		}
		periodMarker.add(mark);

	}

	public DQSignal() {
		
		for (RawSignalType type: RawSignalType.values())
			values.put(type, new ArrayList<>());
		this.timeStamp = System.currentTimeMillis();
	}
	
	public DQSignal(ArrayList<Double> reference, ArrayList<Double> measurement) {
		values.put(RawSignalType.ref, reference);
		values.put(RawSignalType.meas, measurement);
		this.timeStamp = System.currentTimeMillis();
	}
	
	public DQSignal(ArrayList<Double> reference, ArrayList<Double> measurement, ArrayList<Double> mode, ArrayList<Double> trigger) {
		this(reference,measurement);
		values.put(RawSignalType.mode, mode);
		values.put(RawSignalType.trigger, trigger);
	}

	public ArrayList<Double> getReference() {
		return values.get(RawSignalType.ref);
	}

	public void setReference(ArrayList<Double> reference) {
		values.put(RawSignalType.ref, reference);
	}

	public ArrayList<Double> getMeasurement() {
		return values.get(RawSignalType.meas);
	}

	public void setMeasurement(ArrayList<Double> measurement) {
		values.put(RawSignalType.meas, measurement);
	}

	public ArrayList<Double> getMode() {
		return values.get(RawSignalType.mode);
	}

	public void setMode(ArrayList<Double> mode) {
		values.put(RawSignalType.mode, mode);
	}

	public ArrayList<Double> getTrigger() {
		return values.get(RawSignalType.trigger);
	}

	public void setTrigger(ArrayList<Double> trigger) {
		values.put(RawSignalType.trigger, trigger);
	}
	
	public ArrayList<Double> get(RawSignalType typye) {
		return values.get(typye);
	}
	
	public void set(RawSignalType type, ArrayList<Double> values) {
		this.values.put(type, values);
	}
	
	public boolean contains (RawSignalType type) {
		if (!values.containsKey(type)) return false;
		else return !values.get(type).isEmpty();
	}
	
	public String toString() {
		String s="";
		for (Entry<RawSignalType, ArrayList<Double>> value: values.entrySet()) {
			s+=value.getKey()+": "+getAverage(value.getValue());
		}
		return s;
		

	}
	private double getAverage(ArrayList<Double> list) {
		OptionalDouble average = list
	            .stream()
	            .mapToDouble(a -> a)
	            .average();
		return average.isPresent() ? average.getAsDouble() : 0; 
	}
	
	public int getLength() {
		return values.get(RawSignalType.ref).size();
	}
	
	public double getAveragedValues(RawSignalType refMeas, ExtractedSignalType type) {
		double sum = 0.0;
		int count = 0;
		try {
		for (DQSignalSinglePeriod singlePeriod: singlePeriods) {
			for (Double singleEntry: singlePeriod.getValues(refMeas).get(type)) {
				sum+=singleEntry;
				count++;
			}
		}
		} catch (Exception e) {
			e.printStackTrace();
			return 0.0;
		}
		return sum/count;
	}

	public void setWL1(double d) {
		wl1=d;
	}

	public void setWL2(double d) {
		wl2=d;
	}

	public void setWL3(double d) {
		wl3=d;
	}
	
	public double getWL1() {
		return wl1;
	}
	
	public double getWL2() {
		return wl2;
	}
	
	public double getWL3() {
		return wl3;
	}
	
	public double getGeometricalDiameter() {
		return geometricalDiameter;
	}
	
	public double getMinGeometricalDiameter() {
		return minFoundGeometricalDiameter;
	}
	
	public double getMaxGeometricalDiameter() {
		return maxFoundGeometricalDiameter;
	}
	
	public static double calculateVolumetricDiameter(double geometricalDiameter, double sigma) {
		return Math.pow(Math.pow(geometricalDiameter, 3d)*Math.pow(Math.E, ((9d/2d)*sigma*sigma)),(1d/3d));
	}
	
	public double getVolumetricDiameter() {
		return calculateVolumetricDiameter(getGeometricalDiameter(), getSigma());
	}
	
	public double getMinVolumetricDiameter() {
		return calculateVolumetricDiameter(getMinGeometricalDiameter(), getSigma());
	}
	
	public double getMaxVolumetricDiameter() {
		return calculateVolumetricDiameter(getMaxGeometricalDiameter(), getSigma());
	}
	
	public double getSigma() {
		return sigma;
	}
	
	public boolean hasMinAndMaxDiameter() {
		return hasMinAndMaxDiameter;
	}

	public void setGeometricalDiameter(DiameterComperator result) {
		this.geometricalDiameter = result.getAverageDiameter();
		this.minFoundGeometricalDiameter = result.getLowesetDiameter();
		this.maxFoundGeometricalDiameter = result.getHighestDiameter();
		this.hasMinAndMaxDiameter = true;
		this.sigma = result.getSigma();
		// System.out.println("Got diameter: "+diameter);
	}
	
	public void setGeometricalDiameter (double d) {
		this.geometricalDiameter = d;
	}


	public void setSigma(double sigma) {
		this.sigma = sigma;
	}
	
	public void addMinDiameter(double minDiameter) {
		this.minFoundGeometricalDiameter = minDiameter;
	}
	
	public void addMaxDiameter(double maxDiameter) {
		this.maxFoundGeometricalDiameter = maxDiameter;
	}

	public void addTransmission(TransmissionType type, double transmissionValue) {
		if (!measuredValues.containsKey(type))
			measuredValues.put(type, new ArrayList<>());
		
		measuredValues.get(type).add(transmissionValue);
	}
	
	public double getTransmission(TransmissionType type) {
		return ArrayListUtils.getAverage(measuredValues.get(type));
	}

	public void setDQ(DQSignalEntry entry) {
		this.dq.put(entry.getType(), entry);
	}
	
	public DQSignalEntry getDQ(DQtype dqType) {
		return dq.get(dqType);
	}

	public void setLength(double cm) {
		this.length = cm;
		
	}

	public void setNumberConcentration(double numberConcentration) {
		this.numberConcentration = numberConcentration;
		//double averageParticleDiameter = getAverageOfLogDistribution(sigma, diameter);
		
		//this.numberOfParticlesPerCubicMeter = 4;
	}
	
	private double getAverageOfLogDistribution(double sigma2, double diameter2) {
		return Math.pow(Math.E, (diameter2+(sigma*sigma)/2));
	}

	public double getNumberConcentration() {
		return numberConcentration;
	}
	
	public double getVolumeConcentration() {
		return getNumberConcentration()*(Math.PI/6)*Math.pow(getVolumetricDiameter(),3);
	}

	public void setFactor(TransmissionType type, double factor) {
		factors.put(type, factor);
	}
	
	public double getFactor(TransmissionType type) {
		return factors.get(type);
	}
	
	
	

}

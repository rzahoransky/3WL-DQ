package rzahoransky.dqpipeline.dqSignal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.OptionalDouble;

import rzahoransky.dqpipeline.dataExtraction.DiameterComperator;
import rzahoransky.utils.DQListUtils;
import rzahoransky.utils.DQtype;
import rzahoransky.utils.ExtractedSignalType;
import rzahoransky.utils.RawSignalType;
import rzahoransky.utils.TransmissionType;

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
	private ArrayList<Integer> periodMarks = new ArrayList<>();
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
	private double numberConcentration = 0;
	
	protected HashMap<SignalTypeHash, Double> averagedValues = new HashMap<>();
	
	public boolean isValid = true;

	private long lastFactorUpdate = 0;

	private long nanoSeconds;
	
	public ArrayList<DQSignalSinglePeriod> getSinglePeriods() {
		return singlePeriods;
	}
	
	/** get timestamp in milliseconds (granularity around 10ms) **/
	public long getTimeStamp() {
		return timeStamp;
	}

	public ArrayList<Integer> getPeriodMarker() {
		return periodMarks;
	}
	
	public void addPeriodMark(int mark) {
		
		if (!periodMarks.isEmpty()) {
			//create single period entries
			singlePeriods.add(new DQSignalSinglePeriod(this, periodMarks.get(periodMarks.size()-1), mark));
		}
		periodMarks.add(mark);
	}

	public DQSignal() {
		
		for (RawSignalType type: RawSignalType.values())
			values.put(type, new ArrayList<>());
		
		this.timeStamp = System.currentTimeMillis();
	}
	
	public void setTimestamp(long time) {
		this.timeStamp = time;
	}
	
	public DQSignal(ArrayList<Double> reference, ArrayList<Double> measurement) {
		values.put(RawSignalType.ref, reference);
		values.put(RawSignalType.meas, measurement);
		this.timeStamp = System.currentTimeMillis();
		this.nanoSeconds = System.nanoTime();
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
	
	public int getSize() {
		return values.get(RawSignalType.ref).size();
	}
	
	/** get averaged primitve values from all containing periods of the raw signal
	 * 
	 * @param refMeas Reference or measurement?
	 * @param type Offset, wl1, wl2 or wl3?
	 * @return the averaged values of all containing signal periods
	 */
	public double getAveragedValues(RawSignalType refMeas, ExtractedSignalType type) {
		SignalTypeHash hash = new SignalTypeHash(refMeas, type);
		if (averagedValues.containsKey(hash)) {
			return averagedValues.get(hash);
		}
		
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
		averagedValues.put(hash, sum/count);
		return sum/count;
	}
	
	public void setAveragedValue(RawSignalType refMeas, ExtractedSignalType type, double averagedValue) {
		averagedValues.put(new SignalTypeHash(refMeas, type), averagedValue);
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
	
	public void addMinGeometricalDiameter(double minDiameter) {
		this.minFoundGeometricalDiameter = minDiameter;
	}
	
	public void addMaxGeometricalDiameter(double maxDiameter) {
		this.maxFoundGeometricalDiameter = maxDiameter;
	}

	public void addTransmission(TransmissionType type, double transmissionValue) {
		if (!measuredValues.containsKey(type))
			measuredValues.put(type, new ArrayList<>());
		
		measuredValues.get(type).add(transmissionValue);
	}
	
	public double getTransmission(TransmissionType type) {
		try {
		return DQListUtils.getAverage(measuredValues.get(type));
		} catch (NullPointerException e) {
			return 0d;
		}
	}

	public void setDQ(DQSignalEntry entry) {
		this.dq.put(entry.getType(), entry);
	}
	
	public DQSignalEntry getDQ(DQtype dqType) {
		return dq.get(dqType);
	}

	public void setMeasureLength(double cm) {
		this.length = cm;
	}
	
	public double getMeasureLength() {
		return length;
	}
	
	public void removeRawSignal() {
		periodMarks = null;
		values = null;
		singlePeriods = null;
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
		return getNumberConcentration()*(Math.PI/6)*Math.pow(getVolumetricDiameter()/(1e6),3);
	}

	public void setFactor(TransmissionType type, double factor) {
		factors.put(type, factor);
	}
	
	public double getFactor(TransmissionType type) {
		return factors.get(type);
	}
	
	
	public double getHighestTransmission() {
		double maxTransmission = 0;
		try {
			for (TransmissionType type : TransmissionType.values()) {
				maxTransmission = Math.max(maxTransmission, getTransmission(type));
			}
			return maxTransmission;
		} catch (Exception e) {
			return 0;
		}
	}
	
	public double getLowestTransmission() {
		double minTransmission = 1;
		try {
			for (TransmissionType type : TransmissionType.values()) {
				minTransmission = Math.min(minTransmission, getTransmission(type));
			}
			return minTransmission;
		} catch (Exception e) {
			return 0;
		}
	}
	
	public long getNanoSecondTimestamp() {
		return nanoSeconds;
	}	
	
	public void setNanoSecondTimestamp(long timeStampNanoSeconds) {
		nanoSeconds = timeStampNanoSeconds;
	}
	
	/** state if transmission allows for particle diameter detection **/
	public boolean checkTransmission() {
		return checkTransmission(0.02, 0.98);
	}
	
	public boolean checkTransmission(double lower, double upper) {
		return (getHighestTransmission()<upper) && (getLowestTransmission()>lower);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((averagedValues == null) ? 0 : averagedValues.hashCode());
		result = prime * result + ((dq == null) ? 0 : dq.hashCode());
		result = prime * result + ((factors == null) ? 0 : factors.hashCode());
		long temp;
		temp = Double.doubleToLongBits(geometricalDiameter);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + (hasMinAndMaxDiameter ? 1231 : 1237);
		result = prime * result + (isValid ? 1231 : 1237);
		result = prime * result + (int) (lastFactorUpdate ^ (lastFactorUpdate >>> 32));
		temp = Double.doubleToLongBits(length);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(maxFoundGeometricalDiameter);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((measuredValues == null) ? 0 : measuredValues.hashCode());
		temp = Double.doubleToLongBits(minFoundGeometricalDiameter);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + (int) (nanoSeconds ^ (nanoSeconds >>> 32));
		temp = Double.doubleToLongBits(numberConcentration);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((periodMarks == null) ? 0 : periodMarks.hashCode());
		temp = Double.doubleToLongBits(sigma);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((singlePeriods == null) ? 0 : singlePeriods.hashCode());
		result = prime * result + (int) (timeStamp ^ (timeStamp >>> 32));
		result = prime * result + ((values == null) ? 0 : values.hashCode());
		temp = Double.doubleToLongBits(wl1);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(wl2);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(wl3);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DQSignal other = (DQSignal) obj;
		if (averagedValues == null) {
			if (other.averagedValues != null)
				return false;
		} else if (!averagedValues.equals(other.averagedValues))
			return false;
		if (dq == null) {
			if (other.dq != null)
				return false;
		} else if (!dq.equals(other.dq))
			return false;
		if (factors == null) {
			if (other.factors != null)
				return false;
		} else if (!factors.equals(other.factors))
			return false;
		if (Double.doubleToLongBits(geometricalDiameter) != Double.doubleToLongBits(other.geometricalDiameter))
			return false;
		if (hasMinAndMaxDiameter != other.hasMinAndMaxDiameter)
			return false;
		if (isValid != other.isValid)
			return false;
		if (lastFactorUpdate != other.lastFactorUpdate)
			return false;
		if (Double.doubleToLongBits(length) != Double.doubleToLongBits(other.length))
			return false;
		if (Double.doubleToLongBits(maxFoundGeometricalDiameter) != Double
				.doubleToLongBits(other.maxFoundGeometricalDiameter))
			return false;
		if (measuredValues == null) {
			if (other.measuredValues != null)
				return false;
		} else if (!measuredValues.equals(other.measuredValues))
			return false;
		if (Double.doubleToLongBits(minFoundGeometricalDiameter) != Double
				.doubleToLongBits(other.minFoundGeometricalDiameter))
			return false;
		if (nanoSeconds != other.nanoSeconds)
			return false;
		if (Double.doubleToLongBits(numberConcentration) != Double.doubleToLongBits(other.numberConcentration))
			return false;
		if (periodMarks == null) {
			if (other.periodMarks != null)
				return false;
		} else if (!periodMarks.equals(other.periodMarks))
			return false;
		if (Double.doubleToLongBits(sigma) != Double.doubleToLongBits(other.sigma))
			return false;
		if (singlePeriods == null) {
			if (other.singlePeriods != null)
				return false;
		} else if (!singlePeriods.equals(other.singlePeriods))
			return false;
		if (timeStamp != other.timeStamp)
			return false;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		if (Double.doubleToLongBits(wl1) != Double.doubleToLongBits(other.wl1))
			return false;
		if (Double.doubleToLongBits(wl2) != Double.doubleToLongBits(other.wl2))
			return false;
		if (Double.doubleToLongBits(wl3) != Double.doubleToLongBits(other.wl3))
			return false;
		return true;
	}
	
	


}

/** class to determine measurement type**/
class SignalTypeHash {
	
	private RawSignalType refOrMeas;
	private ExtractedSignalType type;

	public SignalTypeHash(RawSignalType refOrMeas, ExtractedSignalType type) {
		this.refOrMeas = refOrMeas;
		this.type = type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((refOrMeas == null) ? 0 : refOrMeas.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SignalTypeHash other = (SignalTypeHash) obj;
		if (refOrMeas != other.refOrMeas)
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
}

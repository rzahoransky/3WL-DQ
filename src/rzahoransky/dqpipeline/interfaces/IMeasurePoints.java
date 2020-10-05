package rzahoransky.dqpipeline.interfaces;

import rzahoransky.utils.ExtractedSignalType;

//TODO: put both methods into one method
public interface IMeasurePoints {
	
	/**
	 * return measure points for 5WL device, depending on used wavelengths (visible or infared)
	 * @param measureType WL1...WL3 or offset
	 * @param visibleWavelengths are RGB diodes used?
	 * @return
	 */
	public double[] getRelativeMeasurePoint(ExtractedSignalType measureType, boolean visibleWavelengths);
	
	/**
	 * return measure points for 3WL device.
	 * @param measureType WL1...WL3 or offset
	 * @return
	 */
	public double[] getRelativeMeasurePoint(ExtractedSignalType measureType);

}

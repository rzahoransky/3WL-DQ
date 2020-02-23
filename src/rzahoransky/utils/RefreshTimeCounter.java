package rzahoransky.utils;

import rzahoransky.gui.measureSetup.MeasureSetupEntry;

public class RefreshTimeCounter {
	
	protected long lastUpdate = System.nanoTime();
	protected long refreshRate = 48;
	
	public RefreshTimeCounter(long refreshRateInMs) {
		this.refreshRate = refreshRateInMs*1000000; //mili to nanoseconds
	}
	
	/** 
	 * Reads refresh time from {@link MeasureSetUp}.
	 */
	public RefreshTimeCounter() {
		this(MeasureSetUp.getInstance().getRefreshTime());
	}
	
	public boolean timeForUpdate() {
		long now = System.nanoTime();
		if(Math.abs(now - lastUpdate) >= refreshRate) {
			lastUpdate = now;
			return true;
		} else {
			return false;
		}
	}

}

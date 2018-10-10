package rzahoransky.utils;

public class TimeCounter {
	
	protected long lastUpdate = System.currentTimeMillis();
	protected long refreshRate = 48;
	
	public TimeCounter(long refreshRateInMs) {
		this.refreshRate = refreshRateInMs;
	}
	
	public boolean timeForUpdate() {
		long now = System.currentTimeMillis();
		if(now - lastUpdate >= refreshRate) {
			lastUpdate = now;
			return true;
		} else {
			return false;
		}
	}

}

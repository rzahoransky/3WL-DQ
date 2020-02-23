package rzahoransky.utils;

/** Interface to register for DQPipeline statistics like measurements per second**/
public interface DQTimerListener {
	
		public void newTimeStatistics(DQTimer timer);

}

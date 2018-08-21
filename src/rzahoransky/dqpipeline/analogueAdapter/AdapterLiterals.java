package rzahoransky.dqpipeline.analogueAdapter;

public class AdapterLiterals {

	public enum ADCard {
		Dev1,Dev2;
	}
	
	public enum SingleChannels {
		ai0, ai1, ai2, ai3, ai4, ai5, ai6, ai7, ai8, ai9;
	}
	
	public enum CombinedChannels {
		ai0To1("ai0:1"), ai0To3("ai0:3");
		
		private String value;

		private CombinedChannels(String value) {
			this.value=value;
		}
		
		public String toString() {
			return value;
		}
	}

}

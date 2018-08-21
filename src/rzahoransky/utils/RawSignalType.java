package rzahoransky.utils;

public enum RawSignalType {
	
	ref("Reference"), meas("Measurement"), trigger("Trigger"), mode("Mode Selector");
	
	private String value;

	private RawSignalType(String name) {
		this.value=name;
	}
	
	private RawSignalType() {
		
	}
	
	public String toString() {
		return value;
	}
}

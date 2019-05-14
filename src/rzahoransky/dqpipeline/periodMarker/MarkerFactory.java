package rzahoransky.dqpipeline.periodMarker;

import rzahoransky.dqpipeline.interfaces.AbstractDQPipelineElement;
import rzahoransky.gui.measureSetup.MeasureSetUp;
import rzahoransky.gui.measureSetup.MeasureSetupEntry;

public class MarkerFactory {
	
	public static AbstractDQPipelineElement getPeriodMarker(MarkerType type) {
		switch (type) {
		case FiveWLMarker:
			return new FiveWLMarker();
		case ThreeWlMarker:
			return new ThreeWLMarker();
		default:
			return new FiveWLMarker();
		}
	}
	
	public static AbstractDQPipelineElement getPeriodMarker() {
		try {
		String markerTypeString = MeasureSetUp.getInstance().getProperty(MeasureSetupEntry.MARKER_TYPE);
		MarkerType markerType = MarkerType.valueOf(markerTypeString);
		System.out.println("Marker Type is: "+markerType);
		return getPeriodMarker(markerType);
		} catch (Exception e) {
			System.out.println("Marker type fallback to FiveWLMarker");
			return new FiveWLMarker();
		}
	}

}

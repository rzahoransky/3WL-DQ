package rzahoransky.dqpipeline.periodMarker;

import rzahoransky.dqpipeline.interfaces.AbstractDQPipelineElement;
import rzahoransky.gui.measureSetup.MeasureSetupEntry;
import rzahoransky.utils.MeasureSetUp;

public class MarkerFactory {
	
	public static AbstractDQPipelineElement getPeriodMarker(MarkerType type) {
		switch (type) {
		case FiveWLMarker:
			return new FiveWLMarker(); //for 5WL-Device with individual trigger line
		case ThreeWlMarker:
			return new ThreeWLMarker(); //for 3WL-Device with no trigger line
		default:
			return new FiveWLMarker();
		}
	}
	
	/** get trigger marker for this measurement device **/
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

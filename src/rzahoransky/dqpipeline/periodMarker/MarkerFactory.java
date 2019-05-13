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
			break;
		}
		return null;
	}
	
	public static AbstractDQPipelineElement getPeriodMarker() {
		String markerTypeString = MeasureSetUp.getInstance().getProperty(MeasureSetupEntry.MARKER_TYPE);
		MarkerType markerType = MarkerType.valueOf(markerTypeString);
		System.out.println("Marker Type is: "+markerType);
		return getPeriodMarker(markerType);
	}

}

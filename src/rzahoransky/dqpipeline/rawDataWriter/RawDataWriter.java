package rzahoransky.dqpipeline.rawDataWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.interfaces.AbstractDQPipelineElement;
import rzahoransky.utils.RawSignalType;

public class RawDataWriter extends AbstractDQPipelineElement {

	private File file;
	private FileWriter fw;
	private RawSignalType[] signales = {RawSignalType.ref, RawSignalType.meas, RawSignalType.mode, RawSignalType.trigger};

	public RawDataWriter(String filename) {
		this.file = new File(filename);
		try {
			this.fw = new FileWriter(file);
			fw.write("# Time; Reference; Measure; Mode; Trigger\r\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public DQSignal processDQElement(DQSignal in) {
		DQSignal element = in;
		try {
			output(element);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return element;

	}

	private void output(DQSignal element) throws IOException {
		for (int i = 0; i<element.getLength();i++) {
				fw.write(element.getTimeStamp()+"; "+
				element.get(RawSignalType.ref).get(i)+"; "+ 
				element.get(RawSignalType.meas).get(i) +"; "+
				element.get(RawSignalType.mode).get(i) +"; "+
				element.get(RawSignalType.trigger).get(i));
				fw.write("\r\n");
		}
		
	}

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return null;
	}

}

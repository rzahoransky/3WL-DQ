package rzahoransky.dqpipeline.dataWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.interfaces.DQPipelineElement;

public class OutputWriter implements DQPipelineElement {

	private File file;
	private FileWriter fw;

	public OutputWriter(File file) {
		this.file = file;
		try {
			this.fw = openFile(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private FileWriter openFile(File file2) throws IOException {
		FileWriter fw = new FileWriter(file2);
		return fw;
		
	}
	
	private void generateHeader() {
		
	}

	@Override
	public DQSignal processDQElement(DQSignal in) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return "Stores Measurement Data";
	}

}

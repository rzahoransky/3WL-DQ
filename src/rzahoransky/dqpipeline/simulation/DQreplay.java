package rzahoransky.dqpipeline.simulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.interfaces.AbstractDQPipelineElement;
import rzahoransky.dqpipeline.interfaces.AdapterInterface;
import rzahoransky.utils.RawSignalType;

public class DQreplay extends AbstractDQPipelineElement implements AdapterInterface{

	private File file;
	private BufferedReader br;
	private int curIndice;
	private ArrayList<Long> indices;

	public DQreplay() {
		file = new File("aufnahme.txt");
		try {
			br = new BufferedReader(new FileReader(file));
			indices = getTimeIndices(br);
			curIndice = 0;
			resetBr();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void resetBr() throws FileNotFoundException {
		br = new BufferedReader(new FileReader(file));
	}
	
	@Override
	public String description() {
		return "reads AD raw data from file";
	}

	@Override
	public DQSignal processDQElement(DQSignal in) {
		String line = "";
		DQSignal measurement = new DQSignal();
		try {
			while ((line = br.readLine()) != null) {
				readline(line, measurement);
			}
			resetBr();
			updateCurIndice();
			return measurement;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	
	private void updateCurIndice() {
		if (curIndice == indices.size()-1)
			curIndice=0;
		else curIndice++;
	}

	private void readline(String line, DQSignal measurement) {
		
		String[] s = line.split(";");
		
		try {
			long time = Long.parseLong(s[0]);
			
			if(time == indices.get(curIndice)) {
				//read these samples
				measurement.get(RawSignalType.ref).add(Double.parseDouble(s[1]));
				measurement.get(RawSignalType.meas).add(Double.parseDouble(s[2]));
				measurement.get(RawSignalType.trigger).add(Double.parseDouble(s[3]));
			}
			
		} catch (Exception e) {
			return;
		}
	}
	
	private long getFirstTimeIndex(BufferedReader br) throws IOException {
			String content;
			while((content=br.readLine())!=null) {
				try {
					return Long.parseLong(content.split(";")[0]);
				} catch (Exception e) {
					
				}
			}
			br.reset();
			return 0;
	}
	
	private ArrayList<Long> getTimeIndices(BufferedReader br) throws IOException {
		HashSet<Long> set = new HashSet<>();
		
		String content;
		
		while ((content=br.readLine())!=null) {
			try {
				set.add(Long.parseLong(content.split(";")[0].trim()));
			} catch (Exception e) {
				//cannot parse, skip
			}
		}
		
		ArrayList<Long> list = new ArrayList<>(set);
		Collections.sort(list);
		
		return list;
	}
	@Override
	public void setADCardOrConfigParameter(String device) {
		// Nothing to do here
		
	}

	@Override
	public void clearTask() {
		// TODO Auto-generated method stub
		
	}

}

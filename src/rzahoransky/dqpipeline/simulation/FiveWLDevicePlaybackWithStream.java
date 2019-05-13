package rzahoransky.dqpipeline.simulation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.interfaces.AbstractDQPipelineElement;
import rzahoransky.dqpipeline.interfaces.AdapterInterface;
import rzahoransky.utils.BufferedRandomAccess;
import rzahoransky.utils.RawSignalType;

public class FiveWLDevicePlaybackWithStream extends AbstractDQPipelineElement implements AdapterInterface{

	private RandomAccessFile raf;
	private int curIndice;
	private ArrayList<Long> indices;
	int curLine;
	private ArrayList<Long> offsetList;
	private SortedMap<Long, ArrayList<Integer>> timeIndexMap; //TimeIndex -> Line in file
	private Iterator<Entry<Long, ArrayList<Integer>>> iterator;
	
	public FiveWLDevicePlaybackWithStream() {
		
	}
	

	public FiveWLDevicePlaybackWithStream(String file) {
		initFile(file);
	}
	
	private void initFile(String file) {
		try {
			//raf = new RandomAccessFile(file,"r");
			raf = new BufferedRandomAccess(file, "r", 4096);
			System.out.println("Offset starting...");
			this.offsetList = createOffsetList(raf);
			System.out.println("Offset created");
			this.timeIndexMap = createTimeIndeMapping(raf);
			this.iterator = timeIndexMap.entrySet().iterator();
			indices = getTimeIndices(raf);
			curIndice = 0;
			curLine = 0;
			resetBr();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private SortedMap<Long, ArrayList<Integer>> createTimeIndeMapping(RandomAccessFile br2) throws IOException {
		TreeMap<Long, ArrayList<Integer>> map = new TreeMap<>();
		String line;
		int curLine = 0;
		while ((line = br2.readLine()) != null ) {
			try {
			Long thisLineTimeIndex = Long.parseLong(line.split(";")[0]);
			if (map.containsKey(thisLineTimeIndex)) {
				map.get(thisLineTimeIndex).add(curLine);
			} else {
				ArrayList<Integer> arrayList = new ArrayList<>();
				arrayList.add(curLine);
				map.put(thisLineTimeIndex, arrayList);
			}
			} catch (Exception e) {
				//could not parse, continue with next line
			}
			curLine++;
		}
		return map;
	}

	private void resetBr() throws IOException {
		raf.seek(0);
	}
	
	@Override
	public String description() {
		return "reads AD raw data from file";
	}

	@Override
	public DQSignal processDQElement(DQSignal in) {
		
		try {
			Thread.sleep(30);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DQSignal measurement = new DQSignal();
		
		if (!iterator.hasNext())
			iterator = timeIndexMap.entrySet().iterator();
		
		Entry<Long, ArrayList<Integer>> entry = iterator.next();
		System.out.println("Processing time index "+entry.getKey());
		
		for (Integer lineNumber: entry.getValue()) { //traverse all lines that corrpesond to the time Index returned from the iterator
			readLine(lineNumber, measurement);
		}
		return measurement;
		
	}
	
	private void readLine(int line, DQSignal measurement) {
		try {
			//prepare raf
			raf.seek(offsetList.get(line));
			readline(raf.readLine(), measurement);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean continueWithLine(String line) {
		long thisIndex = Long.parseLong(line.split(";")[0]);
		return thisIndex == indices.get(curIndice);
	}

	private void updateCurIndice() {
		if (curIndice == indices.size()-1)
			curIndice=0;
		else curIndice++;
	}

	private void readline(String line, DQSignal measurement) {
		
		if(line == null) {
			return;
		}
		
		String[] s = line.split(";");
		
		if (s.length<=4) return;
		
		try {
			long time = Long.parseLong(s[0]);
			
				//read these samples
				//System.out.println("Reading "+curIndice);
				measurement.get(RawSignalType.ref).add(Double.parseDouble(s[1]));
				measurement.get(RawSignalType.meas).add(Double.parseDouble(s[2]));
				measurement.get(RawSignalType.mode).add(Double.parseDouble(s[3]));
				measurement.get(RawSignalType.trigger).add(Double.parseDouble(s[4]));
				
				MappedByteBuffer test;
			
		} catch (Exception e) {
			e.printStackTrace();
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
	
	private ArrayList<Long> getTimeIndices(RandomAccessFile br2) throws IOException {
		HashSet<Long> set = new HashSet<>();
		
		String content;
		
		while ((content=br2.readLine())!=null) {
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
		initFile(device);
		
	}
	
	/**
	 * returns a list where the index of the entry corresponds with the byte offset
	 * of the file such that list.get(x) returns the byte offset to read from the
	 * beginning of line x
	 **/
	private ArrayList<Long> createOffsetList(RandomAccessFile br2) throws IOException {

		ArrayList<Long> arrayList = new ArrayList<Long>();
		while ((br2.readLine()) != null) {
			arrayList.add(br2.getFilePointer());
		}
		br2.seek(0);
		return arrayList;
	}


	@Override
	public void clearTask() {
		// TODO Auto-generated method stub
		
	}
}

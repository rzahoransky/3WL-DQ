package rzahoransky.utils;

import java.util.LinkedList;
import java.util.List;

import rzahoransky.dqpipeline.dqSignal.DQSignal;

public class RingBuffer <T extends DQSignal> {
	protected int size = 3;
	protected LinkedList<T> buffer = new LinkedList<>();
	
	public RingBuffer(int size) {
		this.size = size;
	}
	
	public void add(T element) {
		while (buffer.size()>size)
			buffer.removeFirst();
		buffer.add(element);
	}
	
	public LinkedList<T> getBuffer() {
		return buffer;
	}
	
	public void setBufferSize(int size) {
		this.size = size;
	}
	
	public int getBufferSize() {
		return this.size;
	}
	
	public DQSignal getAverage() {
		return DQListUtils.getAverageDQSignal((List<DQSignal>) buffer);
	}

	public void clear() {
		buffer.clear();
		
	}

}

package rzahoransky.dqpipeline;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import kirkwood.nidaq.access.NiDaq;
import kirkwood.nidaq.access.NiDaqException;
import rzahoransky.dqpipeline.analogueAdapter.FiveWLNIDaqAdapter;
import rzahoransky.dqpipeline.dataExtraction.DQExtractor;
import rzahoransky.dqpipeline.dataExtraction.FiveWLExtractor;
import rzahoransky.dqpipeline.dataExtraction.FiveWLMeasurePoints;
import rzahoransky.dqpipeline.dataExtraction.ProbabilityBasedDiameterExtractor;
import rzahoransky.dqpipeline.dataExtraction.TransmissionExtractor;
import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.interfaces.AdapterInterface;
import rzahoransky.dqpipeline.interfaces.DQPipelineElement;
import rzahoransky.dqpipeline.listener.DQSignalListener;
import rzahoransky.dqpipeline.periodMarker.FiveWLMarker;
import rzahoransky.dqpipeline.simulation.FiveWLOneHeadSimulator;
import rzahoransky.dqpipeline.visualization.DQMeasurementVisualizer;
import rzahoransky.dqpipeline.visualization.DQSinglePeriodMeasurementVisualizer;
import rzahoransky.dqpipeline.visualization.DQVisualizer;
import rzahoransky.dqpipeline.visualization.LaserVoltageVisualizer;
import rzahoransky.dqpipeline.visualization.TransmissionVisualizer;
import rzahoransky.errors.PipelineAlreadyRunningException;

public class DQPipeline {
	
	private volatile boolean run = false;
	private int capacity = 1;
	
	private ArrayList<DQPipelineElement> pipelineElements = new ArrayList<>();
	private ArrayList<BlockingQueue<DQSignal>> queues = new ArrayList<>();
	private ArrayList<Thread> pipelineThreads = new ArrayList<>();
	
	private List<DQSignalListener> listeners = new LinkedList<>();
	
	private long sleep = 0;
	
	private volatile DQSignal currentSignal;
	private AdapterInterface adapter;
	
	public static void main (String args[]) throws InterruptedException, NiDaqException {
		AdapterInterface adapter = new FiveWLNIDaqAdapter();
		//adapter = new ThreeWLOneHeadSimulator();
		//adapter = new DQreplay();
		//adapter = new FiveWLDevicePlayback("testRGB.txt");
		//adapter = new FiveWLDevicePlaybackWithStream("testInfared.txt");
		//adapter = new FiveWLDevicePlaybackWithStream("testRGB.txt");
		adapter = new FiveWLOneHeadSimulator();
		//adapter.setADCardOrConfigParameter("Dev1");
		adapter.setADCardOrConfigParameter(NiDaq.getDeviceNames().get(0));
		
		DQPipelineElement vis = new DQMeasurementVisualizer();
		DQPipelineElement vi2 = new DQSinglePeriodMeasurementVisualizer(true);
		
		
		DQPipeline pipeline = new DQPipeline();
		
		DQPipelineElement triggerMarker = new FiveWLMarker();
		
		DQPipelineElement valueExtractor = new FiveWLExtractor(new FiveWLMeasurePoints());
		
		DQPipelineElement extractedDataVis = new LaserVoltageVisualizer(false);
		
		DQPipelineElement transmissionExtractor = new TransmissionExtractor(true);
		DQPipelineElement dqExtractor = new DQExtractor();
		DQPipelineElement sizeExtractor = new ProbabilityBasedDiameterExtractor(new File("D:/mietemp/rgb-latex-in-water.miezip"));
		//DQPipelineElement concenentrationExtractor = new ConcentrationExtractor(measureLengthInCm, wl1, wl2, wl3)
		
		//DQPipelineElement writer = new RawDataWriter("testInfared.txt");
		
		pipeline.addPipelineElement(adapter);
		pipeline.addPipelineElement(triggerMarker);
		pipeline.addPipelineElement(vis);
		pipeline.addPipelineElement(vi2);
		pipeline.addPipelineElement(valueExtractor);
		pipeline.addPipelineElement(extractedDataVis);
		pipeline.addPipelineElement(transmissionExtractor);
		pipeline.addPipelineElement(new TransmissionVisualizer(true));
		pipeline.addPipelineElement(dqExtractor);
		pipeline.addPipelineElement(new DQVisualizer());
		pipeline.addPipelineElement(sizeExtractor);
		//pipeline.addPipelineElement(writer);
		pipeline.start();
		//Thread.sleep(1000);
		//System.out.println("Killing threads");
		//pipeline.stop();
	}
	
	public void addPipelineElement(DQPipelineElement element) {
		if(run) throw new PipelineAlreadyRunningException("This pipeline is already in a running state and connot be altered");
		
		pipelineElements.add(element);
		ArrayBlockingQueue<DQSignal> out = new ArrayBlockingQueue<>(capacity);
		if (element instanceof AdapterInterface) {
			this.adapter = (AdapterInterface) element;
		}
		
//		if(!queues.isEmpty())
//			element.setInQueue(queues.get(queues.size()-1)); //First element must be a producer
//		
//		element.setQoutQueue(out);
//		queues.add(out);
	}

	
	public void start() {
		
		queues.clear();
		pipelineThreads.clear();
		
		for (DQPipelineElement element: pipelineElements) {
			BlockingQueue<DQSignal> out = new ArrayBlockingQueue<>(capacity);
			PipelineThread dqPipelineThread = new PipelineThread(element);
			
			if (!queues.isEmpty()) { //first thread is producer
				dqPipelineThread.setInQueue(queues.get(queues.size()-1));
			}
			
			dqPipelineThread.setQoutQueue(out);
			
			queues.add(out);
			pipelineThreads.add(dqPipelineThread);
		}
		
		//create last thread to terminate the queues
		pipelineThreads.add(new NullThread(queues.get(queues.size()-1)));
		
		run = true;
		
		for (Thread t: pipelineThreads) {
			t.start();
		}
		
		
	}
	
	public void stop() {
		run = false;
//		for (Thread t: pipelineThreads)
//			t.interrupt();
	}
	
	protected void setCurrentSignal(DQSignal signal) {
		currentSignal = signal;
		for (DQSignalListener listener: listeners)
			listener.newSignal(currentSignal);
	}
	
	public DQSignal getCurrentSignal() {
		return currentSignal;
	}
	
	public void addNewSignalListener(DQSignalListener listener) {
		listeners.add(listener);
	}
	
	public boolean removeListener(DQSignalListener listener) {
		return listeners.remove(listener);
	}
	
	
	
	

	
	class PipelineThread extends Thread {
		
		private DQPipelineElement element;
		protected BlockingQueue<DQSignal> in;
		protected BlockingQueue<DQSignal> out;


		public PipelineThread(DQPipelineElement element) {
			this.element = element;
			setDaemon(true);
			setName(element.description());
		}
		
		public void setInQueue(BlockingQueue<DQSignal> in) {
			this.in = in;

		}

		public void setQoutQueue(BlockingQueue<DQSignal> out) {
			this.out=out;
		}
		
		public String toString() {
			return element.description();
		}

		@Override
		public void run() {
			while (run && !isInterrupted()) {
				try {
					if (in == null)
						out.put(element.processDQElement(null)); // first thread in Pipeline: Producer
					else
						out.put(element.processDQElement(in.take()));
				} catch (InterruptedException e) {
					// check if thread should terminate
					if (!run || isInterrupted()) {
						System.out.println("Thread " + element.description() + " ending...");
						//stop();
						return;
					}
				}
			}
			//stop();
			System.out.println("Thread " + element.description() + " ending...");
			return;
		}
		
	}
	
	class NullThread extends Thread {
		
		private BlockingQueue<DQSignal> in;

		public NullThread(BlockingQueue<DQSignal> lastQueueElement) {
			this.in=lastQueueElement;
			setDaemon(true);
		}
		
		public void run() {
			while (run) {
			try {
				setCurrentSignal(in.take());
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				//check if thread should terminate
				if (!run) {
					return;
				}
			}
			catch (Exception everythingElse) {
				everythingElse.printStackTrace();
			}
			}
			return;
		}


		
	}

}

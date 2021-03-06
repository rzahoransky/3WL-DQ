package rzahoransky.dqpipeline;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import kirkwood.nidaq.access.NiDaq;
import kirkwood.nidaq.access.NiDaqException;
import rzahoransky.dqpipeline.analogueAdapter.GenericNIDaqAdapter;
import rzahoransky.dqpipeline.dataExtraction.DQExtractor;
import rzahoransky.dqpipeline.dataExtraction.ProbabilityBasedDiameterExtractor;
import rzahoransky.dqpipeline.dataExtraction.TransmissionExtractor;
import rzahoransky.dqpipeline.dataExtraction.rawDataExtraction.FiveWLExtractor;
import rzahoransky.dqpipeline.dataExtraction.rawDataExtraction.RawVoltageExtractorFactory;
import rzahoransky.dqpipeline.dataExtraction.rawDataExtraction.ThreeWLMeasurePoints;
import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.interfaces.AdapterInterface;
import rzahoransky.dqpipeline.interfaces.DQPipelineElement;
import rzahoransky.dqpipeline.listener.DQSignalListener;
import rzahoransky.dqpipeline.periodMarker.FiveWLMarker;
import rzahoransky.dqpipeline.periodMarker.MarkerFactory;
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
	
	public static void main (String args[]) throws InterruptedException, NiDaqException {
		AdapterInterface adapter = new GenericNIDaqAdapter();
		adapter = new FiveWLOneHeadSimulator();
		adapter.setADCardOrConfigParameter(NiDaq.getDeviceNames().get(0));
		
		DQPipelineElement vis = new DQMeasurementVisualizer();
		DQPipelineElement vi2 = new DQSinglePeriodMeasurementVisualizer(true);
		
		
		DQPipeline pipeline = new DQPipeline();
		
		DQPipelineElement triggerMarker = MarkerFactory.getPeriodMarker();
		
		DQPipelineElement valueExtractor = RawVoltageExtractorFactory.getRawVoltageExtractor();
		
		DQPipelineElement extractedDataVis = new LaserVoltageVisualizer(true);
		
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
	}
	
	public void addPipelineElement(DQPipelineElement element) {
		if(run) 
			throw new PipelineAlreadyRunningException("This pipeline is already in a running state and connot be altered");
		
		pipelineElements.add(element);
	}

	
	/**
	 * Add all DQQueue elements into queue. Initialize queue and run it
	 */
	public void start() {
		
		queues.clear();
		pipelineThreads.clear();
		
		for (DQPipelineElement element: pipelineElements) {
			BlockingQueue<DQSignal> out = new ArrayBlockingQueue<>(capacity);
			DQPipelineThread dqPipelineThread = new DQPipelineThread(element);
			
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
	
	/**
	 * Set queue to stop
	 */
	public void stop() {
		run = false;
		for (DQSignalListener listener: listeners)
			listener.closing();
	}
	
	/** inform listener of new DQPipeline element**/
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
	
	
	
	

	
	/**
	 * Encapsulates each {@link DQPipelineElement} in a thread. 
	 * Binds in and out Queue of a {@link BlockingQueue} to the {@link DQPipelineElement}
	 * @author richard
	 *
	 */
	class DQPipelineThread extends Thread {
		
		private DQPipelineElement element;
		protected BlockingQueue<DQSignal> in;
		protected BlockingQueue<DQSignal> out;


		public DQPipelineThread(DQPipelineElement element) {
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
			while (run && !isInterrupted()) { //run until it should be interrupted
				try {
					if (in == null) { 
						//If there is no in-element: This is a NI Adapter (source)
						DQSignal fromAdapter = element.processDQElement(null);
						if (fromAdapter != null) //if nothing from NI Adapter received: Skip and try again in next loop
							out.offer(fromAdapter, 1000, TimeUnit.MILLISECONDS); 
					} else { 
						//normal DQPipelineElement: Get element from in-queue and offer the result of method processDQElement to the out queue
						DQSignal elementFromPredecessor = in.poll(1000, TimeUnit.MILLISECONDS);
						if (elementFromPredecessor != null)
							out.offer(element.processDQElement(elementFromPredecessor), 1000, TimeUnit.MILLISECONDS);
					}
				} catch (InterruptedException e) {
					// check if thread should terminate
					if (!run || isInterrupted()) {
						System.out.println("Thread " + element.description() + " ending...");
						element.endProcessing(); //tell dqElement to end processing...
						return; //step out of while loop
					}
				}
			}
			// stop();
			System.out.println("Thread " + element.description() + " ending...");
			element.endProcessing(); //inform this DQPipeline element to end last tasks (e.g. flush write to file)
			return;
		}
		
	}
	
	/**
	 * Thread at the end of the pipeline to terminate the last DQElement and make
	 * space for new element. Additionally, notifies listeners.
	 * 
	 * @author richard
	 *
	 */
	class NullThread extends Thread {

		private BlockingQueue<DQSignal> in;

		public NullThread(BlockingQueue<DQSignal> lastQueueElement) {
			this.in = lastQueueElement;
			setDaemon(true);
			setName("Queue-Daemon");
		}

		public void run() {
			while (run) {
				try {
					setCurrentSignal(in.take()); //inform DQPipeline listener
					Thread.sleep(sleep);
				} catch (InterruptedException e) {
					if (!run) {
						return; // check if thread should terminate
					}
				} catch (Exception everythingElse) {
					everythingElse.printStackTrace();
				}
				
			}
			return;
		}

	}

}

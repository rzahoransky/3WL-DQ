package rzahoransky.dqpipeline.simulation;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

import kirkwood.nidaq.access.NiDaqException;
import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.interfaces.AbstractDQPipelineElement;
import rzahoransky.dqpipeline.interfaces.AdapterInterface;

public class ThreeWLOneHeadSimulator extends AbstractDQPipelineElement implements AdapterInterface{
	
	int step = 0;
	int periodLength = 160;
	int sampleSize = 500;
	Random r = new Random();

	public ThreeWLOneHeadSimulator() {
		
	}
	
	public ThreeWLOneHeadSimulator(int periodLength, int sampleSize) {
		this.periodLength=periodLength;
		this.sampleSize=sampleSize;
	}

	
	private DQSignal generateMeasurement() {
		int numberOfPeriods = (sampleSize/periodLength) + 2; //generate three period longer than required (to be safe :) )
		
		ArrayList<Double> measure = new ArrayList<>(sampleSize+periodLength);
		ArrayList<Double> reference = new ArrayList<>(sampleSize+periodLength);
		
		for (int i = 0; i< numberOfPeriods;i++) {
			DQSignal period = generateFullPeriod(periodLength);
			measure.addAll(period.getMeasurement());
			reference.addAll(period.getReference());
		}
		
		//begin at random position
		
		int offset = r.nextInt(periodLength);
		
		ArrayList<Double> subsetMeasure = new ArrayList<>();
		ArrayList<Double> subsetReference = new ArrayList<>();
				
		subsetMeasure.addAll(measure.subList(offset, offset+sampleSize));
		subsetReference.addAll(reference.subList(offset, offset+sampleSize));

		
		
		DQSignal result = new DQSignal(subsetReference, subsetMeasure);
		return result;
		
		
		
		//RawDataMeasure measure = generateFullPeriod();
	}
	
	private DQSignal generateFullPeriod(int periodLength) {
		ArrayList<Double> measure = new ArrayList<>(sampleSize);
		ArrayList<Double> reference = new ArrayList<>(sampleSize);
		
		//20% steps
		
		for (step = 0;step<0.2*periodLength;step++) {
			measure.add(r.nextDouble()*0.5+5); //measure:5V+/-0.5V
			reference.add(r.nextDouble()*0.5+7); //reference: 7V +/-0.5V
		}
		
		for (;step<0.4*periodLength;step++) {
			measure.add(r.nextDouble()*0.5+4); //measure:4V+/-0.5V
			reference.add(r.nextDouble()*0.5+5); //reference: 5V +/-0.5V
		}
		
		for (;step<0.6*periodLength;step++) {
			measure.add(r.nextDouble()*0.3+3); //measure:3V+/-0.3V
			reference.add(r.nextDouble()*0.3+4); //reference: 4V +/-0.3V
		}
		
		for (;step<periodLength;step++) {
			measure.add(r.nextDouble()*0.3+0); //measure:0V+/-0.3V
			reference.add(r.nextDouble()*0.2-2); //reference: -2V +/-0.2V
		}
		
		DQSignal current = new DQSignal(reference, measure);
		
		return current;
		
	}

	@Override
	public DQSignal processDQElement(DQSignal in) {
		return generateMeasurement();
		
	}

	@Override
	public String description() {
		return "3WL simulator";
	}
	@Override
	public void setADCardOrConfigParameter(String device) {
		// TODO Auto-generated method stub
		
	}

}

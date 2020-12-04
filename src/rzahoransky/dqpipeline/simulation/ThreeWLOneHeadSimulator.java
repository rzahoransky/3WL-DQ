package rzahoransky.dqpipeline.simulation;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;

import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.interfaces.AbstractDQPipelineElement;
import rzahoransky.dqpipeline.interfaces.AdapterInterface;

public class ThreeWLOneHeadSimulator extends AbstractDQPipelineElement implements AdapterInterface{
	
	int step = 0;
	long sleep = 60;
	int periodLength = 160;
	int sampleSize = 400;
	Random r = new Random();
	
	JSlider wl1Meas = new JSlider(JSlider.VERTICAL,-1000,1000,1000);
	JSlider wl2Meas = new JSlider(JSlider.VERTICAL,-1000,1000,1000);
	JSlider wl3Meas = new JSlider(JSlider.VERTICAL,-1000,1000,1000);
	JSlider offsetMeas = new JSlider(JSlider.VERTICAL,-1000,1000,1000);
	JSlider wl1Ref = new JSlider(JSlider.VERTICAL,-1000,1000,1000);
	JSlider wl2Ref = new JSlider(JSlider.VERTICAL,-1000,1000,1000);
	JSlider wl3Ref = new JSlider(JSlider.VERTICAL,-1000,1000,1000);
	JSlider offsetRef = new JSlider(JSlider.VERTICAL,-1000,1000,1000);

	public ThreeWLOneHeadSimulator() {
		displayControls();
	}
	
	public ThreeWLOneHeadSimulator(int periodLength, int sampleSize) {
		this.periodLength=periodLength;
		this.sampleSize=sampleSize;
		displayControls();
	}
	
	public void displayControls() {
		JFrame control = new JFrame("Simulator Control");
		control.setSize(new Dimension(60, 800));
		control.add(sliderPanel());
		control.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		control.setVisible(true);
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
		
		//create trigger and mode with 0 values
		ArrayList<Double> mode = new ArrayList<>();
		ArrayList<Double> trigger = new ArrayList<>();
		
		for (int i = 0; i<subsetReference.size(); i++) {
			mode.add(0d);
			trigger.add(0d);
		}

		
		
		//DQSignal result = new DQSignal(subsetReference, subsetMeasure);
		DQSignal result = new DQSignal(subsetReference, subsetMeasure, mode, trigger);
		return result;
		
		
		
		//RawDataMeasure measure = generateFullPeriod();
	}
	
	private DQSignal generateFullPeriod(int periodLength) {
		ArrayList<Double> measure = new ArrayList<>(sampleSize);
		ArrayList<Double> reference = new ArrayList<>(sampleSize);
		
		//20% steps
		
		for (step = 0;step<0.2*periodLength;step++) {
			measure.add(r.nextDouble()*0.00+wl1Meas.getValue()/100d); //measure was :5V+/-0.5V
			reference.add(r.nextDouble()*0.00+wl1Ref.getValue()/100d); //reference was : 7V +/-0.5V
		}
		
		for (;step<0.4*periodLength;step++) {
			measure.add(r.nextDouble()*0.00+wl2Meas.getValue()/100d); //measure:4V+/-0.5V
			reference.add(r.nextDouble()*0.00+wl2Ref.getValue()/100d); //reference: 5V +/-0.5V
		}
		
		for (;step<0.6*periodLength;step++) {
			measure.add(r.nextDouble()*0.0+wl3Meas.getValue()/100d); //measure:3V+/-0.3V
			reference.add(r.nextDouble()*0.0+wl3Ref.getValue()/100d); //reference: 4V +/-0.3V
		}
		
		//offset
		for (;step<periodLength;step++) {
			measure.add(r.nextDouble()*0.0+offsetMeas.getValue()/100d); //measure:0V+/-0.3V
			reference.add(r.nextDouble()*0.0+offsetRef.getValue()/100d); //reference: -2V +/-0.2V
		}
		
		DQSignal current = new DQSignal(reference, measure);
		
		return current;
		
	}

	@Override
	public DQSignal processDQElement(DQSignal in) {
		try {
			Thread.sleep(sleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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

	@Override
	public void clearTask() {
		// TODO Auto-generated method stub
		
	}
	
	private JPanel sliderPanel() {
		JPanel panel = new JPanel();
		//panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridy=0;
		c.gridx=0;
		c.weighty=1;
		c.fill=GridBagConstraints.VERTICAL;
		c.gridheight=1;
		panel.add(wl1Meas,c);
		c.gridx++;
		panel.add(wl2Meas,c);
		c.gridx++;
		panel.add(wl3Meas,c);
		c.gridx++;
		panel.add(offsetMeas,c);
		c.gridy=1;
		c.gridx=0;
		panel.add(wl1Ref,c);
		c.gridx++;
		panel.add(wl2Ref,c);
		c.gridx++;
		panel.add(wl3Ref,c);
		c.gridx++;
		panel.add(offsetRef,c);
		return panel;
	}


}

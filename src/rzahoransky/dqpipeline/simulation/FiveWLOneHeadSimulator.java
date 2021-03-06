package rzahoransky.dqpipeline.simulation;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;

import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.interfaces.AbstractDQPipelineElement;
import rzahoransky.dqpipeline.interfaces.AdapterInterface;

public class FiveWLOneHeadSimulator extends AbstractDQPipelineElement implements AdapterInterface{
	
	int step = 0;
	int periodLength = 80;
	int sampleSize = 1200;
	long sleep = 30;
	Random r = new Random();
	JSlider wl1 = new JSlider(JSlider.VERTICAL,-1000,1000,1000);
	JSlider wl2 = new JSlider(JSlider.VERTICAL,-1000,1000,1000);
	JSlider wl3 = new JSlider(JSlider.VERTICAL,-1000,1000,1000);
	JCheckBox triggerBox = new JCheckBox("Trigger");
	ArrayList<Double> zeros = new ArrayList<>();

	public FiveWLOneHeadSimulator() {
		JFrame control = new JFrame("Simulator Control");
		control.setSize(new Dimension(60, 800));
		control.add(sliderPanel());
		control.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		triggerBox.setSelected(true);
		control.setVisible(true);
		
		for (int i = 0; i<periodLength*sampleSize;i++) {
			zeros.add(0d);
		}
	}
	
	public FiveWLOneHeadSimulator(int periodLength, int sampleSize) {
		this.periodLength=periodLength;
		this.sampleSize=sampleSize;
	}

	
	private DQSignal generateMeasurement() {
		int numberOfPeriods = (sampleSize/periodLength) + 2; //generate three period longer than required (to be safe :) )
		
		ArrayList<Double> measure = new ArrayList<>(sampleSize+periodLength);
		ArrayList<Double> reference = new ArrayList<>(sampleSize+periodLength);
		ArrayList<Double> mode = new ArrayList<>(sampleSize+periodLength);
		ArrayList<Double> trigger = new ArrayList<>(sampleSize+periodLength);
		
		for (int i = 0; i< numberOfPeriods;i++) {
			DQSignal period = generateFullPeriod(periodLength);
			measure.addAll(period.getMeasurement());
			reference.addAll(period.getReference());
			mode.addAll(period.getMode());
			if(triggerBox.isSelected())
					trigger.addAll(period.getTrigger());
			else
				trigger.addAll(zeros);
		}
		
		//begin at random position
		
		int offset = r.nextInt(periodLength);
		
		ArrayList<Double> subsetMeasure = new ArrayList<>();
		ArrayList<Double> subsetReference = new ArrayList<>();
		ArrayList<Double> subsetMode = new ArrayList<>();
		ArrayList<Double> subsetTrigger = new ArrayList<>();
				
		subsetMeasure.addAll(measure.subList(offset, offset+sampleSize));
		subsetReference.addAll(reference.subList(offset, offset+sampleSize));
		subsetMode.addAll(mode.subList(offset, offset+sampleSize));
		subsetTrigger.addAll(trigger.subList(offset, offset+sampleSize));

		
		
		DQSignal result = new DQSignal(subsetReference, subsetMeasure, subsetMode, subsetTrigger);
		return result;
		
		
		
		//RawDataMeasure measure = generateFullPeriod();
	}
	
	private DQSignal generateFullPeriod(int periodLength) {
		ArrayList<Double> measure = new ArrayList<>(sampleSize);
		ArrayList<Double> reference = new ArrayList<>(sampleSize);
		ArrayList<Double> mode = new ArrayList<>(sampleSize);
		ArrayList<Double> trigger = new ArrayList<>(sampleSize);
		int triggerAt = (int) (0.66*periodLength);
		
		//20% steps
		
		//WL1
		for (step = 0;step<0.2*periodLength;step++) {
			measure.add(r.nextDouble()*0.0+wl1.getValue()/100d); //measure:5V+/-0.5V
			reference.add(r.nextDouble()*0.1+7); //reference: 7V +/-0.5V
			mode.add(4d); //mode is 4
			trigger.add(0d);
		}
		
		//WL2
		for (;step<0.4*periodLength;step++) {
			measure.add(r.nextDouble()*0.0+wl2.getValue()/100d); //measure:4V+/-0.5V
			reference.add(r.nextDouble()*0.1+5); //reference: 5V +/-0.5V
			mode.add(4d); //mode is 4
			trigger.add(0d);
		}
		
		//WL3
		for (;step<0.6*periodLength;step++) {
			measure.add(r.nextDouble()*0.00+wl3.getValue()/100d); //measure:3V+/-0.3V
			reference.add(r.nextDouble()*0.1+4); //reference: 4V +/-0.3V
			mode.add(4d); //mode is 4
			trigger.add(0d);
		}
		
		//Offset
		for (;step<periodLength;step++) {
			measure.add(r.nextDouble()*0.0+-7); //measure:-7V+/-0.3V
			reference.add(r.nextDouble()*0.0-8); //reference: -8V +/-0.2V
			mode.add(4d); //mode is 4
			if (step==triggerAt)
				trigger.add(4d);
			else 
				trigger.add(0d);
			
		}
		
		DQSignal current = new DQSignal(reference, measure, mode, trigger);
		
		return current;
		
	}

	@Override
	public DQSignal processDQElement(DQSignal in) {
		try {
			Thread.sleep(sleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
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
	
	private JPanel sliderPanel() {
		JPanel panel = new JPanel();
		//panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridy=0;
		c.gridx=0;
		c.weighty=1;
		c.fill=GridBagConstraints.VERTICAL;
		c.gridheight=GridBagConstraints.RELATIVE;
		panel.add(wl1,c);
		c.gridx++;
		panel.add(wl2,c);
		c.gridx++;
		panel.add(wl3,c);
		c.gridy=1;
		c.gridx=0;
		c.gridwidth=3;
		c.weightx=1;
		c.weighty=0;
		c.fill=GridBagConstraints.HORIZONTAL;
		panel.add(triggerBox,c);
		return panel;
	}

	@Override
	public void clearTask() {
		// TODO Auto-generated method stub
		
	}


}

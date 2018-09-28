package rzahoransky.gui.adjustmentGui;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JOptionPane;

import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.interfaces.DQPipelineElement;
import rzahoransky.dqpipeline.listener.DQSignalListener;
import rzahoransky.utils.TransmissionType;

public class PlayTheDQSound implements Runnable, DQSignalListener {

	private volatile TransmissionType type;
	volatile double transmission = 0;
	private boolean stop;
	private boolean makeSound = true;
	private AudioFormat af;
	private SourceDataLine line;
	public static final int SAMPLE_RATE = 64 * 1024; // ~16KHz
	private byte[] sin = new byte[300];
	
	public PlayTheDQSound() {
		try {
			setupAudio();
			makeSound = true;
		} catch (LineUnavailableException e) {
			JOptionPane.showMessageDialog(null, "Audio not available");
		}
		
		stop=false;
		type = TransmissionType.TRANSMISSIONWL1;
	}
	
	private void setupAudio() throws LineUnavailableException {
	    af = new AudioFormat(SAMPLE_RATE, 8, 1, true, true);
        line = AudioSystem.getSourceDataLine(af);
        line.open(af,SAMPLE_RATE);
        line.start();
	}

	@Override
	public void run() {
		while (!stop) {
			if(makeSound) {
				double exp = ((double) 5) / 12d;
	            for (int i = 0; i < sin.length; i++) {
	            	double f = 220 *transmission* Math.pow(2d, exp);
	                double period = (double)SAMPLE_RATE / f;
	                double angle = 2.0 * Math.PI * i / period;
	                sin[i] = (byte)(Math.sin(angle) * 127f);
	                
	                //second voice
//	            	double f2 = 10 *transmission* Math.pow(2d, exp);
//	                double period2 = (double)SAMPLE_RATE / f2;
//	                double angle2 = 2.0 * Math.PI * i / period2;
//	                sin[i] += (byte)(Math.sin(angle2) * 127f);
	            }
	            
	            //find phase 0
	            
	            int phaseZero = 0;
	            for (int i = sin.length-1; i>0; i--) {
	            	if (sin[i]<2)
	            		phaseZero=i;
	            }
	            if (line.available()>10)
	            	line.write(sin, 0, phaseZero);
	            //System.out.println(line.available());
	            //System.out.println(count);
	            
			} else {
				try {
					Thread.sleep(30);
				} catch (InterruptedException e) {}
			}
		}
		
	}
	
	public void setTransmissionType(TransmissionType type) {
		this.type = type;
	}
	
	public void stop() {
		this.stop = true;
	}

	@Override
	public void newSignal(DQSignal currentSignal) {
		transmission = currentSignal.getTransmission(type);
		//System.out.println(System.currentTimeMillis());
	}
	
	

}

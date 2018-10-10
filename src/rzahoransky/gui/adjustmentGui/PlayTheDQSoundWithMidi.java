package rzahoransky.gui.adjustmentGui;

import java.util.HashMap;

import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import javax.swing.JOptionPane;

import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.listener.DQSignalListener;
import rzahoransky.dqpipeline.listener.DQSignalListenerAdapter;
import rzahoransky.utils.TransmissionType;

public class PlayTheDQSoundWithMidi extends DQSignalListenerAdapter implements AudioOutput {


	private volatile TransmissionType type;
	volatile double transmission = 0;

	protected Synthesizer synth;
	public Soundbank soundbank;
	protected Receiver synthRcvr;
	protected int intrument = 78; //instrument 78
	//protected int intrument = 94; //instrument 78
	protected ShortMessage msg;

	public PlayTheDQSoundWithMidi(TransmissionType type) {
		try {
			setupAudio();
			this.type = type;
			msg = new ShortMessage(ShortMessage.NOTE_ON,1,0,100);
		} catch (MidiUnavailableException | InvalidMidiDataException e) {
			JOptionPane.showMessageDialog(null, "Audio not available");
		}

		type = TransmissionType.TRANSMISSIONWL3;
	}

	private void setupAudio() throws MidiUnavailableException, InvalidMidiDataException {
		synth = MidiSystem.getSynthesizer();
		synth.open();
		soundbank = synth.getDefaultSoundbank();
		synthRcvr = synth.getReceiver();
		Instrument intstrument = soundbank.getInstruments()[intrument];
		synthRcvr.send(new ShortMessage(ShortMessage.PROGRAM_CHANGE, 1,this.intrument, 0),-1);
	}

	public void setTransmissionType(TransmissionType type) {
		this.type = type;
	}

	@Override
	public void newSignal(DQSignal currentSignal) {
		
		try {
			transmission = currentSignal.getTransmission(type);
			msg.setMessage(ShortMessage.NOTE_OFF, msg.getChannel(), msg.getData1(), msg.getData2());
			//synthRcvr.send(msg, -1);
			double data1 = Math.max(0, Math.min(transmission*60,120));
			msg.setMessage(ShortMessage.NOTE_ON, msg.getChannel(),(int) data1, msg.getData2());
			synthRcvr.send(msg, -1);
			//System.out.println("Playing...");
		} catch (Exception e) {
			// Do nothing...
		} //play, channel, tone, pressure
		
	}

	@Override
	public void close() {
		try {
		synthRcvr.close();
		synth.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}

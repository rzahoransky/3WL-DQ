package rzahoransky.test;

import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;

public class Midi {
	
	public static void main(String[] args) throws MidiUnavailableException, InterruptedException, InvalidMidiDataException {
	    Synthesizer synth = MidiSystem.getSynthesizer();
        Soundbank soundbank = synth.getDefaultSoundbank();
        Instrument[] instr = soundbank.getInstruments();
        synth.loadInstrument(instr[40]);

	    MidiChannel chan[] = synth.getChannels();
	    synth.open();
	    // Check for null; maybe not all 16 channels exist.
	    if (chan[4] != null) {
	    	ShortMessage sm = new ShortMessage( );
	    	sm.setMessage(ShortMessage.PROGRAM_CHANGE, 9, 40, 0); //9 ==> is the channel 10.
	         chan[4].noteOn(110, 255);
	         chan[4].noteOn(60, 93);
	         chan[4].noteOn(40, 93);
	         Thread.sleep(100);
	    }
	    
	    ShortMessage myMsg = new ShortMessage();
	    // Play the note Middle C (60) moderately loud
	    // (velocity = 93)on channel 4 (zero-based).
	    myMsg.setMessage(ShortMessage.NOTE_ON, 4, 60, 93); 
	    //Synthesizer synth = MidiSystem.getSynthesizer();
	    Receiver synthRcvr = synth.getReceiver();
	    
	    ShortMessage sm = new ShortMessage( );
	    sm.setMessage(ShortMessage.PROGRAM_CHANGE, 4, 4, 0); //9 ==> is the channel 10.
	    synthRcvr.send(sm, -1);
	    synthRcvr.send(myMsg, -1); // -1 means no time stamp
	    Thread.sleep(1000);
	    sm = new ShortMessage( );
	    sm.setMessage(ShortMessage.PROGRAM_CHANGE, 4, 50, 0); //9 ==> is the channel 10.
	    synthRcvr.send(sm, -1);
	    synthRcvr.send(myMsg, -1); // -1 means no time stamp
	    Thread.sleep(1000);
	}

}

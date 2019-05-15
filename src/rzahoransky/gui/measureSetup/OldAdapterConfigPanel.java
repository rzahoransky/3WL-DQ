package rzahoransky.gui.measureSetup;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import gui.FileGui;
import kirkwood.nidaq.access.NiDaqException;
import rzahoransky.dqpipeline.analogueAdapter.GenericNIDaqAdapter;
import rzahoransky.dqpipeline.interfaces.AdapterInterface;
import rzahoransky.dqpipeline.simulation.FiveWLDevicePlaybackWithStream;

public class OldAdapterConfigPanel extends JPanel {

	private static final long serialVersionUID = 7606088489371310818L;
	
	public static void main(String[] args) {
		GenericNIDaqAdapter adapter = new GenericNIDaqAdapter();
		OldAdapterConfigPanel panel = new OldAdapterConfigPanel(adapter);
		JFrame test = new JFrame("Ad ACard Test");
		test.add(panel);
		test.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		test.setVisible(true);
	}

	public OldAdapterConfigPanel(AdapterInterface adapter) {
		
		if (adapter instanceof GenericNIDaqAdapter) 
			add(NiDaqAdapterPanel(adapter));
		else if (adapter instanceof FiveWLDevicePlaybackWithStream)
			add(SimulationAdapterPanel((FiveWLDevicePlaybackWithStream)adapter));
	}

	private JPanel SimulationAdapterPanel(FiveWLDevicePlaybackWithStream adapter) {
		FileGui gui = new FileGui("Choose measurement file", new FileNameExtensionFilter("DQL-Files (*.csv)","csv"));
		gui.getTextField().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				adapter.setADCardOrConfigParameter(gui.getTextField().getText());
				
			}
		});
		return gui;
	}

	private JComboBox<String> NiDaqAdapterPanel(AdapterInterface adapter) {
		
		JComboBox<String> combo = new JComboBox<>();
		
		for (int i = 0; i<10;i++) {
			String device = "Dev"+i;
			if(testDevice(device))
				combo.addItem(device);
		}
		
		combo.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				adapter.setADCardOrConfigParameter((String) combo.getModel().getSelectedItem());
			}
		});
		
		return combo;
	}
	
	private boolean testDevice(String device) {
		
		GenericNIDaqAdapter adapter = new GenericNIDaqAdapter();
		adapter.setADCardOrConfigParameter(device);
		try {
			adapter.getNextMeasurement();
			adapter.clearTask();
		} catch (NiDaqException e) {
			e.printStackTrace();
			return false;
		}
		return true;
		
//		NiDaq daq = new NiDaq();
//		Pointer task;
//		int samplesPerChannel = 1;
//		try {
//			task = daq.createTask("");
//			// reference channel
//			daq.createAIVoltageChannel(task, device + "/" + "ai0:3", "", Nicaiu.DAQmx_Val_Cfg_Default, -10,
//					10, Nicaiu.DAQmx_Val_Volts, null);
//			/**
//
//			// measurement channel
//			daq.createAIVoltageChannel(task, adCard + "/" + "ai1:ai1", "", Nicaiu.DAQmx_Val_Cfg_Default,
//					minVoltage, maxVoltage, Nicaiu.DAQmx_Val_Volts, null);
//
//			// mode channel 
//				daq.createAIVoltageChannel(task, adCard + "/" + "ai2:ai2", "", Nicaiu.DAQmx_Val_Cfg_Default, minVoltage,
//						maxVoltage, Nicaiu.DAQmx_Val_Volts, null);
//
//			// trigger channel 
//				daq.createAIVoltageChannel(task, adCard + "/" + "ai3:ai3", "", Nicaiu.DAQmx_Val_Cfg_Default,
//						minVoltage, maxVoltage, Nicaiu.DAQmx_Val_Volts, null);
//			
//			**/
//			daq.cfgSampClkTiming(task, "", 100000.0, Nicaiu.DAQmx_Val_Rising, Nicaiu.DAQmx_Val_FiniteSamps, samplesPerChannel);
//			//init Buffer
//			DoubleBuffer db = DoubleBuffer.allocate(samplesPerChannel*4);
//			daq.readAnalogF64(task, samplesPerChannel, -1, Nicaiu.DAQmx_Val_GroupByChannel, db, db.capacity(), reads);
//		} catch (NiDaqException e) {
//			return false;
//		}
//		
//		return true;
		


	}

}

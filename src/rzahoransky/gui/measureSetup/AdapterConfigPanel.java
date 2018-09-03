package rzahoransky.gui.measureSetup;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import kirkwood.nidaq.access.NiDaq;
import kirkwood.nidaq.access.NiDaqException;
import rzahoransky.dqpipeline.analogueAdapter.FiveWLNIDaqAdapter;
import rzahoransky.dqpipeline.interfaces.AdapterInterface;
import rzahoransky.dqpipeline.simulation.FiveWLDevicePlaybackWithStream;

public class AdapterConfigPanel extends JPanel{

	public AdapterConfigPanel(AdapterInterface adapterInterface) {
		if (adapterInterface instanceof FiveWLDevicePlaybackWithStream) {
			
		} else {
			getNIDevices();
		}
	}

	private void getNIDevices() {
		JComboBox<String> devices = new JComboBox<>();
		
		try {
			for (String device: new NiDaq().getDeviceNames()) {
				devices.addItem(device);
			}
		} catch (NiDaqException e) {
			// Could not read devices
			e.printStackTrace();
		}
		
		add(devices);
		
	}

}

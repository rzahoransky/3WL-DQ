package rzahoransky.gui.measureSetup;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import kirkwood.nidaq.access.NiDaq;
import kirkwood.nidaq.access.NiDaqException;
import rzahoransky.dqpipeline.analogueAdapter.FiveWLNIDaqAdapter;
import rzahoransky.dqpipeline.interfaces.DQPipelineElement;
import rzahoransky.dqpipeline.simulation.FiveWLDevicePlaybackWithStream;

public class AdapterSelectGui extends JPanel implements ActionListener {
	
	JComboBox<String> combo;
	GridBagConstraints c = new GridBagConstraints();
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("Test");
		frame.setSize(400, 400);
		frame.add(new AdapterSelectGui());
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public AdapterSelectGui() {
		setLayout(new GridBagLayout());
		c.weightx=1;
		c.fill=GridBagConstraints.NONE;
		add(new JLabel("NiDaq Adapter: "),c);
		c.gridy++;
		combo = new JComboBox<>();
		combo.setMinimumSize(new Dimension(100, 30));
		combo.setMaximumSize(new Dimension(100, 30));
		addDevivces();
		combo.addActionListener(this);
		c.anchor=GridBagConstraints.LAST_LINE_START;
		add(combo,c);
	}
	
	public String getSelectedDevice() {
		return (String) combo.getSelectedItem();
	}
	
	public boolean isDevice() {
		return ((String)combo.getSelectedItem()).toUpperCase().contains("DEV");
	}
	
	private void addDevivces() {
		try {
			List<String> devices = NiDaq.getDeviceNames();
			
			if(!devices.isEmpty()) {
			for (String device: devices) {
				combo.addItem(device);
			}
			
			//read old Device
			String dev = MeasureSetUp.getInstance().getProperty(MeasureSetupEntry.NIADAPTER);
			if(includes(dev))
				combo.setSelectedItem(MeasureSetUp.getInstance().getProperty(MeasureSetupEntry.NIADAPTER));
			
			} else {
				combo.addItem("NONE FOUND");
			}
			
		} catch (NiDaqException e) {
			combo.addItem("NiDaq Error");
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		//MeasureSetUp.getInstance().setProperty(MeasureSetupEntry.NIADAPTER, (String) combo.getSelectedItem());
	}
	
	public boolean includes(String s) {
		for (int i = 0;i<combo.getModel().getSize();i++) {
			if(combo.getModel().getElementAt(i).equals(s))
				return true;
		}
		return false;
	}
	

}

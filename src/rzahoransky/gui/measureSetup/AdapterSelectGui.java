package rzahoransky.gui.measureSetup;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import kirkwood.nidaq.access.NiDaq;
import kirkwood.nidaq.access.NiDaqException;
import rzahoransky.utils.properties.MeasureSetUp;
import rzahoransky.utils.properties.MeasureSetupEntry;

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
		try {
			addDevivces();
		} catch (UnsatisfiedLinkError e) {
			showNoDriverMessage();
		}
		combo.addActionListener(this);
		c.anchor=GridBagConstraints.LAST_LINE_START;
		add(combo,c);
	}
	
	private void showNoDriverMessage() {
		JOptionPane.showMessageDialog(this,"NI driver not found. Please download from National Instrument Website (NI-DAQmx)\r\n"
				+ "www.ni.com",
			    "Driver not found", JOptionPane.ERROR_MESSAGE);
		System.exit(-1);
		
	}

	public String getSelectedDevice() {
		return (String) combo.getSelectedItem();
	}
	
	public boolean hasDevices() {
		return ((String)combo.getSelectedItem()).toUpperCase().contains("DEV") || ((String)combo.getSelectedItem()).toUpperCase().contains("DAQ");
	}
	
	private void addDevivces() throws UnsatisfiedLinkError {
		try {
			List<String> devices = NiDaq.getDeviceNames();

			if (!devices.isEmpty()) {
				for (String device : devices) {
					combo.addItem(device);
				}
				combo.setSelectedIndex(0);
				combo.setEditable(false);

				// read old Device
				String dev = MeasureSetUp.getInstance().getProperty(MeasureSetupEntry.NIADAPTER);
				if (includes(dev))
					combo.setSelectedItem(MeasureSetUp.getInstance().getProperty(MeasureSetupEntry.NIADAPTER));
				else
					MeasureSetUp.getInstance().setProperty(MeasureSetupEntry.NIADAPTER, devices.get(0));

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
		MeasureSetUp.getInstance().setProperty(MeasureSetupEntry.NIADAPTER, (String) combo.getSelectedItem());
	}
	
	public boolean includes(String s) {
		for (int i = 0;i<combo.getModel().getSize();i++) {
			if(combo.getModel().getElementAt(i).equals(s))
				return true;
		}
		return false;
	}
	

}

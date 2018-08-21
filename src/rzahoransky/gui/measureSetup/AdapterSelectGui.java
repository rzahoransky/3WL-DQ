package rzahoransky.gui.measureSetup;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import rzahoransky.dqpipeline.DQPipelineElement;
import rzahoransky.dqpipeline.analogueAdapter.FiveWLNIDaqAdapter;
import rzahoransky.dqpipeline.simulation.FiveWLDevicePlaybackWithStream;

public class AdapterSelectGui extends JPanel implements ActionListener {
	
	JComboBox<DQPipelineElement> combo = new JComboBox<>();
	GridBagConstraints c = new GridBagConstraints();
	JPanel adapterConfig = new JPanel();
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("Test");
		frame.setSize(400, 400);
		frame.add(new AdapterSelectGui());
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public AdapterSelectGui() {
		setLayout(new GridBagLayout());
		combo.addItem(new FiveWLNIDaqAdapter());
		combo.addItem(new FiveWLDevicePlaybackWithStream());
		combo.setMinimumSize(new Dimension(100, 30));
		combo.setMaximumSize(new Dimension(100, 30));
		combo.addActionListener(this);
		c.weightx=0;
		c.fill=GridBagConstraints.NONE;
		add(combo,c);
		add(adapterConfig);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		remove(adapterConfig);
		//adapterConfig = ((DQPipelineElement)combo.getSelectedItem()).showConfig();
		c.gridy=1;
		c.weightx=1;
		c.fill=GridBagConstraints.HORIZONTAL;
		add(adapterConfig,c);
		revalidate();
	}
	

}

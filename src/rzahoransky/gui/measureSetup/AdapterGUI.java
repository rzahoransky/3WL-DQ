package rzahoransky.gui.measureSetup;

import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import rzahoransky.dqpipeline.analogueAdapter.FiveWLNIDaqAdapter;
import rzahoransky.dqpipeline.interfaces.AdapterInterface;
import rzahoransky.dqpipeline.interfaces.DQPipelineElement;
import rzahoransky.dqpipeline.simulation.FiveWLDevicePlaybackWithStream;

public class AdapterGUI extends JPanel implements ItemListener {
	
	JComboBox<AdapterInterface> combo = new JComboBox<>();
	JPanel configPanel;
	
	public static void main(String[] args) {
		JFrame test = new JFrame("AdapterGUI test");
		test.setSize(400, 400);
		test.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		test.add(new AdapterGUI());
		test.setVisible(true);
	}

	public AdapterGUI() {
		setLayout(new FlowLayout());
		setupComboBox();
		setupConfigPanels();
		configPanel.setSize(300, 300);
		add(configPanel);
	}

	private void setupConfigPanels() {
		configPanel = new JPanel();
		configPanel.setLayout(new CardLayout());
		for (int i = 0; i<combo.getItemCount(); i++) {
			AdapterConfigPanel config = new AdapterConfigPanel(combo.getItemAt(i));
			configPanel.add(config, combo.getItemAt(i).toString());
		}
		
	}


	private void setupComboBox() {
		combo.addItem(new FiveWLNIDaqAdapter());
		combo.addItem(new FiveWLDevicePlaybackWithStream());
		add(combo);
		combo.addItemListener(this);
		
	}

	@Override
	public void itemStateChanged(ItemEvent arg0) {
		((CardLayout)configPanel.getLayout()).show(configPanel, arg0.getItem().toString());
		
	}
	
	

}

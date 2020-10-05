package rzahoransky.gui.measureGui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import rzahoransky.dqpipeline.DQPipeline;
import rzahoransky.utils.MeasureSetUp;

public class MeasureToolbar extends JToolBar {
	
	FactorsGui factors = new FactorsGui();
	
	public MeasureToolbar() {
		setFloatable(false);
		//add(getPauseBtn());
		add(getIoBtn());
		add(getFactorsGuiBtn());
		add(getShowVoltageVisualizerBtn());
	}
	
	private JToggleButton getPauseBtn() {
		JToggleButton pauseBtn = new JToggleButton("||");
		pauseBtn.createToolTip().setTipText("Pause processing");
		Color forgroundColor = pauseBtn.getBackground();

		pauseBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				MeasureSetUp.getInstance().setPause(!MeasureSetUp.getInstance().getPause());
				
				if (MeasureSetUp.getInstance().getPause()) {
					pauseBtn.setBackground(Color.RED);
					pauseBtn.setSelected(true);
				} else {
					pauseBtn.setBackground(forgroundColor);
					pauseBtn.setSelected(false);
				}
				
				
			}
		});

		return pauseBtn;
	}

	protected JButton getFactorsGuiBtn() {
		JButton factorBtn = new JButton("Show Factor List");
		factorBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				factors.setVisible(true);
				
			}
		});
		
		return factorBtn;
	}
	
	protected JButton getIoBtn() {
		return MeasureSetUp.getInstance().getTransmissionExtractor().getI0Btn();
	}
	
	protected JButton getShowVoltageVisualizerBtn() {
		JButton voltageBtn = new JButton("Show detector voltage");
		voltageBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				MeasureSetUp.getInstance().getLaserVoltageVisualizer().getFrame().setVisible(true);
			}
		});
		return voltageBtn;
	}

}

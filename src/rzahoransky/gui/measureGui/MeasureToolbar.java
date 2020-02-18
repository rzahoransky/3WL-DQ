package rzahoransky.gui.measureGui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JToolBar;

import rzahoransky.dqpipeline.DQPipeline;
import rzahoransky.gui.measureSetup.MeasureSetUp;

public class MeasureToolbar extends JToolBar {
	
	FactorsGui factors = new FactorsGui();
	
	public MeasureToolbar() {
		setFloatable(false);
		add(getPauseBtn());
		add(getIoBtn());
		add(getFactorsGuiBtn());
	}
	
	private JButton getPauseBtn() {
		JButton pauseBtn = new JButton("||");
		pauseBtn.createToolTip().setTipText("Pause processing");
		
		pauseBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				MeasureSetUp.getInstance().getPipeline();
				
			}
		});
		
		return null;
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

}

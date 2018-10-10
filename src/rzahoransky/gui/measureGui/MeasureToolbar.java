package rzahoransky.gui.measureGui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JToolBar;

import rzahoransky.gui.measureSetup.MeasureSetUp;

public class MeasureToolbar extends JToolBar {
	
	FactorsGui factors = new FactorsGui();
	
	public MeasureToolbar() {
		setFloatable(false);
		add(getIoBtn());
		add(getFactorGuiBtn());
	}
	
	protected JButton getFactorGuiBtn() {
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

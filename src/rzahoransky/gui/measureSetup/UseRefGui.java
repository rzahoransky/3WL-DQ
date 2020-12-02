package rzahoransky.gui.measureSetup;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import presets.Wavelengths;
import rzahoransky.utils.MeasureSetUp;

public class UseRefGui extends JPanel{

	public static void main(String[] args) {
		JFrame frame = new JFrame("Use Reference Test");
		frame.setSize(new Dimension(500,100));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new UseRefGui());
		frame.setVisible(true);

	}
	
	public UseRefGui() {
		for (Wavelengths wl: Wavelengths.values()) {
			add(new RefCheckBox(wl));
		}
	}
	
	class RefCheckBox extends JCheckBox{
		private Wavelengths wl;
		private MeasureSetUp setup = MeasureSetUp.getInstance();

		public RefCheckBox(Wavelengths wl) {
			this.wl = wl;
			setSelected(setup.getUseReference(wl));
			setText("Use Reference for "+wl.toShortString());
			this.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					setup.setUseReference(wl, isSelected());
					//System.out.println("Changed Reference usage for: "+wl.toString()+" to: "+isSelected());
				}
			});
		}
	}

}

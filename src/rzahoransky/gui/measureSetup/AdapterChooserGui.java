package rzahoransky.gui.measureSetup;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class AdapterChooserGui extends JPanel{
	
	MeasureSetUp setup = MeasureSetUp.getInstance();
	JLabel text;
	
	public AdapterChooserGui() {
		setupComponents();
	}
	
	private void setupComponents() {
		text = new JLabel("NIdaq Adapter: ");
	}
	

}

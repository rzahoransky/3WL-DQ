package rzahoransky.gui.adjustmentGui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import rzahoransky.utils.TransmissionType;

public class ButtonGroupGui extends JPanel {

	protected ButtonGroup btnGroup = new ButtonGroup();
	protected ArrayList<JRadioButton> buttons = new ArrayList<>();
	protected GridBagConstraints c = new GridBagConstraints();

	public static void main(String[] args) {
		JFrame frame = new JFrame("Test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 300);
		ButtonGroupGui gui = new ButtonGroupGui();
		gui.add(new JRadioButton("vgfsr"));
		gui.add(new JRadioButton("vgf3sr"));
		gui.add(new JRadioButton("vgfs24r"));
		gui.add(new JRadioButton("vgfs233r"));
		frame.add(gui);
		frame.setVisible(true);
	}

	ButtonGroupGui() {
		setLayout(new GridBagLayout());
		c.weightx=1;
		c.gridx=GridBagConstraints.RELATIVE;
		c.fill=GridBagConstraints.HORIZONTAL;
	}

	public void addJRadioButton(String text, ActionListener listener) {
		JRadioButton btn = new JRadioButton(text);
		btn.addActionListener(listener);
		buttons.add(btn);
		btnGroup.add(btn);
		add(btn,c);
		c.gridx++;
	}
	
	public void add(JRadioButton btn) {
		btnGroup.add(btn);
		buttons.add(btn);
		add(btn,c);
		//c.gridx++;
	}
	
	public void setActive (TransmissionType type) {
		for (JRadioButton button: buttons) {
			if (button.getText().toUpperCase().equals(type.toString().toUpperCase()))
				button.setSelected(true);
			else button.setSelected(false);
		}
	}

}

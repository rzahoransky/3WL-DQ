package rzahoransky.gui.measureGui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;

import rzahoransky.dqpipeline.DQPipeline;
import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.listener.DQSignalListener;

public class NumericDiameterGui extends JPanel implements DQSignalListener{

	protected JLabel diameter = new JLabel("");
	protected JLabel sigma = new JLabel("");
	protected JLabel density = new JLabel("");
	protected static final JLabel diameterString = new JLabel("Diameter in μm");
	protected static final JLabel sigmaString = new JLabel("Sigma: ");
	protected static final JLabel densityString = new JLabel("Particles per cm³");
	DecimalFormat df = new DecimalFormat("0.000"); 
	
	
	public NumericDiameterGui(DQPipeline pipeline) {
		Font font = new Font("Arial", Font.BOLD, 14);
		diameter.setFont(font);
		sigma.setFont(font);
		density.setFont(font);
		
		Font font_normal = new Font("Arial", Font.PLAIN, 14);
		diameterString.setFont(font_normal);
		sigmaString.setFont(font_normal);
		densityString.setFont(font_normal);
		
		pipeline.addNewSignalListener(this);
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx=0;
		c.gridy=0;
		c.weightx=1;
		c.weighty=1;
		c.fill=GridBagConstraints.HORIZONTAL;
		add(diameterString,c);
		c.gridx++;
		add(diameter, c);
		c.gridx++;
		add(sigmaString,c);
		c.gridx++;
		add(sigma,c);
		c.gridx++;
		add(densityString,c);
		c.gridx++;
		//density.setSize(50, 20);
		//density.setMaximumSize(new Dimension(50, 20));
		add(density,c);
	}
	
	

	@Override
	public void newSignal(DQSignal currentSignal) {
		try {
		diameter.setText(formatDouble(currentSignal.getDiameter().getAverageDiameter()));
		sigma.setText(formatDouble(currentSignal.getSigma()));
		density.setText(formatDouble(currentSignal.getVolumeConcentration()));
		} catch (Exception e) {
			diameter.setText("N/A");
			sigma.setText("N/A");
			density.setText("N/A");
		}
	}
	
	public String formatDouble(double d, int digits) {
		return String.format("%."+digits+"g%n", d);
	}
	
	public String formatDouble(double d) {
		return df.format(d);
	}

}

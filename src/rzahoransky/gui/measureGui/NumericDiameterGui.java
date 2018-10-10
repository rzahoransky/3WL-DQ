package rzahoransky.gui.measureGui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import rzahoransky.dqpipeline.DQPipeline;
import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.listener.DQSignalListener;

public class NumericDiameterGui extends JPanel implements DQSignalListener{

	protected int align = SwingConstants.LEFT;
	protected JLabel diameter = new JLabel("",align);
	protected JLabel diameterVol = new JLabel("",align);
	protected JLabel sigma = new JLabel("",align);
	protected JLabel density = new JLabel("",align);
	protected static final JLabel diameterLabel = new JLabel("Diameter: ");
	protected static final JLabel diameterVolString = new JLabel("Diameter in μm (volumetrical)");
	protected static final JLabel sigmaLabel = new JLabel("Sigma: ");
	protected static final JLabel densityLabel = new JLabel("Density: ");
	DecimalFormat df = new DecimalFormat("0.000"); 
	DecimalFormat scientific = new DecimalFormat("0.00E00");
	String densityFormat = "%s p/m³";
	
	
	public NumericDiameterGui(DQPipeline pipeline) {
		Font font = new Font("Arial", Font.BOLD, 16);
		diameter.setFont(font);
		sigma.setFont(font);
		density.setFont(font);
		
		Font font_normal = new Font("Arial", Font.PLAIN, 16);
		diameterLabel.setFont(font_normal);
		sigmaLabel.setFont(font_normal);
		densityLabel.setFont(font_normal);
		
		pipeline.addNewSignalListener(this);
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx=0;
		c.gridy=0;
		c.weightx=1;
		c.weighty=1;
		c.fill=GridBagConstraints.HORIZONTAL;
		add(diameterLabel,c);
		c.gridx++;
		c.anchor=GridBagConstraints.EAST;
		add(diameter, c);
		c.gridx++;
		
		//c.gridx++;
		//add(diameterVolString,c);
		//c.gridx++;
		//add(diameterVol,c);
		c.gridx++;
		
		add(sigmaLabel,c);
		c.gridx++;
		add(sigma,c);
		c.gridx++;
		add(densityLabel,c);
		c.gridx++;
		//density.setSize(50, 20);
		//density.setMaximumSize(new Dimension(50, 20));
		add(density,c);
		
		setSizes();
	}
	
	protected void setSizes() {
		Dimension d = densityLabel.getPreferredSize();
		d.width=d.width+30;
		diameter.setSize(d);
		diameter.setPreferredSize(d);
		diameter.setMinimumSize(d);
		diameter.setMaximumSize(d);
		
		sigma.setSize(d);
		sigma.setPreferredSize(d);
		sigma.setMinimumSize(d);
		sigma.setMaximumSize(d);
		
		density.setSize(d);
		density.setPreferredSize(d);
		density.setMinimumSize(d);
		density.setMaximumSize(d);
		
//		diameter.setSize(d);
//		diameter.setPreferredSize(d);
//		diameter.setMinimumSize(d);
//		diameter.setMaximumSize(d);
//		
//		
//		protected JLabel diameter = new JLabel("",SwingConstants.LEFT);
//		protected JLabel diameterVol = new JLabel("",SwingConstants.LEFT);
//		protected JLabel sigma = new JLabel("",SwingConstants.LEFT);
//		protected JLabel density = new JLabel("",SwingConstants.LEFT);
//		protected static final JLabel diameterLabel = new JLabel("Diameter: ");
//		protected static final JLabel diameterVolString = new JLabel("Diameter in μm (volumetrical)");
//		protected static final JLabel sigmaLabel = new JLabel("Sigma: ");
//		protected static final JLabel densityLabel = new JLabel("Density: ");
	}
	

	@Override
	public void newSignal(DQSignal currentSignal) {
		try {
		diameter.setText(formatDouble(currentSignal.getGeometricalDiameter()));
		sigma.setText(formatDouble(currentSignal.getSigma()));
		density.setText(String.format(densityFormat,scientific.format(currentSignal.getNumberConcentration())));
		diameterVol.setText(formatDouble(currentSignal.getVolumetricDiameter()));
		
		} catch (Exception e) {
			diameter.setText("N/A");
			sigma.setText("N/A");
			density.setText("N/A");
			diameterVol.setText("N/A");
		}
	}
	
	public String formatDouble(double d, int digits) {
		return String.format("%."+digits+"g%n", d);
	}
	
	public String formatDouble(double d) {
		return df.format(d);
	}

	@Override
	public void closing() {
		// TODO Auto-generated method stub
		
	}

}

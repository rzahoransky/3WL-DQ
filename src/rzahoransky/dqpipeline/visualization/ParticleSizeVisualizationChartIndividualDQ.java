package rzahoransky.dqpipeline.visualization;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.attribute.UserPrincipalLookupService;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartPanel;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;

import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.utils.DQtype;

public class ParticleSizeVisualizationChartIndividualDQ extends ParticleSizeVisualizerChart {
	boolean useSingleDQ = false;
	DQtype dqTypeToUse = DQtype.DQ1;
	SizeSelectorPanel selectorPanel = new SizeSelectorPanel();
	JPanel visualizationPanel = new JPanel();

	public ParticleSizeVisualizationChartIndividualDQ(boolean showAsFrame) {
		super(showAsFrame);
	}
	
	protected void updateConcentration(DQSignal measurement, YIntervalSeriesCollection concentrationDataset) {
		double concentration;
		if (!useSingleDQ) { //"normal" mode
			concentration = Math.max(measurement.getVolumeConcentration(), Double.MIN_NORMAL);
		} else {
			concentration = (measurement.getParticlePropertiesFromDQ(dqTypeToUse).getVolumeConcentration());
		}
		concentrationDataset.getSeries(0).add(System.currentTimeMillis(), concentration, concentration, concentration);
	}

	protected void updateDiameter(DQSignal measurement, YIntervalSeriesCollection collection) {
		long now = System.currentTimeMillis();

		YIntervalSeries diameterSeries = collection.getSeries(0);

		double d = 0;
		if (!useSingleDQ) { //"normal" mode
			d = measurement.getGeometricalDiameter();
		} else {
			d = (measurement.getParticlePropertiesFromDQ(dqTypeToUse).radius * 2.0);
		}
		
		//test for min and max values
		if (measurement.hasMinAndMaxDiameter()) {
			diameterSeries.add(now, d, measurement.getMinGeometricalDiameter(),
					measurement.getMaxGeometricalDiameter());
		} else {
			double sigma = measurement.getSigma();
			diameterSeries.add(now, d, d - sigma, d + sigma);
		}
	}
	
	
	
	public JPanel getChartPanel() {
		JPanel panel = new JPanel();
		GridBagConstraints c = new GridBagConstraints();
		panel.setLayout(new GridBagLayout());
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx=0;
		c.gridy=0;
		panel.add(chartPanel,c);
		c.gridy++;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weighty = 0;
		c.weightx = 1;
		panel.add(selectorPanel,c);
		return panel;
	}
	
	@Override
	public String description() {
		return "Visualizes Particle Diameter for individual DQ";
	}
	
	class DQSelector extends JRadioButton implements ChangeListener{
		private DQtype dqType;

		public DQSelector(DQtype dqType) {
			setText(dqType.toString());
			this.dqType = dqType;
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			// TODO Auto-generated method stub
			
		}
	}
	
	class SizeSelectorPanel extends JPanel implements ActionListener {
		JRadioButton dq12 = new JRadioButton("DQ1/DQ2");
		JRadioButton dq1 = new JRadioButton("DQ1");
		JRadioButton dq2 = new JRadioButton("DQ2");
		JRadioButton dq3 = new JRadioButton("DQ3");
		ButtonGroup group = new ButtonGroup();
		
		public SizeSelectorPanel() {
			group.add(dq12);
			group.add(dq1);
			group.add(dq2);
			group.add(dq3);
			dq12.setSelected(true);
			dq12.addActionListener(this);
			dq1.addActionListener(this);
			dq2.addActionListener(this);
			dq3.addActionListener(this);
			this.add(dq12);
			this.add(dq1);
			this.add(dq2);
			this.add(dq3);
		}
		
		public void getSelectedDq() {
			if (dq1.isSelected()) {
				useSingleDQ = true;
				dqTypeToUse = DQtype.DQ1;
			} else if (dq2.isSelected()) {
				useSingleDQ = true;
				dqTypeToUse = DQtype.DQ2;
			} else if (dq3.isSelected()) {
				useSingleDQ = true;
				dqTypeToUse = DQtype.DQ3;
			} else {
				useSingleDQ = false;
				
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			getSelectedDq();
		}
	}

}

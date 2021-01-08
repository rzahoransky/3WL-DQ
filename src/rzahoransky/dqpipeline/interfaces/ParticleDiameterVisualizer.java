package rzahoransky.dqpipeline.interfaces;

import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;

public interface ParticleDiameterVisualizer extends DQPipelineElement {
	
	public JPanel getChartPanel();
	
	public void setMaxAge(int ageOrCount);

}

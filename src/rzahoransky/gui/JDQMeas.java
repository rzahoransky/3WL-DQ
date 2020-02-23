package rzahoransky.gui;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import rzahoransky.dqpipeline.DQPipeline;
import rzahoransky.utils.MeasureSetUp;

public class JDQMeas extends JFrame{
	DQPipeline pipeline;
	MeasureSetUp setup;

	public JDQMeas() {
		setupFrame();
	}

	private void setupFrame() {
		setSize(600, 800);
		addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				System.out.println("Shutting down pipeline");
				pipeline.stop();
				super.windowClosing(e);
				e.getWindow().dispose();
			}
		});
		setLayout(new BorderLayout());
	}

}

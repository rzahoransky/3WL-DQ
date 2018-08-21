package rzahoransky.gui.measureGui;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.JFrame;

import rzahoransky.gui.measureSetup.MeasureSetUp;

public class MeasureGui extends JFrame {


	public MeasureGui() {
		
	}
	
	private void setupFrame() {
		setSize(new Dimension(600, 500));
		setTitle("3WL DQ Particle Size Measurement");
		
	}


}

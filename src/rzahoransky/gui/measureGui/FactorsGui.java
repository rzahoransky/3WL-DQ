package rzahoransky.gui.measureGui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.omg.PortableServer.ServantRetentionPolicyOperations;

import rzahoransky.dqpipeline.dqSignal.DQSignal;
import rzahoransky.dqpipeline.listener.DQSignalListener;
import rzahoransky.gui.measureSetup.MeasureSetUp;
import rzahoransky.utils.TransmissionType;

public class FactorsGui extends JFrame implements DQSignalListener, ListSelectionListener, KeyListener {
	
	protected static HashSet<FactorHash> factors = new HashSet<>();
	protected static JList<FactorHash> factorList;
	protected static DefaultListModel<FactorHash> model = new DefaultListModel<>();
	JScrollPane scroll;
	
	public FactorsGui() {
		factorList = new JList<>(model);
		scroll = new JScrollPane(factorList);
		setSize(new Dimension(250, 400));
		setLayout(new BorderLayout());
		add(scroll,BorderLayout.CENTER);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Factor List");
		setLocationByPlatform(true);
		
		MeasureSetUp.getInstance().getPipeline().addNewSignalListener(this);
		
		factorList.addListSelectionListener(this);
		factorList.addKeyListener(this);
		
		addCloseListener(this);
	}
	
	private void addCloseListener(FactorsGui factorsGui) {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent evt) {
				MeasureSetUp.getInstance().getPipeline().removeListener(factorsGui);
			}
		});
		
	}

	@Override
	public void newSignal(DQSignal currentSignal) {
		FactorHash hash = new FactorHash(currentSignal);
		if (!factors.contains(hash)) {
			model.addElement(hash);
			factorList.setSelectedValue(hash, true);
			factors.add(hash);
		}
	}
	


	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		if (!arg0.getValueIsAdjusting()) {
			FactorHash factor = factorList.getSelectedValue();
			for (TransmissionType type: TransmissionType.values()) {
				MeasureSetUp.getInstance().getTransmissionExtractor().setFactor(type, factor.getFactor(type));
			}
		}
		
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		int code=arg0.getKeyCode();
		System.out.println("Pressed");
		if(code==KeyEvent.VK_DELETE) {
			FactorHash current = factorList.getSelectedValue();
			factorList.removeListSelectionListener(this);
			model.removeElement(current);
			factors.remove(current);
			factorList.addListSelectionListener(this);
		}
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		
	}

	@Override
	public void closing() {
		dispose();
		
	}
	

}

class FactorHash {
	
	double factorWL1 = 0;
	double factorWL2 = 0;
	double factorWL3 = 0;
	long time = 0;
	NumberFormat nformat = new DecimalFormat("0.000");
	SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
	
	public FactorHash(DQSignal in) {
		setFactor(in, true);
	}
	
	public void setFactor (TransmissionType type, double factor, boolean setTimeStamp) {
		if (setTimeStamp)
			time = System.currentTimeMillis();
		switch (type) {
		case TRANSMISSIONWL1:
			factorWL1 = factor;
			break;
		case TRANSMISSIONWL2:
			factorWL2 = factor;
			break;
		case TRANSMISSIONWL3:
			factorWL3 = factor;
			break;

		default:
			break;
		}
	}
	
	public double getFactor (TransmissionType type) {
		switch (type) {
		case TRANSMISSIONWL1:
			return factorWL1;
		case TRANSMISSIONWL2:
			return factorWL2;
		case TRANSMISSIONWL3:
			return factorWL3;
		}
		return 0d;
	}
	
	public void setFactor (DQSignal in, boolean setTimeStamp) {
		if (setTimeStamp)
			time = in.getTimeStamp();
		
		for (TransmissionType factor: TransmissionType.values()) {
			setFactor(factor, in.getFactor(factor), false);
		}
	}
	
	public String toString() {
		//Date time = new Date(this.time);
		return format.format(time)+": ("+nformat.format(factorWL1)+" / "+nformat.format(factorWL2)+" / "+nformat.format(factorWL3)+")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(factorWL1);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(factorWL2);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(factorWL3);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FactorHash other = (FactorHash) obj;
		if (Double.doubleToLongBits(factorWL1) != Double.doubleToLongBits(other.factorWL1))
			return false;
		if (Double.doubleToLongBits(factorWL2) != Double.doubleToLongBits(other.factorWL2))
			return false;
		if (Double.doubleToLongBits(factorWL3) != Double.doubleToLongBits(other.factorWL3))
			return false;
		return true;
	}
	
}

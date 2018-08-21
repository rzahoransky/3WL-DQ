package rzahoransky.dqpipeline.analogueAdapter;

import java.nio.DoubleBuffer;
import java.util.LinkedList;
import java.util.List;

import com.sun.jna.Pointer;

import kirkwood.nidaq.access.NiDaq;
import kirkwood.nidaq.access.NiDaqException;
import kirkwood.nidaq.jna.Nicaiu;

public class AdapterInformation {
	
	public static void main(String[] args) {
		
		try {
			List<String> names = new NiDaq().getDeviceNames();
			for (String name: names)
				System.out.println(name);
		} catch (NiDaqException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		List<String> devices = getAvailableDevices();
		
		for (String dev: devices) {
			System.out.println(dev);
		}
	}

	public AdapterInformation() {

	}
	
	public static List<String> getAvailableDevices() {
		LinkedList<String> result = new LinkedList<>();
		for (int i=0;i<20;i++) {
			String device = "Dev"+i;
			if(checkDevice(device))
				result.add(device);
		}
		return result;
	}
	
	public static boolean checkDevice(String adCard) {
		NiDaq daq = new NiDaq();
		Pointer task = null;
		try {
			task = daq.createTask("");
			daq.createAIVoltageChannel(task, adCard + "/" + "ai0:3", "", Nicaiu.DAQmx_Val_Cfg_Default, -10, 10, Nicaiu.DAQmx_Val_Volts, null);
			daq.startTask(task);
			daq.clearTask(task);
			return true;
		} catch (NiDaqException e) {
			try {
				daq.clearTask(task);
			} catch (NiDaqException e1) {
				
			}
		}
		return false;
	}

}

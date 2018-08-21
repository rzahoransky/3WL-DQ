package rzahoransky.threadControl;

public class Continue {
	private static boolean cont = true;

	public static boolean cont() {
		return cont;
	}
	
	public static void stopAndQuit() {
		cont = false;
	}

}

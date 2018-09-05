package rzahoransky.test;

import jdistlib.LogNormal;

public class LogNormalTest {
	
	public static void main(String[] args) {
		LogNormal one = new LogNormal(1, 2);
		System.out.println(one.density(1, false));
	}

	public LogNormalTest() {
		// TODO Auto-generated constructor stub
	}

}

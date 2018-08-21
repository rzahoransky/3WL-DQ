package rzahoransky.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

public class ArrayListUtils {

	public ArrayListUtils() {
		// TODO Auto-generated constructor stub
	}
	
	public static double getAverage(List<Double> list) {
		OptionalDouble average = list
	            .stream()
	            .mapToDouble(a -> a)
	            .average();
		return average.isPresent() ? average.getAsDouble() : 0; 
	}

}

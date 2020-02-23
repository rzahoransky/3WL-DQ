package rzahoransky.utils;

import java.util.*;
//from Github User corsiKa
/**
 * Print values with automatic unit prefix
 * @author corsiKa
 *
 */
public class Measurement {
	
    public static final Map<Integer,String> prefixes;
    
    static {
        Map<Integer,String> tempPrefixes = new HashMap<Integer,String>();
        tempPrefixes.put(0,"");
        tempPrefixes.put(3,"k");
        tempPrefixes.put(6,"M");
        tempPrefixes.put(9,"G");
        tempPrefixes.put(12,"T");
        tempPrefixes.put(-3,"m");
        tempPrefixes.put(-6,"u");
        tempPrefixes.put(-9, "n");
        prefixes = Collections.unmodifiableMap(tempPrefixes);
    }

    String type;
    double value;

    /**
     * Use with given value and add a unit. If the value is printed out it will includ the provided type.
     * E.g new {@link Measurement}(1100,"m") results in 1.1km.
     * @param value value 
     * @param type unit.
     */
    public Measurement(double value, String type) {
        this.value = value;
        this.type = type;
    }
    
    public Measurement(double value) {
    	this(value, "");
    }

    public String toString() {
        double tval = value;
        int order = 0;
        while(tval > 1000.0) {
            tval /= 1000.0;
            order += 3;
        }
        while(tval < 1.0) {
            tval *= 1000.0;
            order -= 3;
        }
        return tval + prefixes.get(order) + type;
    }

    public static void main(String[] args) {
        Measurement dist = new Measurement(1337,"m"); // should be 1.337Km
        Measurement freq = new Measurement(12345678,"hz"); // should be 12.3Mhz
        Measurement tiny = new Measurement(0.00034,"m"); // should be 0.34mm
        Measurement itzibitzy = new Measurement(0.0000000128, "m"); //should be 12.8nm

        System.out.println(dist);
        System.out.println(freq);
        System.out.println(tiny);
        System.out.println(itzibitzy);

    }

}



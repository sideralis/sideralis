package fr.dox.sideralis.math;

/**
 * A class which contains only static functions used for calculation of arcsin and arctan.
 * @author Bernard
 */
public class MathFunctions {    
    /**
     * Function arcsin: return the inverse sinus in radian
     * You can also use it to calculate the arccos as arccos(z) = PI/2 - arcsin(z)
     * @param z the input param
     * @return the radian angle corresponding to the inverse sin of z.
     */
    public static double arcsin(double z) {
    	return Math.asin(z);
    }
    /** 
     * Function inverse tangent
     * @param z the value we want to know the arctan
     * @param signN the sign of the numerator, this information is needed to determine the correct quadran.
     * @return the arctan value of z in radian
     */
    public static double arctan(double z,boolean signN) {
    	double res;
        res = Math.atan(z);
        if (signN == false && z>=0)
            res += Math.PI;
        if (signN == true && z<0)
            res += Math.PI;
        
        return res;
    }
    /**
     * Convert a double value representing a degree in a string
     * @param val in degre
     * @param flag to display the number also as a real number
     * @return a string representing a degree value
     */
    public static String convert2deg(double val, boolean flag) {
        String res;
        double tmp;
        int deg,m,s;
        int val2;
        
        //val = Math.toDegrees(val);
        res = "";
        val = (val+360)%360;
        deg = (int)(val);        
        tmp = (val-deg)*60;
        m = (int)tmp;
        tmp = (tmp - m)*60;
        s = (int)tmp;
        val2 = (int)((val - ((int)(val*1000))/1000)*1000);
        res = res + deg + "°"+m+"'"+s+"\"";
        if (flag)
            res += " (" + (int)val+"."+val2+"°)";

        return res;        
    }
    /**
     * Convert a double value representing an hour in a string
     * @param val the hour
     * @param flag to display the number also as a real number
     * @return a string like 11h23m56s
     */
    public static String convert2hms(double val, boolean flag) {
        String res;
        double tmp;
        int h,m,s;
        int val2;
        
        res = "";
        val = (val+24) %24;
        h = (int)val;
        tmp = (val-h)*60;
        m = (int)tmp;
        tmp = tmp-(int)tmp;
        tmp *= 60;
        s = (int)tmp;
        val2 = (int)((val - ((int)(val*1000))/1000)*1000);
        res = res + h+"h"+m+"m"+s+"s";
        if (flag)
            res += " ("+(int)val+"."+val2+"h)";
        return res;
    }

    /** 
     * Conversion from ua to billion of km
     */
    public static double toMKm(double l) {
        double res = (int)(l*149597870.691/1000000);
        return res;
    }
}

package uk.ac.uea.cmp.phygen.core.math;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Dan
 * Date: 27/04/13
 * Time: 16:01
 * To change this template use File | Settings | File Templates.
 */
public class Statistics {

    public static double sumDoubles(List<Double> list) {
        double sum = 0.0;

        for(Double val : list) {
            sum += val.doubleValue();
        }

        return sum;
    }

    public static double sumDoubles(double[] array) {
        double sum = 0.0;

        for(int i = 0; i < array.length; i++) {
            sum += array[i];
        }

        return sum;
    }


    public static double sumIntegers(List<Integer> list) {
        int sum = 0;

        for(Integer val : list) {
            sum += val.intValue();
        }

        return sum;
    }

    public static double sumIntegers(int[] array) {
        int sum = 0;

        for(int i = 0; i < array.length; i++) {
            sum += array[i];
        }

        return sum;
    }

    public static long sumLongs(List<Long> list) {

        long sum = 0L;

        for(Long val : list) {
            sum += val.longValue();
        }

        return sum;
    }

    public static double sumLongs(long[] array) {
        long sum = 0L;

        for(int i = 0; i < array.length; i++) {
            sum += array[i];
        }

        return sum;
    }
}

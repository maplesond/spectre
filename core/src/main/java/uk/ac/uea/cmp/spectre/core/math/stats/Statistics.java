/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2014  UEA School of Computing Sciences
 *
 * This program is free software: you can redistribute it and/or modify it under the term of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package uk.ac.uea.cmp.spectre.core.math.stats;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Dan
 * Date: 27/04/13
 * Time: 16:01
 * To change this template use File | Settings | File Templates.
 */
public class Statistics {

    private String desc;
    private double sum;
    private double mean;
    private double stddev;

    public Statistics(String desc, int[] counts) {

        this.desc = desc;
        this.sum = Statistics.sumIntegers(counts);
        this.mean = Statistics.mean(counts);
        this.stddev = Statistics.stddev(counts, mean);
    }

    public Statistics(String desc, double[] counts) {

        this.desc = desc;
        this.sum = Statistics.sumDoubles(counts);
        this.mean = Statistics.mean(counts);
        this.stddev = Statistics.stddev(counts, mean);
    }


    public String getDesc() {
        return desc;
    }

    public double getSum() {
        return sum;
    }

    public double getMean() {
        return mean;
    }

    public double getStddev() {
        return stddev;
    }

    public static String createHeaderString(String prefix) {
        return prefix != null ?
                prefix + "sum\t" + prefix + "mean\t" + prefix + "std_dev" :
                "sum\tmean\tstd_dev";

    }

    public String createTabSeparatedString() {
        return sum + "\t" + mean + "\t" + stddev;
    }


    // ***** Static functions ********


    // ****** Mean of various arrays *******

    public static double mean(int[] array)      { return (double)sumIntegers(array) / (double)array.length; }
    public static double mean(long[] array)     { return (double)sumLongs(array) / (double)array.length; }
    public static double mean(double[] array)   { return sumDoubles(array) / (double)array.length; }

    public static double variance(int[] array)      { return variance(array, mean(array)); }
    public static double variance(long[] array)     { return variance(array, mean(array)); }
    public static double variance(double[] array)   { return variance(array, mean(array)); }


    // ****** Variance of various arrays *******

    public static double variance(int[] array, double mean) {

        double variance = 0.0;
        for (int i : array) {
            variance += (mean - (double)i) * (mean - (double)i);
        }
        return variance / array.length;
    }

    public static double variance(long[] array, double mean) {

        double variance = 0.0;
        for (long l : array) {
            variance += (mean - (double)l) * (mean - (double)l);
        }
        return variance / array.length;
    }

    public static double variance(double[] array, double mean) {

        double variance = 0.0;
        for (double d : array) {
            variance += (mean - d) * (mean - d);
        }
        return variance / array.length;
    }


    // ****** Standard Deviation of various arrays *******

    public static double stddev(int[] array)                    { return stddev(variance(array)); }
    public static double stddev(long[] array)                   { return stddev(variance(array)); }
    public static double stddev(double[] array)                 { return stddev(variance(array)); }
    public static double stddev(int[] array, double mean)       { return stddev(variance(array, mean)); }
    public static double stddev(long[] array, double mean)      { return stddev(variance(array, mean)); }
    public static double stddev(double[] array, double mean)    { return stddev(variance(array, mean)); }
    public static double stddev(double variance)                { return Math.sqrt(variance); }


    // ****** Standard Error of various arrays ******

    public static double stderr(int[] array)                    { return stderr(stddev(array), array.length); }
    public static double stderr(long[] array)                   { return stderr(stddev(array), array.length); }
    public static double stderr(double[] array)                 { return stderr(stddev(array), array.length); }
    public static double stderr(double stddev, int n)           { return stddev / Math.sqrt(n); }


    // ****** Summing lists and arrays *******

    public static double sumDoubles(List<Double> list) {
        double sum = 0.0;

        for (Double val : list) {
            sum += val.doubleValue();
        }

        return sum;
    }

    public static double sumDoubles(double[] array) {
        double sum = 0.0;

        for (int i = 0; i < array.length; i++) {
            sum += array[i];
        }

        return sum;
    }


    public static int sumIntegers(List<Integer> list) {
        int sum = 0;

        for (Integer val : list) {
            sum += val.intValue();
        }

        return sum;
    }

    public static int sumIntegers(int[] array) {
        int sum = 0;

        for (int i = 0; i < array.length; i++) {
            sum += array[i];
        }

        return sum;
    }

    public static long sumLongs(List<Long> list) {

        long sum = 0L;

        for (Long val : list) {
            sum += val.longValue();
        }

        return sum;
    }

    public static long sumLongs(long[] array) {
        long sum = 0L;

        for (int i = 0; i < array.length; i++) {
            sum += array[i];
        }

        return sum;
    }
}

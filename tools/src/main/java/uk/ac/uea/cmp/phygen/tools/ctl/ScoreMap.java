/*
 * Phylogenetics Tool suite
 * Copyright (C) 2013  UEA CMP Phylogenetics Group
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.uea.cmp.phygen.tools.ctl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Sarah
 */
public class ScoreMap extends LinkedHashMap<Methods, Double[]> {
    
    private int nbScores;
    
    public ScoreMap() {
        super();
        
        nbScores = 0;
    }
    
    public ScoreMap(Methods[] methods, int nbScores) {
        
        for(Methods method : methods) {
            Double[] scores = new Double[nbScores];
        
            for(Double d : scores) {
                d = new Double(0.0);
            }
            
            this.put(method, scores);
        }
        this.nbScores = nbScores;
    }

    public int getNbScores() {
        return nbScores;
    }
    
    
    
    public void setScore(Methods method, int index, Double value) {
        if (!this.containsKey(method)) {
            throw new IllegalArgumentException("Method not found: " + method.toString());
        }
        else {
            this.get(method)[index] = value;
        }
    }
    
    
    public double average(Methods method) {

        if (!this.containsKey(method)) {
            throw new IllegalArgumentException("Method not found: " + method.toString());
        }
        
        double average = 0.0;
        
        Double[] data = this.get(method);

        for (double d : data) {
            average += d;
        }
        average = average / data.length;

        return average;
    }
    
    public int countTimesBetter(Methods method1, Methods method2) {
        
        if (!this.containsKey(method1)) {
            throw new IllegalArgumentException("Method1 not found: " + method1.toString());
        }
        if (!this.containsKey(method2)) {
            throw new IllegalArgumentException("Method2 not found: " + method2.toString());
        }
        
        int count = 0;

        Double[] data1 = this.get(method1);
        Double[] data2 = this.get(method2);
        
        if (data1.length != data2.length) {
            throw new IllegalStateException("ScoreMap has become corrupted.  Method1 entry has a different number of scores to Method2.  Method1: " + data1.length + "; Method2: " + data2.length);
        }
        
        for (int i = 0; i < data1.length; i++) {
            if (data1[i].doubleValue() < data2[i].doubleValue()) {
                count++;
            }
        }

        return count;
    }
    
    
    public int countTimesEqual(Methods method1, Methods method2) {
        
        if (!this.containsKey(method1)) {
            throw new IllegalArgumentException("Method1 not found: " + method1.toString());
        }
        if (!this.containsKey(method2)) {
            throw new IllegalArgumentException("Method2 not found: " + method2.toString());
        }
        
        int count = 0;

        Double[] data1 = this.get(method1);
        Double[] data2 = this.get(method2);
        
        if (data1.length != data2.length) {
            throw new IllegalStateException("ScoreMap has become corrupted.  Method1 entry has a different number of scores to Method2.  Method1: " + data1.length + "; Method2: " + data2.length);
        }
        
        for (int i = 0; i < data1.length; i++) {
            if (data1[i].doubleValue() == data2[i].doubleValue()) {
                count++;
            }
        }

        return count;
    }
    
    public double[] treeLengthsAtIndex(int index) {
        
        double[] converted = new double[this.size()];
            
        int i = 0;
        for(Double[] data : this.values()) {
            converted[i++] = data[index];
        }
        
        return converted;
    }
    
    public Methods[] getOrderedMethods() {
        return this.keySet().toArray(new Methods[this.keySet().size()]);
    }
    
    public double bestTreeLength(int scoreIndex) {
        
        double[] treeLengths = treeLengthsAtIndex(scoreIndex);
        
        List converted = Arrays.asList(treeLengths);
        return (double)Collections.min(converted);
    }
    
    public int getBestMethodIndex(int scoreIndex) {
        double[] treeLengths = treeLengthsAtIndex(scoreIndex);
        
        List converted = Arrays.asList(treeLengths);
        double min = (double)Collections.min(converted);
        
        for(int i = 0; i < treeLengths.length; i++) {
            if (treeLengths[i] == min)
                return i;
        }
        
        return -1;
    }
    
    public double bestAverage() {
        
        double bestAverage = 0.0;
        
        for(int i = 0; i < this.getNbScores(); i++) {
            bestAverage += bestTreeLength(i);
        }
        
        return bestAverage / this.getNbScores();
    }
    
    public double bestAverage(Methods method1, Methods method2) {
        
        if (!this.containsKey(method1)) {
            throw new IllegalArgumentException("Method1 not found: " + method1.toString());
        }
        if (!this.containsKey(method2)) {
            throw new IllegalArgumentException("Method2 not found: " + method2.toString());
        }
        
        Double[] data1 = this.get(method1);
        Double[] data2 = this.get(method2);
        
        double bestAverage = 0.0;
        
        for(int i = 0; i < this.getNbScores(); i++) {
            
            if (data1[i].doubleValue() < data2[i].doubleValue())
                bestAverage += data1[i].doubleValue();
            else
                bestAverage += data2[i].doubleValue();
        }
        
        return bestAverage / this.getNbScores();
    }
    
    public double variance(Methods method) {
        
        double variance = 0.0;
        Double[] data = this.get(method);
        double av = average(method);
        for (Double d : data) {
            variance += ( av - (double) d)*(av - (double) d);
        }
        
        return variance / data.length;
    }
    
   public double bestdistance(Methods method1, Methods method2) {
        
        double variance = 0.0;
        Double[] data1 = this.get(method1);
        
        double average = bestAverage(method1, method2);
        
        for (Double d : data1) {
            variance += (average - (double) d)*(average - (double) d);
        }
        
        return variance / data1.length;
    }
}

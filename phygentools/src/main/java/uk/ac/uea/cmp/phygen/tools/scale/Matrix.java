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
package uk.ac.uea.cmp.phygen.tools.scale;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Matrix {



//Updates the quartet weights in the new input files (scales the weights before they are processed by Chopper)
    public static void updateQuartetWeights(String path, String prefix, double[] w) throws IOException {

        Runtime rt = Runtime.getRuntime();
        rt.gc();
        //open Quartet file
        for (int i = 0; i < w.length; i++) {

            //Opens a quartet file
            List<String> lnr = FileUtils.readLines(new File(path + prefix + (i + 1) + ".qua"));

            //ArrayLists to store the weights and quartets from the quartet file
            ArrayList<double[]> weights = new ArrayList<>();
            ArrayList<String> quartets = new ArrayList<>();
            int t = 0;

            //Read every line of file to extract quartets and quartet weights
            for (int p = 0; p < lnr.size(); p++) {
                String aLine = lnr.get(p);
                if (aLine.isEmpty() == false) {

                    if (aLine.startsWith("quartet:")) {
                        int wIdx = aLine.indexOf("weights:");
                        String quartet = aLine.substring(8, wIdx).trim();
                        quartets.add(quartet);
                        String weight = aLine.substring(wIdx + 8, aLine.lastIndexOf(";")).trim();
                        double[] qweights = new double[3];
                        String[] help = new String[3];
                        help = weight.split(" ");
                        for (int h = 0; h < 3; h++) {
                            qweights[h] = Double.parseDouble(help[h]) * w[i];
                        }
                        weights.add(qweights);
                        t++;
                    }
                }
            }


            StringBuffer buffer = new StringBuffer();
            for (int s = 0; s < (lnr.size() - t); s++) {
                buffer.append(lnr.get(s) + "\n");
            }
            for (int s = 0; s < weights.size(); s++) {
                double[] help = weights.get(s);
                buffer.append("quartet: " + quartets.get(s) + " weights:");
                for (int z = 0; z < 3; z++) {
                    buffer.append(" " + help[z]);
                }
                buffer.append(";\n");
            }
            FileUtils.writeStringToFile(new File(path + prefix + (i + 1) + ".qua"), buffer.toString());
            //System.out.println("Quartets updated");  
            //System.out.println("free memory before garbage collection: " + rt.freeMemory());

            rt.gc();
        }
        makeScript(path, prefix, w);
    }

    //Creates script for the scaled/updated quartet file as new input for uk.ac.uea.cmp.phygen.superq.chopper
    public static void makeScript(String path, String prefix, double[] w) throws IOException {

        String content = "";
        for (int i = 0; i < w.length; i++) {
            content += "qweights " + prefix + (i + 1) + ".qua\n";
        }

        FileUtils.writeStringToFile(new File(path + prefix + ".script"), content);
    }
}

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
package uk.ac.uea.cmp.phygen.superq.chopper;

import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetWeights;
import uk.ac.uea.cmp.phygen.core.math.tuple.Triplet;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created by IntelliJ IDEA. User: Analysis Date: 2004-jul-12 Time: 00:00:09 To
 * change this template use Options | File Templates.
 */
public class Combiner {

    // note: this now simply computes a weighted sum
    public static void add(QuartetWeights qW, QuartetWeights aW, double w) {

        Triplet[] finalData = qW.getData();

        // this is the part that may be done in any number of ways

        // here, take weighted sum of every quartet
        // where the quartet is nonzero

        Triplet[] data = aW.getData();

        for (int n = 0; n < data.length; n++) {

            Triplet<Double> fT = finalData[n];
            Triplet<Double> T = data[n];

            // just a potentially weighted mean
            // with this, quartets supported by few
            // trees will be weak, although if the trees are strong, they may be too

            Triplet nT = new Triplet<Double>(
                    fT.getA() + w * T.getA(),
                    fT.getB() + w * T.getB(),
                    fT.getC() + w * T.getC()
            );

            finalData[n] = nT;

        }

        qW.setData(finalData);

    }

    public static void divide(QuartetWeights qW, QuartetWeights summer) {

        Triplet[] data = qW.getData();
        Triplet[] sData = summer.getData();

        for (int n = 0; n < data.length; n++) {

            Triplet<Double> T = data[n];
            Triplet<Double> sT = sData[n];

            Triplet<Double> nT = new Triplet<>();

            if (sT.getA() != 0) {

                nT.setA(T.getA() / sT.getA());
                nT.setB(T.getB() / sT.getB());
                nT.setC(T.getC() / sT.getC());

            } else {

                // this is a case where the quartet is noninformative!
                // thus, we might use this information for such purposes...

                nT.setA(T.getA());
                nT.setB(T.getB());
                nT.setC(T.getC());

            }

            data[n] = nT;

        }

        qW.setData(data);

    }

    public static void sum(QuartetWeights summer, LinkedList taxonNames, Source loader) {

        // so... we go through taxonNames, which is the metalist
        // check every quartet defined for it
        // take the taxonList for the objects in loader, and their corresponding weights
        // if a quartet is defined for that list, add its weight to the corresponding summer position
        // summer must have been translated according to the metalist

        ListIterator lI = loader.getTaxonNames().listIterator();
        ListIterator wI = loader.getWeights().listIterator();

        while (lI.hasNext()) {

            double w = ((Double) wI.next()).doubleValue();
            LinkedList lesserNames = (LinkedList) lI.next();

            // course through all quartets of taxonNames
            // if taxonNames (quartet entries) are contained in lesserNames
            // add w to summer (quartet)

            int N = taxonNames.size();

            for (int a = 0; a < N - 3; a++) {

                for (int b = a + 1; b < N - 2; b++) {

                    for (int c = b + 1; c < N - 1; c++) {

                        for (int d = c + 1; d < N; d++) {

                            String sA = (String) taxonNames.get(a);
                            String sB = (String) taxonNames.get(b);
                            String sC = (String) taxonNames.get(c);
                            String sD = (String) taxonNames.get(d);

                            if (lesserNames.contains(sA)
                                && lesserNames.contains(sB)
                                && lesserNames.contains(sC)
                                && lesserNames.contains(sD)) {

                                double oldW = summer.getWeight(a + 1, b + 1, c + 1, d + 1);

                                summer.setWeight(a + 1, b + 1, c + 1, d + 1, oldW + w, oldW + w, oldW + w);

                            }

                        }

                    }

                }

            }

        }

    }
}

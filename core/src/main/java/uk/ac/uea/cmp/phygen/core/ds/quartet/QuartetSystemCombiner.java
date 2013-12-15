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

package uk.ac.uea.cmp.phygen.core.ds.quartet;

import uk.ac.uea.cmp.phygen.core.ds.Taxa;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: Dan Date: 29/07/13 Time: 22:51 To change this template use File | Settings | File
 * Templates.
 */
public class QuartetSystemCombiner {

    private QuartetSystemList originalSystems;

    private Taxa taxa;
    private WeightedQuartetMap quartetWeights;
    private WeightedQuartetMap summer;

    public QuartetSystemCombiner() {
        this(new Taxa(), new WeightedQuartetMap(), new WeightedQuartetMap());
    }

    public QuartetSystemCombiner(Taxa taxa, WeightedQuartetMap quartetWeights, WeightedQuartetMap summer) {
        this.taxa = taxa;
        this.quartetWeights = quartetWeights;
        this.summer = summer;
        this.originalSystems = new QuartetSystemList();
    }

    public Taxa getTaxa() {
        return taxa;
    }

    public WeightedQuartetMap getQuartetWeights() {
        return quartetWeights;
    }

    public WeightedQuartetMap getSummer() {
        return summer;
    }

    public void divide() {
        this.quartetWeights.divide(this.summer);
    }


    public void saveInformation(File outputFile) throws IOException {

        int N = taxa.size();

        FileWriter out = new FileWriter(outputFile + ".info");

        out.write("taxanumber: " + N + ";\ndescription: supernetwork quartets;\nsense: max;\n");

        NumberFormat nF = NumberFormat.getIntegerInstance();
        nF.setMinimumIntegerDigits(3);
        nF.setMaximumIntegerDigits(3);

        for (int n = 0; n < N; n++) {
            out.write("taxon:   " + nF.format(n + 1) + "   name: " + taxa.get(n).getName() + ";\n");
        }

        for (int a = 1; a <= N - 3; a++) {

            for (int b = a + 1; b <= N - 2; b++) {

                for (int c = b + 1; c <= N - 1; c++) {

                    for (int d = c + 1; d <= N; d++) {

                        if (summer.getWeight(new Quartet(a, b, c, d)) > 0.0) {

                            out.write("quartet: " + nF.format(a) + " " + nF.format(b) + " " + nF.format(c) + " " + nF.format(d)
                                    + " weights: "
                                    + "1 1 1;\n");

                        } else {

                            out.write("quartet: " + nF.format(a) + " " + nF.format(b) + " " + nF.format(c) + " " + nF.format(d)
                                    + " weights: "
                                    + "0 0 0;\n");
                        }
                    }
                }
            }
        }

        out.close();
    }

    public QuartetSystemCombiner combine(QuartetSystem qnet) {

        // Keep a copy of the original datasets
        this.originalSystems.add(qnet);

        // Save current taxa
        Taxa oldTaxa = new Taxa(this.taxa);

        // Combine chopped tree's taxa set, with everything found in loader.  Only keeps distinct taxa.
        this.taxa.addAll(qnet.getTaxa());

        // Is this still necessary any longer?... I'd prefer not to touch the original datasets
        // I don't like doing this much, but this method goes back through all the data sets and updates the Taxa indicies
        // so they match our new master taxa list
        //dataSets.translateTaxaIndicies(this.taxa);

        // Convert the taxa sets in the current quartet weights and the summer
        this.quartetWeights.translate(oldTaxa, this.taxa);
        this.summer.translate(oldTaxa, this.taxa);

        // Agglomerate quartets
        this.quartetWeights.weighedAdd(qnet.getQuartets(), qnet.getWeight());

        // Do some summing???
        this.summer.sum(this.taxa, qnet.getTaxa(), qnet.getWeight());

        return this;
    }

    public QuartetSystem create() {
        return new QuartetSystem(this.taxa, 1.0, this.quartetWeights);
    }

    public List<Taxa> getOriginalTaxaSets() {

        List<Taxa> originalTaxaSets = new ArrayList<>();

        for(QuartetSystem qnet : this.originalSystems) {
            originalTaxaSets.add(qnet.getTaxa());
        }

        return originalTaxaSets;
    }
}

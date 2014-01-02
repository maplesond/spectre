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
import uk.ac.uea.cmp.phygen.core.ds.Taxon;

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
    private List<Taxa> translated;
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
        this.translated = new ArrayList<>();
    }

    public Taxa getTaxa() {
        return taxa;
    }

    public List<Taxa> getTranslatedTaxaSets() {
        return translated;
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
        this.taxa.addAll(qnet.getTaxa(), false);

        // Store a separate version of the quartet system taxa list, which specify the position in the new complete dataset
        this.translated.add(this.translateTaxaIndicies(this.taxa, qnet.getTaxa()));

        // Convert the taxa sets in the current quartet weights and the summer
        this.quartetWeights.translate(oldTaxa, this.taxa);
        this.summer.translate(oldTaxa, this.taxa);

        // Agglomerate quartets
        this.quartetWeights.weighedAdd(qnet.getQuartets(), qnet.getWeight());

        // Do some summing???
        this.summer.sum(this.taxa, qnet.getTaxa(), qnet.getWeight());

        return this;
    }

    /**
     * Using the indices of the master taxa set, updates the new taxa set with the master's indicies
     * @param allTaxa The combined taxa set
     * @param subset The subset of taxa, which is a subset of the combined master taxa set.
     * @returns Returns a new copy of subset, which has its indicies updated to reflect those in the master taxa set.
     *
     */
    private Taxa translateTaxaIndicies(Taxa allTaxa, Taxa subset) {

        Taxa newTaxa = new Taxa();
        for(Taxon t : subset) {
            Taxon masterTaxon = allTaxa.getByName(t.getName());

            newTaxa.add(new Taxon(t.getName(), masterTaxon.getId()));
        }

        return newTaxa;
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

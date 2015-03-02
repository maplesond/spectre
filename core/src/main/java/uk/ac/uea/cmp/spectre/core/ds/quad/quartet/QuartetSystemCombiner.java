/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2015  UEA School of Computing Sciences
 *
 * This program is free software: you can redistribute it and/or modify it under the term of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package uk.ac.uea.cmp.spectre.core.ds.quad.quartet;

import uk.ac.uea.cmp.spectre.core.ds.Identifier;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;
import uk.ac.uea.cmp.spectre.core.ds.quad.Quad;
import uk.ac.uea.cmp.spectre.core.ds.quad.SpectreQuad;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA. User: Dan Date: 29/07/13 Time: 22:51 To change this template use File | Settings | File
 * Templates.
 */
public class QuartetSystemCombiner {

    private QuartetSystemList originalSystems;

    private IdentifierList taxa;
    private List<IdentifierList> translatedTaxaList;
    private CanonicalWeightedQuartetMap quartetWeights;
    private CanonicalWeightedQuartetMap summer;

    public QuartetSystemCombiner() {
        this(new IdentifierList(), new CanonicalWeightedQuartetMap(), new CanonicalWeightedQuartetMap());
    }

    public QuartetSystemCombiner(IdentifierList taxa, CanonicalWeightedQuartetMap quartetWeights, CanonicalWeightedQuartetMap summer) {
        this.taxa = taxa;
        this.quartetWeights = quartetWeights;
        this.summer = summer;
        this.originalSystems = new QuartetSystemList();
        this.translatedTaxaList = new ArrayList<>();
    }

    public IdentifierList getTaxa() {
        return taxa;
    }

    public List<IdentifierList> getTranslatedTaxaSets() {
        return translatedTaxaList;
    }

    public CanonicalWeightedQuartetMap getQuartetWeights() {
        return quartetWeights;
    }

    public CanonicalWeightedQuartetMap getSummer() {
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

            Identifier taxon = taxa.get(n);
            out.write("taxon:   " + nF.format(taxon.getId()) + "   name: " + taxon.getName() + ";\n");
        }

        for (int a = 1; a <= N - 3; a++) {

            for (int b = a + 1; b <= N - 2; b++) {

                for (int c = b + 1; c <= N - 1; c++) {

                    for (int d = c + 1; d <= N; d++) {

                        if (summer.get(new SpectreQuad(a, b, c, d)) > 0.0) {

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

    public QuartetSystemCombiner combine(QuartetSystem qs) {

        // Keep a copy of the original datasets
        this.originalSystems.add(qs);

        // Save current taxa
        //Taxa oldTaxa = new Taxa(this.taxa);

        // Combine chopped tree's taxa set, with everything found in loader.  Only keeps distinct taxa.
        this.taxa.addAll(qs.getTaxa(), false);

        // Store a separate version of the quartet system taxa list, which specify the position in the new complete dataset
        Map<Integer, Integer> subsetToMasterLut = this.translateTaxaIndicies(this.taxa, qs.getTaxa(), true);
        this.translatedTaxaList.add(lutToTaxa(subsetToMasterLut, this.taxa));

        // Combine quartets
        this.combine(qs.getQuartets(), subsetToMasterLut, qs.getWeight());

        return this;
    }


    /**
     * note: this now simply computes a weighted sum. This is the part that may be done in any number of ways here,
     * take weighted sum of every quartet where the quartet is nonzero
     * @param other The other map of canonical weighted quartet to combine into this
     * @param translation Translation map, which helps to translate quartets in other into this
     * @param scalingFactor Scaling factor to apply to the other quartets
     */
    public void combine(CanonicalWeightedQuartetMap other, Map<Integer, Integer> translation, double scalingFactor) {

        for (Quad q : other.keySet()) {
            Quad translated = this.translateQuartet(q, translation);

            this.quartetWeights.incrementWeight(translated, other.get(q) * scalingFactor);
            this.summer.incrementWeight(translated, scalingFactor);
        }
    }

    private Quad translateQuartet(Quad q, Map<Integer, Integer> lut) {

        return new SpectreQuad(
                lut.get(q.getA()),
                lut.get(q.getB()),
                lut.get(q.getC()),
                lut.get(q.getD())
        );
    }

    /**
     * Using the indices of the master taxa set, updates the new taxa set with the master's indicies
     *
     * @param allTaxa The combined taxa set
     * @param subset  The subset of taxa, which is a subset of the combined master taxa set.
     * @returns Returns a new copy of subset, which has its indicies updated to reflect those in the master taxa set.
     */
    private Map<Integer, Integer> translateTaxaIndicies(IdentifierList allTaxa, IdentifierList subset, boolean subsetToMaster) {

        Map<Integer, Integer> lut = new HashMap<>();
        for (Identifier t : subset) {
            Identifier masterTaxon = allTaxa.getByName(t.getName());

            lut.put(
                    subsetToMaster ? t.getId() : masterTaxon.getId(),
                    subsetToMaster ? masterTaxon.getId() : t.getId());
        }

        return lut;
    }

    private IdentifierList lutToTaxa(Map<Integer, Integer> lut, IdentifierList allTaxa) {

        IdentifierList newTaxa = new IdentifierList();

        for (Map.Entry<Integer, Integer> entry : lut.entrySet()) {
            newTaxa.add(allTaxa.getById(entry.getValue()));
        }

        return newTaxa;
    }

    public GroupedQuartetSystem create() {
        return new GroupedQuartetSystem(new QuartetSystem(this.taxa, 1.0, this.quartetWeights));
    }

    public List<IdentifierList> getOriginalTaxaSets() {

        List<IdentifierList> originalTaxaSets = new ArrayList<>();

        for (QuartetSystem qnet : this.originalSystems) {
            originalTaxaSets.add(qnet.getTaxa());
        }

        return originalTaxaSets;
    }
}

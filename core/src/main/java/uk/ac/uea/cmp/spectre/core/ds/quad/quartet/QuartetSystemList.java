/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2017  UEA School of Computing Sciences
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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;
import uk.ac.uea.cmp.spectre.core.ds.quad.Quad;
import uk.ac.uea.cmp.spectre.core.ds.quad.quartet.load.QLoaderFactory;
import uk.ac.uea.cmp.spectre.core.ds.tree.newick.NewickTree;
import uk.ac.uea.cmp.spectre.core.io.qweight.QWeightWriter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 18/11/13
 * Time: 22:42
 * To change this template use File | Settings | File Templates.
 */
public class QuartetSystemList extends ArrayList<QuartetSystem> {

    private static Logger log = LoggerFactory.getLogger(QuartetSystemList.class);

    /**
     * Creates an empty quartet network list.
     */
    public QuartetSystemList() {
        super();
    }

    /**
     * Creates a list of quartet networks initalised with a single element
     *
     * @param initialElement First quartet system to use as a seed for this list
     */
    public QuartetSystemList(QuartetSystem initialElement) {
        super();
        this.add(initialElement);
    }

    /**
     * Creates a list of quartet networks, converted from a list of NewickTrees.
     *
     * @param trees A list of newick trees
     */
    public QuartetSystemList(List<NewickTree> trees) {

        super();

        for (NewickTree newickTree : trees) {
            this.add(new QuartetSystem(newickTree));
        }
    }

    /**
     * Creates a list of quartet networks from a file source
     *
     * @param inputFiles The files to load
     * @throws IOException Thrown if there were any issues loading the file.
     */
    public QuartetSystemList(File[] inputFiles) throws IOException {

        super();

        for (File file : inputFiles) {

            int nbQuartetSystems = 0;
            for (QuartetSystem qs : new QLoaderFactory().create(FilenameUtils.getExtension(file.getName())).load(file, 1.0)) {
                this.add(qs);
                nbQuartetSystems++;
            }

            log.info("Loaded " + nbQuartetSystems + " quartet system" + (nbQuartetSystems > 1 ? "s" : "") + " from " + file.getAbsolutePath());
        }
    }

    public IdentifierList combineTaxaSets() {

        IdentifierList result = new IdentifierList();

        for (QuartetSystem data : this) {
            result.addAll(data.getTaxa());
        }

        return result;
    }


    public List<IdentifierList> getTaxaSets() {

        List<IdentifierList> result = new ArrayList<>();

        for (QuartetSystem data : this) {
            result.add(data.getTaxa());
        }

        return result;
    }


    /*public void translateTaxaIndicies(Taxa superTaxaSet) {
        for(QuartetSystem data : this) {
            data.setTaxaIndicies(superTaxaSet);
        }
    }*/

    public List<Double> getWeights() {

        List<Double> weights = new ArrayList<>();

        for (QuartetSystem data : this) {
            weights.add(data.getWeight());
        }

        return weights;
    }

    /**
     * Updates the quartet weights in the new input files (scales the weights before they are processed by Chopper)
     *
     * @param w The weights to apply to the quartet networks.
     */
    public void scaleWeights(double[] w) {

        if (this.size() != w.length)
            throw new IllegalArgumentException("This quartet network list and the weight list are different sizes");

        // Loop through each quartet network and update it
        for (int i = 0; i < w.length; i++) {

            QuartetSystem qs = this.get(i);

            double weight = w[i];

            for (Map.Entry<Quad, Double> entry : qs.getQuartets().entrySet()) {
                entry.setValue(entry.getValue() * weight);
            }
        }
    }

    public void saveQWeights(File outputPrefix) throws IOException {

        File outputDir = outputPrefix.getParentFile();
        String prefix = outputPrefix.getName();

        for (int i = 1; i <= this.size(); i++) {

            File outputFile = new File(outputDir, prefix + i + ".qua");

            new QWeightWriter().writeQuartets(outputFile, this.get(i));
        }

        // Write a script file to easily load all these files later
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < this.size(); i++) {
            lines.add("qweights " + prefix + (i + 1) + ".qua\n");
        }

        FileUtils.writeLines(new File(outputDir, prefix + ".script"), lines);
    }


}

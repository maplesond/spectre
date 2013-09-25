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
package uk.ac.uea.cmp.phygen.qnet;

import org.apache.commons.io.FilenameUtils;
import uk.ac.uea.cmp.phygen.core.ds.TaxonList;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetWeights;
import uk.ac.uea.cmp.phygen.core.math.optimise.Optimiser;
import uk.ac.uea.cmp.phygen.core.math.optimise.OptimiserException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * The QNet main class
 *
 * Presently runnable holder for Stefan Gr�newalds circular ordering-generating
 * algorithm
 *
 */
public class QNet {

    /**
     * Structure for quartet weights.
     *
     * In this object, quartet weights are held by quartet + tree.
     *
     * Say... ArrayList of ArrayLists of ArrayLists of triplets?
     *
     * Always access by small->large... with subtraction of the previous and
     * one... so 3, 5, 1, 8 is 1, 3, 5, 8 is 0 1 1 2 and then choose which tree
     * according to order u, v, x, y
     */
    private QuartetWeights theQuartetWeights;

    /**
     * Taxon names
     *
     * ArrayList of Strings
     */
    private List<String> taxonNames;

    /**
     * Listing holder...
     *
     * Just an ArrayList of TaxonLists
     */
    private List<TaxonList> theLists;

    /**
     * Number of taxa...
     */
    private int N;

    /**
     * Choice of min/max usage
     */
    private boolean useMax;

    public QNet() {

        this.theQuartetWeights = new QuartetWeights();
        this.taxonNames = new ArrayList<String>();
        this.theLists = new ArrayList<TaxonList>();
        this.N = 0;
        this.useMax = false;
    }


    public int getN() {

        return N;
    }

    public void setN(int newN) {

        this.N = newN;
    }

    public QuartetWeights getWeights() {

        return theQuartetWeights;
    }

    public List<String> getTaxonNames() {

        return taxonNames;
    }

    public boolean useMax() {

        return useMax;
    }

    public void setUseMax(boolean newUseMax) {

        useMax = newUseMax;
    }

    public List<TaxonList> getTheLists() {

        return theLists;
    }


    public WeightsComputeNNLSInformative.ComputedWeights execute(File input, boolean log, double tolerance, Optimiser optimiser) throws IOException, QNetException, OptimiserException {

        if (FilenameUtils.getExtension(input.getName()).equals("nex")) {
            QNetLoader.loadNexus(this, input.getAbsolutePath(), log);
        }
        else {
            QNetLoader.load(this, input.getAbsolutePath(), log);
        }

        NewCyclicOrderer.order(this);

        File infoFile = new File(input, ".info");
        WeightsComputeNNLSInformative.ComputedWeights solution = WeightsComputeNNLSInformative.computeWeights(this, infoFile.getAbsolutePath(), tolerance, optimiser);

        return solution;
    }


}
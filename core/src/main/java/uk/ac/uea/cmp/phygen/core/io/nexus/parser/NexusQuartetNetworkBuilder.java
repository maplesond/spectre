/*
 * Phylogenetics Tool suite
 * Copyright (C) 2013  UEA CMP Phylogenetics Group
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

package uk.ac.uea.cmp.phygen.core.io.nexus.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.phygen.core.ds.Taxa;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetNetwork;
import uk.ac.uea.cmp.phygen.core.ds.quartet.WeightedQuartetMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 01/12/13
 * Time: 21:33
 * To change this template use File | Settings | File Templates.
 */
public class NexusQuartetNetworkBuilder {

    private static Logger log = LoggerFactory.getLogger(NexusQuartetNetworkBuilder.class);

    private int expectedNbTaxa;
    private Taxa taxa;
    private double weight;
    private WeightedQuartetMap weightedQuartets;


    public NexusQuartetNetworkBuilder() {
        this.expectedNbTaxa = 0;
        this.taxa = null;
        this.weight = 1.0;
        this.weightedQuartets = new WeightedQuartetMap();
    }


    public QuartetNetwork createQuartetNetwork() {

        final int nbTaxa = taxa != null ? taxa.size() : expectedNbTaxa;

        if (expectedNbTaxa != 0 && taxa != null && nbTaxa != expectedNbTaxa) {
            log.warn("Expected number of taxa (" + expectedNbTaxa + ") is different from the number of found taxa (" + nbTaxa + ").");
        }

        return new QuartetNetwork(this.taxa, this.weight, null); //this.weightedQuartets);
    }

    public int getExpectedNbTaxa() {
        return expectedNbTaxa;
    }

    public void setExpectedNbTaxa(int expectedNbTaxa) {
        this.expectedNbTaxa = expectedNbTaxa;
    }

    public void setTaxa(Taxa taxa) {
        this.taxa = taxa;
    }

    public Taxa getTaxa() {
        return taxa;
    }
}

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

package uk.ac.uea.cmp.phygen.core.ds.quartet;

import org.apache.commons.lang3.tuple.Pair;
import uk.ac.uea.cmp.phygen.core.ds.Taxa;
import uk.ac.uea.cmp.phygen.core.ds.tree.newick.NewickTree;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 18/11/13
 * Time: 22:37
 * To change this template use File | Settings | File Templates.
 */
public class GroupedQuartetSystem {

    public static enum Sense {
        MIN,
        MAX;

        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }


    private Taxa taxa;
    private double weight;
    private Sense sense;
    private WeightedQuartetGroupMap quartets;

    public GroupedQuartetSystem() {
        this(new Taxa(), 1.0, new WeightedQuartetGroupMap());
    }

    public GroupedQuartetSystem(Taxa taxa, double weight, WeightedQuartetGroupMap quartets) {
        this.taxa = taxa;
        this.weight = weight;
        this.sense = Sense.MAX;
        this.quartets = quartets;

        final int expectedNbQuartets = Quartet.over4(taxa.size());

        if (this.quartets.size() != expectedNbQuartets) {
            throw new IllegalArgumentException("Found unexpected number of quartets.  Something went wrong creating the " +
                    "quartet hash.  There were " + taxa.size() + " taxa in the distance matrix, which should correspond to " + expectedNbQuartets +
                    " quartets in the hash.  Instead we found " + this.quartets.size() + " quartets in the hash.");
        }
    }

    public GroupedQuartetSystem(NewickTree newickTree) {
        this(newickTree.getTaxa(), newickTree.getScalingFactor(), new WeightedQuartetGroupMap(newickTree.createQuartets()));
    }

    public GroupedQuartetSystem(QuartetSystem quartetSystem) {

        this.taxa = quartetSystem.getTaxa();
        this.weight = quartetSystem.getWeight();
        this.sense = Sense.valueOf(quartetSystem.getSense().toString().toUpperCase());
        this.quartets = new WeightedQuartetGroupMap();

        for(Map.Entry<Quartet, Double> entry : quartetSystem.getQuartets().entrySet()) {

            Pair<Quartet, Integer> keys = entry.getKey().getGroupKeys();

            this.quartets.put(keys.getLeft(), keys.getRight(), entry.getValue());
        }

    }

    public Taxa getTaxa() {
        return taxa;
    }

    public void setTaxa(Taxa taxa) {
        this.taxa = taxa;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Sense getSense() {
        return sense;
    }

    public void setSense(Sense sense) {
        this.sense = sense;
    }

    public WeightedQuartetGroupMap getQuartets() {
        return quartets;
    }

    public void setQuartets(WeightedQuartetGroupMap quartets) {
        this.quartets = quartets;
    }



    public void normaliseQuartets(boolean logscale) {
       this.quartets.normalize(logscale, sense == Sense.MAX);
    }

    public List<Quartet> sortedQuartets() {

        List<Quartet> keys = new LinkedList<>(this.quartets.keySet());
        Collections.sort(keys);

        return keys;
    }

}

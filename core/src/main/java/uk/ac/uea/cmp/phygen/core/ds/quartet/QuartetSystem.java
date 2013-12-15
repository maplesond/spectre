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

import uk.ac.uea.cmp.phygen.core.ds.Taxa;
import uk.ac.uea.cmp.phygen.core.ds.distance.DistanceMatrix;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 18/11/13
 * Time: 22:37
 * To change this template use File | Settings | File Templates.
 */
public class QuartetSystem {

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
    private WeightedQuartetMap quartets;

    public QuartetSystem() {
        this(new Taxa(), 1.0, new WeightedQuartetMap());
    }

    public QuartetSystem(Taxa taxa, double weight, WeightedQuartetMap quartets) {
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

    public QuartetSystem(DistanceMatrix distanceMatrix, double weight) {

        final int N = distanceMatrix.getNbTaxa();
        final int expectedNbQuartets = Quartet.over4(N);

        this.quartets = new WeightedQuartetMap(distanceMatrix);

        if (this.quartets.size() != expectedNbQuartets) {
            throw new IllegalArgumentException("Found unexpected number of quartets.  Something went wrong creating the " +
                    "quartet hash.  There were " + N + " taxa in the distance matrix, which should correspond to " + expectedNbQuartets +
                    "quartets in the hash.  Instead we found " + this.quartets.size() + " quartets in the hash.");
        }

        this.taxa = distanceMatrix.getTaxaSet();
        this.weight = weight;
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

    public WeightedQuartetMap getQuartets() {
        return quartets;
    }

    public void setQuartets(WeightedQuartetMap quartets) {
        this.quartets = quartets;
    }



    public void normaliseQuartets(boolean logscale) {
       this.quartets.normalize(logscale, sense == Sense.MAX);
    }

    public void setTaxaIndicies(Taxa superTaxaSet) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}

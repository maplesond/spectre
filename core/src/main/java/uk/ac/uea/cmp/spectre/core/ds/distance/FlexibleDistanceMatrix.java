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

package uk.ac.uea.cmp.spectre.core.ds.distance;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import uk.ac.uea.cmp.spectre.core.ds.Identifier;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;

import java.util.*;

/**
 * Created by dan on 27/02/14.
 */
public class FlexibleDistanceMatrix extends AbstractDistanceMatrix {

    private Map<Pair<Identifier, Identifier>, Double> matrix;
    private IdentifierList taxa;


    public FlexibleDistanceMatrix() {
        this(0);
    }

    public FlexibleDistanceMatrix(final int size) {

        this.matrix = new HashMap<>();
        this.taxa = new IdentifierList(size);
        for (Identifier id : this.taxa) {
            this.addIdentifier(id);
        }
    }

    public FlexibleDistanceMatrix(final IdentifierList taxa) {

        this.matrix = new HashMap<>();
        this.taxa = taxa;
        for (Identifier id : this.taxa) {
            this.addIdentifier(id);
        }
    }

    public FlexibleDistanceMatrix(DistanceMatrix copy) {

        this.taxa = new IdentifierList(copy.getTaxa());
        this.matrix = new HashMap<>();

        for (Identifier ti : this.taxa) {
            for (Identifier tj : this.taxa) {
                this.setDistance(ti, tj, copy.getDistance(ti, tj));
            }
        }
    }

    public FlexibleDistanceMatrix(double[][] distances) {
        this(new IdentifierList(distances.length), distances);
    }

    public FlexibleDistanceMatrix(IdentifierList taxa, double[][] distances) {

        if (taxa.size() != distances.length) {
            throw new IllegalArgumentException("Taxa size is not the same as matrix size.  Taxa size: " +
                    taxa.size() + "; Matrix size: " + distances.length);
        }

        this.taxa = new IdentifierList(taxa);
        this.matrix = new HashMap<>();
        for (int i = 0; i < taxa.size(); i++) {
            for (int j = 0; j < taxa.size(); j++) {
                this.setDistance(this.taxa.get(i), this.taxa.get(j), distances[i][j]);
            }
        }
    }


    protected Pair<Identifier, Identifier> getSortedPair(final Identifier taxon1, final Identifier taxon2) {
        return taxon1.getId() < taxon2.getId() ? new ImmutablePair<>(taxon1, taxon2) : new ImmutablePair<>(taxon2, taxon1);
    }


    @Override
    public double getDistance(final Identifier taxon1, final Identifier taxon2) {

        if (taxon1 == null || taxon2 == null)
            throw new IllegalArgumentException("Need two valid taxa to get a distance");

        if (taxon1.equals(taxon2)) {
            return 0.0;
        }

        Double val = this.matrix.get(this.getSortedPair(taxon1, taxon2));

        return val == null ? 0.0 : val;
    }

    @Override
    public double getDistance(final int taxon1Id, final int taxon2Id) {
        return this.getDistance(this.taxa.getById(taxon1Id), this.taxa.getById(taxon2Id));
    }

    @Override
    public double getDistance(String taxon1Name, String taxon2Name) {
        return this.getDistance(this.taxa.getByName(taxon1Name), this.taxa.getByName(taxon2Name));
    }

    @Override
    public void addIdentifier(Identifier id) {

        if (!taxa.contains(id)) {
            this.taxa.add(id);

            for (Identifier i : this.taxa) {
                this.setDistance(i, id, 0.0);
            }
        }
    }

    @Override
    public double setDistance(Identifier taxon1, Identifier taxon2, final double value) {

        if (taxon1 == null || taxon2 == null)
            throw new IllegalArgumentException("Need two valid taxa to set a distance");

        if (!taxa.contains(taxon1)) {
            this.addIdentifier(taxon1);
        }

        if (!taxa.contains(taxon2)) {
            this.addIdentifier(taxon2);
        }

        // Don't do anything if both taxa are the same, except return 0.0, otherwise update the matrix and return the old
        // result
        Double oldVal = taxon1.equals(taxon2) ?
                Double.valueOf(0.0) :
                this.matrix.put(this.getSortedPair(taxon1, taxon2), value);

        return oldVal == null ? 0.0 : oldVal;
    }

    @Override
    public double setDistance(String taxonName1, String taxonName2, final double value) {
        return this.setDistance(this.taxa.getByName(taxonName1), this.taxa.getByName(taxonName2), value);
    }

    @Override
    public double setDistance(final int taxonId1, final int taxonId2, final double value) {
        return this.setDistance(this.taxa.getById(taxonId1), this.taxa.getById(taxonId2), value);
    }

    @Override
    public double incrementDistance(final int taxonId1, final int taxonId2, final double increment) {
        return this.incrementDistance(this.taxa.getById(taxonId1), this.taxa.getById(taxonId2), increment);
    }

    @Override
    public double incrementDistance(String taxon1Name, String taxon2Name, double increment) {
        return this.incrementDistance(this.taxa.getByName(taxon1Name), this.taxa.getByName(taxon2Name), increment);
    }


    @Override
    public IdentifierList getTaxa() {
        return this.taxa;
    }

    @Override
    public IdentifierList getTaxa(Comparator<Identifier> comparator) {
        return this.taxa.sortCopy(comparator);
    }


    @Override
    public DistanceList getDistances(Identifier taxon, Comparator<Identifier> comparator) {

        DistanceList dl = new FlexibleDistanceList(taxon);

        IdentifierList sorted = comparator == null ? this.taxa : this.taxa.sortCopy(comparator);

        for (Identifier t : sorted) {

            if (t != taxon) {
                dl.setDistance(t, this.getDistance(taxon, t));
            }
        }

        return dl;
    }


    @Override
    public DistanceList getDistances(int taxonId, Comparator<Identifier> comparator) {
        return this.getDistances(this.taxa.getById(taxonId), comparator);
    }


    @Override
    public DistanceList getDistances(String taxonName, Comparator<Identifier> comparator) {
        return this.getDistances(this.taxa.getByName(taxonName), comparator);
    }


    @Override
    public List<DistanceList> getAllDistances(Comparator<Identifier> comparator) {

        IdentifierList sorted = comparator == null ? this.taxa : this.taxa.sortCopy(comparator);

        List<DistanceList> allDistances = new ArrayList<>(this.size());

        for (Identifier t : sorted) {
            allDistances.add(this.getDistances(t, comparator));
        }

        return allDistances;
    }


    @Override
    public double[][] getMatrix(Comparator<Identifier> comparator) {

        double[][] matrix = new double[this.size()][this.size()];

        IdentifierList sorted = comparator == null ? this.taxa : this.taxa.sortCopy(comparator);

        for (int i = 0; i < sorted.size(); i++) {

            Identifier ti = sorted.get(i);

            for (int j = 0; j < sorted.size(); j++) {

                Identifier tj = sorted.get(j);

                matrix[i][j] = ti == tj ? 0.0 : this.getDistance(ti, tj);
            }

        }

        return matrix;
    }


    @Override
    public int size() {
        return this.taxa.size();
    }

    @Override
    public void removeTaxon(Identifier taxon) {

        List<Pair<Identifier, Identifier>> toRemove = new ArrayList<>();

        for (Pair<Identifier, Identifier> key : this.matrix.keySet()) {

            if (key.getLeft().equals(taxon) || key.getRight().equals(taxon)) {
                toRemove.add(key);
            }
        }

        for (int i = 0; i < toRemove.size(); i++) {
            this.matrix.remove(toRemove.get(i));
        }

        this.taxa.remove(taxon);
    }

    @Override
    public void removeTaxon(int taxonId) {
        this.removeTaxon(this.taxa.getById(taxonId));
    }

    @Override
    public void removeTaxon(String taxonName) {
        this.removeTaxon(this.taxa.getByName(taxonName));
    }

    @Override
    public Map<Pair<Identifier, Identifier>, Double> getMap() {
        return this.matrix;
    }

}

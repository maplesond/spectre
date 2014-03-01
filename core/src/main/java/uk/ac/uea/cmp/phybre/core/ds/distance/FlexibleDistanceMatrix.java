package uk.ac.uea.cmp.phybre.core.ds.distance;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import uk.ac.uea.cmp.phybre.core.ds.Taxa;
import uk.ac.uea.cmp.phybre.core.ds.Taxon;

import java.util.*;

/**
 * Created by dan on 27/02/14.
 */
public class FlexibleDistanceMatrix implements DistanceMatrix {

    private Map<Pair<Taxon,Taxon>, Double> matrix;
    private Taxa taxa;


    public FlexibleDistanceMatrix() {
        this(0);
    }

    public FlexibleDistanceMatrix(final int size) {

        this.matrix = new HashMap<>();
        this.taxa = new Taxa(size);
        for(Taxon ti : this.taxa) {
            for(Taxon tj : this.taxa) {
                this.setDistance(ti, tj, 0.0);
            }
        }
    }

    public FlexibleDistanceMatrix(DistanceMatrix copy) {

        this.taxa = new Taxa(copy.getTaxa());
        this.matrix = new HashMap<>();

        for(Taxon ti : this.taxa) {
            for(Taxon tj : this.taxa) {
                this.setDistance(ti, tj, copy.getDistance(ti, tj));
            }
        }
    }

    public FlexibleDistanceMatrix(Taxa taxa, double[][] distances) {

        if(taxa.size() != distances.length) {
            throw new IllegalArgumentException("Taxa size is not the same as matrix size.  Taxa size: " +
                    taxa.size() + "; Matrix size: " + distances.length);
        }

        this.taxa = new Taxa(taxa);
        this.matrix = new HashMap<>();
        for(int i = 0; i < taxa.size(); i++) {
            for(int j = 0; j < taxa.size(); j++) {
                this.setDistance(this.taxa.get(i), this.taxa.get(j), distances[i][j]);
            }
        }
    }


    protected Pair<Taxon, Taxon> getSortedPair(Taxon taxon1, Taxon taxon2) {

        Taxon a = taxon1;
        Taxon b = taxon2;

        if (a.getId() > b.getId()) {
            a = taxon2;
            b = taxon1;
        }

        return new ImmutablePair<>(a, b);
    }


    @Override
    public double getDistance(final Taxon taxon1, final Taxon taxon2) {

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
    public double setDistance(Taxon taxon1, Taxon taxon2, final double value) {

        if (taxon1 == null || taxon2 == null)
            throw new IllegalArgumentException("Need two valid taxa to set a distance");

        if (!taxa.contains(taxon1)) {
            taxa.add(taxon1);
        }

        if (!taxa.contains(taxon2)) {
            taxa.add(taxon2);
        }

        // Don't do anything if both taxa are the same, except return 0.0, otherwise update the matrix and return the old
        // result
        Double oldVal = taxon1.equals(taxon2) ?
                new Double(0.0) :
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
    public double incrementDistance(final Taxon taxon1, final Taxon taxon2, final double increment) {

        double newValue = this.getDistance(taxon1, taxon2) + increment;

        this.setDistance(taxon1, taxon2, newValue);

        return newValue;
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
    public int getNbTaxa() {
        return this.taxa.size();
    }

    @Override
    public Taxa getTaxa() {
        return this.taxa;
    }

    @Override
    public Taxa getTaxa(Comparator<Taxon> comparator) {
        return this.taxa.sort(comparator);
    }

    @Override
    public DistanceList getDistances(Taxon taxon, Comparator<Taxon> comparator) {

        DistanceList dl = new FlexibleDistanceList(taxon);

        Taxa sorted = comparator == null ? this.taxa : this.taxa.sort(comparator);

        for(Taxon t : sorted) {

            if (t != taxon) {
                dl.setDistance(t, this.getDistance(taxon, t));
            }
        }

        return dl;
    }

    @Override
    public DistanceList getDistances(int taxonId, Comparator<Taxon> comparator) {
        return this.getDistances(this.taxa.getById(taxonId), comparator);
    }

    @Override
    public DistanceList getDistances(String taxonName, Comparator<Taxon> comparator) {
        return this.getDistances(this.taxa.getByName(taxonName), comparator);
    }

    @Override
    public List<DistanceList> getAllDistances() {

        return this.getAllDistances(null);
    }

    @Override
    public List<DistanceList> getAllDistances(Comparator<Taxon> comparator) {

        Taxa sorted = comparator == null ? this.taxa : this.taxa.sort(comparator);

        List<DistanceList> allDistances = new ArrayList<>(this.size());

        for(Taxon t : sorted) {
            allDistances.add(this.getDistances(t, comparator));
        }

        return allDistances;
    }

    @Override
    public double[][] getMatrix() {
        return this.getMatrix(null);
    }

    @Override
    public double[][] getMatrix(Comparator<Taxon> comparator) {

        double[][] matrix = new double[this.size()][this.size()];

        Taxa sorted = comparator == null ? this.taxa : this.taxa.sort(comparator);

        for(int i = 0; i < sorted.size(); i++) {

            Taxon ti = sorted.get(i);

            for(int j = 0; j < sorted.size(); j++) {

                Taxon tj = sorted.get(j);

                matrix[i][j] = ti == tj ? 0.0 : this.getDistance(ti, tj);
            }

        }

        return matrix;
    }


    @Override
    public int size() {
        return this.getNbTaxa();
    }

}

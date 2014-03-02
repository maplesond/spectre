package uk.ac.uea.cmp.phybre.core.ds.distance;

import uk.ac.uea.cmp.phybre.core.ds.Identifier;
import uk.ac.uea.cmp.phybre.core.ds.IdentifierList;

import java.util.HashMap;

/**
 * Created by dan on 27/02/14.
 */
public class FlexibleDistanceList extends HashMap<Identifier, Double> implements DistanceList {

    private IdentifierList otherTaxa;
    private Identifier primaryTaxon;


    /**
     * Constructs an empty <tt>FlexibleDistanceList</tt>
     */
    public FlexibleDistanceList(Identifier primaryTaxon) {
        super();

        this.otherTaxa = new IdentifierList();
        this.primaryTaxon = primaryTaxon;
    }



    @Override
    public Double put(Identifier key, Double value) {

        Double oldValue = super.put(key, value);

        if (oldValue == null) {
            otherTaxa.add(key);
        }

        return oldValue;
    }

    @Override
    public Double remove(Object key) {
        this.otherTaxa.remove(key);
        return super.remove(key);
    }



    @Override
    public double getDistance(Identifier taxon) {
        return this.get(taxon);
    }

    @Override
    public double getDistance(int taxonId) {
        return this.get(this.otherTaxa.getById(taxonId));
    }

    @Override
    public double getDistance(String taxonName) {
        return this.get(this.otherTaxa.getByName(taxonName));
    }

    @Override
    public double setDistance(Identifier taxon, double value) {
        Double oldVal = this.put(taxon, value);
        return oldVal == null ? 0.0 : oldVal;
    }

    @Override
    public double setDistance(String taxonName, double value) {
        return this.setDistance(this.otherTaxa.getByName(taxonName), value);
    }

    @Override
    public double setDistance(int taxonId, double value) {
        return this.setDistance(this.otherTaxa.getById(taxonId), value);
    }

    @Override
    public double incrementDistance(Identifier taxon, double increment) {

        double newValue = this.getDistance(taxon) + increment;
        this.setDistance(taxon, newValue);
        return newValue;
    }

    @Override
    public double incrementDistance(int taxonId, double increment) {
        return this.incrementDistance(this.otherTaxa.getById(taxonId), increment);
    }

    @Override
    public double incrementDistance(String taxonName, double increment) {
        return this.incrementDistance(this.otherTaxa.getByName(taxonName), increment);
    }

    @Override
    public Identifier getTaxon() {
        return this.primaryTaxon;
    }

    @Override
    public IdentifierList getOtherTaxa() {
        return this.otherTaxa;
    }


    @Override
    public double sum() {

        double sum = 0.0;

        for(Double value : this.values()) {
            sum += value;
        }

        return sum;
    }
}

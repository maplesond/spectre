package uk.ac.uea.cmp.phygen.tools.chopper.loader;

import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetWeights;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 25/09/13
 * Time: 21:01
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractLoader implements Source {

    protected LinkedList<QuartetWeights> qWs;
    protected LinkedList<Double> weights;
    protected LinkedList<String> taxonNames;
    protected int index;

    protected AbstractLoader() {
        this(new LinkedList<QuartetWeights>(), new LinkedList<Double>(), new LinkedList<String>(), 0);
    }

    protected AbstractLoader(LinkedList<QuartetWeights> qWs, LinkedList<Double> weights, LinkedList<String> taxonNames,
                             int index) {
        this.qWs = qWs;
        this.weights = weights;
        this.taxonNames = taxonNames;
        this.index = index;
    }

    @Override
    public LinkedList<QuartetWeights> getQuartetWeights() {
        return qWs;
    }

    @Override
    public LinkedList<Double> getWeights() {
        return weights;
    }

    @Override
    public LinkedList<String> getTaxonNames() {
        return taxonNames;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public void process() {
    }


    @Override
    public void harvestNames(LinkedList<String> newTaxonNames) {

        ListIterator lI = taxonNames.listIterator();
        while (lI.hasNext()) {
            String taxonName = (String) lI.next();
            if (!newTaxonNames.contains(taxonName)) {
                newTaxonNames.add(taxonName);
            }
        }
    }

    @Override
    public void translate(LinkedList<String> newTaxonNames) {
        qWs.set(0, qWs.get(0).translate(taxonNames, newTaxonNames));
    }

    @Override
    public double getWSum() {
        return weights.get(0);
    }

    @Override
    public QuartetWeights getNextQuartetWeights() {
        index++;
        return this.qWs.get(0);
    }

    @Override
    public double getNextWeight() {
        return weights.get(0);
    }

    @Override
    public boolean hasMoreSets() {
        return index < 1;
    }
}

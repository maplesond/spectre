package uk.ac.uea.cmp.phygen.tools.chopper.loader;

import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetWeights;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 25/09/13
 * Time: 21:01
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractLoader implements Source {

    protected List<QuartetWeights> qWs;
    protected List<Double> weights;
    protected List<String> taxonNames;
    protected int index;

    protected AbstractLoader() {
        this(new ArrayList<QuartetWeights>(), new ArrayList<Double>(), new ArrayList<String>(), 0);
    }

    protected AbstractLoader(List<QuartetWeights> qWs, List<Double> weights, List<String> taxonNames,
                             int index) {
        this.qWs = qWs;
        this.weights = weights;
        this.taxonNames = taxonNames;
        this.index = index;
    }

    @Override
    public List<QuartetWeights> getQuartetWeights() {
        return qWs;
    }

    @Override
    public List<Double> getWeights() {
        return weights;
    }

    @Override
    public List<List<String>> getTaxonNames() {

        List<List<String>> result = new ArrayList<>();
        result.add(taxonNames);
        return result;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public void process() {
    }


    @Override
    public void harvestNames(List<String> newTaxonNames) {

        for(String taxonName : this.taxonNames) {
            if (!newTaxonNames.contains(taxonName)) {
                newTaxonNames.add(taxonName);
            }
        }
    }

    @Override
    public void translate(List<String> newTaxonNames) {
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

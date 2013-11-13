package uk.ac.uea.cmp.phygen.tools.chopper.loader;

import uk.ac.uea.cmp.phygen.core.ds.Taxa;
import uk.ac.uea.cmp.phygen.core.ds.Taxon;
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
    protected Taxa taxonNames;
    protected int index;

    protected AbstractLoader() {
        this(new ArrayList<QuartetWeights>(), new ArrayList<Double>(), new Taxa(), 0);
    }

    protected AbstractLoader(List<QuartetWeights> qWs, List<Double> weights, Taxa taxonNames,
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
    public void addTaxa(Taxa taxa) {
        this.taxonNames.addAll(taxa);
    }

    @Override
    public List<Taxa> findTaxaSets() {

        List<Taxa> result = new ArrayList<>();
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
    public void translate(Taxa newTaxonNames) {
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

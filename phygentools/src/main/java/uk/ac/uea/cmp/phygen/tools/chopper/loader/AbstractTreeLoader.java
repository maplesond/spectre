package uk.ac.uea.cmp.phygen.tools.chopper.loader;

import uk.ac.uea.cmp.phygen.core.ds.Taxa;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetWeights;
import uk.ac.uea.cmp.phygen.core.ds.tree.newick.NewickTree;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 25/09/13
 * Time: 21:06
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractTreeLoader extends AbstractLoader {

    protected boolean branchLengths, treeWeights;
    protected List<NewickTree> trees;

    protected AbstractTreeLoader() {
        this(new ArrayList<QuartetWeights>(), new ArrayList<Double>(), new Taxa(), 0,
                false, false, new ArrayList<NewickTree>());
    }

    protected AbstractTreeLoader(List<QuartetWeights> qWs, List<Double> weights, Taxa taxonNames,
                                 int index, boolean branchLengths, boolean treeWeights, List<NewickTree> trees) {
        super(qWs, weights, taxonNames, index);
        this.branchLengths = branchLengths;
        this.treeWeights = treeWeights;
        this.trees = trees;
    }

    public boolean isBranchLengths() {
        return branchLengths;
    }

    public boolean isTreeWeights() {
        return treeWeights;
    }

    public List<NewickTree> getTrees() {
        return trees;
    }

    @Override
    public void process() {

        for(NewickTree tree : this.trees) {
            this.qWs.add(tree.createQuartets());
        }
    }

    @Override
    public List<Taxa> findTaxaSets() {

        List<Taxa> result = new ArrayList<>();

        for(NewickTree tree : this.trees) {
            result.add(tree.getTaxa());
        }

        return result;
    }

    @Override
    public void addTaxa(Taxa taxaSuperSet) {
        for(NewickTree tree : this.trees) {
            taxaSuperSet.addAll(tree.getTaxa());
        }
    }

    @Override
    public void translate(Taxa newTaxonNames) {
        taxonNames = newTaxonNames;
        for(NewickTree tree : this.trees) {
            tree.setIndiciesToExternalTaxaList(taxonNames);
        }
    }

    @Override
    public double getWSum() {

        double sum = 0.0;

        for (int n = 0; n < weights.size(); n++) {
            sum += weights.get(n);
        }

        return sum;
    }

    @Override
    public QuartetWeights getNextQuartetWeights() {
        return trees.get(index++).createQuartets();
    }

    @Override
    public double getNextWeight() {
        return weights.get(index-1);
    }

    @Override
    public boolean hasMoreSets() {
        return index < trees.size();
    }
}

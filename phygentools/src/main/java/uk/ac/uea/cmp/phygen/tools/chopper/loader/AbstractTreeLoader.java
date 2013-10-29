package uk.ac.uea.cmp.phygen.tools.chopper.loader;

import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetWeights;
import uk.ac.uea.cmp.phygen.tools.chopper.Node;
import uk.ac.uea.cmp.phygen.tools.chopper.Tree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 25/09/13
 * Time: 21:06
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractTreeLoader extends AbstractLoader {

    protected boolean branchLengths, treeWeights;
    protected List<Tree> trees;

    protected AbstractTreeLoader() {
        this(new ArrayList<QuartetWeights>(), new ArrayList<Double>(), new ArrayList<String>(), 0,
                false, false, new ArrayList<Tree>());
    }

    protected AbstractTreeLoader(List<QuartetWeights> qWs, List<Double> weights, List<String> taxonNames,
                                 int index, boolean branchLengths, boolean treeWeights, List<Tree> trees) {
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

    public List<Tree> getTrees() {
        return trees;
    }

    @Override
    public void process() {

        for(Tree tree : this.trees) {
            this.qWs.add(tree.quartetize(taxonNames.size()));
        }
    }

    @Override
    public void harvestNames(List<String> newTaxonNames) {
        for(Tree tree : this.trees) {
            tree.harvestNames(newTaxonNames);
        }
    }

    @Override
    public List<List<String>> getTaxonNames() {

        List<List<String>> result = new ArrayList<>();

        for(Tree tree : this.trees) {
            List<String> treeNames = new ArrayList<>();
            tree.harvestNames(treeNames);
            result.add(treeNames);
        }

        return result;
    }

    @Override
    public void translate(List<String> newTaxonNames) {
        taxonNames = newTaxonNames;
        for(Tree tree : this.trees) {
            tree.index(taxonNames);
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
        return trees.get(index++).quartetize(taxonNames.size());
    }

    @Override
    public double getNextWeight() {
        return weights.get(index);
    }

    @Override
    public boolean hasMoreSets() {
        return index < trees.size();
    }
}

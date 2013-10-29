package uk.ac.uea.cmp.phygen.tools.chopper.loader;

import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetWeights;
import uk.ac.uea.cmp.phygen.tools.chopper.Node;
import uk.ac.uea.cmp.phygen.tools.chopper.Tree;

import java.util.LinkedList;
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
    protected LinkedList<Tree> trees;

    protected AbstractTreeLoader() {
        this(new LinkedList<QuartetWeights>(), new LinkedList<Double>(), new LinkedList<String>(), 0,
                false, false, new LinkedList<Tree>());
    }

    protected AbstractTreeLoader(LinkedList<QuartetWeights> qWs, LinkedList<Double> weights, LinkedList<String> taxonNames,
                                 int index, boolean branchLengths, boolean treeWeights, LinkedList<Tree> trees) {
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

    public LinkedList<Tree> getTrees() {
        return trees;
    }

    @Override
    public void process() {

        ListIterator<Tree> lI = trees.listIterator();

        LinkedList<QuartetWeights> qWs = new LinkedList<>();

        while (lI.hasNext()) {
            qWs.add(lI.next().quartetize(taxonNames.size()));
        }
    }

    @Override
    public void harvestNames(LinkedList<String> newTaxonNames) {
        ListIterator lI = trees.listIterator();
        while (lI.hasNext()) {
            ((Node) lI.next()).harvestNames(newTaxonNames);
        }
    }

    @Override
    public LinkedList<String> getTaxonNames() {
        LinkedList<String> result = new LinkedList<>();
        ListIterator lI = trees.listIterator();
        while (lI.hasNext()) {
            LinkedList<String> treeNames = new LinkedList<>();
            ((Node) lI.next()).harvestNames(treeNames);
            result.addAll(treeNames);
        }

        return result;
    }

    @Override
    public void translate(LinkedList<String> newTaxonNames) {
        taxonNames = newTaxonNames;
        ListIterator lI = trees.listIterator();
        while (lI.hasNext()) {
            ((Node) lI.next()).index(taxonNames);
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

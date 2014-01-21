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

package uk.ac.uea.cmp.phybre.core.ds.tree.newick.parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.phybre.core.ds.Taxon;
import uk.ac.uea.cmp.phybre.core.ds.tree.newick.NewickNode;
import uk.ac.uea.cmp.phybre.core.ds.tree.newick.NewickSubTree;
import uk.ac.uea.cmp.phybre.core.ds.tree.newick.NewickTree;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 08/11/13
 * Time: 16:17
 * To change this template use File | Settings | File Templates.
 */
public class NewickTreePopulator implements NewickTreeListener {

    private static Logger log = LoggerFactory.getLogger(NewickTreePopulator.class);

    private NewickTree tree;
    private boolean verbose;

    private NewickNode currentNode;
    private int depth;

    /**
     * Provide the object to populate with findings from the parse context
     * @param tree
     */
    public NewickTreePopulator(NewickTree tree, boolean verbose) {
        this.tree = tree;
        this.verbose = verbose;
        this.currentNode = tree;
        this.depth = 0;
    }

    @Override
    public void enterBranchset(@NotNull NewickTreeParser.BranchsetContext ctx) {


    }

    @Override
    public void exitBranchset(@NotNull NewickTreeParser.BranchsetContext ctx) {

    }

    @Override
    public void enterLeaf(@NotNull NewickTreeParser.LeafContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitLeaf(@NotNull NewickTreeParser.LeafContext ctx) {

    }

    @Override
    public void enterWeight(@NotNull NewickTreeParser.WeightContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitWeight(@NotNull NewickTreeParser.WeightContext ctx) {
        if (ctx.WEIGHT() != null && !ctx.WEIGHT().getSymbol().getText().isEmpty()) {

            String weightStr = ctx.WEIGHT().getText().substring(1);

            if (!weightStr.isEmpty()) {

                double scalingFactor = Double.parseDouble(ctx.WEIGHT().getText().substring(1));

                if (verbose && log.isDebugEnabled())
                    log.debug("Scaling factor: " + scalingFactor);

                tree.setScalingFactor(scalingFactor);
            }
        }
    }

    @Override
    public void enterSubtree(@NotNull NewickTreeParser.SubtreeContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitSubtree(@NotNull NewickTreeParser.SubtreeContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterName(@NotNull NewickTreeParser.NameContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitName(@NotNull NewickTreeParser.NameContext ctx) {

        if (ctx.WORD() != null) {
            String name = ctx.WORD().getText();

            if (verbose && log.isDebugEnabled())
                log.debug("Name: " + name);

            this.currentNode.setTaxon(new Taxon(name));
        }
    }

    @Override
    public void enterLength(@NotNull NewickTreeParser.LengthContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitLength(@NotNull NewickTreeParser.LengthContext ctx) {
        if (ctx.LENGTH() != null && !ctx.LENGTH().getText().isEmpty()) {

            String lenStr = ctx.LENGTH().getText().substring(1);

            if (!lenStr.isEmpty()) {

                double length = Double.parseDouble(lenStr);

                if (verbose && log.isDebugEnabled())
                    log.debug("Length: " + length);

                this.currentNode.setLength(length);
            }
        }
    }

    @Override
    public void enterBranch(@NotNull NewickTreeParser.BranchContext ctx) {
        NewickNode newBranch = new NewickSubTree();
        this.currentNode.addBranch(newBranch);
        this.currentNode = newBranch;
        this.depth++;

        if (verbose && log.isDebugEnabled())
            log.debug("Created new child branch.  Depth: " + depth);
    }

    @Override
    public void exitBranch(@NotNull NewickTreeParser.BranchContext ctx) {

        this.currentNode = this.currentNode.getParent();
        this.depth--;

        if (verbose && log.isDebugEnabled())
            log.debug("Exited branch.  Depth: " + depth);
    }

    @Override
    public void enterParse(@NotNull NewickTreeParser.ParseContext ctx) {

    }

    @Override
    public void exitParse(@NotNull NewickTreeParser.ParseContext ctx) {

    }

    @Override
    public void enterInternal(@NotNull NewickTreeParser.InternalContext ctx) {

    }

    @Override
    public void exitInternal(@NotNull NewickTreeParser.InternalContext ctx) {

    }

    @Override
    public void visitTerminal(@NotNull TerminalNode node) {

    }

    @Override
    public void visitErrorNode(@NotNull ErrorNode node) {

    }

    @Override
    public void enterEveryRule(@NotNull ParserRuleContext ctx) {

    }

    @Override
    public void exitEveryRule(@NotNull ParserRuleContext ctx) {

    }
}

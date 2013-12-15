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

package uk.ac.uea.cmp.phygen.core.io.qweight.parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.phygen.core.ds.Taxon;
import uk.ac.uea.cmp.phygen.core.ds.quartet.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 01/12/13
 * Time: 22:52
 * To change this template use File | Settings | File Templates.
 */
public class QWeightPopulator implements QWeightListener {


    private static Logger log = LoggerFactory.getLogger(QWeightPopulator.class);

    private QuartetNetwork quartetNetwork;
    private WeightedQuartetMap weightedQuartets;

    private int expectedTaxa;

    public QWeightPopulator(QuartetNetwork quartetNetwork) {
        this.quartetNetwork = quartetNetwork;
        this.weightedQuartets = new WeightedQuartetMap();
        this.expectedTaxa = 0;
    }

    @Override
    public void enterW3(@NotNull QWeightParser.W3Context ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitW3(@NotNull QWeightParser.W3Context ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterSense(@NotNull QWeightParser.SenseContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitSense(@NotNull QWeightParser.SenseContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterTaxa(@NotNull QWeightParser.TaxaContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitTaxa(@NotNull QWeightParser.TaxaContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterW1(@NotNull QWeightParser.W1Context ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitW1(@NotNull QWeightParser.W1Context ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterW2(@NotNull QWeightParser.W2Context ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitW2(@NotNull QWeightParser.W2Context ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterV(@NotNull QWeightParser.VContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitV(@NotNull QWeightParser.VContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterU(@NotNull QWeightParser.UContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitU(@NotNull QWeightParser.UContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterWords(@NotNull QWeightParser.WordsContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitWords(@NotNull QWeightParser.WordsContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterSense_option(@NotNull QWeightParser.Sense_optionContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitSense_option(@NotNull QWeightParser.Sense_optionContext ctx) {
        this.quartetNetwork.setSense(QuartetNetwork.Sense.valueOf(ctx.getText().toUpperCase()));
    }

    @Override
    public void enterDescription(@NotNull QWeightParser.DescriptionContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitDescription(@NotNull QWeightParser.DescriptionContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterNbtaxa(@NotNull QWeightParser.NbtaxaContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitNbtaxa(@NotNull QWeightParser.NbtaxaContext ctx) {
        this.expectedTaxa = Integer.parseInt(ctx.NUMERIC().getText());
    }

    @Override
    public void enterQuartets(@NotNull QWeightParser.QuartetsContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitQuartets(@NotNull QWeightParser.QuartetsContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterParse(@NotNull QWeightParser.ParseContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitParse(@NotNull QWeightParser.ParseContext ctx) {

        // log some warnings if things aren't proceeding as expected
        final int actualTaxa = this.quartetNetwork.getTaxa().size();

        if (actualTaxa != this.expectedTaxa) {

            log.warn("Found the unexpected number of taxa in file.  Was expecting: " + this.expectedTaxa + ";  found: " + actualTaxa);
        }

        final int expectedQuartets = Quartet.over4(this.expectedTaxa);
        final int actualQuartets = this.quartetNetwork.getQuartets().size();

        if (actualQuartets != expectedQuartets) {

            log.warn("Found the unexpected number of quartets in file.  Was expecting: " + expectedQuartets + " (calculated " +
                     " from " + this.expectedTaxa + " taxa); found: " + actualQuartets);
        }
    }

    @Override
    public void enterQuartet(@NotNull QWeightParser.QuartetContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitQuartet(@NotNull QWeightParser.QuartetContext ctx) {

        int a = Integer.parseInt(ctx.x().NUMERIC().getText());
        int b = Integer.parseInt(ctx.y().NUMERIC().getText());
        int c = Integer.parseInt(ctx.u().NUMERIC().getText());
        int d = Integer.parseInt(ctx.v().NUMERIC().getText());

        double w1 = Double.parseDouble(ctx.w1().NUMERIC().getText());
        double w2 = Double.parseDouble(ctx.w2().NUMERIC().getText());
        double w3 = Double.parseDouble(ctx.w3().NUMERIC().getText());

        Quartet quartet = new Quartet(a, b, c, d);
        QuartetWeights weights = new QuartetWeights(w1, w2, w3);

        this.quartetNetwork.getQuartets().put(quartet, weights);
    }

    @Override
    public void enterTaxon(@NotNull QWeightParser.TaxonContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitTaxon(@NotNull QWeightParser.TaxonContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        int id = Integer.parseInt(ctx.NUMERIC().getText());
        this.quartetNetwork.getTaxa().add(new Taxon(name, id));
    }

    @Override
    public void enterY(@NotNull QWeightParser.YContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitY(@NotNull QWeightParser.YContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterX(@NotNull QWeightParser.XContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitX(@NotNull QWeightParser.XContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visitTerminal(@NotNull TerminalNode node) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visitErrorNode(@NotNull ErrorNode node) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterEveryRule(@NotNull ParserRuleContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitEveryRule(@NotNull ParserRuleContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}

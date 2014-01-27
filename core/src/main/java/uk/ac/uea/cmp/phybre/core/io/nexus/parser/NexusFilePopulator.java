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

package uk.ac.uea.cmp.phybre.core.io.nexus.parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.phybre.core.ds.Taxa;
import uk.ac.uea.cmp.phybre.core.ds.Taxon;
import uk.ac.uea.cmp.phybre.core.ds.distance.DistanceMatrixBuilder;
import uk.ac.uea.cmp.phybre.core.ds.split.SplitBlock;
import uk.ac.uea.cmp.phybre.core.io.nexus.Nexus;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 15/11/13
 * Time: 19:17
 * To change this template use File | Settings | File Templates.
 */
public class NexusFilePopulator implements NexusFileListener {

    private static Logger log = LoggerFactory.getLogger(NexusFilePopulator.class);

    private Nexus nexus;
    private boolean verbose;

    private DistanceMatrixBuilder distanceMatrixBuilder;
    private NexusSplitSystemBuilder splitSystemBuilder;
    private NexusQuartetNetworkBuilder quartetNetworkBuilder;



    public NexusFilePopulator(Nexus nexus, boolean verbose) {
        this.nexus = nexus;
        this.verbose = verbose;
        this.distanceMatrixBuilder = new DistanceMatrixBuilder();
        this.splitSystemBuilder = new NexusSplitSystemBuilder();
        this.quartetNetworkBuilder = new NexusQuartetNetworkBuilder();
    }

    @Override
    public void enterQuartets_block_header(@NotNull NexusFileParser.Quartets_block_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitQuartets_block_header(@NotNull NexusFileParser.Quartets_block_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterCycle_item_list(@NotNull NexusFileParser.Cycle_item_listContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitCycle_item_list(@NotNull NexusFileParser.Cycle_item_listContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterTree_definition(@NotNull NexusFileParser.Tree_definitionContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitTree_definition(@NotNull NexusFileParser.Tree_definitionContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterConfidences_splits(@NotNull NexusFileParser.Confidences_splitsContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitConfidences_splits(@NotNull NexusFileParser.Confidences_splitsContext ctx) {
        this.splitSystemBuilder.setHasConfidences(
                ctx.boolean_option() == null ?
                        true :
                        ctx.boolean_option().getText().equalsIgnoreCase("true") ||
                                ctx.boolean_option().getText().equalsIgnoreCase("yes")
        );
    }

    @Override
    public void enterNchar(@NotNull NexusFileParser.NcharContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitNchar(@NotNull NexusFileParser.NcharContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterRoot(@NotNull NexusFileParser.RootContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitRoot(@NotNull NexusFileParser.RootContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterFormat_distances_item(@NotNull NexusFileParser.Format_distances_itemContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitFormat_distances_item(@NotNull NexusFileParser.Format_distances_itemContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterTree_list(@NotNull NexusFileParser.Tree_listContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitTree_list(@NotNull NexusFileParser.Tree_listContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterTriangle_option(@NotNull NexusFileParser.Triangle_optionContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitTriangle_option(@NotNull NexusFileParser.Triangle_optionContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterMatrix_quartets_data(@NotNull NexusFileParser.Matrix_quartets_dataContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitMatrix_quartets_data(@NotNull NexusFileParser.Matrix_quartets_dataContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterDistances_header(@NotNull NexusFileParser.Distances_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitDistances_header(@NotNull NexusFileParser.Distances_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterBlock_taxa(@NotNull NexusFileParser.Block_taxaContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitBlock_taxa(@NotNull NexusFileParser.Block_taxaContext ctx) {

        int nTax = -1;

        if (ctx.dimensions_taxa() != null && ctx.dimensions_taxa().ntax() != null ) {

            NexusFileParser.NtaxContext ctxNtax = ctx.dimensions_taxa().ntax();

            if (ctxNtax.NUMERIC() != null && !ctxNtax.NUMERIC().getText().isEmpty()) {

                nTax = Integer.parseInt(ctxNtax.NUMERIC().getText());

                if (verbose && log.isDebugEnabled())
                    log.debug("Taxa Block: ntax:" + nTax);
                }
        }

        Taxa taxa = this.nexus.getTaxa();

        if (ctx.taxlabels() != null) {

            taxa.add(new Taxon(ctx.taxlabels().IDENTIFIER().getText()));

            NexusFileParser.Identifier_listContext idListCtx = ctx.taxlabels().identifier_list();

            while(idListCtx != null && idListCtx.IDENTIFIER() != null) {
                taxa.add(new Taxon(idListCtx.IDENTIFIER().getText()));
                idListCtx = idListCtx.identifier_list();
            }
        }

        if (verbose && log.isDebugEnabled()) {
            log.debug("Taxa Block: Found: " + taxa.size());
        }

        if (nTax != taxa.size()) {
            log.warn("Nexus file says there should be " + nTax + " taxa but parser only found " + taxa.size() + " taxa.");
        }


    }

    @Override
    public void enterState_composed_word(@NotNull NexusFileParser.State_composed_wordContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitState_composed_word(@NotNull NexusFileParser.State_composed_wordContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterMissing(@NotNull NexusFileParser.MissingContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitMissing(@NotNull NexusFileParser.MissingContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterMatrix_header(@NotNull NexusFileParser.Matrix_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitMatrix_header(@NotNull NexusFileParser.Matrix_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterNsplits(@NotNull NexusFileParser.NsplitsContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitNsplits(@NotNull NexusFileParser.NsplitsContext ctx) {


    }

    @Override
    public void enterTree_label_optional(@NotNull NexusFileParser.Tree_label_optionalContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitTree_label_optional(@NotNull NexusFileParser.Tree_label_optionalContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterWeights_splits(@NotNull NexusFileParser.Weights_splitsContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitWeights_splits(@NotNull NexusFileParser.Weights_splitsContext ctx) {
        this.splitSystemBuilder.setWeighted(
                ctx.boolean_option() == null ?
                        true :
                        ctx.boolean_option().getText().equalsIgnoreCase("true") ||
                                ctx.boolean_option().getText().equalsIgnoreCase("yes")
        );
    }

    @Override
    public void enterMatrix_splits_data(@NotNull NexusFileParser.Matrix_splits_dataContext ctx) {


    }

    @Override
    public void exitMatrix_splits_data(@NotNull NexusFileParser.Matrix_splits_dataContext ctx) {

        if (ctx.NUMERIC() != null  && !ctx.NUMERIC().isEmpty()) {

            double weight = ctx.NUMERIC().size() == 2 ?
                    Double.parseDouble(ctx.NUMERIC(1).getText()) :
                    Double.parseDouble(ctx.NUMERIC(0).getText());

            NexusFileParser.Matrix_splits_listContext ctxList = ctx.matrix_splits_list();

            List<Integer> setA = new LinkedList<>();

            while (ctxList != null) {

                if (ctxList.NUMERIC() != null) {

                    int val = Integer.parseInt(ctxList.NUMERIC().getText());

                    setA.add(val);
                }

                ctxList = ctxList.matrix_splits_list();
            }

            this.splitSystemBuilder.addSplit(new SplitBlock(setA), weight);
        }
    }

    @Override
    public void enterProperties_splits_item(@NotNull NexusFileParser.Properties_splits_itemContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitProperties_splits_item(@NotNull NexusFileParser.Properties_splits_itemContext ctx) {

        if (ctx.properties_splits_name() != null) {

            String valStr = ctx.NUMERIC() != null ? ctx.NUMERIC().getText() : null;

            if (ctx.properties_splits_name().getText().equalsIgnoreCase("cyclic")) {
                this.splitSystemBuilder.setCyclic(true);
            }
            else if (ctx.properties_splits_name().getText().equalsIgnoreCase("fit")) {
                this.splitSystemBuilder.setFit(Double.parseDouble(valStr));
            }
        }
    }

    @Override
    public void enterDiagonal(@NotNull NexusFileParser.DiagonalContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitDiagonal(@NotNull NexusFileParser.DiagonalContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterCycle_header(@NotNull NexusFileParser.Cycle_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitCycle_header(@NotNull NexusFileParser.Cycle_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterCycle(@NotNull NexusFileParser.CycleContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitCycle(@NotNull NexusFileParser.CycleContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterTranslate_list(@NotNull NexusFileParser.Translate_listContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitTranslate_list(@NotNull NexusFileParser.Translate_listContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterState_complex_word(@NotNull NexusFileParser.State_complex_wordContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitState_complex_word(@NotNull NexusFileParser.State_complex_wordContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterLabels_header(@NotNull NexusFileParser.Labels_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitLabels_header(@NotNull NexusFileParser.Labels_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterLabels(@NotNull NexusFileParser.LabelsContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitLabels(@NotNull NexusFileParser.LabelsContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterDimensions_distances(@NotNull NexusFileParser.Dimensions_distancesContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitDimensions_distances(@NotNull NexusFileParser.Dimensions_distancesContext ctx) {

        if (ctx.newtaxa() != null) {

            NexusFileParser.NtaxContext ctxNtax = ctx.newtaxa().ntax();

            if (ctxNtax.NUMERIC() != null && !ctxNtax.NUMERIC().getText().isEmpty()) {
                this.distanceMatrixBuilder.setNbTaxa(Integer.parseInt(ctxNtax.NUMERIC().getText()));
            }

            NexusFileParser.NcharContext ctxNchar = ctx.nchar();

            if (ctxNchar.NUMERIC() != null && !ctxNchar.NUMERIC().getText().isEmpty()) {
                this.distanceMatrixBuilder.setNbChars(Integer.parseInt(ctxNchar.NUMERIC().getText()));
            }
        }
    }

    @Override
    public void enterLabels_splits(@NotNull NexusFileParser.Labels_splitsContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitLabels_splits(@NotNull NexusFileParser.Labels_splitsContext ctx) {
        this.splitSystemBuilder.setHasLabels(
                ctx.labels_option() == null ?
                        ctx.labels_header().getText().equalsIgnoreCase("labels") :
                        ctx.labels_option().getText().equalsIgnoreCase("true") ||
                                ctx.labels_option().getText().equalsIgnoreCase("yes"));
    }

    @Override
    public void enterProperties(@NotNull NexusFileParser.PropertiesContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitProperties(@NotNull NexusFileParser.PropertiesContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterIdentifier_list(@NotNull NexusFileParser.Identifier_listContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitIdentifier_list(@NotNull NexusFileParser.Identifier_listContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterFormat(@NotNull NexusFileParser.FormatContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitFormat(@NotNull NexusFileParser.FormatContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterDimensions_splits(@NotNull NexusFileParser.Dimensions_splitsContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitDimensions_splits(@NotNull NexusFileParser.Dimensions_splitsContext ctx) {

        if (this.nexus.getTaxa() != null) {
            this.splitSystemBuilder.setTaxa(this.nexus.getTaxa());
        }

        if (ctx.ntax() != null) {
            if (ctx.ntax().NUMERIC() != null) {
                this.splitSystemBuilder.setExpectedNbTaxa(Integer.parseInt(ctx.ntax().NUMERIC().getText()));
            }
        }

        if (ctx.nsplits() != null) {
            if (ctx.nsplits().NUMERIC() != null) {
                this.splitSystemBuilder.setExpectedNbSplits(Integer.parseInt(ctx.nsplits().NUMERIC().getText()));
            }
        }
    }

    @Override
    public void enterFormat_distances(@NotNull NexusFileParser.Format_distancesContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitFormat_distances(@NotNull NexusFileParser.Format_distancesContext ctx) {

        NexusFileParser.Format_distances_listContext ctxFormatList = ctx.format_distances_list();

        while (ctxFormatList != null) {
            NexusFileParser.Format_distances_itemContext ctxFormatItem = ctxFormatList.format_distances_item();

            if (ctxFormatItem != null) {
                if (ctxFormatItem.triangle() != null) {
                    String triangleString = ctxFormatItem.triangle().triangle_option().getText();
                    this.distanceMatrixBuilder.setTriangle(DistanceMatrixBuilder.Triangle.valueOf(triangleString.toUpperCase()));
                }
                else if (ctxFormatItem.diagonal() != null) {
                    String diagonal = ctxFormatItem.diagonal().getText();
                    this.distanceMatrixBuilder.setDiagonal(diagonal.equals("diagonal"));
                }
                else if (ctxFormatItem.labels() != null) {

                    if (ctxFormatItem.labels().labels_option() != null) {
                        String labelStr = ctxFormatItem.labels().labels_option().getText();

                        if (labelStr.equalsIgnoreCase("yes") || labelStr.equalsIgnoreCase("true")) {
                            this.distanceMatrixBuilder.setLabels(DistanceMatrixBuilder.Labels.LEFT);
                        }

                        this.distanceMatrixBuilder.setLabels(DistanceMatrixBuilder.Labels.valueOf(labelStr.toUpperCase()));
                    }
                    else if (ctxFormatItem.labels().labels_header().getText().equalsIgnoreCase("nolabels")) {
                        this.distanceMatrixBuilder.setLabels(DistanceMatrixBuilder.Labels.NONE);
                    }
                    else {
                        this.distanceMatrixBuilder.setLabels(DistanceMatrixBuilder.Labels.LEFT);
                    }
                }
                else if (ctxFormatItem.missing() != null) {
                    // Not sure what to do with this.. leave it for now.
                }
                else if (ctxFormatItem.getText().equals("interleave")) {
                    this.distanceMatrixBuilder.setInterleave(true);
                }
            }

            ctxFormatList = ctxFormatList.format_distances_list();
        }
    }

    @Override
    public void enterTaxlabels_optional(@NotNull NexusFileParser.Taxlabels_optionalContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitTaxlabels_optional(@NotNull NexusFileParser.Taxlabels_optionalContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterBlock_declaration(@NotNull NexusFileParser.Block_declarationContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitBlock_declaration(@NotNull NexusFileParser.Block_declarationContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterFormat_splits_list(@NotNull NexusFileParser.Format_splits_listContext ctx) {

    }

    @Override
    public void exitFormat_splits_list(@NotNull NexusFileParser.Format_splits_listContext ctx) {

    }

    @Override
    public void enterY_quartet(@NotNull NexusFileParser.Y_quartetContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitY_quartet(@NotNull NexusFileParser.Y_quartetContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterConfidences_header(@NotNull NexusFileParser.Confidences_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitConfidences_header(@NotNull NexusFileParser.Confidences_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterProperties_splits(@NotNull NexusFileParser.Properties_splitsContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitProperties_splits(@NotNull NexusFileParser.Properties_splitsContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterX_quartet(@NotNull NexusFileParser.X_quartetContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitX_quartet(@NotNull NexusFileParser.X_quartetContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterNewick_tree(@NotNull NexusFileParser.Newick_treeContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitNewick_tree(@NotNull NexusFileParser.Newick_treeContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterTree_rest(@NotNull NexusFileParser.Tree_restContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitTree_rest(@NotNull NexusFileParser.Tree_restContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterNewtaxa(@NotNull NexusFileParser.NewtaxaContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitNewtaxa(@NotNull NexusFileParser.NewtaxaContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterProperties_splits_name(@NotNull NexusFileParser.Properties_splits_nameContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitProperties_splits_name(@NotNull NexusFileParser.Properties_splits_nameContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterBlock_splits(@NotNull NexusFileParser.Block_splitsContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitBlock_splits(@NotNull NexusFileParser.Block_splitsContext ctx) {

        this.nexus.setSplitSystem(this.splitSystemBuilder.createSplitSystem());
    }

    @Override
    public void enterMatrix_entry_list(@NotNull NexusFileParser.Matrix_entry_listContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitMatrix_entry_list(@NotNull NexusFileParser.Matrix_entry_listContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterWeight_quartet(@NotNull NexusFileParser.Weight_quartetContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitWeight_quartet(@NotNull NexusFileParser.Weight_quartetContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterFormat_splits_item(@NotNull NexusFileParser.Format_splits_itemContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitFormat_splits_item(@NotNull NexusFileParser.Format_splits_itemContext ctx) {

    }

    @Override
    public void enterIntervals_splits(@NotNull NexusFileParser.Intervals_splitsContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitIntervals_splits(@NotNull NexusFileParser.Intervals_splitsContext ctx) {
        this.splitSystemBuilder.setHasIntervals(
                ctx.boolean_option() == null ?
                    true :
                    ctx.boolean_option().getText().equalsIgnoreCase("true") ||
                            ctx.boolean_option().getText().equalsIgnoreCase("yes")
        );
    }

    @Override
    public void enterBlock_quartets(@NotNull NexusFileParser.Block_quartetsContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitBlock_quartets(@NotNull NexusFileParser.Block_quartetsContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterNexus_header(@NotNull NexusFileParser.Nexus_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitNexus_header(@NotNull NexusFileParser.Nexus_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterTaxlabels(@NotNull NexusFileParser.TaxlabelsContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitTaxlabels(@NotNull NexusFileParser.TaxlabelsContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterBlock(@NotNull NexusFileParser.BlockContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitBlock(@NotNull NexusFileParser.BlockContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterMatrix_splits_list(@NotNull NexusFileParser.Matrix_splits_listContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitMatrix_splits_list(@NotNull NexusFileParser.Matrix_splits_listContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterTree_label(@NotNull NexusFileParser.Tree_labelContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitTree_label(@NotNull NexusFileParser.Tree_labelContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterV_quartet(@NotNull NexusFileParser.V_quartetContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitV_quartet(@NotNull NexusFileParser.V_quartetContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterFormat_distances_list(@NotNull NexusFileParser.Format_distances_listContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitFormat_distances_list(@NotNull NexusFileParser.Format_distances_listContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterFormat_splits(@NotNull NexusFileParser.Format_splitsContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitFormat_splits(@NotNull NexusFileParser.Format_splitsContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterCs_quartet(@NotNull NexusFileParser.Cs_quartetContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitCs_quartet(@NotNull NexusFileParser.Cs_quartetContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterSplits_block_header(@NotNull NexusFileParser.Splits_block_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitSplits_block_header(@NotNull NexusFileParser.Splits_block_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterLength(@NotNull NexusFileParser.LengthContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitLength(@NotNull NexusFileParser.LengthContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterTree_block_header(@NotNull NexusFileParser.Tree_block_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitTree_block_header(@NotNull NexusFileParser.Tree_block_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterDimensions(@NotNull NexusFileParser.DimensionsContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitDimensions(@NotNull NexusFileParser.DimensionsContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterBoolean_option(@NotNull NexusFileParser.Boolean_optionContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitBoolean_option(@NotNull NexusFileParser.Boolean_optionContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterTree_header(@NotNull NexusFileParser.Tree_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitTree_header(@NotNull NexusFileParser.Tree_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterParse(@NotNull NexusFileParser.ParseContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitParse(@NotNull NexusFileParser.ParseContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterMatrix_data(@NotNull NexusFileParser.Matrix_dataContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitMatrix_data(@NotNull NexusFileParser.Matrix_dataContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterEnd(@NotNull NexusFileParser.EndContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitEnd(@NotNull NexusFileParser.EndContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterTranslate_header(@NotNull NexusFileParser.Translate_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitTranslate_header(@NotNull NexusFileParser.Translate_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterDimensions_taxa(@NotNull NexusFileParser.Dimensions_taxaContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitDimensions_taxa(@NotNull NexusFileParser.Dimensions_taxaContext ctx) {

    }

    @Override
    public void enterTaxa_header(@NotNull NexusFileParser.Taxa_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitTaxa_header(@NotNull NexusFileParser.Taxa_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterLabels_option(@NotNull NexusFileParser.Labels_optionContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitLabels_option(@NotNull NexusFileParser.Labels_optionContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterStar(@NotNull NexusFileParser.StarContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitStar(@NotNull NexusFileParser.StarContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterIntervals_header(@NotNull NexusFileParser.Intervals_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitIntervals_header(@NotNull NexusFileParser.Intervals_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterBlocks(@NotNull NexusFileParser.BlocksContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitBlocks(@NotNull NexusFileParser.BlocksContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterCycle_item(@NotNull NexusFileParser.Cycle_itemContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitCycle_item(@NotNull NexusFileParser.Cycle_itemContext ctx) {

        if (ctx.NUMERIC() != null) {
            this.splitSystemBuilder.addCycleItem(Integer.parseInt(ctx.NUMERIC().getText()));
        }
    }

    @Override
    public void enterReference(@NotNull NexusFileParser.ReferenceContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitReference(@NotNull NexusFileParser.ReferenceContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterU_quartet(@NotNull NexusFileParser.U_quartetContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitU_quartet(@NotNull NexusFileParser.U_quartetContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterBlock_trees(@NotNull NexusFileParser.Block_treesContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitBlock_trees(@NotNull NexusFileParser.Block_treesContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterBegin(@NotNull NexusFileParser.BeginContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitBegin(@NotNull NexusFileParser.BeginContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterNtax(@NotNull NexusFileParser.NtaxContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitNtax(@NotNull NexusFileParser.NtaxContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterWeights_header(@NotNull NexusFileParser.Weights_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitWeights_header(@NotNull NexusFileParser.Weights_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterTriangle(@NotNull NexusFileParser.TriangleContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitTriangle(@NotNull NexusFileParser.TriangleContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterMatrix_quartet(@NotNull NexusFileParser.Matrix_quartetContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitMatrix_quartet(@NotNull NexusFileParser.Matrix_quartetContext ctx) {

        int x = Integer.parseInt(ctx.x_quartet().NUMERIC().getText());
        int y = Integer.parseInt(ctx.y_quartet().NUMERIC().getText());
        int u = Integer.parseInt(ctx.u_quartet().NUMERIC().getText());
        int v = Integer.parseInt(ctx.v_quartet().NUMERIC().getText());

        //ctx.
        //Quartet quartet = new Quartet();

    }

    @Override
    public void enterBlock_distances(@NotNull NexusFileParser.Block_distancesContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitBlock_distances(@NotNull NexusFileParser.Block_distancesContext ctx) {

        NexusFileParser.Matrix_dataContext mtxData = ctx.matrix_data();

        while(mtxData != null) {

            if (ctx.matrix_data().IDENTIFIER() != null) {
                String taxon = ctx.matrix_data().IDENTIFIER().getText();

                // don't do anything with this for now (presumably there is taxa info in the taxa block any how.
            }

            NexusFileParser.Matrix_entry_listContext mtxCtx = mtxData.matrix_entry_list();

            List<Double> row = new ArrayList<>();

            while (mtxCtx != null && mtxCtx.NUMERIC() != null) {

                String number = mtxCtx.NUMERIC().getText();

                row.add(Double.parseDouble(number));

                mtxCtx = mtxCtx.matrix_entry_list();
            }

            this.distanceMatrixBuilder.getRows().add(row);

            mtxData = mtxData.matrix_data();
        }


        // We should have all the information to build a distance matrix at this point... so do it.
        this.nexus.setDistanceMatrix(this.distanceMatrixBuilder.createDistanceMatrix());
    }

    @Override
    public void enterTranslate(@NotNull NexusFileParser.TranslateContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitTranslate(@NotNull NexusFileParser.TranslateContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterTaxlabels_header(@NotNull NexusFileParser.Taxlabels_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitTaxlabels_header(@NotNull NexusFileParser.Taxlabels_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterState_composed_list(@NotNull NexusFileParser.State_composed_listContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitState_composed_list(@NotNull NexusFileParser.State_composed_listContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterSc_quartet(@NotNull NexusFileParser.Sc_quartetContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitSc_quartet(@NotNull NexusFileParser.Sc_quartetContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterLabel_quartet(@NotNull NexusFileParser.Label_quartetContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitLabel_quartet(@NotNull NexusFileParser.Label_quartetContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterNewtaxa_optional(@NotNull NexusFileParser.Newtaxa_optionalContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitNewtaxa_optional(@NotNull NexusFileParser.Newtaxa_optionalContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterProperties_splits_list(@NotNull NexusFileParser.Properties_splits_listContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitProperties_splits_list(@NotNull NexusFileParser.Properties_splits_listContext ctx) {
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

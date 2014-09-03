/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2014  UEA School of Computing Sciences
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

package uk.ac.uea.cmp.spectre.core.io.nexus.parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.spectre.core.ds.Identifier;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrixBuilder;
import uk.ac.uea.cmp.spectre.core.ds.network.Edge;
import uk.ac.uea.cmp.spectre.core.ds.network.NetworkLabel;
import uk.ac.uea.cmp.spectre.core.ds.network.Vertex;
import uk.ac.uea.cmp.spectre.core.ds.split.SpectreSplitBlock;
import uk.ac.uea.cmp.spectre.core.io.nexus.Nexus;

import java.awt.*;
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
    private NexusQuartetSystemBuilder quartetSystemBuilder;
    private NexusNetworkBuilder networkBuilder;


    public NexusFilePopulator(Nexus nexus, boolean verbose) {
        this.nexus = nexus;
        this.verbose = verbose;
        this.distanceMatrixBuilder = new DistanceMatrixBuilder();
        this.splitSystemBuilder = new NexusSplitSystemBuilder();
        this.quartetSystemBuilder = new NexusQuartetSystemBuilder();
        this.networkBuilder = new NexusNetworkBuilder();
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
    public void enterDimensions_network(@NotNull NexusFileParser.Dimensions_networkContext ctx) {

    }

    @Override
    public void exitDimensions_network(@NotNull NexusFileParser.Dimensions_networkContext ctx) {

        int nbExpectedTaxa = Integer.parseInt(ctx.ntax().INT().getText());
        int nbExpectedEdges = Integer.parseInt(ctx.nedges().INT().getText());
        int nbExpectedVertices = Integer.parseInt(ctx.nvertices().INT().getText());

        this.networkBuilder.setExpectedDimensions(nbExpectedTaxa, nbExpectedVertices, nbExpectedEdges);
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
    public void enterEdges_network(@NotNull NexusFileParser.Edges_networkContext ctx) {

    }

    @Override
    public void exitEdges_network(@NotNull NexusFileParser.Edges_networkContext ctx) {

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
    public void enterNv_width(@NotNull NexusFileParser.Nv_widthContext ctx) {

    }

    @Override
    public void exitNv_width(@NotNull NexusFileParser.Nv_widthContext ctx) {

        this.networkBuilder.getCurrentVertex().setWidth(Integer.parseInt(ctx.INT().getText()));
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

        if (ctx.dimensions_taxa() != null && ctx.dimensions_taxa().ntax() != null) {

            NexusFileParser.NtaxContext ctxNtax = ctx.dimensions_taxa().ntax();

            if (ctxNtax.INT() != null && !ctxNtax.INT().getText().isEmpty()) {

                nTax = Integer.parseInt(ctxNtax.INT().getText());

                if (verbose && log.isDebugEnabled())
                    log.debug("Taxa Block: ntax:" + nTax);
            }
        }

        IdentifierList taxa = this.nexus.getTaxa();

        if (ctx.tax_labels() != null) {

            taxa.add(new Identifier(ctx.tax_labels().IDENTIFIER().getText()));

            NexusFileParser.Identifier_listContext idListCtx = ctx.tax_labels().identifier_list();

            while (idListCtx != null && idListCtx.IDENTIFIER() != null) {
                taxa.add(new Identifier(idListCtx.IDENTIFIER().getText()));
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
    public void enterTranslate_network_data(@NotNull NexusFileParser.Translate_network_dataContext ctx) {

    }

    @Override
    public void exitTranslate_network_data(@NotNull NexusFileParser.Translate_network_dataContext ctx) {

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
    public void enterTax_info_header(@NotNull NexusFileParser.Tax_info_headerContext ctx) {

    }

    @Override
    public void exitTax_info_header(@NotNull NexusFileParser.Tax_info_headerContext ctx) {

    }

    @Override
    public void enterVertex_options(@NotNull NexusFileParser.Vertex_optionsContext ctx) {

    }

    @Override
    public void exitVertex_options(@NotNull NexusFileParser.Vertex_optionsContext ctx) {

    }


    @Override
    public void enterMatrix_splits_data(@NotNull NexusFileParser.Matrix_splits_dataContext ctx) {


    }

    @Override
    public void exitMatrix_splits_data(@NotNull NexusFileParser.Matrix_splits_dataContext ctx) {

        if (ctx.FLOAT() != null) {

            double weight = Double.parseDouble(ctx.FLOAT().getText());

            NexusFileParser.Matrix_splits_listContext ctxList = ctx.matrix_splits_list();

            List<Integer> setA = new LinkedList<>();

            while (ctxList != null) {

                if (ctxList.INT() != null) {

                    int val = Integer.parseInt(ctxList.INT().getText());

                    setA.add(val);
                }

                ctxList = ctxList.matrix_splits_list();
            }

            this.splitSystemBuilder.addSplit(new SpectreSplitBlock(setA), weight);
        }
    }

    @Override
    public void enterProperties_splits_item(@NotNull NexusFileParser.Properties_splits_itemContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitProperties_splits_item(@NotNull NexusFileParser.Properties_splits_itemContext ctx) {

        if (ctx.properties_splits_name() != null) {

            String valStr = ctx.number() != null ? ctx.number().getText() : null;

            if (ctx.properties_splits_name().getText().equalsIgnoreCase("cyclic")) {
                this.splitSystemBuilder.setCyclic(true);
            } else if (ctx.properties_splits_name().getText().equalsIgnoreCase("fit")) {
                this.splitSystemBuilder.setFit(Double.parseDouble(valStr));
            }
        }
    }

    @Override
    public void enterNl_color_lc(@NotNull NexusFileParser.Nl_color_lcContext ctx) {

    }

    @Override
    public void exitNl_color_lc(@NotNull NexusFileParser.Nl_color_lcContext ctx) {

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
    public void enterNl_font(@NotNull NexusFileParser.Nl_fontContext ctx) {

    }

    @Override
    public void exitNl_font(@NotNull NexusFileParser.Nl_fontContext ctx) {

        String fontString = ctx.IDENTIFIER().getText();

        String[] parts = fontString.split("-");

        String family = parts[0];
        String type = parts[1];
        int size = Integer.parseInt(parts[2]);

        NetworkLabel label = this.networkBuilder.getCurrentLabel();

        label.setFontFamily(family);
        label.setFontStyle(type);
        label.setFontSize(size);
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
    public void enterTranslate_network_header(@NotNull NexusFileParser.Translate_network_headerContext ctx) {

    }

    @Override
    public void exitTranslate_network_header(@NotNull NexusFileParser.Translate_network_headerContext ctx) {

    }

    @Override
    public void enterRotate_network(@NotNull NexusFileParser.Rotate_networkContext ctx) {

    }

    @Override
    public void exitRotate_network(@NotNull NexusFileParser.Rotate_networkContext ctx) {

        this.networkBuilder.setRotateAbout(Float.parseFloat(ctx.FLOAT().getText()));
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
    public void enterKey_value_pairs(@NotNull NexusFileParser.Key_value_pairsContext ctx) {

    }

    @Override
    public void exitKey_value_pairs(@NotNull NexusFileParser.Key_value_pairsContext ctx) {

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

            if (ctxNtax.INT() != null && !ctxNtax.INT().getText().isEmpty()) {
                this.distanceMatrixBuilder.setNbTaxa(Integer.parseInt(ctxNtax.INT().getText()));
            }

            NexusFileParser.NcharContext ctxNchar = ctx.nchar();

            if (ctxNchar.INT() != null && !ctxNchar.INT().getText().isEmpty()) {
                this.distanceMatrixBuilder.setNbChars(Integer.parseInt(ctxNchar.INT().getText()));
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
    public void enterNumber(@NotNull NexusFileParser.NumberContext ctx) {

    }

    @Override
    public void exitNumber(@NotNull NexusFileParser.NumberContext ctx) {

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
    public void enterInterleave(@NotNull NexusFileParser.InterleaveContext ctx) {

    }

    @Override
    public void exitInterleave(@NotNull NexusFileParser.InterleaveContext ctx) {

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
    public void enterEdges_network_option(@NotNull NexusFileParser.Edges_network_optionContext ctx) {

    }

    @Override
    public void exitEdges_network_option(@NotNull NexusFileParser.Edges_network_optionContext ctx) {

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
            if (ctx.ntax().INT() != null) {
                this.splitSystemBuilder.setExpectedNbTaxa(Integer.parseInt(ctx.ntax().INT().getText()));
            }
        }

        if (ctx.nsplits() != null) {
            if (ctx.nsplits().INT() != null) {
                this.splitSystemBuilder.setExpectedNbSplits(Integer.parseInt(ctx.nsplits().INT().getText()));
            }
        }
    }

    @Override
    public void enterNv_shape(@NotNull NexusFileParser.Nv_shapeContext ctx) {

    }

    @Override
    public void exitNv_shape(@NotNull NexusFileParser.Nv_shapeContext ctx) {

        this.networkBuilder.getCurrentVertex().setShape(ctx.getText());
    }

    @Override
    public void enterAssumptions_data(@NotNull NexusFileParser.Assumptions_dataContext ctx) {

    }

    @Override
    public void exitAssumptions_data(@NotNull NexusFileParser.Assumptions_dataContext ctx) {

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
                } else if (ctxFormatItem.diagonal() != null) {
                    String diagonal = ctxFormatItem.diagonal().getText();
                    this.distanceMatrixBuilder.setDiagonal(diagonal.equals("diagonal"));
                } else if (ctxFormatItem.labels() != null) {

                    if (ctxFormatItem.labels().labels_option() != null) {
                        String labelStr = ctxFormatItem.labels().labels_option().getText();

                        if (labelStr.equalsIgnoreCase("yes") || labelStr.equalsIgnoreCase("true")) {
                            this.distanceMatrixBuilder.setLabels(DistanceMatrixBuilder.Labels.LEFT);
                        }

                        this.distanceMatrixBuilder.setLabels(DistanceMatrixBuilder.Labels.valueOf(labelStr.toUpperCase()));
                    } else if (ctxFormatItem.labels().labels_header().getText().equalsIgnoreCase("nolabels")) {
                        this.distanceMatrixBuilder.setLabels(DistanceMatrixBuilder.Labels.NONE);
                    } else {
                        this.distanceMatrixBuilder.setLabels(DistanceMatrixBuilder.Labels.LEFT);
                    }
                } else if (ctxFormatItem.missing() != null) {
                    // Not sure what to do with this.. leave it for now.
                } else if (ctxFormatItem.getText().equals("interleave")) {
                    String interleaveString = ctxFormatItem.interleave().labels_option().getText();
                    this.distanceMatrixBuilder.setInterleave(interleaveString.equalsIgnoreCase("yes") || interleaveString.equalsIgnoreCase("true"));
                }
            }

            ctxFormatList = ctxFormatList.format_distances_list();
        }
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
    public void enterVlabels_network_data(@NotNull NexusFileParser.Vlabels_network_dataContext ctx) {

    }

    @Override
    public void exitVlabels_network_data(@NotNull NexusFileParser.Vlabels_network_dataContext ctx) {

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
    public void enterVlabels_network(@NotNull NexusFileParser.Vlabels_networkContext ctx) {

    }

    @Override
    public void exitVlabels_network(@NotNull NexusFileParser.Vlabels_networkContext ctx) {

    }

    @Override
    public void enterTax_labels_optional(@NotNull NexusFileParser.Tax_labels_optionalContext ctx) {

    }

    @Override
    public void exitTax_labels_optional(@NotNull NexusFileParser.Tax_labels_optionalContext ctx) {

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
    public void enterTranslate_network(@NotNull NexusFileParser.Translate_networkContext ctx) {

    }

    @Override
    public void exitTranslate_network(@NotNull NexusFileParser.Translate_networkContext ctx) {

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
    public void enterNl_color_bg(@NotNull NexusFileParser.Nl_color_bgContext ctx) {

    }

    @Override
    public void exitNl_color_bg(@NotNull NexusFileParser.Nl_color_bgContext ctx) {

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
    public void enterDraw_network_options(@NotNull NexusFileParser.Draw_network_optionsContext ctx) {

    }

    @Override
    public void exitDraw_network_options(@NotNull NexusFileParser.Draw_network_optionsContext ctx) {

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
    public void enterDraw_network(@NotNull NexusFileParser.Draw_networkContext ctx) {

    }

    @Override
    public void exitDraw_network(@NotNull NexusFileParser.Draw_networkContext ctx) {

        this.networkBuilder.setDrawToScale(true);
    }

    @Override
    public void enterNvertices(@NotNull NexusFileParser.NverticesContext ctx) {

    }

    @Override
    public void exitNvertices(@NotNull NexusFileParser.NverticesContext ctx) {

    }

    @Override
    public void enterNe_unknown(@NotNull NexusFileParser.Ne_unknownContext ctx) {

    }

    @Override
    public void exitNe_unknown(@NotNull NexusFileParser.Ne_unknownContext ctx) {
        // Not sure what to do with this one
    }

    @Override
    public void enterDraw_network_header(@NotNull NexusFileParser.Draw_network_headerContext ctx) {

    }

    @Override
    public void exitDraw_network_header(@NotNull NexusFileParser.Draw_network_headerContext ctx) {

    }

    @Override
    public void enterVlabels_network_label(@NotNull NexusFileParser.Vlabels_network_labelContext ctx) {

    }

    @Override
    public void exitVlabels_network_label(@NotNull NexusFileParser.Vlabels_network_labelContext ctx) {

        NetworkLabel label = this.networkBuilder.getCurrentLabel();

        int id = Integer.parseInt(ctx.INT().getText());
        String name = ctx.IDENTIFIER().getText();

        label.setName(name);
        label.setVertexId(id);
    }

    @Override
    public void enterFormat_splits_item(@NotNull NexusFileParser.Format_splits_itemContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitFormat_splits_item(@NotNull NexusFileParser.Format_splits_itemContext ctx) {

    }

    @Override
    public void enterTax_labels_header(@NotNull NexusFileParser.Tax_labels_headerContext ctx) {

    }

    @Override
    public void exitTax_labels_header(@NotNull NexusFileParser.Tax_labels_headerContext ctx) {

    }

    @Override
    public void enterVlabels_network_entry(@NotNull NexusFileParser.Vlabels_network_entryContext ctx) {

        NetworkLabel label = new NetworkLabel();

        this.networkBuilder.setCurrentLabel(label);
    }

    @Override
    public void exitVlabels_network_entry(@NotNull NexusFileParser.Vlabels_network_entryContext ctx) {

        // Create a link between the vertex and the label
        this.networkBuilder.getVertices().get(this.networkBuilder.getCurrentLabel().getVertexId()).setLabel(this.networkBuilder.getCurrentLabel());

        // Add the label to the map
        this.networkBuilder.getLabels().put(this.networkBuilder.getCurrentLabel().getVertexId(), this.networkBuilder.getCurrentLabel());
    }

    @Override
    public void enterNl_l(@NotNull NexusFileParser.Nl_lContext ctx) {

    }

    @Override
    public void exitNl_l(@NotNull NexusFileParser.Nl_lContext ctx) {

        // Not sure what to do with this for the moment

        // this.networkBuilder.getCurrentLabel()
    }

    @Override
    public void enterVertices_network_header(@NotNull NexusFileParser.Vertices_network_headerContext ctx) {

    }

    @Override
    public void exitVertices_network_header(@NotNull NexusFileParser.Vertices_network_headerContext ctx) {

    }

    @Override
    public void enterEdges_network_header(@NotNull NexusFileParser.Edges_network_headerContext ctx) {

    }

    @Override
    public void exitEdges_network_header(@NotNull NexusFileParser.Edges_network_headerContext ctx) {

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
    public void enterVertices_network_data(@NotNull NexusFileParser.Vertices_network_dataContext ctx) {

    }

    @Override
    public void exitVertices_network_data(@NotNull NexusFileParser.Vertices_network_dataContext ctx) {

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
    public void enterNtax_header(@NotNull NexusFileParser.Ntax_headerContext ctx) {

    }

    @Override
    public void exitNtax_header(@NotNull NexusFileParser.Ntax_headerContext ctx) {

    }

    @Override
    public void enterTax_labels(@NotNull NexusFileParser.Tax_labelsContext ctx) {

    }

    @Override
    public void exitTax_labels(@NotNull NexusFileParser.Tax_labelsContext ctx) {

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
    public void enterTax_info_entries(@NotNull NexusFileParser.Tax_info_entriesContext ctx) {

    }

    @Override
    public void exitTax_info_entries(@NotNull NexusFileParser.Tax_info_entriesContext ctx) {

    }

    @Override
    public void enterTax_info(@NotNull NexusFileParser.Tax_infoContext ctx) {

    }

    @Override
    public void exitTax_info(@NotNull NexusFileParser.Tax_infoContext ctx) {

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
    public void enterDraw_network_option(@NotNull NexusFileParser.Draw_network_optionContext ctx) {

    }

    @Override
    public void exitDraw_network_option(@NotNull NexusFileParser.Draw_network_optionContext ctx) {

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
    public void enterNl_y(@NotNull NexusFileParser.Nl_yContext ctx) {

    }

    @Override
    public void exitNl_y(@NotNull NexusFileParser.Nl_yContext ctx) {
        this.networkBuilder.getCurrentLabel().setOffsetY(Integer.parseInt(ctx.INT().getText()));
    }

    @Override
    public void enterNv_b(@NotNull NexusFileParser.Nv_bContext ctx) {

    }

    @Override
    public void exitNv_b(@NotNull NexusFileParser.Nv_bContext ctx) {
        // Not sure what this is so we'll ignore it for now...
    }

    @Override
    public void enterNetwork_block_header(@NotNull NexusFileParser.Network_block_headerContext ctx) {

    }

    @Override
    public void exitNetwork_block_header(@NotNull NexusFileParser.Network_block_headerContext ctx) {

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
    public void enterScale_network(@NotNull NexusFileParser.Scale_networkContext ctx) {

    }

    @Override
    public void exitScale_network(@NotNull NexusFileParser.Scale_networkContext ctx) {

    }

    @Override
    public void enterNv_height(@NotNull NexusFileParser.Nv_heightContext ctx) {

    }

    @Override
    public void exitNv_height(@NotNull NexusFileParser.Nv_heightContext ctx) {
        this.networkBuilder.getCurrentVertex().setHeight(Integer.parseInt(ctx.INT().getText()));
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
    public void enterNedges(@NotNull NexusFileParser.NedgesContext ctx) {

    }

    @Override
    public void exitNedges(@NotNull NexusFileParser.NedgesContext ctx) {

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
    public void enterBlock_network(@NotNull NexusFileParser.Block_networkContext ctx) {

    }

    @Override
    public void exitBlock_network(@NotNull NexusFileParser.Block_networkContext ctx) {

        this.nexus.setNetwork(this.networkBuilder.createNetwork());
    }

    @Override
    public void enterNv_color_fg(@NotNull NexusFileParser.Nv_color_fgContext ctx) {

    }

    @Override
    public void exitNv_color_fg(@NotNull NexusFileParser.Nv_color_fgContext ctx) {
        this.networkBuilder.getCurrentVertex().setLineColor(
                new Color(
                        Integer.parseInt(ctx.INT(0).getText()),
                        Integer.parseInt(ctx.INT(1).getText()),
                        Integer.parseInt(ctx.INT(2).getText())));
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
    public void enterNl_x(@NotNull NexusFileParser.Nl_xContext ctx) {

    }

    @Override
    public void exitNl_x(@NotNull NexusFileParser.Nl_xContext ctx) {
        this.networkBuilder.getCurrentLabel().setOffsetX(Integer.parseInt(ctx.INT().getText()));
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
    public void enterBlock_assumptions(@NotNull NexusFileParser.Block_assumptionsContext ctx) {

    }

    @Override
    public void exitBlock_assumptions(@NotNull NexusFileParser.Block_assumptionsContext ctx) {

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
    public void enterEdges_network_options(@NotNull NexusFileParser.Edges_network_optionsContext ctx) {

    }

    @Override
    public void exitEdges_network_options(@NotNull NexusFileParser.Edges_network_optionsContext ctx) {

    }

    @Override
    public void enterTax_info_entry(@NotNull NexusFileParser.Tax_info_entryContext ctx) {

    }

    @Override
    public void exitTax_info_entry(@NotNull NexusFileParser.Tax_info_entryContext ctx) {

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
    public void enterVertex_option(@NotNull NexusFileParser.Vertex_optionContext ctx) {

    }

    @Override
    public void exitVertex_option(@NotNull NexusFileParser.Vertex_optionContext ctx) {

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
    public void enterNv_color_bg(@NotNull NexusFileParser.Nv_color_bgContext ctx) {

    }

    @Override
    public void exitNv_color_bg(@NotNull NexusFileParser.Nv_color_bgContext ctx) {
        this.networkBuilder.getCurrentVertex().setBackgroundColor(
                new Color(
                        Integer.parseInt(ctx.INT(0).getText()),
                        Integer.parseInt(ctx.INT(1).getText()),
                        Integer.parseInt(ctx.INT(2).getText()))
        );
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
    public void enterNe_color(@NotNull NexusFileParser.Ne_colorContext ctx) {

    }

    @Override
    public void exitNe_color(@NotNull NexusFileParser.Ne_colorContext ctx) {
        this.networkBuilder.getCurrentEdge().setColor(
                new Color(
                        Integer.parseInt(ctx.INT(0).getText()),
                        Integer.parseInt(ctx.INT(1).getText()),
                        Integer.parseInt(ctx.INT(2).getText()))
        );
    }

    @Override
    public void enterEdges_network_entry(@NotNull NexusFileParser.Edges_network_entryContext ctx) {
        Edge edge = new Edge();

        this.networkBuilder.setCurrentEdge(edge);
    }

    @Override
    public void exitEdges_network_entry(@NotNull NexusFileParser.Edges_network_entryContext ctx) {

        int id = Integer.parseInt(ctx.INT().get(0).getText());
        int top = Integer.parseInt(ctx.INT().get(1).getText());
        int bot = Integer.parseInt(ctx.INT().get(2).getText());

        Edge edge = this.networkBuilder.getCurrentEdge();

        Vertex vTop = this.networkBuilder.getVertices().get(top);
        Vertex vBot = this.networkBuilder.getVertices().get(bot);

        vBot.getElist().add(edge);
        vTop.getElist().add(edge);

        edge.setNxnum(id);
        edge.setTop(vTop);
        edge.setBot(vBot);

        this.networkBuilder.getEdges().put(this.networkBuilder.getCurrentEdge().getNxnum(), edge);
    }

    @Override
    public void enterAssumptions_data_entry(@NotNull NexusFileParser.Assumptions_data_entryContext ctx) {

    }

    @Override
    public void exitAssumptions_data_entry(@NotNull NexusFileParser.Assumptions_data_entryContext ctx) {

    }

    @Override
    public void enterVlabels_option(@NotNull NexusFileParser.Vlabels_optionContext ctx) {

    }

    @Override
    public void exitVlabels_option(@NotNull NexusFileParser.Vlabels_optionContext ctx) {

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
    public void enterVertices_network_entry(@NotNull NexusFileParser.Vertices_network_entryContext ctx) {

        Vertex v = new Vertex();

        this.networkBuilder.setCurrentVertex(v);
    }

    @Override
    public void exitVertices_network_entry(@NotNull NexusFileParser.Vertices_network_entryContext ctx) {

        int id = Integer.parseInt(ctx.INT().getText());
        double x = Double.parseDouble(ctx.FLOAT(0).getText());
        double y = Double.parseDouble(ctx.FLOAT(1).getText());

        Vertex v = this.networkBuilder.getCurrentVertex();

        v.setX(x);
        v.setY(y);
        v.setNxnum(id);

        this.networkBuilder.getVertices().put(id, v);
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
    public void enterAssumptions_block_header(@NotNull NexusFileParser.Assumptions_block_headerContext ctx) {

    }

    @Override
    public void exitAssumptions_block_header(@NotNull NexusFileParser.Assumptions_block_headerContext ctx) {

    }

    @Override
    public void enterCycle_item(@NotNull NexusFileParser.Cycle_itemContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitCycle_item(@NotNull NexusFileParser.Cycle_itemContext ctx) {

        if (ctx.INT() != null) {
            this.splitSystemBuilder.addCycleItem(Integer.parseInt(ctx.INT().getText()));
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
    public void enterVertices_network(@NotNull NexusFileParser.Vertices_networkContext ctx) {

    }

    @Override
    public void exitVertices_network(@NotNull NexusFileParser.Vertices_networkContext ctx) {

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
    public void enterEdges_network_data(@NotNull NexusFileParser.Edges_network_dataContext ctx) {

    }

    @Override
    public void exitEdges_network_data(@NotNull NexusFileParser.Edges_network_dataContext ctx) {

    }

    @Override
    public void enterVlabels_network_header(@NotNull NexusFileParser.Vlabels_network_headerContext ctx) {

    }

    @Override
    public void exitVlabels_network_header(@NotNull NexusFileParser.Vlabels_network_headerContext ctx) {

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
    public void enterMatrix_split_identifier(@NotNull NexusFileParser.Matrix_split_identifierContext ctx) {

    }

    @Override
    public void exitMatrix_split_identifier(@NotNull NexusFileParser.Matrix_split_identifierContext ctx) {

    }

    @Override
    public void enterMatrix_quartet(@NotNull NexusFileParser.Matrix_quartetContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitMatrix_quartet(@NotNull NexusFileParser.Matrix_quartetContext ctx) {

        int x = Integer.parseInt(ctx.x_quartet().INT().getText());
        int y = Integer.parseInt(ctx.y_quartet().INT().getText());
        int u = Integer.parseInt(ctx.u_quartet().INT().getText());
        int v = Integer.parseInt(ctx.v_quartet().INT().getText());

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

        while (mtxData != null) {

            if (ctx.matrix_data().IDENTIFIER() != null) {
                String taxon = ctx.matrix_data().IDENTIFIER().getText();

                // don't do anything with this for now (presumably there is taxa info in the taxa block any how.
            }

            NexusFileParser.Matrix_entry_listContext mtxCtx = mtxData.matrix_entry_list();

            List<Double> row = new ArrayList<>();

            while (mtxCtx != null && mtxCtx.number() != null) {

                String number = mtxCtx.number().getText();

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
    public void enterVlabels_options(@NotNull NexusFileParser.Vlabels_optionsContext ctx) {

    }

    @Override
    public void exitVlabels_options(@NotNull NexusFileParser.Vlabels_optionsContext ctx) {

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
    public void enterNe_width(@NotNull NexusFileParser.Ne_widthContext ctx) {

    }

    @Override
    public void exitNe_width(@NotNull NexusFileParser.Ne_widthContext ctx) {
        this.networkBuilder.getCurrentEdge().setWidth(Integer.parseInt(ctx.INT().getText()));
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
    public void enterKey_value_pair(@NotNull NexusFileParser.Key_value_pairContext ctx) {

    }

    @Override
    public void exitKey_value_pair(@NotNull NexusFileParser.Key_value_pairContext ctx) {

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
    public void enterTranslate_network_entry(@NotNull NexusFileParser.Translate_network_entryContext ctx) {

    }

    @Override
    public void exitTranslate_network_entry(@NotNull NexusFileParser.Translate_network_entryContext ctx) {

    }

    @Override
    public void enterNe_split(@NotNull NexusFileParser.Ne_splitContext ctx) {

    }

    @Override
    public void exitNe_split(@NotNull NexusFileParser.Ne_splitContext ctx) {

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

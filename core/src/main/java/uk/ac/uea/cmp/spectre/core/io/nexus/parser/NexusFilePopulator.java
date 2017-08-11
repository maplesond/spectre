/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2017  UEA School of Computing Sciences
 *
 * This program is free software: you can redistribute it and/or modify it under the term of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package uk.ac.uea.cmp.spectre.core.io.nexus.parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.spectre.core.ds.Identifier;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrixBuilder;
import uk.ac.uea.cmp.spectre.core.ds.network.Edge;
import uk.ac.uea.cmp.spectre.core.ds.network.NetworkLabel;
import uk.ac.uea.cmp.spectre.core.ds.network.Vertex;
import uk.ac.uea.cmp.spectre.core.ds.network.draw.Leader;
import uk.ac.uea.cmp.spectre.core.ds.network.draw.ViewerConfig;
import uk.ac.uea.cmp.spectre.core.ds.quad.Quad;
import uk.ac.uea.cmp.spectre.core.ds.quad.SpectreQuad;
import uk.ac.uea.cmp.spectre.core.ds.quad.quadruple.Quadruple;
import uk.ac.uea.cmp.spectre.core.ds.split.SpectreSplitBlock;
import uk.ac.uea.cmp.spectre.core.io.nexus.Nexus;
import uk.ac.uea.cmp.spectre.core.ui.gui.geom.IndexedPoint;
import uk.ac.uea.cmp.spectre.core.util.StringUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.DoubleConsumer;

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
    private NexusLocationBuilder locationBuilder;
    private NexusQuadruplesBuilder quadBuilder;
    private ViewerConfig viewerConfig;
    private NexusCharacterBuilder charBuilder;


    public NexusFilePopulator(Nexus nexus, boolean verbose) {
        this.nexus = nexus;
        this.verbose = verbose;
        this.distanceMatrixBuilder = new DistanceMatrixBuilder();
        this.splitSystemBuilder = new NexusSplitSystemBuilder();
        this.quartetSystemBuilder = new NexusQuartetSystemBuilder();
        this.networkBuilder = new NexusNetworkBuilder();
        this.locationBuilder = new NexusLocationBuilder();
        this.quadBuilder = new NexusQuadruplesBuilder();
        this.viewerConfig = new ViewerConfig();
        this.charBuilder = new NexusCharacterBuilder();
    }

    @Override
    public void enterQuartets_block_header(NexusFileParser.Quartets_block_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitQuartets_block_header(NexusFileParser.Quartets_block_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterCycle_item_list(NexusFileParser.Cycle_item_listContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitCycle_item_list(NexusFileParser.Cycle_item_listContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterTree_definition(NexusFileParser.Tree_definitionContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitTree_definition(NexusFileParser.Tree_definitionContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterConfidences_splits(NexusFileParser.Confidences_splitsContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitConfidences_splits(NexusFileParser.Confidences_splitsContext ctx) {
        this.splitSystemBuilder.setHasConfidences(
                ctx.boolean_option() == null ?
                        true :
                        BooleanUtils.toBoolean(ctx.boolean_option().getText())
        );
    }

    @Override
    public void enterNchar(NexusFileParser.NcharContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitNchar(NexusFileParser.NcharContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterDimensions_network(NexusFileParser.Dimensions_networkContext ctx) {

    }

    @Override
    public void exitDimensions_network(NexusFileParser.Dimensions_networkContext ctx) {

        int nbExpectedTaxa = Integer.parseInt(ctx.ntax().integer().getText());
        int nbExpectedEdges = Integer.parseInt(ctx.nedges().integer().getText());
        int nbExpectedVertices = Integer.parseInt(ctx.nvertices().integer().getText());

        this.networkBuilder.setExpectedDimensions(nbExpectedTaxa, nbExpectedVertices, nbExpectedEdges);
    }

    @Override
    public void enterRoot(NexusFileParser.RootContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitRoot(NexusFileParser.RootContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterFormat_distances_item(NexusFileParser.Format_distances_itemContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitFormat_distances_item(NexusFileParser.Format_distances_itemContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterTree_list(NexusFileParser.Tree_listContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitTree_list(NexusFileParser.Tree_listContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterTriangle_option(NexusFileParser.Triangle_optionContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitTriangle_option(NexusFileParser.Triangle_optionContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterEdges_network(NexusFileParser.Edges_networkContext ctx) {

    }

    @Override
    public void exitEdges_network(NexusFileParser.Edges_networkContext ctx) {

    }

    @Override
    public void enterMatrix_quartets_data(NexusFileParser.Matrix_quartets_dataContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitMatrix_quartets_data(NexusFileParser.Matrix_quartets_dataContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterNv_width(NexusFileParser.Nv_widthContext ctx) {

    }

    @Override
    public void exitNv_width(NexusFileParser.Nv_widthContext ctx) {

        this.networkBuilder.getCurrentVertex().setWidth(Integer.parseInt(ctx.integer().getText()));
    }

    @Override
    public void enterDistances_header(NexusFileParser.Distances_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitDistances_header(NexusFileParser.Distances_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterBlock_taxa(NexusFileParser.Block_taxaContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitBlock_taxa(NexusFileParser.Block_taxaContext ctx) {

        int nTax = -1;

        if (ctx.dimensions_taxa() != null && ctx.dimensions_taxa().ntax() != null) {

            NexusFileParser.NtaxContext ctxNtax = ctx.dimensions_taxa().ntax();

            if (ctxNtax.integer() != null && !ctxNtax.integer().getText().isEmpty()) {

                nTax = Integer.parseInt(ctxNtax.integer().getText());

                if (verbose && log.isDebugEnabled())
                    log.debug("Taxa Block: ntax:" + nTax);
            }
        }

        IdentifierList taxa = this.nexus.getTaxa();

        if (ctx.tax_labels() != null) {

            if (ctx.tax_labels().identifier_list() != null) {

                NexusFileParser.Identifier_listContext idListCtx = ctx.tax_labels().identifier_list();

                while (idListCtx != null) {
                    if (idListCtx.identifier() != null) {
                        String id = StringUtils.stripQuotes(idListCtx.identifier().getText());
                        taxa.add(new Identifier(id));
                    }
                    idListCtx = idListCtx.identifier_list();
                }
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
    public void enterState_composed_word(NexusFileParser.State_composed_wordContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitState_composed_word(NexusFileParser.State_composed_wordContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterMissing(NexusFileParser.MissingContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitMissing(NexusFileParser.MissingContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterTranslate_network_data(NexusFileParser.Translate_network_dataContext ctx) {

    }

    @Override
    public void exitTranslate_network_data(NexusFileParser.Translate_network_dataContext ctx) {

    }

    @Override
    public void enterMatrix_header(NexusFileParser.Matrix_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitMatrix_header(NexusFileParser.Matrix_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterNsplits(NexusFileParser.NsplitsContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitNsplits(NexusFileParser.NsplitsContext ctx) {


    }

    @Override
    public void enterTree_label_optional(NexusFileParser.Tree_label_optionalContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitTree_label_optional(NexusFileParser.Tree_label_optionalContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterWeights_splits(NexusFileParser.Weights_splitsContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitWeights_splits(NexusFileParser.Weights_splitsContext ctx) {
        this.splitSystemBuilder.setWeighted(
                ctx.boolean_option() == null ?
                        true :
                        BooleanUtils.toBoolean(ctx.boolean_option().getText())
        );
    }

    @Override
    public void enterTax_info_header(NexusFileParser.Tax_info_headerContext ctx) {

    }

    @Override
    public void exitTax_info_header(NexusFileParser.Tax_info_headerContext ctx) {

    }

    @Override
    public void enterVertex_options(NexusFileParser.Vertex_optionsContext ctx) {

    }

    @Override
    public void exitVertex_options(NexusFileParser.Vertex_optionsContext ctx) {

    }


    @Override
    public void enterMatrix_splits_data(NexusFileParser.Matrix_splits_dataContext ctx) {


    }

    @Override
    public void exitMatrix_splits_data(NexusFileParser.Matrix_splits_dataContext ctx) {

        if (ctx.floatingp() != null) {

            double weight = Double.parseDouble(ctx.floatingp().getText());

            NexusFileParser.Matrix_splits_listContext ctxList = ctx.matrix_splits_list();

            List<Integer> setA = new LinkedList<>();

            while (ctxList != null) {

                if (ctxList.integer() != null) {

                    int val = Integer.parseInt(ctxList.integer().getText());

                    setA.add(val);
                }

                ctxList = ctxList.matrix_splits_list();
            }

            this.splitSystemBuilder.addSplit(new SpectreSplitBlock(setA), weight);
        }
    }

    @Override
    public void enterProperties_splits_item(NexusFileParser.Properties_splits_itemContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitProperties_splits_item(NexusFileParser.Properties_splits_itemContext ctx) {

        if (ctx.properties_splits_name() != null) {


            if (ctx.properties_splits_name().getText().equalsIgnoreCase("cyclic")) {
                String valStr = ctx.boolean_option() != null ? ctx.boolean_option().getText() : null;
                boolean val = valStr != null ? BooleanUtils.toBoolean(valStr) : true;
                this.splitSystemBuilder.setCyclic(val);
            } /*else if (ctx.properties_splits_name().getText().equalsIgnoreCase("weakly_compatible") ||
                    ctx.properties_splits_name().getText().equalsIgnoreCase("compatible")) {
                String valStr = ctx.boolean_option() != null ? ctx.boolean_option().getText() : null;
                boolean val = valStr != null ? BooleanUtils.toBoolean(valStr) : true;
                this.splitSystemBuilder.setCompatible(val);
            } */else if (ctx.properties_splits_name().getText().equalsIgnoreCase("fit")) {
                String valStr = ctx.number() != null ? ctx.number().getText() : null;
                this.splitSystemBuilder.setFit(Double.parseDouble(valStr));
            }
        }
    }

    @Override
    public void enterNl_color_lc(NexusFileParser.Nl_color_lcContext ctx) {

    }

    @Override
    public void exitNl_color_lc(NexusFileParser.Nl_color_lcContext ctx) {

    }

    @Override
    public void enterDiagonal(NexusFileParser.DiagonalContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitDiagonal(NexusFileParser.DiagonalContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterNl_font(NexusFileParser.Nl_fontContext ctx) {

    }

    @Override
    public void exitNl_font(NexusFileParser.Nl_fontContext ctx) {

        String fontString = StringUtils.stripQuotes(ctx.identifier().getText());

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
    public void enterCycle_header(NexusFileParser.Cycle_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitCycle_header(NexusFileParser.Cycle_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterCycle(NexusFileParser.CycleContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitCycle(NexusFileParser.CycleContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterTranslate_list(NexusFileParser.Translate_listContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitTranslate_list(NexusFileParser.Translate_listContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterState_complex_word(NexusFileParser.State_complex_wordContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitState_complex_word(NexusFileParser.State_complex_wordContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterTranslate_network_header(NexusFileParser.Translate_network_headerContext ctx) {

    }

    @Override
    public void exitTranslate_network_header(NexusFileParser.Translate_network_headerContext ctx) {

    }

    @Override
    public void enterRotate_network(NexusFileParser.Rotate_networkContext ctx) {

    }

    @Override
    public void exitRotate_network(NexusFileParser.Rotate_networkContext ctx) {

        this.networkBuilder.setRotateAbout(Float.parseFloat(ctx.floatingp().getText()));
    }

    @Override
    public void enterLabels_header(NexusFileParser.Labels_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitLabels_header(NexusFileParser.Labels_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterKey_value_pairs(NexusFileParser.Key_value_pairsContext ctx) {

    }

    @Override
    public void exitKey_value_pairs(NexusFileParser.Key_value_pairsContext ctx) {

    }

    @Override
    public void enterLabels(NexusFileParser.LabelsContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitLabels(NexusFileParser.LabelsContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterDimensions_distances(NexusFileParser.Dimensions_distancesContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitDimensions_distances(NexusFileParser.Dimensions_distancesContext ctx) {

        if (ctx.newtaxa() != null) {

            NexusFileParser.NtaxContext ctxNtax = ctx.newtaxa().ntax();

            if (ctxNtax.integer() != null && !ctxNtax.integer().getText().isEmpty()) {
                this.distanceMatrixBuilder.setNbTaxa(Integer.parseInt(ctxNtax.integer().getText()));
            }

            NexusFileParser.NcharContext ctxNchar = ctx.nchar();

            if (ctxNchar.integer() != null && !ctxNchar.integer().getText().isEmpty()) {
                this.distanceMatrixBuilder.setNbChars(Integer.parseInt(ctxNchar.integer().getText()));
            }
        }
    }

    @Override
    public void enterLabels_splits(NexusFileParser.Labels_splitsContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitLabels_splits(NexusFileParser.Labels_splitsContext ctx) {
        this.splitSystemBuilder.setHasLabels(
                ctx.labels_option() == null ?
                        ctx.labels_header().getText().equalsIgnoreCase("labels") :
                        ctx.labels_option().getText().equalsIgnoreCase("true") ||
                                ctx.labels_option().getText().equalsIgnoreCase("yes"));
    }

    @Override
    public void enterNumber(NexusFileParser.NumberContext ctx) {

    }

    @Override
    public void exitNumber(NexusFileParser.NumberContext ctx) {

    }

    @Override
    public void enterProperties(NexusFileParser.PropertiesContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitProperties(NexusFileParser.PropertiesContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterIdentifier_list(NexusFileParser.Identifier_listContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitIdentifier_list(NexusFileParser.Identifier_listContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterName_list(NexusFileParser.Name_listContext ctx) {

    }

    @Override
    public void exitName_list(NexusFileParser.Name_listContext ctx) {

    }

    @Override
    public void enterIdentifier(NexusFileParser.IdentifierContext ctx) {

    }

    @Override
    public void exitIdentifier(NexusFileParser.IdentifierContext ctx) {

    }

    @Override
    public void enterId(NexusFileParser.IdContext ctx) {

    }

    @Override
    public void exitId(NexusFileParser.IdContext ctx) {

    }

    @Override
    public void enterName(NexusFileParser.NameContext ctx) {

    }

    @Override
    public void exitName(NexusFileParser.NameContext ctx) {

    }

    @Override
    public void enterInterleave(NexusFileParser.InterleaveContext ctx) {

    }

    @Override
    public void exitInterleave(NexusFileParser.InterleaveContext ctx) {

    }

    @Override
    public void enterFormat(NexusFileParser.FormatContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitFormat(NexusFileParser.FormatContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterEdges_network_option(NexusFileParser.Edges_network_optionContext ctx) {

    }

    @Override
    public void exitEdges_network_option(NexusFileParser.Edges_network_optionContext ctx) {

    }

    @Override
    public void enterDimensions_splits(NexusFileParser.Dimensions_splitsContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitDimensions_splits(NexusFileParser.Dimensions_splitsContext ctx) {

        if (this.nexus.getTaxa() != null) {
            this.splitSystemBuilder.setTaxa(this.nexus.getTaxa());
        }

        if (ctx.ntax() != null) {
            if (ctx.ntax().integer() != null) {
                this.splitSystemBuilder.setExpectedNbTaxa(Integer.parseInt(ctx.ntax().integer().getText()));
            }
        }

        if (ctx.nsplits() != null) {
            if (ctx.nsplits().integer() != null) {
                this.splitSystemBuilder.setExpectedNbSplits(Integer.parseInt(ctx.nsplits().integer().getText()));
            }
        }
    }

    @Override
    public void enterNv_shape(NexusFileParser.Nv_shapeContext ctx) {

    }

    @Override
    public void exitNv_shape(NexusFileParser.Nv_shapeContext ctx) {
        this.networkBuilder.getCurrentVertex().setShape(ctx.shape_option().getText());
    }

    @Override
    public void enterShape_option(NexusFileParser.Shape_optionContext ctx) {

    }

    @Override
    public void exitShape_option(NexusFileParser.Shape_optionContext ctx) {

    }

    @Override
    public void enterAssumptions_data(NexusFileParser.Assumptions_dataContext ctx) {

    }

    @Override
    public void exitAssumptions_data(NexusFileParser.Assumptions_dataContext ctx) {

    }

    @Override
    public void enterFormat_distances(NexusFileParser.Format_distancesContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitFormat_distances(NexusFileParser.Format_distancesContext ctx) {

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
    public void enterBlock_declaration(NexusFileParser.Block_declarationContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitBlock_declaration(NexusFileParser.Block_declarationContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterFormat_splits_list(NexusFileParser.Format_splits_listContext ctx) {

    }

    @Override
    public void exitFormat_splits_list(NexusFileParser.Format_splits_listContext ctx) {

    }

    @Override
    public void enterConfidences_header(NexusFileParser.Confidences_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitConfidences_header(NexusFileParser.Confidences_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterVlabels_network_data(NexusFileParser.Vlabels_network_dataContext ctx) {

    }

    @Override
    public void exitVlabels_network_data(NexusFileParser.Vlabels_network_dataContext ctx) {

    }

    @Override
    public void enterProperties_splits(NexusFileParser.Properties_splitsContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitProperties_splits(NexusFileParser.Properties_splitsContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterVlabels_network(NexusFileParser.Vlabels_networkContext ctx) {

    }

    @Override
    public void exitVlabels_network(NexusFileParser.Vlabels_networkContext ctx) {

    }

    @Override
    public void enterTax_labels_optional(NexusFileParser.Tax_labels_optionalContext ctx) {

    }

    @Override
    public void exitTax_labels_optional(NexusFileParser.Tax_labels_optionalContext ctx) {

    }

    @Override
    public void enterNewick_tree(NexusFileParser.Newick_treeContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitNewick_tree(NexusFileParser.Newick_treeContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterTranslate_network(NexusFileParser.Translate_networkContext ctx) {

    }

    @Override
    public void exitTranslate_network(NexusFileParser.Translate_networkContext ctx) {

    }

    @Override
    public void enterTree_rest(NexusFileParser.Tree_restContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitTree_rest(NexusFileParser.Tree_restContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterNl_color_bg(NexusFileParser.Nl_color_bgContext ctx) {

    }

    @Override
    public void exitNl_color_bg(NexusFileParser.Nl_color_bgContext ctx) {

    }

    @Override
    public void enterNewtaxa(NexusFileParser.NewtaxaContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitNewtaxa(NexusFileParser.NewtaxaContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterProperties_splits_name(NexusFileParser.Properties_splits_nameContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitProperties_splits_name(NexusFileParser.Properties_splits_nameContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterBlock_splits(NexusFileParser.Block_splitsContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitBlock_splits(NexusFileParser.Block_splitsContext ctx) {

        this.nexus.setSplitSystem(this.splitSystemBuilder.createSplitSystem());
    }

    @Override
    public void enterDraw_network_options(NexusFileParser.Draw_network_optionsContext ctx) {

    }

    @Override
    public void exitDraw_network_options(NexusFileParser.Draw_network_optionsContext ctx) {

    }

    @Override
    public void enterMatrix_entry_list(NexusFileParser.Matrix_entry_listContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitMatrix_entry_list(NexusFileParser.Matrix_entry_listContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterDraw_network(NexusFileParser.Draw_networkContext ctx) {

    }

    @Override
    public void exitDraw_network(NexusFileParser.Draw_networkContext ctx) {

        this.networkBuilder.setDrawToScale(true);
    }

    @Override
    public void enterNvertices(NexusFileParser.NverticesContext ctx) {

    }

    @Override
    public void exitNvertices(NexusFileParser.NverticesContext ctx) {

    }

    @Override
    public void enterNe_unknown(NexusFileParser.Ne_unknownContext ctx) {

    }

    @Override
    public void exitNe_unknown(NexusFileParser.Ne_unknownContext ctx) {
        // Not sure what to do with this one
    }

    @Override
    public void enterDraw_network_header(NexusFileParser.Draw_network_headerContext ctx) {

    }

    @Override
    public void exitDraw_network_header(NexusFileParser.Draw_network_headerContext ctx) {

    }

    @Override
    public void enterVlabels_network_label(NexusFileParser.Vlabels_network_labelContext ctx) {

    }

    @Override
    public void exitVlabels_network_label(NexusFileParser.Vlabels_network_labelContext ctx) {

        int id = Integer.parseInt(ctx.integer().getText());

        String name = StringUtils.stripQuotes(ctx.name().getText());

        this.networkBuilder.getCurrentLabel().setName(name);
        this.networkBuilder.getCurrentLabel().setVertexId(id);
    }

    @Override
    public void enterFormat_splits_item(NexusFileParser.Format_splits_itemContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitFormat_splits_item(NexusFileParser.Format_splits_itemContext ctx) {

    }

    @Override
    public void enterTax_labels_header(NexusFileParser.Tax_labels_headerContext ctx) {

    }

    @Override
    public void exitTax_labels_header(NexusFileParser.Tax_labels_headerContext ctx) {

    }

    @Override
    public void enterInteger(NexusFileParser.IntegerContext ctx) {

    }

    @Override
    public void exitInteger(NexusFileParser.IntegerContext ctx) {

    }

    @Override
    public void enterFloatingp(NexusFileParser.FloatingpContext ctx) {

    }

    @Override
    public void exitFloatingp(NexusFileParser.FloatingpContext ctx) {

    }

    @Override
    public void enterVlabels_network_entry(NexusFileParser.Vlabels_network_entryContext ctx) {

        NetworkLabel label = new NetworkLabel();

        this.networkBuilder.setCurrentLabel(label);
    }

    @Override
    public void exitVlabels_network_entry(NexusFileParser.Vlabels_network_entryContext ctx) {

        // Create a link between the vertex and the label
        this.networkBuilder.getVertices().get(this.networkBuilder.getCurrentLabel().getVertexId()).setLabel(this.networkBuilder.getCurrentLabel());

        // Add the label to the map
        this.networkBuilder.getLabels().put(this.networkBuilder.getCurrentLabel().getVertexId(), this.networkBuilder.getCurrentLabel());
    }

    @Override
    public void enterNl_l(NexusFileParser.Nl_lContext ctx) {

    }

    @Override
    public void exitNl_l(NexusFileParser.Nl_lContext ctx) {

        // Not sure what to do with this for the moment

        // this.networkBuilder.getCurrentLabel()
    }

    @Override
    public void enterVertices_network_header(NexusFileParser.Vertices_network_headerContext ctx) {

    }

    @Override
    public void exitVertices_network_header(NexusFileParser.Vertices_network_headerContext ctx) {

    }

    @Override
    public void enterEdges_network_header(NexusFileParser.Edges_network_headerContext ctx) {

    }

    @Override
    public void exitEdges_network_header(NexusFileParser.Edges_network_headerContext ctx) {

    }

    @Override
    public void enterIntervals_splits(NexusFileParser.Intervals_splitsContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitIntervals_splits(NexusFileParser.Intervals_splitsContext ctx) {
        this.splitSystemBuilder.setHasIntervals(
                ctx.boolean_option() == null ?
                        true :
                        BooleanUtils.toBoolean(ctx.boolean_option().getText())
        );
    }

    @Override
    public void enterBlock_quartets(NexusFileParser.Block_quartetsContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitBlock_quartets(NexusFileParser.Block_quartetsContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterVertices_network_data(NexusFileParser.Vertices_network_dataContext ctx) {

    }

    @Override
    public void exitVertices_network_data(NexusFileParser.Vertices_network_dataContext ctx) {

    }

    @Override
    public void enterNexus_header(NexusFileParser.Nexus_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitNexus_header(NexusFileParser.Nexus_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterNtax_header(NexusFileParser.Ntax_headerContext ctx) {

    }

    @Override
    public void exitNtax_header(NexusFileParser.Ntax_headerContext ctx) {

    }

    @Override
    public void enterTax_labels(NexusFileParser.Tax_labelsContext ctx) {

    }

    @Override
    public void exitTax_labels(NexusFileParser.Tax_labelsContext ctx) {

    }

    @Override
    public void enterBlock(NexusFileParser.BlockContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitBlock(NexusFileParser.BlockContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterMatrix_splits_list(NexusFileParser.Matrix_splits_listContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitMatrix_splits_list(NexusFileParser.Matrix_splits_listContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterTree_label(NexusFileParser.Tree_labelContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitTree_label(NexusFileParser.Tree_labelContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterTax_info_entries(NexusFileParser.Tax_info_entriesContext ctx) {

    }

    @Override
    public void exitTax_info_entries(NexusFileParser.Tax_info_entriesContext ctx) {

    }

    @Override
    public void enterTax_info(NexusFileParser.Tax_infoContext ctx) {

    }

    @Override
    public void exitTax_info(NexusFileParser.Tax_infoContext ctx) {

    }

    @Override
    public void enterFormat_distances_list(NexusFileParser.Format_distances_listContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitFormat_distances_list(NexusFileParser.Format_distances_listContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterDraw_network_option(NexusFileParser.Draw_network_optionContext ctx) {

    }

    @Override
    public void exitDraw_network_option(NexusFileParser.Draw_network_optionContext ctx) {

    }

    @Override
    public void enterFormat_splits(NexusFileParser.Format_splitsContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitFormat_splits(NexusFileParser.Format_splitsContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterNl_y(NexusFileParser.Nl_yContext ctx) {

    }

    @Override
    public void exitNl_y(NexusFileParser.Nl_yContext ctx) {
        this.networkBuilder.getCurrentLabel().setOffsetY(Integer.parseInt(ctx.integer().getText()));
    }

    @Override
    public void enterNv_b(NexusFileParser.Nv_bContext ctx) {

    }

    @Override
    public void exitNv_b(NexusFileParser.Nv_bContext ctx) {
        // Not sure what this is so we'll ignore it for now...
    }

    @Override
    public void enterNetwork_block_header(NexusFileParser.Network_block_headerContext ctx) {

    }

    @Override
    public void exitNetwork_block_header(NexusFileParser.Network_block_headerContext ctx) {

    }

    @Override
    public void enterScale_network(NexusFileParser.Scale_networkContext ctx) {

    }

    @Override
    public void exitScale_network(NexusFileParser.Scale_networkContext ctx) {

    }

    @Override
    public void enterNv_height(NexusFileParser.Nv_heightContext ctx) {

    }

    @Override
    public void exitNv_height(NexusFileParser.Nv_heightContext ctx) {
        this.networkBuilder.getCurrentVertex().setHeight(Integer.parseInt(ctx.integer().getText()));
    }

    @Override
    public void enterSplits_block_header(NexusFileParser.Splits_block_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitSplits_block_header(NexusFileParser.Splits_block_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterLength(NexusFileParser.LengthContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitLength(NexusFileParser.LengthContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterNedges(NexusFileParser.NedgesContext ctx) {

    }

    @Override
    public void exitNedges(NexusFileParser.NedgesContext ctx) {
    }

    @Override
    public void enterTree_block_header(NexusFileParser.Tree_block_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitTree_block_header(NexusFileParser.Tree_block_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterBlock_network(NexusFileParser.Block_networkContext ctx) {

    }

    @Override
    public void exitBlock_network(NexusFileParser.Block_networkContext ctx) {

        this.nexus.setNetwork(this.networkBuilder.createNetwork());
    }

    @Override
    public void enterNv_color_fg(NexusFileParser.Nv_color_fgContext ctx) {

    }

    @Override
    public void exitNv_color_fg(NexusFileParser.Nv_color_fgContext ctx) {
        this.networkBuilder.getCurrentVertex().setLineColor(
                new Color(
                        Integer.parseInt(ctx.integer(0).getText()),
                        Integer.parseInt(ctx.integer(1).getText()),
                        Integer.parseInt(ctx.integer(2).getText())));
    }

    @Override
    public void enterDimensions(NexusFileParser.DimensionsContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitDimensions(NexusFileParser.DimensionsContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterBoolean_option(NexusFileParser.Boolean_optionContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitBoolean_option(NexusFileParser.Boolean_optionContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterTree_header(NexusFileParser.Tree_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitTree_header(NexusFileParser.Tree_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterNl_x(NexusFileParser.Nl_xContext ctx) {

    }

    @Override
    public void exitNl_x(NexusFileParser.Nl_xContext ctx) {
        this.networkBuilder.getCurrentLabel().setOffsetX(Integer.parseInt(ctx.integer().getText()));
    }

    @Override
    public void enterParse(NexusFileParser.ParseContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitParse(NexusFileParser.ParseContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
        int i = 0;
    }

    @Override
    public void enterMatrix_data(NexusFileParser.Matrix_dataContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitMatrix_data(NexusFileParser.Matrix_dataContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterBlock_assumptions(NexusFileParser.Block_assumptionsContext ctx) {

    }

    @Override
    public void exitBlock_assumptions(NexusFileParser.Block_assumptionsContext ctx) {

    }

    @Override
    public void enterEnd(NexusFileParser.EndContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitEnd(NexusFileParser.EndContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterEdges_network_options(NexusFileParser.Edges_network_optionsContext ctx) {

    }

    @Override
    public void exitEdges_network_options(NexusFileParser.Edges_network_optionsContext ctx) {

    }

    @Override
    public void enterTax_info_entry(NexusFileParser.Tax_info_entryContext ctx) {

    }

    @Override
    public void exitTax_info_entry(NexusFileParser.Tax_info_entryContext ctx) {

    }

    @Override
    public void enterBlock_characters(NexusFileParser.Block_charactersContext ctx) {

    }

    @Override
    public void exitBlock_characters(NexusFileParser.Block_charactersContext ctx) {
        this.nexus.setAlignments(this.charBuilder.createAlignments());
    }

    @Override
    public void enterCharacters_header(NexusFileParser.Characters_headerContext ctx) {

    }

    @Override
    public void exitCharacters_header(NexusFileParser.Characters_headerContext ctx) {

    }

    @Override
    public void enterChar_dimensions(NexusFileParser.Char_dimensionsContext ctx) {

    }

    @Override
    public void exitChar_dimensions(NexusFileParser.Char_dimensionsContext ctx) {


    }

    @Override
    public void enterChar_dim_options(NexusFileParser.Char_dim_optionsContext ctx) {

    }

    @Override
    public void exitChar_dim_options(NexusFileParser.Char_dim_optionsContext ctx) {

    }

    @Override
    public void enterChar_dim_option(NexusFileParser.Char_dim_optionContext ctx) {

    }

    @Override
    public void exitChar_dim_option(NexusFileParser.Char_dim_optionContext ctx) {
        if (ctx.ntax() != null) {
            this.charBuilder.setExpectedNbSeqs(Integer.parseInt(ctx.ntax().integer().getText()));
        }
        if (ctx.cd_nchar() != null) {
            this.charBuilder.setExpectedNbChars(Integer.parseInt(ctx.cd_nchar().integer().getText()));
        }
    }

    @Override
    public void enterCd_nchar(NexusFileParser.Cd_ncharContext ctx) {

    }

    @Override
    public void exitCd_nchar(NexusFileParser.Cd_ncharContext ctx) {

    }

    @Override
    public void enterChar_format(NexusFileParser.Char_formatContext ctx) {

    }

    @Override
    public void exitChar_format(NexusFileParser.Char_formatContext ctx) {

    }

    @Override
    public void enterChar_format_header(NexusFileParser.Char_format_headerContext ctx) {

    }

    @Override
    public void exitChar_format_header(NexusFileParser.Char_format_headerContext ctx) {

    }

    @Override
    public void enterChar_format_options(NexusFileParser.Char_format_optionsContext ctx) {

    }

    @Override
    public void exitChar_format_options(NexusFileParser.Char_format_optionsContext ctx) {

    }

    @Override
    public void enterChar_format_option(NexusFileParser.Char_format_optionContext ctx) {

    }

    @Override
    public void exitChar_format_option(NexusFileParser.Char_format_optionContext ctx) {

    }

    @Override
    public void enterCf_datatype(NexusFileParser.Cf_datatypeContext ctx) {

    }

    @Override
    public void exitCf_datatype(NexusFileParser.Cf_datatypeContext ctx) {
        //this.charBuilder.getFormat()
    }

    @Override
    public void enterCf_missing(NexusFileParser.Cf_missingContext ctx) {

    }

    @Override
    public void exitCf_missing(NexusFileParser.Cf_missingContext ctx) {
        String missing = StringUtils.stripQuotes(ctx.missing_option().getText());
        this.charBuilder.getFormat().missing = missing.length() >= 1 ? missing.charAt(0) : '?';
    }

    @Override
    public void enterCf_gap(NexusFileParser.Cf_gapContext ctx) {

    }

    @Override
    public void exitCf_gap(NexusFileParser.Cf_gapContext ctx) {
        String gap = StringUtils.stripQuotes(ctx.gap_option().getText());
        this.charBuilder.getFormat().gap = gap.length() >= 1 ? gap.charAt(0) : '-';
    }

    @Override
    public void enterCf_symbols(NexusFileParser.Cf_symbolsContext ctx) {

    }

    @Override
    public void exitCf_symbols(NexusFileParser.Cf_symbolsContext ctx) {
        String symbols = StringUtils.stripQuotes(ctx.identifier().getText());
        this.charBuilder.getFormat().symbols = symbols;
    }

    @Override
    public void enterCf_labels(NexusFileParser.Cf_labelsContext ctx) {

    }

    @Override
    public void exitCf_labels(NexusFileParser.Cf_labelsContext ctx) {
        boolean labels = ctx.boolean_option() != null ? BooleanUtils.toBoolean(ctx.boolean_option().getText()) : true;
        this.charBuilder.getFormat().labels = labels;
    }

    @Override
    public void enterCf_transpose(NexusFileParser.Cf_transposeContext ctx) {

    }

    @Override
    public void exitCf_transpose(NexusFileParser.Cf_transposeContext ctx) {
        boolean transpose = ctx.boolean_option() != null ? BooleanUtils.toBoolean(ctx.boolean_option().getText()) : true;
        this.charBuilder.getFormat().transposed = transpose;
    }

    @Override
    public void enterCf_interleave(NexusFileParser.Cf_interleaveContext ctx) {

    }

    @Override
    public void exitCf_interleave(NexusFileParser.Cf_interleaveContext ctx) {
        boolean interleave = ctx.boolean_option() != null ? Boolean.getBoolean(ctx.boolean_option().getText()) : true;
        this.charBuilder.getFormat().interleaved = interleave;
    }

    @Override
    public void enterMissing_option(NexusFileParser.Missing_optionContext ctx) {

    }

    @Override
    public void exitMissing_option(NexusFileParser.Missing_optionContext ctx) {

    }

    @Override
    public void enterGap_option(NexusFileParser.Gap_optionContext ctx) {

    }

    @Override
    public void exitGap_option(NexusFileParser.Gap_optionContext ctx) {
        //this.charBuilder.getFormat().
    }

    @Override
    public void enterChar_matrix(NexusFileParser.Char_matrixContext ctx) {

    }

    @Override
    public void exitChar_matrix(NexusFileParser.Char_matrixContext ctx) {

    }

    @Override
    public void enterChar_sequences(NexusFileParser.Char_sequencesContext ctx) {

    }

    @Override
    public void exitChar_sequences(NexusFileParser.Char_sequencesContext ctx) {

    }

    @Override
    public void enterChar_seq_entry(NexusFileParser.Char_seq_entryContext ctx) {

    }

    @Override
    public void exitChar_seq_entry(NexusFileParser.Char_seq_entryContext ctx) {

        String seq = ctx.getText().replaceAll("\'","");
        seq = seq.replaceAll("\"","");
        this.charBuilder.addSeq(seq);
    }

    @Override
    public void enterTranslate_header(NexusFileParser.Translate_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitTranslate_header(NexusFileParser.Translate_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterDimensions_taxa(NexusFileParser.Dimensions_taxaContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitDimensions_taxa(NexusFileParser.Dimensions_taxaContext ctx) {

    }

    @Override
    public void enterVertex_option(NexusFileParser.Vertex_optionContext ctx) {

    }

    @Override
    public void exitVertex_option(NexusFileParser.Vertex_optionContext ctx) {

    }

    @Override
    public void enterTaxa_header(NexusFileParser.Taxa_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitTaxa_header(NexusFileParser.Taxa_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterLabels_option(NexusFileParser.Labels_optionContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitLabels_option(NexusFileParser.Labels_optionContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterMdata(NexusFileParser.MdataContext ctx) {

    }

    @Override
    public void exitMdata(NexusFileParser.MdataContext ctx) {

    }

    @Override
    public void enterNv_color_bg(NexusFileParser.Nv_color_bgContext ctx) {

    }

    @Override
    public void exitNv_color_bg(NexusFileParser.Nv_color_bgContext ctx) {
        this.networkBuilder.getCurrentVertex().setBackgroundColor(
                new Color(
                        Integer.parseInt(ctx.integer(0).getText()),
                        Integer.parseInt(ctx.integer(1).getText()),
                        Integer.parseInt(ctx.integer(2).getText()))
        );
    }

    @Override
    public void enterStar(NexusFileParser.StarContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitStar(NexusFileParser.StarContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterNe_color(NexusFileParser.Ne_colorContext ctx) {

    }

    @Override
    public void exitNe_color(NexusFileParser.Ne_colorContext ctx) {
        this.networkBuilder.getCurrentEdge().setColor(
                new Color(
                        Integer.parseInt(ctx.integer(0).getText()),
                        Integer.parseInt(ctx.integer(1).getText()),
                        Integer.parseInt(ctx.integer(2).getText()))
        );
    }

    @Override
    public void enterEdges_network_entry(NexusFileParser.Edges_network_entryContext ctx) {
        Edge edge = new Edge();

        this.networkBuilder.setCurrentEdge(edge);
    }

    @Override
    public void exitEdges_network_entry(NexusFileParser.Edges_network_entryContext ctx) {

        int id = Integer.parseInt(ctx.integer().get(0).getText());
        int top = Integer.parseInt(ctx.integer().get(1).getText());
        int bot = Integer.parseInt(ctx.integer().get(2).getText());

        Edge edge = this.networkBuilder.getCurrentEdge();

        Vertex vTop = this.networkBuilder.getVertices().get(top);
        Vertex vBot = this.networkBuilder.getVertices().get(bot);

        vBot.getEdgeList().add(edge);
        vTop.getEdgeList().add(edge);

        edge.setNxnum(id);
        edge.setTop(vTop);
        edge.setBottom(vBot);

        this.networkBuilder.getEdges().put(this.networkBuilder.getCurrentEdge().getNxnum(), edge);
    }

    @Override
    public void enterAssumptions_data_entry(NexusFileParser.Assumptions_data_entryContext ctx) {

    }

    @Override
    public void exitAssumptions_data_entry(NexusFileParser.Assumptions_data_entryContext ctx) {

    }

    @Override
    public void enterVlabels_option(NexusFileParser.Vlabels_optionContext ctx) {

    }

    @Override
    public void exitVlabels_option(NexusFileParser.Vlabels_optionContext ctx) {

    }

    @Override
    public void enterIntervals_header(NexusFileParser.Intervals_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitIntervals_header(NexusFileParser.Intervals_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterVertices_network_entry(NexusFileParser.Vertices_network_entryContext ctx) {

        Vertex v = new Vertex();

        this.networkBuilder.setCurrentVertex(v);
    }

    @Override
    public void exitVertices_network_entry(NexusFileParser.Vertices_network_entryContext ctx) {

        int id = Integer.parseInt(ctx.integer().getText());
        double x = Double.parseDouble(ctx.floatingp(0).getText());
        double y = Double.parseDouble(ctx.floatingp(1).getText());

        Vertex v = this.networkBuilder.getCurrentVertex();

        v.setX(x);
        v.setY(y);
        v.setNxnum(id);

        this.networkBuilder.getVertices().put(id, v);
    }

    @Override
    public void enterBlocks(NexusFileParser.BlocksContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitBlocks(NexusFileParser.BlocksContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
        int i = 0;
    }

    @Override
    public void enterAssumptions_block_header(NexusFileParser.Assumptions_block_headerContext ctx) {

    }

    @Override
    public void exitAssumptions_block_header(NexusFileParser.Assumptions_block_headerContext ctx) {

    }

    @Override
    public void enterCycle_item(NexusFileParser.Cycle_itemContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitCycle_item(NexusFileParser.Cycle_itemContext ctx) {

        if (ctx.integer() != null) {
            this.splitSystemBuilder.addCycleItem(Integer.parseInt(ctx.integer().getText()));
        }
    }

    @Override
    public void enterReference(NexusFileParser.ReferenceContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitReference(NexusFileParser.ReferenceContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterBlock_trees(NexusFileParser.Block_treesContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitBlock_trees(NexusFileParser.Block_treesContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterVertices_network(NexusFileParser.Vertices_networkContext ctx) {

    }

    @Override
    public void exitVertices_network(NexusFileParser.Vertices_networkContext ctx) {

    }

    @Override
    public void enterBegin(NexusFileParser.BeginContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitBegin(NexusFileParser.BeginContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterNtax(NexusFileParser.NtaxContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitNtax(NexusFileParser.NtaxContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterEdges_network_data(NexusFileParser.Edges_network_dataContext ctx) {

    }

    @Override
    public void exitEdges_network_data(NexusFileParser.Edges_network_dataContext ctx) {

    }

    @Override
    public void enterVlabels_network_header(NexusFileParser.Vlabels_network_headerContext ctx) {

    }

    @Override
    public void exitVlabels_network_header(NexusFileParser.Vlabels_network_headerContext ctx) {

    }

    @Override
    public void enterWeights_header(NexusFileParser.Weights_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitWeights_header(NexusFileParser.Weights_headerContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterTriangle(NexusFileParser.TriangleContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitTriangle(NexusFileParser.TriangleContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterMatrix_quartet(NexusFileParser.Matrix_quartetContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitMatrix_quartet(NexusFileParser.Matrix_quartetContext ctx) {

        int x = Integer.parseInt(ctx.integer(0).getText());
        int y = Integer.parseInt(ctx.integer(1).getText());
        int u = Integer.parseInt(ctx.integer(2).getText());
        int v = Integer.parseInt(ctx.integer(3).getText());

        //ctx.
        //Quartet quartet = new Quartet();

    }

    @Override
    public void enterBlock_quadruples(NexusFileParser.Block_quadruplesContext ctx) {

    }

    @Override
    public void exitBlock_quadruples(NexusFileParser.Block_quadruplesContext ctx) {
        this.nexus.setQuadruples(this.quadBuilder.createQuadrupleSystem());
    }

    @Override
    public void enterQuadruples_block_header(NexusFileParser.Quadruples_block_headerContext ctx) {

    }

    @Override
    public void exitQuadruples_block_header(NexusFileParser.Quadruples_block_headerContext ctx) {

    }

    @Override
    public void enterDimensions_quadruples(NexusFileParser.Dimensions_quadruplesContext ctx) {

    }

    @Override
    public void exitDimensions_quadruples(NexusFileParser.Dimensions_quadruplesContext ctx) {
        if (ctx.ntax() != null) {
            if (ctx.ntax().integer() != null) {
                this.quadBuilder.setExpectedNbTaxa(Integer.parseInt(ctx.ntax().integer().getText()));
            }
        }

        if (ctx.nquadruples() != null) {
            if (ctx.nquadruples().integer() != null) {
                this.quadBuilder.setExpectedNbQuadruples(Integer.parseInt(ctx.nquadruples().integer().getText()));
            }
        }
    }

    @Override
    public void enterNquadruples(NexusFileParser.NquadruplesContext ctx) {

    }

    @Override
    public void exitNquadruples(NexusFileParser.NquadruplesContext ctx) {

    }

    @Override
    public void enterNquadruples_header(NexusFileParser.Nquadruples_headerContext ctx) {

    }

    @Override
    public void exitNquadruples_header(NexusFileParser.Nquadruples_headerContext ctx) {

    }

    @Override
    public void enterFormat_quadruples(NexusFileParser.Format_quadruplesContext ctx) {

    }

    @Override
    public void exitFormat_quadruples(NexusFileParser.Format_quadruplesContext ctx) {

    }

    @Override
    public void enterMatrix_quadruples_data(NexusFileParser.Matrix_quadruples_dataContext ctx) {

    }

    @Override
    public void exitMatrix_quadruples_data(NexusFileParser.Matrix_quadruples_dataContext ctx) {

    }

    @Override
    public void enterMatrix_quadruple(NexusFileParser.Matrix_quadrupleContext ctx) {

    }

    @Override
    public void exitMatrix_quadruple(NexusFileParser.Matrix_quadrupleContext ctx) {

        Quad taxa = null;
        if (ctx.integer() != null && ctx.integer().size() == 4) {
            int a = Integer.parseInt(ctx.integer(0).getText());
            int b = Integer.parseInt(ctx.integer(1).getText());
            int c = Integer.parseInt(ctx.integer(2).getText());
            int d = Integer.parseInt(ctx.integer(3).getText());
            taxa = new SpectreQuad(a, b, c, d);
        }

        double[] weights = null;
        if (ctx.floatingp() != null && ctx.floatingp().size() == 7) {
            weights = new double[7];
            for (int i = 0; i < 7; i++) {
                weights[i] = Double.parseDouble(ctx.floatingp(i).getText());
            }
        }

        if (taxa != null && weights != null) {
            this.quadBuilder.addQuad(new Quadruple(taxa, weights));
        }
    }

    @Override
    public void enterBlock_locations(NexusFileParser.Block_locationsContext ctx) {

    }

    @Override
    public void exitBlock_locations(NexusFileParser.Block_locationsContext ctx) {
        this.nexus.setLocations(this.locationBuilder.getLocations());
    }

    @Override
    public void enterLocations_block_header(NexusFileParser.Locations_block_headerContext ctx) {

    }

    @Override
    public void exitLocations_block_header(NexusFileParser.Locations_block_headerContext ctx) {

    }

    @Override
    public void enterDimensions_locations(NexusFileParser.Dimensions_locationsContext ctx) {

    }

    @Override
    public void exitDimensions_locations(NexusFileParser.Dimensions_locationsContext ctx) {
        this.locationBuilder.setNbExpectedTaxa(Integer.parseInt(ctx.ntax().integer().getText()));
    }

    @Override
    public void enterMatrix_locations_data(NexusFileParser.Matrix_locations_dataContext ctx) {

    }

    @Override
    public void exitMatrix_locations_data(NexusFileParser.Matrix_locations_dataContext ctx) {

    }

    @Override
    public void enterLocation_entry(NexusFileParser.Location_entryContext ctx) {

    }

    @Override
    public void exitLocation_entry(NexusFileParser.Location_entryContext ctx) {

        double x = Double.parseDouble(ctx.floatingp().get(0).getText());
        double y = Double.parseDouble(ctx.floatingp().get(1).getText());

        IndexedPoint ip = new IndexedPoint(-1, x, y, ctx.identifier().getText());

        this.locationBuilder.addLocation(ip);
    }

    @Override
    public void enterBlock_distances(NexusFileParser.Block_distancesContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitBlock_distances(NexusFileParser.Block_distancesContext ctx) {

        IdentifierList taxa = this.nexus.getTaxa();

        boolean populateTaxa = taxa == null || taxa.isEmpty();
        DistanceMatrixBuilder.Labels labeling = this.distanceMatrixBuilder.getLabels();

        if (populateTaxa) {
            taxa = new IdentifierList();
        }
        else {
            if (this.distanceMatrixBuilder.getNbTaxa() != taxa.size()) {
                throw new IllegalArgumentException("Distance matrix NTAX property and taxa block are inconsistent.");
            }
        }

        int max_row_size = this.distanceMatrixBuilder.getNbTaxa();
        DistanceMatrixBuilder.Triangle tri = this.distanceMatrixBuilder.getTriangle();

        int exp_size = 0;
        if (tri != DistanceMatrixBuilder.Triangle.BOTH) {
            for (int i = 1; i <= this.distanceMatrixBuilder.getNbTaxa(); i++) {
                exp_size += i;
            }
        }
        else {
            exp_size = max_row_size * this.distanceMatrixBuilder.getNbTaxa();
        }

        if (labeling != DistanceMatrixBuilder.Labels.NONE) {
            exp_size += this.distanceMatrixBuilder.getNbTaxa();
        }

        if (exp_size != ctx.mdata().children.size()) {
            throw new IllegalArgumentException("Distance matrix is not the expected size.  Expected: " + exp_size + "; Actual size: " + ctx.mdata().children.size());
        }

        int row_size = max_row_size + (labeling == DistanceMatrixBuilder.Labels.NONE ? 0 : 1);
        if (tri == DistanceMatrixBuilder.Triangle.LOWER) {
            row_size = labeling == DistanceMatrixBuilder.Labels.NONE ? 1 : 2;
        }

        int id_idx = 0;
        for(int i = 0; i < this.distanceMatrixBuilder.getNbTaxa(); i++) {
            List<Double> data = new ArrayList<>();
            String taxa_name = populateTaxa ? "" : taxa.get(i).getName();
            for (int j = 0; j < row_size; j++) {
                String entry = ctx.mdata().children.get(id_idx).getText();
                if (j == 0 && labeling == DistanceMatrixBuilder.Labels.LEFT) {
                    taxa_name = StringUtils.stripQuotes(entry);
                }
                else if (j == row_size - 1 && labeling == DistanceMatrixBuilder.Labels.RIGHT) {
                    taxa_name = StringUtils.stripQuotes(entry);
                }
                else {
                    data.add(Double.parseDouble(entry));
                }
                id_idx++;
            }
            // Check taxa name is valid
            if (!populateTaxa && taxa.getByName(taxa_name) == null) {
                throw new IllegalArgumentException("Could not find " + taxa_name + " described in distance matrix in the taxa block.");
            }
            else if (populateTaxa) {
                taxa.add(new Identifier(taxa_name));
            }

            this.distanceMatrixBuilder.addRow(data, taxa.getByName(taxa_name));

            if (tri == DistanceMatrixBuilder.Triangle.UPPER) {
                row_size--;
            }
            else if (tri == DistanceMatrixBuilder.Triangle.LOWER) {
                row_size++;
            }
        }

        // We should have all the information to build a distance matrix at this point... so do it.
        this.nexus.setDistanceMatrix(this.distanceMatrixBuilder.createDistanceMatrix());
    }

    @Override
    public void enterVlabels_options(NexusFileParser.Vlabels_optionsContext ctx) {

    }

    @Override
    public void exitVlabels_options(NexusFileParser.Vlabels_optionsContext ctx) {

    }

    @Override
    public void enterTranslate(NexusFileParser.TranslateContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitTranslate(NexusFileParser.TranslateContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterNe_width(NexusFileParser.Ne_widthContext ctx) {

    }

    @Override
    public void exitNe_width(NexusFileParser.Ne_widthContext ctx) {
        this.networkBuilder.getCurrentEdge().setWidth(Integer.parseInt(ctx.integer().getText()));
    }

    @Override
    public void enterState_composed_list(NexusFileParser.State_composed_listContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitState_composed_list(NexusFileParser.State_composed_listContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterBlock_viewer(NexusFileParser.Block_viewerContext ctx) {
    }

    @Override
    public void exitBlock_viewer(NexusFileParser.Block_viewerContext ctx) {
        this.nexus.setViewerConfig(this.viewerConfig);
    }

    @Override
    public void enterViewer_block_header(NexusFileParser.Viewer_block_headerContext ctx) {

    }

    @Override
    public void exitViewer_block_header(NexusFileParser.Viewer_block_headerContext ctx) {

    }

    @Override
    public void enterDimensions_viewer(NexusFileParser.Dimensions_viewerContext ctx) {

    }

    @Override
    public void exitDimensions_viewer(NexusFileParser.Dimensions_viewerContext ctx) {
        int width = Integer.parseInt(ctx.vwidth().integer().getText());
        int height = Integer.parseInt(ctx.vheight().integer().getText());
        this.viewerConfig.setDimensions(new Dimension(width, height));
    }

    @Override
    public void enterVwidth(NexusFileParser.VwidthContext ctx) {

    }

    @Override
    public void exitVwidth(NexusFileParser.VwidthContext ctx) {

    }

    @Override
    public void enterVheight(NexusFileParser.VheightContext ctx) {

    }

    @Override
    public void exitVheight(NexusFileParser.VheightContext ctx) {

    }

    @Override
    public void enterMatrix_viewer(NexusFileParser.Matrix_viewerContext ctx) {

    }

    @Override
    public void exitMatrix_viewer(NexusFileParser.Matrix_viewerContext ctx) {
        int i = 0;
    }

    @Override
    public void enterMatrix_viewer_options(NexusFileParser.Matrix_viewer_optionsContext ctx) {

    }

    @Override
    public void exitMatrix_viewer_options(NexusFileParser.Matrix_viewer_optionsContext ctx) {

    }

    @Override
    public void enterMatrix_viewer_option(NexusFileParser.Matrix_viewer_optionContext ctx) {

    }

    @Override
    public void exitMatrix_viewer_option(NexusFileParser.Matrix_viewer_optionContext ctx) {

    }

    @Override
    public void enterVm_ratio(NexusFileParser.Vm_ratioContext ctx) {

    }

    @Override
    public void exitVm_ratio(NexusFileParser.Vm_ratioContext ctx) {
        double ratio = Double.parseDouble(ctx.floatingp().getText());
        this.viewerConfig.setRatio(ratio);
    }

    @Override
    public void enterVm_angle(NexusFileParser.Vm_angleContext ctx) {

    }

    @Override
    public void exitVm_angle(NexusFileParser.Vm_angleContext ctx) {
        double angle = Double.parseDouble(ctx.floatingp().getText());
        this.viewerConfig.setRatio(angle);
    }

    @Override
    public void enterVm_showtrivial(NexusFileParser.Vm_showtrivialContext ctx) {

    }

    @Override
    public void exitVm_showtrivial(NexusFileParser.Vm_showtrivialContext ctx) {
        boolean st = BooleanUtils.toBoolean(ctx.boolean_option().getText());
        this.viewerConfig.setShowTrivial(st);
    }

    @Override
    public void enterVm_showrange(NexusFileParser.Vm_showrangeContext ctx) {

    }

    @Override
    public void exitVm_showrange(NexusFileParser.Vm_showrangeContext ctx) {
        boolean st = BooleanUtils.toBoolean(ctx.boolean_option().getText());
        this.viewerConfig.setShowRange(st);
    }

    @Override
    public void enterVm_showlabels(NexusFileParser.Vm_showlabelsContext ctx) {

    }

    @Override
    public void exitVm_showlabels(NexusFileParser.Vm_showlabelsContext ctx) {
        boolean sl = BooleanUtils.toBoolean(ctx.boolean_option().getText());
        this.viewerConfig.setShowLabels(sl);
    }

    @Override
    public void enterVm_colorlabels(NexusFileParser.Vm_colorlabelsContext ctx) {

    }

    @Override
    public void exitVm_colorlabels(NexusFileParser.Vm_colorlabelsContext ctx) {
        boolean cl = BooleanUtils.toBoolean(ctx.boolean_option().getText());
        this.viewerConfig.setColorLabels(cl);
    }

    @Override
    public void enterVm_leaders(NexusFileParser.Vm_leadersContext ctx) {

    }

    @Override
    public void exitVm_leaders(NexusFileParser.Vm_leadersContext ctx) {
        String leader = StringUtils.stripQuotes(ctx.identifier().getText()).toUpperCase();
        this.viewerConfig.setLeaderType(Leader.LeaderType.valueOf(leader));
    }

    @Override
    public void enterVm_leaderstroke(NexusFileParser.Vm_leaderstrokeContext ctx) {

    }

    @Override
    public void exitVm_leaderstroke(NexusFileParser.Vm_leaderstrokeContext ctx) {
        String stroke = StringUtils.stripQuotes(ctx.identifier().getText()).toUpperCase();
        this.viewerConfig.setLeaderStroke(Leader.LeaderStroke.valueOf(stroke));
    }

    @Override
    public void enterVm_leadercolor(NexusFileParser.Vm_leadercolorContext ctx) {

    }

    @Override
    public void exitVm_leadercolor(NexusFileParser.Vm_leadercolorContext ctx) {
        int r = Integer.parseInt(ctx.integer(0).getText());
        int g = Integer.parseInt(ctx.integer(1).getText());
        int b = Integer.parseInt(ctx.integer(2).getText());
        this.viewerConfig.setLeaderColor(new Color(r, g, b));
    }

    @Override
    public void enterKey_value_pair(NexusFileParser.Key_value_pairContext ctx) {

    }

    @Override
    public void exitKey_value_pair(NexusFileParser.Key_value_pairContext ctx) {

    }

    @Override
    public void enterNewtaxa_optional(NexusFileParser.Newtaxa_optionalContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitNewtaxa_optional(NexusFileParser.Newtaxa_optionalContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterProperties_splits_list(NexusFileParser.Properties_splits_listContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitProperties_splits_list(NexusFileParser.Properties_splits_listContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterTranslate_network_entry(NexusFileParser.Translate_network_entryContext ctx) {

    }

    @Override
    public void exitTranslate_network_entry(NexusFileParser.Translate_network_entryContext ctx) {
        List<String> names = new ArrayList<>();
        for (NexusFileParser.NameContext n : ctx.name()) {
            names.add(StringUtils.stripQuotes(n.getText()));
        }
        String name = org.apache.commons.lang3.StringUtils.join(names, ',');
        this.networkBuilder.getTranslate().put(Integer.parseInt(ctx.integer().getText()), name);
    }

    @Override
    public void enterNe_split(NexusFileParser.Ne_splitContext ctx) {

    }

    @Override
    public void exitNe_split(NexusFileParser.Ne_splitContext ctx) {
        this.networkBuilder.getCurrentEdge().setIdxsplit(Integer.parseInt(ctx.integer().getText()));
    }

    @Override
    public void visitTerminal(TerminalNode node) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visitErrorNode(ErrorNode node) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}

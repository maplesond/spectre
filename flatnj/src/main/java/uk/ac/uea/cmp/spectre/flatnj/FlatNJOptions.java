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
package uk.ac.uea.cmp.spectre.flatnj;

import org.apache.commons.lang3.StringUtils;
import uk.ac.earlham.metaopt.Optimiser;
import uk.ac.earlham.metaopt.OptimiserFactory;

import java.io.File;


public class FlatNJOptions {

    public static final double DEFAULT_THRESHOLD = 0.15;
    public static final String DEFAULT_OUTPUT = "flatnj.nex";

    public static final String[] ALLOWED_BLOCKS = new String[]{
            "characters",
            "data",
            "distances",
            "locations",
            "splits",
            "quadruples"
    };


    public static final String DESC_OUTPUT = "Output file - Default value (\"" + DEFAULT_OUTPUT + "\")";

    public static final String DESC_BLOCK = "If input file is a nexus file, then the user must specify which block in the file to use as input. " +
            "Allowed blocks: " + StringUtils.join(ALLOWED_BLOCKS, ", ") + ". It is recommended " +
            "that you specify this option if processing a nexus file, otherwise FlatNJ will process the first suitable block.";

    public static final String DESC_THRESHOLD = "Filtering threshold, i.e. minimal length ratio allowed for two incompatible splits. Default value (" + DEFAULT_THRESHOLD + ")";

    public static final String DESC_OPTIMISER = "The optimiser to use: " + OptimiserFactory.getInstance().listOperationalOptimisers() + " - Default value (JOptimizer)";

    public static final String DESC_STAGES = "Output nexus files at all stages in the pipeline.  Will use output file name with additional suffix for intermediary stages - Default: false";

    public static final String DESC_VERBOSE = "Whether to output detailed logging information";

    public static final String DESC_INPUT = "REQUIRED: Input is quartet-like (4-split) data, although flatnj can automatically " +
            "generate this from multiple sequence alignments, location data (X,Y coordinates) or other compatible split networks.\n" +
            "Input can either be nexus format containing: taxa, character, data, distances, locations, splits or quadruples " +
            "blocks, or a fasta-style format file containing the multiple sequence alignments.";

    private File inFile;
    private File outputFile;
    private Optimiser optimiser;
    private String block;
    private double threshold;
    private boolean saveStages;
    private boolean verbose;

    public FlatNJOptions() {
        this.inFile = null;
        this.outputFile = new File(DEFAULT_OUTPUT);
        this.optimiser = null;
        this.block = null;
        this.threshold = DEFAULT_THRESHOLD;
        this.saveStages = false;
        this.verbose = false;
    }

    public File getInFile() {
        return inFile;
    }

    public void setInFile(File inFile) {
        this.inFile = inFile;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    public Optimiser getOptimiser() {
        return optimiser;
    }

    public void setOptimiser(Optimiser optimiser) {
        this.optimiser = optimiser;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public boolean isSaveStages() {
        return saveStages;
    }

    public void setSaveStages(boolean saveStages) {
        this.saveStages = saveStages;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("Input File: " + this.inFile.getAbsolutePath());
        sb.append("Output File: " + this.outputFile.getAbsolutePath() + "\n");
        sb.append("Nexus block: " + (this.block != null ? this.block : "N/A") + "\n");
        sb.append("Threshold value: " + threshold);
        sb.append("Optimizer: " + (this.optimiser != null ? this.optimiser.getIdentifier() : "N/A"));
        sb.append("Verbose: " + this.verbose);

        return sb.toString();
    }

}

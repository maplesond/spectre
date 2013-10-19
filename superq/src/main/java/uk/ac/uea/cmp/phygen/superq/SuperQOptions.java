/*
 * Phylogenetics Tool suite
 * Copyright (C) 2013  UEA CMP Phylogenetics Group
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
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
package uk.ac.uea.cmp.phygen.superq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.phygen.core.math.optimise.Objective;
import uk.ac.uea.cmp.phygen.core.math.optimise.Optimiser;
import uk.ac.uea.cmp.phygen.core.math.optimise.OptimiserException;
import uk.ac.uea.cmp.phygen.core.math.optimise.apache.ApacheOptimiser;

import java.io.File;


public class SuperQOptions {

    private static Logger logger = LoggerFactory.getLogger(SuperQOptions.class);

    public enum InputFormat {
        SCRIPT,
        NEWICK,
        NEXUS;
    }

    private File inputFile;
    private InputFormat inputFileFormat;
    private File outputFile;
    private Optimiser primarySolver;
    private Optimiser secondarySolver;
    private Objective secondaryObjective;
    private boolean scaleInputTree;
    private Double filter;
    private boolean verbose;

    public SuperQOptions() throws OptimiserException {
        this(null, null, null,
                new ApacheOptimiser(),
                null, null,
                false, null, false);
    }

    public SuperQOptions(File inputFile, InputFormat inputFileFormat, File outputFile,
                         Optimiser primarySolver, Optimiser secondarySolver, Objective secondaryObjective,
                         boolean scaleInputTree, Double filter, boolean verbose) {

        Optimiser tempSolver = primarySolver.isOperational() ? primarySolver : null;
        if (primarySolver != tempSolver) {
            logger.warn("The solver requested: \"" + primarySolver.toString() + "\" is not supported for first optimisation step.  Using: \"" + tempSolver + "\" instead");
        }

        this.inputFile = inputFile;
        this.inputFileFormat = inputFileFormat;
        this.outputFile = outputFile;
        this.primarySolver = tempSolver;
        this.secondaryObjective = secondaryObjective;
        this.secondarySolver = secondarySolver;
        this.scaleInputTree = scaleInputTree;
        this.filter = filter;
        this.verbose = verbose;
    }

    public void createValidateConfig() {
        //check if scaling of input trees is required
        if (this.isScaleInputTree()) {
            if (!(this.getInputFileFormat() == InputFormat.NEWICK ||
                    this.getInputFileFormat() == InputFormat.SCRIPT)) {
                this.setScaleInputTree(false);
                logger.warn("Scale function can only be applied if input format is newick or script.  Running without uk.ac.uea.cmp.phygen.tools.scale function.");
            }
        }
    }


    public File getInputFile() {
        return inputFile;
    }

    public void setInputFile(File inputFile) {
        this.inputFile = inputFile;
    }

    public Objective getSecondaryObjective() {
        return secondaryObjective;
    }

    public void setSecondaryObjective(Objective secondaryObjective) {
        this.secondaryObjective = secondaryObjective;
    }

    public Double getFilter() {
        return filter;
    }

    public void setFilter(Double filter) {
        this.filter = filter;
    }

    public InputFormat getInputFileFormat() {
        return inputFileFormat;
    }

    public void setInputFileFormat(InputFormat inputFileFormat) {
        this.inputFileFormat = inputFileFormat;
    }

    public Optimiser getPrimarySolver() {
        return primarySolver;
    }

    public void setPrimarySolver(Optimiser primarySolver) {
        this.primarySolver = primarySolver;
    }

    public Optimiser getSecondarySolver() {
        return secondarySolver;
    }

    public void setSecondarySolver(Optimiser secondarySolver) {
        this.secondarySolver = secondarySolver;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    public boolean isScaleInputTree() {
        return scaleInputTree;
    }

    public void setScaleInputTree(boolean scaleInputTree) {
        this.scaleInputTree = scaleInputTree;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
}

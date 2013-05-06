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
package uk.ac.uea.cmp.phygen.superq.ui;

import org.apache.log4j.Logger;
import uk.ac.uea.cmp.phygen.superq.optimise.Objective;
import uk.ac.uea.cmp.phygen.superq.optimise.Solver;

import java.io.File;


public class SuperQOptions {
    
    static Logger logger = Logger.getLogger(SuperQOptions.class);

    private File inputFile;
    private InputFormat inputFileFormat;
    private File outputFile;
    private Solver primarySolver;
    private Solver backupSolver;    
    private Objective backupObjective;
    private boolean scaleInputTree;
    private Double filter;
    private boolean verbose;

    public SuperQOptions() {
        this(null, null, null, 
                Solver.GUROBI.getOptimiserSystem().isOperational() ? Solver.GUROBI : Solver.NNLS, 
                Solver.BEST_AVAILABLE, null, 
                false, null, false);
    }    
    
    public SuperQOptions(File inputFile, InputFormat inputFileFormat, File outputFile, 
            Solver primarySolver, Solver backupSolver, Objective backupObjective, 
            boolean scaleInputTree, Double filter, boolean verbose) {
        
        Solver tempSolver = Solver.GUROBI.getOptimiserSystem().isOperational() ? Solver.GUROBI : Solver.NNLS;
        if (primarySolver != Solver.GUROBI && primarySolver != Solver.NNLS) {
            logger.warn("The solver requested: \"" + primarySolver.toString() + "\" is not supported for first optimisation step.  Using: \"" + tempSolver + "\" instead");
        }
        else {
            tempSolver = primarySolver;
        }
        
        this.inputFile = inputFile;
        this.inputFileFormat = inputFileFormat;
        this.outputFile = outputFile;
        this.primarySolver = tempSolver;
        this.backupObjective = backupObjective;
        this.backupSolver = backupSolver;
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
                logger.warn("Scale function can only be applied if input format is newick or script.  Running without uk.ac.uea.cmp.phygen.superq.scale function.");
            }
        }
    }

    
    public File getInputFile() {
        return inputFile;
    }

    public void setInputFile(File inputFile) {
        this.inputFile = inputFile;
    }
        
    public Objective getBackupObjective() {
        return backupObjective;
    }

    public void setBackupObjective(Objective backupObjective) {
        this.backupObjective = backupObjective;
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

    public Solver getPrimarySolver() {
        return primarySolver;
    }

    public void setPrimarySolver(Solver primarySolver) {
        this.primarySolver = primarySolver;
    }
    
    public Solver getBackupSolver() {
        return backupSolver;
    }

    public void setBackupSolver(Solver backupSolver) {
        this.backupSolver = backupSolver;
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

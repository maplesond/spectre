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

package uk.ac.uea.cmp.spectre.net.netme;

import uk.ac.uea.cmp.spectre.core.util.Time;

import java.io.File;

public class NetMEOptions {

    public static final String DESC_INPUT = "The nexus file containing the distance matrix and circular ordering from a split system.";
    public static final String DESC_OUTPUT_OLS = "Whether or not NetME should output the minimum evolution tree derived from Ordinary Least Squares method of tree construction";
    public static final String DESC_OUTPUT_PREFIX = "The location and file prefix for output files.  Default: ./netme.";

    private File inputFile;
    private File outputDir;
    private boolean ols;
    private String prefix;

    public NetMEOptions() {
        this(null, false, new File(""), "netme-" + Time.createTimestamp());
    }

    public NetMEOptions(File inputFile, boolean ols, File outputDir, String prefix) {
        this.inputFile = inputFile;
        this.ols = ols;
        this.outputDir = outputDir;
        this.prefix = prefix;
    }

    public File getInputFile() {
        return inputFile;
    }

    public void setInputFile(File inputFile) {
        this.inputFile = inputFile;
    }

    public boolean isOls() {
        return ols;
    }

    public void setOls(boolean ols) {
        this.ols = ols;
    }

    public File getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(File outputDir) {
        this.outputDir = outputDir;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}

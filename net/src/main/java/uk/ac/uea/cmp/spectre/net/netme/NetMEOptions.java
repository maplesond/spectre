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

package uk.ac.uea.cmp.spectre.net.netme;

import uk.ac.uea.cmp.spectre.core.util.Time;

import java.io.File;

/**
 * Created by dan on 21/01/14.
 */
public class NetMEOptions {

    public static final String DESC_DISTANCES = "The file containing the distance data.";

    public static final String DESC_CIRCULAR_ORDERING = "The nexus file containing the circular ordering.";

    public static final String DESC_OUTPUT_DIR = "The directory to put output from this job.";

    public static final String DESC_OUTPUT_PREFIX = "The prefix to apply to all file names produced by this NetME run.  Default: netme-<timestamp>.";

    private File distancesFile;
    private File circularOrderingFile;
    private File outputDir;
    private String prefix;

    public NetMEOptions() {
        this(null, null, new File(""), "netme-" + Time.createTimestamp());
    }

    public NetMEOptions(File distancesFile, File circularOrderingFile, File outputDir, String prefix) {
        this.distancesFile = distancesFile;
        this.circularOrderingFile = circularOrderingFile;
        this.outputDir = outputDir;
        this.prefix = prefix;
    }

    public File getDistancesFile() {
        return distancesFile;
    }

    public void setDistancesFile(File distancesFile) {
        this.distancesFile = distancesFile;
    }

    public File getCircularOrderingFile() {
        return circularOrderingFile;
    }

    public void setCircularOrderingFile(File circularOrderingFile) {
        this.circularOrderingFile = circularOrderingFile;
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

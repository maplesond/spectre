package uk.ac.uea.cmp.phygen.net.netme;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import uk.ac.uea.cmp.phygen.core.io.PhygenDataType;
import uk.ac.uea.cmp.phygen.core.io.PhygenReaderFactory;
import uk.ac.uea.cmp.phygen.core.util.Time;

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

package uk.ac.uea.cmp.phygen.netme;

import org.apache.commons.cli.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.phygen.core.ds.Distances;
import uk.ac.uea.cmp.phygen.core.ds.split.CircularSplitSystem;
import uk.ac.uea.cmp.phygen.core.ds.split.SplitSystem;
import uk.ac.uea.cmp.phygen.core.io.PhygenReader;
import uk.ac.uea.cmp.phygen.core.io.PhygenReaderFactory;
import uk.ac.uea.cmp.phygen.core.io.nexus.NexusReader;
import uk.ac.uea.cmp.phygen.core.io.nexus.NexusWriter;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Dan
 * Date: 27/04/13
 * Time: 15:38
 * To change this template use File | Settings | File Templates.
 */
public class NetMECLI {

    private static Logger log = LoggerFactory.getLogger(NetMECLI.class);

    private static NetMEOptions processCmdLine(String[] args) throws ParseException {

        // Create the available options
        Options options = NetMEOptions.createOptions();

        // Parse the actual arguments
        CommandLineParser parser = new PosixParser();

        // parse the command line arguments
        CommandLine line = parser.parse(options, args);
        NetMEOptions netMEOptions = new NetMEOptions(line);

        return netMEOptions;
    }


    public static void main(String[] args) {

        try {

            // Process the command line
            NetMEOptions netMEOptions = processCmdLine(args);

            // If help was requested output that and finish before starting Spring
            if (netMEOptions.doHelp()) {
                netMEOptions.printUsage();
            }
            // Otherwise run NetMake proper
            else {

                // Load distances from input file based on file type
                PhygenReader phygenReader = netMEOptions.getDistancesFileType() != null ?
                        PhygenReaderFactory.valueOf(netMEOptions.getDistancesFileType()).create() :
                        PhygenReaderFactory.create(FilenameUtils.getExtension(netMEOptions.getDistancesFile().getName()));

                Distances distances = phygenReader.read(netMEOptions.getDistancesFile());

                // Load circular ordering from nexus file
                int[] circularOrdering = new NexusReader().extractCircOrdering(netMEOptions.getCircularOrderingFile());

                log.info("NetME: Data Loaded");

                MinimumEvolution minEvolutionTree = new MinimumEvolution(distances, circularOrdering);

                log.info("NetME: Started");

                CircularSplitSystem tree = minEvolutionTree.getMETree();


                // Write out
                NexusWriter nexusWriter = new NexusWriter();

                File minEvoFile = new File(netMEOptions.getOutputDir(), netMEOptions.getPrefix() + ".min-evo.nex");
                File origMinEvoFile = new File(netMEOptions.getOutputDir(), netMEOptions.getPrefix() + ".original-min-evo.nex");

                nexusWriter.writeTree(minEvoFile, tree, distances, null);
                nexusWriter.writeTree(origMinEvoFile, tree, distances, minEvolutionTree.getMESplitWeights());

                log.info("NetME: Finished");
            }
        }
        catch (IOException ioe) {
            log.error(ioe.getMessage(), ioe);
            System.exit(2);
        }
        catch (ParseException exp) {
            System.err.println(exp.getMessage());
            System.err.println(StringUtils.join(exp.getStackTrace(), "\n"));
            System.exit(3);
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            System.exit(6);
        }
    }

}

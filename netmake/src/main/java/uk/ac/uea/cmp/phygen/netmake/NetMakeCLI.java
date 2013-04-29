package uk.ac.uea.cmp.phygen.netmake;

import org.apache.commons.cli.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.phygen.core.ds.DistanceMatrix;
import uk.ac.uea.cmp.phygen.core.io.PhygenReader;
import uk.ac.uea.cmp.phygen.core.io.PhygenReaderFactory;
import uk.ac.uea.cmp.phygen.netmake.weighting.Weighting;
import uk.ac.uea.cmp.phygen.netmake.weighting.Weightings;

import java.io.IOException;


/**
 *
 * @author Sarah Bastkowski
 * See S. Bastkowski, 2010:
 * <I>Algorithmen zum Finden von Bäumen in Neighbor Net Netzwerken</I>
 */
public class NetMakeCLI {

    private static Logger log = LoggerFactory.getLogger(NetMakeCLI.class);

    private static NetMakeOptions processCmdLine(String[] args) throws ParseException {

        // Create the available options
        Options options = NetMakeOptions.createOptions();

        // Parse the actual arguments
        CommandLineParser parser = new PosixParser();

        // parse the command line arguments
        CommandLine line = parser.parse(options, args);
        NetMakeOptions netMakeOptions = new NetMakeOptions(line);

        return netMakeOptions;
    }


    public static void main(String[] args) {

        try {

            // Process the command line
            NetMakeOptions netMakeOptions = processCmdLine(args);

            // If help was requested output that and finish before starting Spring
            if (netMakeOptions.doHelp()) {
                netMakeOptions.printUsage();
            }
            // Otherwise run NetMake proper
            else {

                // Configure logging
                BasicConfigurator.configure();

                // Load distanceMatrix from input file based on file type
                PhygenReader phygenReader = netMakeOptions.getInputType() != null ?
                        PhygenReaderFactory.valueOf(netMakeOptions.getInputType()).create() :
                        PhygenReaderFactory.create(FilenameUtils.getExtension(netMakeOptions.getInput().getName()));

                DistanceMatrix distanceMatrix = phygenReader.read(netMakeOptions.getInput());

                // Create weighting objects
                Weighting weighting1 = Weightings.createWeighting(netMakeOptions.getWeightings1(), distanceMatrix, netMakeOptions.getTreeParam(), true);
                Weighting weighting2 = netMakeOptions.getWeightings2() != null ?
                        Weightings.createWeighting(netMakeOptions.getWeightings2(), distanceMatrix, netMakeOptions.getTreeParam(), false) :
                        null;

                // Create the configured NetMake object to process
                NetMake netMake = new NetMake(
                        distanceMatrix,
                        weighting1,
                        weighting2);

                log.info("NetMake: System configured");

                // Run NetMake and save results.
                log.info("NetMake: Started");
                netMake.process();
                netMake.save(netMakeOptions.getOutputDir(), netMakeOptions.getPrefix());

                log.info("NetMake: Finished");
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
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

package uk.ac.uea.cmp.phygen.netme;

import org.apache.commons.cli.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.phygen.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.phygen.core.ds.split.CircularOrdering;
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

                // Configure logging
                BasicConfigurator.configure();

                // Load distanceMatrix from input file based on file type
                PhygenReader phygenReader = netMEOptions.getDistancesFileType() != null ?
                        PhygenReaderFactory.valueOf(netMEOptions.getDistancesFileType()).create() :
                        PhygenReaderFactory.create(FilenameUtils.getExtension(netMEOptions.getDistancesFile().getName()));

                DistanceMatrix distanceMatrix = phygenReader.read(netMEOptions.getDistancesFile());

                // Load circular ordering from nexus file
                CircularOrdering circularOrdering = new NexusReader().extractCircOrdering(netMEOptions.getCircularOrderingFile());

                log.info("NetME: Data Loaded");
                log.info("NetME: Started");

                NetMEResult netMeResult = new NetME().calcMinEvoTree(distanceMatrix, circularOrdering);

                log.info("NetME: Finished");

                // Save result to disk
                netMeResult.save(
                        new File(netMEOptions.getOutputDir(), netMEOptions.getPrefix() + ".min-evo.nex"),
                        new File(netMEOptions.getOutputDir(), netMEOptions.getPrefix() + ".original-min-evo.nex")
                );

                log.info("NetME: Results saved");
            }
        } catch (IOException ioe) {
            log.error(ioe.getMessage(), ioe);
            System.exit(2);
        } catch (ParseException exp) {
            System.err.println(exp.getMessage());
            System.err.println(StringUtils.join(exp.getStackTrace(), "\n"));
            System.exit(3);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            System.exit(6);
        }
    }

}

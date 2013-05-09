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

package uk.ac.uea.cmp.phygen.qnet;

import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class QNetCLI {

    private static Logger log = LoggerFactory.getLogger(QNetCLI.class);

    private static QNetOptions processCmdLine(String[] args) throws ParseException {

        // Create the available options
        Options options = QNetOptions.createOptions();

        // Parse the actual arguments
        CommandLineParser parser = new PosixParser();

        // parse the command line arguments
        CommandLine line = parser.parse(options, args);
        QNetOptions QNetOptions = new QNetOptions(line);

        return QNetOptions;
    }


    public static void main(String[] args) {

        try {

            // Process the command line
            QNetOptions qNetOptions = processCmdLine(args);

            // If help was requested output that and finish before starting Spring
            if (qNetOptions.doHelp()) {
                qNetOptions.printUsage();
            }
            // Otherwise run NetMake proper
            else {

                // Configure logging
                BasicConfigurator.configure();

                // Run QNet
                QNet qnet = new QNet();
                qnet.execute(qNetOptions.getInput(), qNetOptions.isLog(), qNetOptions.getTolerance(), qNetOptions.getNnls());

                // Now what??? Presumably there is some output created?!

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
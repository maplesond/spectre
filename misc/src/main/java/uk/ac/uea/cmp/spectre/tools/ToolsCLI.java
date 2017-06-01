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

package uk.ac.uea.cmp.spectre.tools;

import org.apache.commons.cli.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import uk.ac.uea.cmp.spectre.core.ui.cli.CommandLineHelper;
import uk.ac.uea.cmp.spectre.core.util.SpiFactory;

/**
 * Created with IntelliJ IDEA.
 * User: Dan
 * Date: 27/04/13
 * Time: 15:38
 * To change this template use File | Settings | File Templates.
 */
public class ToolsCLI {

    public static final String OPT_HELP = "help";
    public static final String OPT_VERSION = "version";
    private static SpiFactory<SpectreTool> spectreToolSpiFactory;

    @SuppressWarnings("static-access")
    private static Options createOptions() {

        // Add basic options
        return new Options()
                .addOption(new Option("?", OPT_HELP, false, "Print this message."))
                .addOption(new Option("V", OPT_VERSION, false, "Print the current version."));
    }

    private static void printHelp() {
        CommandLineHelper.printHelp(
                createOptions(),
                "phygentools",
                "Miscellaneous Phylogenetic Tools\n" +
                        "A collection of tools: " + spectreToolSpiFactory.listServicesAsString());
    }


    public static void main(String[] args) {

        try {
            // First try to create the phygen tool factory, this gives us access to all the available tools
            spectreToolSpiFactory = new SpiFactory<>(SpectreTool.class);

            // Process the command line
            CommandLine cmdLine = new PosixParser().parse(createOptions(), args, true);

            if (cmdLine.getArgList().isEmpty() || cmdLine.getArgs()[0].equalsIgnoreCase(OPT_HELP)) {
                printHelp();
            }
            else if (cmdLine.hasOption(OPT_VERSION)) {
                System.out.println("spectre " + CommandLineHelper.class.getPackage().getImplementationVersion());
            }
            else {
                // Required arguments
                SpectreTool spectreTool = spectreToolSpiFactory.create(cmdLine.getArgs()[0]);

                if (spectreTool == null) {
                    throw new ParseException("Requested tool not found: " + cmdLine.getOptionValue(cmdLine.getArgs()[0]));
                } else {
                    spectreTool.execute(ArrayUtils.subarray(cmdLine.getArgs(), 1, cmdLine.getArgs().length));
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println(StringUtils.join(e.getStackTrace(), "\n"));
            System.exit(1);
        }
    }

}

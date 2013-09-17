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

package uk.ac.uea.cmp.phygen.core.ui.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA. User: Dan Date: 15/05/13 Time: 00:08 To change this template use File | Settings | File
 * Templates.
 */
public class CommandLineHelperTest {

    @Test
    public void testStartUpNoArgs() {

        Options options = createOptions();

        String[] args = new String[]{};

        CommandLineHelper.startApp(options, "jar", "project", "description", args);

        assertTrue(true);
    }

    @Test
    public void testStartUpHelp() {

        Options options = createOptions();

        String[] args = new String[]{"--help"};

        CommandLineHelper.startApp(options, "jar", "project", "description", args);
    }

    @Test
    public void testStartUpInvalid() {

        Options options = createOptions();

        String[] args = new String[]{"-i", "file.phy", "-o", "output"};

        CommandLine cl = CommandLineHelper.startApp(options, "jar", "project", "description", args);

        assertTrue(cl == null);
    }

    @Test
    public void testStartUpValid() {

        Options options = createOptions();

        String[] args = new String[]{"-i", "file.phy", "-j", "circular.nex", "-o", "output"};

        CommandLine cl = CommandLineHelper.startApp(options, "jar", "project", "description", args);

        assertTrue(cl != null);
    }


    private static Options createOptions() {

        // Options with arguments
        Option optDistancesFile = OptionBuilder.withArgName("file").isRequired().hasArg()
                .withDescription("The file containing the distance data.").create("i");

        Option optDistancesFileType = OptionBuilder.withArgName("string").hasArg()
                .withDescription("The file type of the distance data file: [NEXUS, PHYLIP].").create("t");

        Option optCircularOrderingFile = OptionBuilder.withArgName("file").isRequired().hasArg()
                .withDescription("The nexus file containing the circular ordering.").create("j");

        Option optOutputDir = OptionBuilder.withArgName("file").hasArg()
                .withDescription("The directory to put output from this job.").create("o");

        Option optOutputPrefix = OptionBuilder.withArgName("string").hasArg()
                .withDescription("The prefix to apply to all files produced by this NetME run.  Default: netme-<timestamp>.").create("p");

        // create Options object
        Options options = new Options();
        options.addOption(optDistancesFile);
        options.addOption(optDistancesFileType);
        options.addOption(optCircularOrderingFile);
        options.addOption(optOutputDir);
        options.addOption(optOutputPrefix);
        options.addOption(CommandLineHelper.HELP_OPTION);

        return options;
    }
}

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

package uk.ac.uea.cmp.phygen.tools.phycor;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import uk.ac.uea.cmp.phygen.core.io.PhygenWriterFactory;
import uk.ac.uea.cmp.phygen.tools.PhygenTool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: Dan Date: 15/05/13 Time: 22:10 To change this template use File | Settings | File
 * Templates.
 */
public class PhylipCorrector extends PhygenTool {

    private static final String OPT_INPUT = "input";
    private static final String OPT_OUTPUT_DIR = "output_dir";
    private static final String OPT_PREFIX = "prefix";


    @Override
    protected Options createInternalOptions() {

        // Create Options object
        Options options = new Options();

        options.addOption(OptionBuilder.withArgName("file").withLongOpt(OPT_INPUT).hasArg().isRequired()
                .withDescription("The phylip file to correct.").create("i"));

        options.addOption(OptionBuilder.withArgName("file").withLongOpt(OPT_OUTPUT_DIR).hasArg().isRequired()
                .withDescription("The path to the directory which will contain the output.").create("o"));

        options.addOption(OptionBuilder.withArgName("file").withLongOpt(OPT_PREFIX).hasArg().isRequired()
                .withDescription("The prefix for the output files").create("p"));

        return options;
    }

    @Override
    protected void execute(CommandLine commandLine) throws IOException {

        File inputFile = new File(commandLine.getOptionValue(OPT_INPUT));
        File outputFile = new File(commandLine.getOptionValue(OPT_OUTPUT_DIR));
        String prefix = commandLine.getOptionValue(OPT_PREFIX);

        List<String> inlines = FileUtils.readLines(inputFile);
        List<List<String>> outlines = new ArrayList<List<String>>();
        String aline = inlines.get(0);

        List<String> current = new ArrayList<String>();
        current.add(aline);

        for (int i = 1; i < inlines.size(); i++) {

            aline = inlines.get(i);

            if (aline.startsWith(" ") && i != 0)
            {
                outlines.add(current);

                current = new ArrayList<String>();
                current.add(aline);
                continue;
            }

            if (aline.lastIndexOf(" ") < 9) {
                //not sure if this cuts right
                String modifiedLine = aline.substring(0, aline.lastIndexOf(" "));
                int modifiedLineLength = modifiedLine.length();
                aline = aline.substring(aline.lastIndexOf(" ") + 1);

                for (int j = 0; j < 10 - modifiedLineLength; j++) {
                    modifiedLine += " ";
                }
                aline = modifiedLine + aline;

            }
            else if (aline.lastIndexOf(" ") > 9) {

                //not sure if this cuts right
                String modifiedLine = aline.substring(0, aline.lastIndexOf(" "));
                aline = aline.substring(aline.lastIndexOf(" ") + 1);

                modifiedLine = modifiedLine.substring(0, 10);
                aline = modifiedLine + aline;

            }
            current.add(aline);
        }

        outlines.add(current);

        int i = 1;
        for(List<String> group : outlines) {

            File groupOut = new File(outputFile, prefix + "_" + i + ".phy");
            FileUtils.writeLines(groupOut, group);

            i++;
        }
    }
}

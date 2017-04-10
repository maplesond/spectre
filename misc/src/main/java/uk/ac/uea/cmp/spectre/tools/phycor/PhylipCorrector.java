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

package uk.ac.uea.cmp.spectre.tools.phycor;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.MetaInfServices;
import uk.ac.uea.cmp.spectre.core.util.Time;
import uk.ac.uea.cmp.spectre.tools.SpectreTool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@MetaInfServices
public class PhylipCorrector extends SpectreTool {

    private static final String OPT_PREFIX = "prefix";


    @Override
    protected Options createInternalOptions() {

        // Create Options object
        Options options = new Options();

        options.addOption(OptionBuilder.withArgName("file").withLongOpt(OPT_PREFIX).hasArg()
                .withDescription("The prefix for the output files").create("o"));

        return options;
    }

    @Override
    protected void execute(CommandLine commandLine) throws IOException {

        File outputDir = new File("");
        String prefix = "corrected-" + Time.createTimestamp();

        if (commandLine.hasOption(OPT_PREFIX)) {
            File op = new File(commandLine.getOptionValue(OPT_PREFIX));
            if (op.getParentFile() != null) {
                outputDir = op.getParentFile();
            }
            prefix = op.getName();
        }

        if (commandLine.getArgs().length == 0) {
            throw new IOException("No input file specified.");
        }
        else if (commandLine.getArgs().length > 1) {
            throw new IOException("Only expected a single input file.");
        }

        File inputFile = new File(commandLine.getArgs()[0]);

        List<String> inlines = FileUtils.readLines(inputFile, "UTF-8");
        List<List<String>> outlines = new ArrayList<List<String>>();
        String aline = inlines.get(0);

        List<String> current = new ArrayList<String>();
        current.add(aline);

        for (int i = 1; i < inlines.size(); i++) {

            aline = inlines.get(i);

            if (aline.startsWith(" ") && i != 0) {
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

            } else if (aline.lastIndexOf(" ") > 9) {

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
        for (List<String> group : outlines) {

            File groupOut = new File(outputDir, prefix + "_" + i + ".phy");
            FileUtils.writeLines(groupOut, group);

            i++;
        }
    }

    @Override
    public String getName() {
        return "phycor";
    }

    @Override
    public String getPosArgs() {
        return "<phylip_file>";
    }

    @Override
    public String getDescription() {
        return "Corrects a broken phylip file.";
    }

    public static void main(String[] args) {

        try {
            new PhylipCorrector().execute(args);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println(StringUtils.join(e.getStackTrace(), "\n"));
            System.exit(1);
        }
    }
}

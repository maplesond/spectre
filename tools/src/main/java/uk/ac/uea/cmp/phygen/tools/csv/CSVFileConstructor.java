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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.uea.cmp.phygen.tools.csv;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import uk.ac.uea.cmp.phygen.tools.PhygenTool;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

/**
 *
 * @author Sarah
 */
public class CSVFileConstructor extends PhygenTool {

    private static final String OPT_NETME_DIR = "netme_dir";
    private static final String OPT_NJ_FASTME_DIR = "nj_fastme_dir";
    private static final String OPT_OUTPUT_DIR = "output_dir";
    private static final String OPT_PREFIX = "prefix";

    @Override
    protected Options createInternalOptions() {

        // Create Options object
        Options options = new Options();

        options.addOption(OptionBuilder.withArgName("file").withLongOpt(OPT_NETME_DIR).hasArg().isRequired()
                .withDescription("The NET-ME directory.").create("i"));

        options.addOption(OptionBuilder.withArgName("file").withLongOpt(OPT_NJ_FASTME_DIR).hasArg().isRequired()
                .withDescription("The NJ FastME directory.").create("j"));

        options.addOption(OptionBuilder.withArgName("file").withLongOpt(OPT_OUTPUT_DIR).hasArg().isRequired()
                .withDescription("The path to the directory which will contain the output.").create("o"));

        options.addOption(OptionBuilder.withArgName("file").withLongOpt(OPT_PREFIX).hasArg().isRequired()
                .withDescription("The prefix for the output files").create("p"));

        return options;
    }

    @Override
    protected void execute(CommandLine commandLine) throws IOException {

        File netMEDir = new File(commandLine.getOptionValue(OPT_NETME_DIR));
        File njFastMEDir = new File(commandLine.getOptionValue(OPT_NJ_FASTME_DIR));
        File outputDir = new File(commandLine.getOptionValue(OPT_OUTPUT_DIR));
        String prefix = commandLine.getOptionValue(OPT_PREFIX);

        StringBuilder NetMEContent = new StringBuilder();
        StringBuilder FastMEContent = new StringBuilder();

        //Weighting switches
        //Can be TSP, Tree, Equal, Greedy or NNet
        String Weighting = "NNet";

        //Creates Array with input files from NetME

        File[] netMEFiles = netMEDir.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return (pathname.getName().endsWith(".data.netme"));
            }
        });

        //Creates Array with input files from fastME

        File[] njFastMEFiles = njFastMEDir.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return (pathname.getName().endsWith(".data"));
            }
        });

        if (netMEFiles.length != njFastMEFiles.length) {
            throw new IOException("Different number of files in each folder");
        }

        final int N = netMEFiles.length;

        for (int k = 0; k < N; k++) {

            //Open files from the algorithms to compare included resulted ME trees
            File netMEFile = netMEFiles[k];

            File njFastMEFile = njFastMEFiles[k];

            double NetMEtreeLength = -1;

            String fastMETreeLength = "";

            List<String> njFastMEFileData = FileUtils.readLines(njFastMEFile);
            boolean found = false;
            int j = njFastMEFileData.size() - 1;


            //Recover FastME Tree length from FastME out file
            while (found == false) {
                String aLine = njFastMEFileData.get(j);
                if (aLine.contains("Performed")) {
                    aLine = njFastMEFileData.get(j - 1);
                    int idx = aLine.indexOf("is ");
                    fastMETreeLength = aLine.substring(idx + 3, aLine.length() - 1);
                    found = true;
                }
                j--;
            }

            FastMEContent.append(fastMETreeLength + ",");
            //Extracting of the tree lengths of NetME out file
            List<String> netMEFileData = FileUtils.readLines(netMEFile);

            //Another Counting Tool

            boolean foundWeighting = false;

            for (int i = 0; i < netMEFileData.size(); i++) {
                String aLine = netMEFileData.get(i);

                if (aLine.contains(Weighting)) {
                    foundWeighting = true;
                } else if (aLine.contains("tree length: ") && foundWeighting == true) {
                    int idx = aLine.indexOf(":");


                    NetMEtreeLength = Double.valueOf(aLine.substring(idx + 2, aLine.length()));

                    DecimalFormat df = new DecimalFormat("#.######");
                    df.setRoundingMode(RoundingMode.HALF_UP);
                    String num = df.format(NetMEtreeLength);

                    NetMEtreeLength = Double.parseDouble(num);

                    foundWeighting = false;
                }

            }
            NetMEContent.append(NetMEtreeLength + ",");
        }


        FileUtils.writeStringToFile(new File(outputDir, prefix + "NetME.csv"), NetMEContent.toString());
        FileUtils.writeStringToFile(new File(outputDir, prefix + "FastME.csv"), FastMEContent.toString());
    }
}

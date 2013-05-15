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
package uk.ac.uea.cmp.phygen.tools.ctl;

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
import java.util.Map;


/*
 * Method to analies the results from NetME and FastME
 * includes: average best treelength, Variance, best score method ect.
 *
 * @author sarah
 */
public class CompareTreelengths extends PhygenTool {

    private static final String OPT_NETME_DIR = "netme_dir";
    private static final String OPT_NJ_FASTME_DIR = "nj_fastme_dir";
    private static final String OPT_OUTPUT = "output";

    @Override
    protected Options createInternalOptions() {

        // Create Options object
        Options options = new Options();

        options.addOption(OptionBuilder.withArgName("file").withLongOpt(OPT_NETME_DIR).hasArg().isRequired()
                .withDescription("The NET-ME directory.").create("i"));

        options.addOption(OptionBuilder.withArgName("file").withLongOpt(OPT_NJ_FASTME_DIR).hasArg().isRequired()
                .withDescription("The NJ FastME directory.").create("j"));

        options.addOption(OptionBuilder.withArgName("file").withLongOpt(OPT_OUTPUT).hasArg().isRequired()
                .withDescription("The file which will contain the output.").create("o"));

        return options;
    }

    @Override
    protected void execute(CommandLine commandLine) throws IOException {

        File netMEDir = new File(commandLine.getOptionValue(OPT_NETME_DIR));
        File njFastMEDir = new File(commandLine.getOptionValue(OPT_NJ_FASTME_DIR));
        File output = new File(commandLine.getOptionValue(OPT_OUTPUT));

        ScoreMap treeScores = readInTreeScores(njFastMEDir.getAbsolutePath(), netMEDir.getAbsolutePath());

        StringBuilder outData = new StringBuilder();
        
        for(Map.Entry<Methods, Double[]> method : treeScores.entrySet()) {
            
            if (method.getKey() != Methods.FASTME) {
                double average = treeScores.average(method.getKey());
                int timesBetterThanFastME = treeScores.countTimesBetter(method.getKey(), Methods.FASTME);
                int timesEqualToFastME = treeScores.countTimesEqual(method.getKey(), Methods.FASTME);
                double variance = treeScores.variance(method.getKey());
                double varianceBest1 = treeScores.bestdistance(method.getKey(), Methods.FASTME);
                double varianceBest2 = treeScores.bestdistance(Methods.FASTME,method.getKey());

                outData.append(method.getKey().getDescription());
                outData.append(": ");
                outData.append(timesBetterThanFastME);
                outData.append("\n");
                outData.append("Number of equal trees: ");
                outData.append(timesEqualToFastME);
                outData.append("\n");
                outData.append("Average Score: ");
                outData.append(average);
                outData.append("\n");
                outData.append("variance: ");
                outData.append(variance);
                outData.append("\n");
                outData.append("Variance to best (Method vs FastME): ");
                outData.append(varianceBest1);
                outData.append("\n");
                outData.append("Variance to best (FastME vs Method): ");
                outData.append(varianceBest2);
                outData.append("\n");
            }
        }
        
        outData.append("Average tree length for FastME trees with NJ: " + treeScores.average(Methods.FASTME) + "\n");
        outData.append("variance: " + treeScores.variance(Methods.FASTME) + "\n");

        FileUtils.writeStringToFile(output, outData.toString());
    }

    private static ScoreMap readInTreeScores(String fastMEinDir, String NetMEinDir) throws IOException {


        File[] netMEFiles = new File(NetMEinDir).listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return (pathname.getName().endsWith(".netme"));
            }
        });

        File[] njFastMEFiles = new File(fastMEinDir).listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return (pathname.getName().endsWith(".data"));
            }
        });



        if (netMEFiles.length != njFastMEFiles.length) {
            throw new IOException("Different number of files in each folder");
        }

        final int N = netMEFiles.length;
        ScoreMap scoreMap = new ScoreMap(Methods.enabledValues(), N);
        

        for (int k = 0; k < N; k++) {

            //Open files from the algorithms to fill arrays with treelengths
            File netMEFile = netMEFiles[k];

            File njFastMEFile = njFastMEFiles[k];

            List<String> njFastMEFileData = FileUtils.readLines(njFastMEFile);
            boolean found = false;
            int j = njFastMEFileData.size() - 1;


            //Recover FastME Tree length from FastME out file
            while (found == false) {
                String aLine = njFastMEFileData.get(j);
                if (aLine.contains("Performed")) {
                    aLine = njFastMEFileData.get(j - 1);
                    int idx = aLine.indexOf("is ");
                    scoreMap.setScore(Methods.FASTME, k, Double.valueOf(aLine.substring(idx + 3, aLine.length() - 1)));
                    found = true;
                }
                j--;
            }

            //Extracting of the tree lengths of NetME out file
            List<String> netMEFileData = FileUtils.readLines(netMEFile);

            Methods lastMethod = null;
            for (int i = 0; i < netMEFileData.size(); i++) {
                
                String aLine = netMEFileData.get(i);

                if (aLine.trim().isEmpty())
                    continue;
                
                String[] words = aLine.split(" ");
                
                lastMethod = Methods.valueOf(words[0].toUpperCase());
                
                aLine = netMEFileData.get(++i);
                
                if (!lastMethod.getEnabled())
                    continue;
                
                if (aLine.contains("tree length: ")) {
                    int idx = aLine.indexOf(":");
                    double treeLength = Double.valueOf(aLine.substring(idx + 2, aLine.length()));

                    DecimalFormat df = new DecimalFormat("#.######");
                    df.setRoundingMode(RoundingMode.HALF_UP);
                    String num = df.format(treeLength);

                    treeLength = Double.parseDouble(num);

                    scoreMap.setScore(lastMethod, k, new Double (treeLength));
                }
            }
        }
        
        return scoreMap;
    }


}

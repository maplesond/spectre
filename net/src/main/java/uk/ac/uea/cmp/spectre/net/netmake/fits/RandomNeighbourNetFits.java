/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2014  UEA School of Computing Sciences
 *
 * This program is free software: you can redistribute it and/or modify it under the term of the GNU General Public
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

package uk.ac.uea.cmp.spectre.net.netmake.fits;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.split.SimpleSplitSystem;
import uk.ac.uea.cmp.spectre.core.io.PhygenReader;
import uk.ac.uea.cmp.spectre.core.io.PhygenReaderFactory;
import uk.ac.uea.cmp.spectre.core.io.nexus.NexusWriter;
import uk.ac.uea.cmp.spectre.net.netmake.weighting.Weightings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA. User: Dan Date: 30/04/13 Time: 20:20 To change this template use File | Settings | File
 * Templates.
 */
public class RandomNeighbourNetFits {

    private final static Logger log = LoggerFactory.getLogger(RandomNeighbourNetFits.class);
    private File inputFile;
    private File outputDir;
    private RandomNeighbourNetFitsConfig config;

    public RandomNeighbourNetFits(File inputFile, File outputDir, RandomNeighbourNetFitsConfig config) {
        this.inputFile = inputFile;
        this.outputDir = outputDir;
        this.config = config;
    }

    public void process() throws IOException {

        // Create an appropriate phygen reader based on the file extension
        PhygenReader phygenReader = PhygenReaderFactory.getInstance().create(FilenameUtils.getExtension(this.inputFile.getName()));

        // Load a distance matrix from the input file
        DistanceMatrix distanceMatrix = phygenReader.readDistanceMatrix(this.inputFile);

        // Generate input files from distance matrix
        List<File> generatedFiles = this.generateInputFiles();

        // Neighbor net algorithm and evaluation
        //List<Fits> fits = calculateFits(generatedFiles);
        List<Fits> fitList = new ArrayList<Fits>();

        writeSummary(new File(this.outputDir, "Summary.txt"), fitList); // writes summary to file
    }

    protected List<File> generateInputFiles() throws IOException {

        log.info("Generating input files ...");

        List<File> generatedFiles = new ArrayList<>();

        int numberOfFiles = config.calcNumberOfFilesToGenerate();
        int currentN = config.getMinN() - config.getStepping();

        for (int cnt = 0; cnt < numberOfFiles; cnt++) {

            // Increase number of taxa if samplesPerStep reached
            if ((cnt % config.getSamplesPerStep()) == 0) {
                currentN += config.getStepping();
            }

            File outFile = new File(this.outputDir, "input/InData_" + cnt + ".nex");
            DistanceMatrix distanceMatrix = config.getDistanceGenerator().generateDistances(currentN);

            new NexusWriter().writeDistanceMatrix(outFile, distanceMatrix);

            generatedFiles.add(outFile);
        }

        log.info("{0}" + " input files" + " written to: {1}input ...", new Object[]{numberOfFiles, this.outputDir.getAbsolutePath()});

        return generatedFiles;
    }

    protected double getFitStatistic(DistanceMatrix distanceMatrix, SimpleSplitSystem aNetwork) {
        /*double fit = 0.;
        int t = distanceMatrix.size();
        double sum1 = 0.;
        double sum2 = 0.;

        for (int i = 0; i < t; i++) {
            for (int j = i + 1; j < t; j++) {
                sum2 += aNetwork.getDistance(i, j) * aNetwork.getDistance(i, j);
            }
        }


        for (int i = 0; i < t; i++) {
            for (int j = i + 1; j < t; j++) {

                final double distIJ = distanceMatrix.getDistance(i, j);
                sum1 += (distIJ - aNetwork.getDistance(i, j))
                        * (distIJ - aNetwork.getDistance(i, j));
            }
        }

        fit = 1. - sum1 / sum2;

        return fit;*/

        return 0.0;
    }

   /* protected double[][] calculateFits(List<File> generatedFiles) throws IOException {

        final int numberOfFiles = generatedFiles.size();
        double[][] fits = new double[numberOfFiles][4]; // fileIndex x Weighting

        for (int i = 0; i < numberOfFiles; i++) {

            // Get handle on generated file to process
            File generatedFile = generatedFiles.get(i);

            calculateFits(generatedFile);
        }
        return fits;
    } */

   /* protected Fits calculateFits(File file) throws IOException {

        // Load file
        DistanceMatrix inputData = new NexusReader().read(file);

        Fits fits = new Fits(file);

        for (Weightings weighting : Weightings.getValuesExceptGreedyME()) {

            NetMake netMake = new NetMake(inputData, weighting.create(inputData.size()));
            netMake.process();
            CircularSplitSystem network = netMake.getNetwork();
            network.calculateTreeWeighting();
            DistanceMatrix calculatedDistances = network.generateDistanceMatrix();

            fits.addFit(weighting.toString(), (getFitStatistic(calculatedDistances, network));
        }
        log.info("Calculated fits for \"{0}\" ...", file.getAbsolutePath());
    } */

    /**
     * @param fitsList
     */
    protected void writeSummary(File summaryFile, List<Fits> fitsList) throws IOException {

        int numberOfFiles = fitsList.size();

        Map<String, StringBuilder> weightingStatsMap = new LinkedHashMap<>();

        for (Weightings weighting : Weightings.getValuesExceptGreedyME()) {
            weightingStatsMap.put(weighting.toString(), new StringBuilder());
        }

        StringBuilder fileContent = new StringBuilder("Fit Statistics\n-----------\n\n");

        fileContent.append("Randomized input satisfies metric constraints: ").append(config.getDistanceGenerator().getClass().getSimpleName()).append("\n\n");

        // Appending weighting summary
        int index = 0;
        for (int currentN = config.getMinN(); currentN <= config.getMaxN(); currentN += config.getStepping()) {
            int offset = (currentN - config.getMinN()) / config.getStepping() * config.getSamplesPerStep();

            for (Map.Entry<String, Double> fit : fitsList.get(index++).getFits().entrySet()) {
                double mean = 0.;

                for (int i = offset; i < offset + config.getSamplesPerStep(); i++) {
                    mean += fit.getValue();
                }

                mean /= (double) config.getSamplesPerStep();

                String meanInfo = "\t" + currentN + " Taxa: " + mean + "\n";

                weightingStatsMap.get(fit.getKey()).append(meanInfo);
            }
        }

        fileContent.append("Weighting Summary\n-----------------\n");

        for (Map.Entry<String, StringBuilder> weightingEntry : weightingStatsMap.entrySet()) {
            fileContent.append("Mean Fits (").append(config.getSamplesPerStep()).append(" sample(s)) for ").append(weightingEntry.getKey()).append(" Weighting:\n");
            fileContent.append(weightingEntry.getValue().toString());
        }
        fileContent.append("\n");

        /* Appending detailed summary */
        fileContent.append("Detailed Summary (").append(numberOfFiles).append(" sample(s))\n----------------\n");
        for (Fits fits : fitsList) {
            fileContent.append(fits.getFile().getAbsolutePath()).append("\t");

            for (Map.Entry<String, Double> fit : fits.getFits().entrySet()) {
                fileContent.append(fit.getKey()).append(": ").append(fit.getValue()).append("\t");
            }

            fileContent.append("\n");
        }

        /* Write summary to file */
        FileUtils.writeStringToFile(summaryFile, fileContent.toString());

        log.info("Summary written to \"Summary.txt\" ...");
    }

    private class Fits {

        private File file;
        private Map<String, Double> fits;

        public Fits(File file) {
            this.file = file;
            this.fits = new LinkedHashMap<>();
        }

        public void addFit(String name, double value) {
            this.fits.put(name, value);
        }

        public File getFile() {
            return file;
        }

        public Map<String, Double> getFits() {
            return fits;
        }

    }
}

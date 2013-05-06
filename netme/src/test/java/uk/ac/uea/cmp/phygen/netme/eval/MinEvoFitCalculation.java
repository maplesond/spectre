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

package uk.ac.uea.cmp.phygen.netme.eval;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.uea.cmp.phygen.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.phygen.core.ds.split.CircularOrdering;
import uk.ac.uea.cmp.phygen.core.io.PhygenReaderFactory;
import uk.ac.uea.cmp.phygen.core.io.nexus.NexusReader;
import uk.ac.uea.cmp.phygen.netmake.NetMake;
import uk.ac.uea.cmp.phygen.netmake.NetMakeResult;
import uk.ac.uea.cmp.phygen.netmake.weighting.GreedyMEWeighting;
import uk.ac.uea.cmp.phygen.netmake.weighting.Weightings;
import uk.ac.uea.cmp.phygen.netme.NetME;
import uk.ac.uea.cmp.phygen.netme.NetMEResult;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: Dan
 * Date: 30/04/13
 * Time: 20:12
 * To change this template use File | Settings | File Templates.
 */
public class MinEvoFitCalculation {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File beesFile = FileUtils.toFile(MinEvoFitCalculation.class.getResource("/bees.nex"));


    protected void runMinEvoFitCalc(File inFile, File outFile, File nnFile) throws IOException {

        DistanceMatrix inputData = PhygenReaderFactory.create(FilenameUtils.getExtension(inFile.getName())).read(inFile);

        StringBuilder fitInfo = new StringBuilder();

        // Loop through all the circular orderings we have available and calculate tree length with net me before
        // appending output.
        for(Map.Entry<String, CircularOrdering> entry : createCircularOrderings(inputData, nnFile).entrySet()) {

            fitInfo.append(calcTreeLengthUsingNetME(entry.getKey(), inputData, entry.getValue()));
        }

        // Write output file to disk
        FileUtils.writeStringToFile(outFile, fitInfo.toString());
    }

    protected Map<String, CircularOrdering> createCircularOrderings(DistanceMatrix distanceMatrix, File nnFile) throws IOException {

        Map<String, CircularOrdering> coMap = new LinkedHashMap<>();

        // Run through all weighting types and create circular ordering using input distance matrix to output string via
        // NetMake
        for (Weightings weightings : Weightings.getValuesExceptGreedyME()) {

            // Run netmake with the distance matrix and this specific weighting and retrieve the network
            NetMakeResult netMakeResult = new NetMake(distanceMatrix, weightings.create(distanceMatrix.size())).process();
            coMap.put(weightings.toString(), netMakeResult.getNetwork().getCircularOrdering());

            // Create circular ordering for GreedyME
            NetMakeResult greedyNetMake = new NetMake(distanceMatrix, new GreedyMEWeighting(distanceMatrix), weightings.create(distanceMatrix.size())).process();
            coMap.put("GREEDY_ME_" + weightings.toString(), greedyNetMake.getNetwork().getCircularOrdering());
        }

        // Create circular ordering from neighbour net file if available
        if (nnFile != null) {
            coMap.put("NNET", new NexusReader().extractCircOrdering(nnFile));
        }

        // Create random circular ordering
        coMap.put("RANDOM", CircularOrdering.createRandomCircularOrdering(distanceMatrix.size()));

        return coMap;
    }


    /**
     * Run netME with the distance matrix and the generated network's circular ordering to get the minumum evolution
     * tree length, weights calculated by OLS method.
     *
     * @param type
     * @param distanceMatrix
     * @param circularOrdering
     * @return
     */
    protected String calcTreeLengthUsingNetME(String type, DistanceMatrix distanceMatrix, CircularOrdering circularOrdering) {

        // Run NET ME
        NetMEResult netMEResult = new NetME().calcMinEvoTree(distanceMatrix, circularOrdering);

        // Calculate tree length
        double treeLength = netMEResult.getMeTree().getTreeSplitWeights().calcTreeLength();

        // Generate output string
        return "\n" + type + "\nME tree length: " + treeLength;
    }


    @Test
    public void testBees() throws IOException {

        File outDir = temporaryFolder.newFolder("test1");

        this.runMinEvoFitCalc(beesFile, outDir, null);

        assertTrue(true);
    }


}

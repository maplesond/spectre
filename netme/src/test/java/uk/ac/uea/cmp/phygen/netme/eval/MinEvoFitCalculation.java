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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.uea.cmp.phygen.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.phygen.core.ds.split.CircularOrdering;
import uk.ac.uea.cmp.phygen.core.io.nexus.NexusReader;
import uk.ac.uea.cmp.phygen.core.io.phylip.PhylipReader;
import uk.ac.uea.cmp.phygen.netmake.NetMake;
import uk.ac.uea.cmp.phygen.netmake.weighting.Weightings;
import uk.ac.uea.cmp.phygen.netme.NetME;
import uk.ac.uea.cmp.phygen.netme.NetMEResult;

import java.io.File;
import java.io.IOException;

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

    private File beesFile = FileUtils.toFile(MinEvoFitCalculation.class.getResource("/eval/bees.nex"));


    protected void runMinEvoFitCalc(File inFile, File outFile, File nnFile) throws IOException {

        DistanceMatrix inputData = new PhylipReader().read(inFile);

        StringBuilder fitInfo = new StringBuilder();

        // Run through all weighting types and append tree length produced using input distance matrix to output string
        for (Weightings weightings : Weightings.values()) {

            // Run netmake with the distance matrix and this specific weighting and retrieve the network
            NetMake netMake = new NetMake(inputData, weightings.create(inputData.size()));
            netMake.process();
            CircularOrdering circOrdering = netMake.getNetwork().getCircularOrdering();

            // Calculate tree length and generate output string
            String output = calcTreeLengthUsingNetME(weightings.toString(), inputData, circOrdering);

            // Add to output
            fitInfo.append(output);
        }


        //OutFile from NNet to provide circular ordering
        if (nnFile != null) {

            // Get circular ordering from neighbor net file
            CircularOrdering circOrdering = new NexusReader().extractCircOrdering(nnFile);

            // Calculate tree length and generate output string
            String output = calcTreeLengthUsingNetME("NNET", inputData, circOrdering);

            // Add to output
            fitInfo.append(output);
        }


        // Create random circular ordering
        CircularOrdering circOrdering = CircularOrdering.createRandomCircularOrdering(inputData.size());

        // Calculate tree length and generate output string
        String output = calcTreeLengthUsingNetME("RANDOM_NET", inputData, circOrdering);

        // Add to output
        fitInfo.append(output);

        // Write output file to disk
        FileUtils.writeStringToFile(outFile, fitInfo.toString());
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

    }


}

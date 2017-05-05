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

package uk.ac.uea.cmp.spectre.net.netmake;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;
import uk.ac.uea.cmp.spectre.core.ds.Sequences;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceCalculatorFactory;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.network.Network;
import uk.ac.uea.cmp.spectre.core.ds.network.draw.PermutationSequenceDraw;
import uk.ac.uea.cmp.spectre.core.ds.split.SpectreSplitSystem;
import uk.ac.uea.cmp.spectre.core.ds.split.SplitBlock;
import uk.ac.uea.cmp.spectre.core.ds.split.SplitSystem;
import uk.ac.uea.cmp.spectre.core.ds.split.circular.ordering.CircularOrderingAlgorithms;
import uk.ac.uea.cmp.spectre.core.ds.split.circular.ordering.CircularOrderingCreator;
import uk.ac.uea.cmp.spectre.core.ds.split.circular.ordering.nm.NetMakeCircularOrderer;
import uk.ac.uea.cmp.spectre.core.ds.split.circular.ordering.nm.weighting.Weighting;
import uk.ac.uea.cmp.spectre.core.ds.split.circular.ordering.nm.weighting.Weightings;
import uk.ac.uea.cmp.spectre.core.ds.split.circular.ordering.nn.NeighborNetImpl;
import uk.ac.uea.cmp.spectre.core.io.SpectreReader;
import uk.ac.uea.cmp.spectre.core.io.SpectreReaderFactory;
import uk.ac.uea.cmp.spectre.core.ui.gui.RunnableTool;
import uk.ac.uea.cmp.spectre.core.ui.gui.StatusTracker;

import java.io.IOException;

/**
 * Performs the NeighborNet algorithm to retrieve a split system and a circular
 * order.
 *
 * @author Sarah Bastkowski
 *         See Sarah Bastkowski, 2010:
 *         <I>Algorithmen zum Finden von Bäumen in Neighbor Net Netzwerken</I>
 */
public class NetMake extends RunnableTool {

    private static Logger log = LoggerFactory.getLogger(NetMake.class);

    private NetMakeOptions options;

    public NetMake() {
        this(new NetMakeOptions());
    }

    public NetMake(NetMakeOptions options) {
        this(options, null);
    }

    public NetMake(NetMakeOptions options, StatusTracker statusTracker) {
        super(statusTracker);
        this.options = options;
    }

    public NetMakeOptions getOptions() {
        return options;
    }

    public void setOptions(NetMakeOptions options) {
        this.options = options;
    }


    public NetMakeResult execute(DistanceMatrix distanceMatrix, CircularOrderingCreator circularOrderingCreator) {

        log.info("Calculating circular ordering:");
        IdentifierList permutation = circularOrderingCreator.createCircularOrdering(distanceMatrix);

        log.info("... By ID  : " + permutation.toString(IdentifierList.IdentifierFormat.BY_ID));
        log.info("... By Name: " + permutation.toString(IdentifierList.IdentifierFormat.BY_NAME));

        log.info("Creating splits network");

        SplitSystem networkSS = new SpectreSplitSystem(distanceMatrix, permutation, SpectreSplitSystem.LeastSquaresCalculator.CIRCULAR);

        log.info("Creating network");
        Network network = new PermutationSequenceDraw(networkSS).createOptimisedNetwork();

        SplitSystem treeSS = null;
        Network tree = null;

        if (circularOrderingCreator.createsTreeSplits() && this.getOptions().getOutputTree() != null) {

            log.info("Creating splits tree");

            SplitSystem treeSplits = circularOrderingCreator.getTreeSplits();

            organiseSplits(treeSplits, permutation);

            // Create tree and network split systems
            treeSS = new SpectreSplitSystem(distanceMatrix, permutation, SpectreSplitSystem.LeastSquaresCalculator.TREE_IN_CYCLE, treeSplits);

            log.info("Creating tree");
            tree = new PermutationSequenceDraw(networkSS).createOptimisedNetwork();
        }


        return new NetMakeResult(distanceMatrix, treeSS, tree, networkSS, network);
    }


    protected void organiseSplits(SplitSystem splits, IdentifierList permutation) {

        IdentifierList permutationInvert = permutation.reverseOrdering();

        for (int i = 0; i < splits.size(); i++) {

            SplitBlock sb = splits.get(i).getASide();

            int k = permutationInvert.get(sb.getFirst() - 1).getId();
            int l = permutationInvert.get(sb.getLast() - 1).getId();

            if (l < k) {
                sb.reverse();
            }
        }
    }


    private void notifyUser(String message) {
        log.info(message);
        this.trackerInitUnknownRuntime(message);
    }

    @Override
    public void run() {

        try {

            // Start timing
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            notifyUser("Loading distance matrix from: " + this.options.getInput().getAbsolutePath());

            // Load distanceMatrix from input file based on file type
            // Get a handle on the spectre factory
            SpectreReaderFactory factory = SpectreReaderFactory.getInstance();

            // Setup appropriate reader to input file based on file type
            SpectreReader spectreReader = factory.create(FilenameUtils.getExtension(this.options.getInput().getName()));

            // Work out if we have a distance matrix already or if we have to calculate it from MSA
            DistanceMatrix distanceMatrix = null;
            Sequences seqs = null;
            if (spectreReader.getIdentifier() == "FASTA") {
                seqs = spectreReader.readAlignment(this.options.getInput());
            }
            else if (spectreReader.getIdentifier() == "NEXUS") {

                // First try to use distance matrix
                distanceMatrix = spectreReader.readDistanceMatrix(this.options.getInput());

                // If not present then look for alignments
                if (distanceMatrix == null) {
                    seqs = spectreReader.readAlignment(this.options.getInput());
                }
            }
            else {
                distanceMatrix = spectreReader.readDistanceMatrix(this.options.getInput());
            }

            if (distanceMatrix == null && seqs != null) {
                log.info("Loaded MSA containing " + seqs.size() + " sequences of length " + seqs.getSeq(0).length());

                DistanceCalculatorFactory dcf = DistanceCalculatorFactory.valueOf(this.options.getDc().toUpperCase().trim());
                distanceMatrix = dcf.createDistanceMatrix(seqs);
                log.info("Distance matrix calculated from MSA using " + dcf.name());
            }
            else if (distanceMatrix == null) {
                throw new IOException("Could not find or generate distance matrix from input");
            }

            log.info("Distance matrix contains " + distanceMatrix.size() + " taxa.");

            // Set circular ordering algorithm
            CircularOrderingAlgorithms coa = CircularOrderingAlgorithms.valueOf(this.options.getCoAlg().toUpperCase());
            CircularOrderingCreator coc = null;

            log.info("Circular ordering algorithm: " + coa.toString());

            if (coa == CircularOrderingAlgorithms.NETMAKE) {

                // Create weighting objects
                Weighting weighting1 = Weightings.createWeighting(this.options.getWeighting1(), distanceMatrix, this.options.getTreeParam(), true);
                Weighting weighting2 = (this.options.getWeighting2() != null && !this.options.getWeighting2().equalsIgnoreCase("none")) ?
                        Weightings.createWeighting(this.options.getWeighting2(), distanceMatrix, this.options.getTreeParam(), false) :
                        null;

                log.info("Weightings configured.");
                log.info("          - Weighting 1: " + weighting1.toString());
                log.info("          - Weighting 2: " + (weighting2 == null ? "null" : weighting2.toString()));
                log.info("Running netmake circular ordering algorithm in mode: " + NetMakeCircularOrderer.getRunMode(weighting1, weighting2).toString());

                coc = new NetMakeCircularOrderer(weighting1, weighting2);
            }
            else if (coa == CircularOrderingAlgorithms.NEIGHBORNET) {

                coc = new NeighborNetImpl();
            }

            notifyUser("Executing netmake");

            NetMakeResult result = this.execute(distanceMatrix, coc);

            notifyUser("Saving results to disk");

            // Save results.
            result.save(this.options.getOutputNetwork(), this.options.getOutputTree());

            this.trackerFinished(true);

            // Print run time on screen
            stopWatch.stop();
            log.info("Completed Successfully - Total run time: " + stopWatch.toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            this.setError(e);
            this.trackerFinished(false);
        } finally {
            this.notifyListener();
        }
    }
}

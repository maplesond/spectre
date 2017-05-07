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

package uk.ac.uea.cmp.spectre.flatnj;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;
import uk.ac.uea.cmp.spectre.core.ds.Locations;
import uk.ac.uea.cmp.spectre.core.ds.Sequences;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.network.Network;
import uk.ac.uea.cmp.spectre.core.ds.network.draw.PermutationSequenceDraw;
import uk.ac.uea.cmp.spectre.core.ds.quad.quadruple.*;
import uk.ac.uea.cmp.spectre.core.ds.split.SpectreSplitSystem;
import uk.ac.uea.cmp.spectre.core.ds.split.SplitSystem;
import uk.ac.uea.cmp.spectre.core.ds.split.flat.PermutationSequence;
import uk.ac.uea.cmp.spectre.core.ds.split.flat.PermutationSequenceFactory;
import uk.ac.uea.cmp.spectre.core.io.fasta.FastaReader;
import uk.ac.uea.cmp.spectre.core.io.nexus.Nexus;
import uk.ac.uea.cmp.spectre.core.io.nexus.NexusWriter;
import uk.ac.uea.cmp.spectre.core.ui.gui.RunnableTool;
import uk.ac.uea.cmp.spectre.core.ui.gui.StatusTrackerWithView;

import java.io.File;
import java.io.IOException;

/**
 * FlatNJ (FlatNetJoining) is a program for computing split networks that allow
 * for interior vertices to be labeled while being (almost) planar.
 *
 * @author balvociute
 */
public class FlatNJ extends RunnableTool {

    private static Logger log = LoggerFactory.getLogger(FlatNJ.class);

    private FlatNJOptions options;

    public FlatNJ(FlatNJOptions options) {
        this(options, null);
    }

    public FlatNJ(FlatNJOptions options, StatusTrackerWithView tracker) {
        super(tracker);
        this.options = options;
    }

    private void notifyUser(String message) {
        log.info(message);
        this.trackerInitUnknownRuntime(message);
    }

    @Override
    public void run() {
        try {

            // Check we have something sensible to work with
            if (this.options == null) {
                throw new IOException("Must specify a valid set of parameters to control FlatNJ.");
            }

            File inFile = options.getInFile();
            File outFile = options.getOutputFile();

            if (inFile == null || !inFile.exists()) {
                throw new IOException("Must specify a valid input file.");
            }

            if (outFile == null || outFile.isDirectory()) {
                throw new IOException("Must specify a valid path for output file.");
            }

            // Print the validated options
            log.info("Recognised these options:\n\n" +
                    this.options.toString());



            // Get a shortcut to runtime object for checking memory usage
            Runtime rt = Runtime.getRuntime();

            // Start timing
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            log.info("Starting job");
            log.debug("FREE MEM - at start: " + rt.freeMemory());

            notifyUser("Loading input data from: " + inFile);

            this.continueRun();

            // Work out input file type
            String extension = FilenameUtils.getExtension(inFile.getName());

            IdentifierList taxa = null;
            Sequences sequences = null;
            DistanceMatrix distanceMatrix = null;
            Locations locations = null;
            SplitSystem ss = null;
            QuadrupleSystem qs = null;

            if (extension.equalsIgnoreCase("fa") || extension.equalsIgnoreCase("faa") || extension.equalsIgnoreCase("fas") || extension.equalsIgnoreCase("fasta")) {
                sequences = new FastaReader().readAlignment(inFile);
                taxa = new IdentifierList(sequences.getTaxaLabels());
                log.info("Extracted " + taxa.size() + " sequences");
            } else if (extension.equalsIgnoreCase("nex") || extension.equalsIgnoreCase("nexus") || extension.equalsIgnoreCase("4s")) {

                // Read taxa block regardless

                Nexus nexus = new uk.ac.uea.cmp.spectre.core.io.nexus.NexusReader().parse(inFile);

                taxa = nexus.getTaxa();

                if (taxa == null) {
                    throw new IOException("No labels for the taxa were indicated");
                }

                if (options.getBlock() == null) {
                    log.info("Nexus file provided as input but no nexus block specified by user.  Will use first suitable block found in nexus file");

                    // First check for existing quadruple system
                    if (nexus.getQuadruples() != null) {
                        qs = nexus.getQuadruples();
                        log.info("Detected and loaded Quadruples Block containing " + qs.getnQuadruples() + " quadruples");
                    } else {

                        // Next check for location data
                        if (nexus.getLocations() != null) {
                            locations = nexus.getLocations();
                            log.info("Detected and loaded Locations Block containing " + locations.size() + " locations");
                        }
                        else {
                            // Next check for split system
                            ss = nexus.getSplitSystem();
                            if (ss != null) {
                                log.info("Detected and loaded Split System Block containing " + ss.getNbSplits() + " splits over " + ss.getNbTaxa() + " taxa");
                            } else {
                                // Next look for MSA
                                if (nexus.getAlignments() != null) {
                                    sequences = nexus.getAlignments();
                                    log.info("Detected and loaded Sequences Block.  Found " + sequences.size() + " sequences");
                                }
                                else {
                                    throw new IOException("Couldn't find a valid block in nexus file.");
                                }
                            }
                        }
                    }
                } else {
                    String blockLowerCase = options.getBlock().toLowerCase();

                    log.info("Searching for " + blockLowerCase + " in nexus file.");
                    boolean loaded = false;

                    if (blockLowerCase.contentEquals("data") || blockLowerCase.contentEquals("characters")) {
                        sequences = nexus.getAlignments();
                        loaded = true;
                    } else if (blockLowerCase.contentEquals("locations")) {
                        locations = nexus.getLocations();
                        loaded = true;
                    } else if (blockLowerCase.contentEquals("splits")) {
                        ss = nexus.getSplitSystem();
                        loaded = true;
                    } else if (blockLowerCase.contentEquals("quadruples")) {
                        qs = nexus.getQuadruples();
                        loaded = true;
                    }

                    if (!loaded) {
                        throw new IOException("Couldn't loaded requested block from nexus file.");
                    }
                }
            }

            rt.gc();
            log.debug("FREE MEM - after loading input: " + rt.freeMemory());

            this.continueRun();

            // Compute the Quadruple system from alternate information if we didn't just load it from disk
            if (qs == null) {

                notifyUser("Computing system of 4-splits (quadruples)");

                QSFactory qsFactory = null;

                if (sequences != null) {
                    qsFactory = new QSFactoryAlignment(sequences, distanceMatrix);
                } else if (locations != null) {
                    qsFactory = new QSFactoryLocation(locations);
                } else if (ss != null) {
                    qsFactory = new QSFactorySplitSystem(ss);
                } else {
                    throw new IOException("No suitable data found to create quadruple system");
                }

                if (qsFactory == null) {
                    throw new IOException("Error creating quadruple system factory");
                }

                qs = qsFactory.computeQS(true);

                if (options.isSaveStages()) {

                    File quadFile = new File(outFile.getParentFile(), outFile.getName() + ".quads.nex");
                    log.info("Saving quadruples to: " + quadFile.getAbsolutePath());
                    NexusWriter writer = new NexusWriter();
                    writer.appendHeader();
                    writer.appendLine();
                    writer.append(taxa);
                    writer.appendLine();
                    writer.append(qs);
                    writer.write(quadFile);
                }
            }

            qs.subtractMin();   //Subtract minimal weights. They will be added back when the network is computed.
            log.info("Computed " + qs.getnQuadruples() + " quadruples");

            rt.gc();
            log.debug("FREE MEM - after creating quadruples: " + rt.freeMemory());

            this.continueRun();

            notifyUser("Computing ordering");
            PermutationSequence ps = new PermutationSequenceFactory().computePermutationSequence(qs);

            rt.gc();
            log.debug("FREE MEM - after computing ordering: " + rt.freeMemory());

            this.continueRun();

            // Updates Permutation Sequence permutationSequence
            notifyUser("Weighting flat split system");
            new WeightCalculatorImpl(ps, qs).fitWeights(options.getOptimiser());

            rt.gc();
            log.debug("FREE MEM - after weighting: " + rt.freeMemory());

            this.continueRun();

            log.info("Filtering splits below threshold: " + options.getThreshold());
            ps.filterSplits(options.getThreshold());

            this.continueRun();

            notifyUser("Finalising splits system");
            ps.setTaxaNames(taxa.getNames());
            SplitSystem fss = new SpectreSplitSystem(ps).makeCanonical();
            log.info("Split system contains " + fss.getNbSplits() + " splits");
            //ss.setActive(ps.getActive());  // Do we want to reset this from active (extra trivial splits would have been added in the constructor)
            fss.incTaxId();

            if (options.isSaveStages()) {
                File ssFile = new File(outFile.getParentFile(), outFile.getName() + ".splits.nex");
                log.info("Saving splits to: " + ssFile.getAbsolutePath());
                new NexusWriter().writeSplitSystem(ssFile, fss);
            }

            this.continueRun();

            notifyUser("Computing network");
            PermutationSequenceDraw psDraw = new PermutationSequenceDraw(ps.getSequence(),
                    ps.getSwaps(),
                    ps.getWeights(),
                    ps.getActive(),
                    ps.getTrivial());
            Network network = psDraw.createOptimisedNetwork();

            NexusWriter writer = new NexusWriter();
            writer.appendHeader();
            writer.appendLine();
            writer.append(taxa);
            writer.appendLine();
            writer.append(fss);
            writer.appendLine();
            writer.append(network);
            writer.write(outFile);

            log.info("Saving complete nexus file to: " + outFile.getAbsolutePath());
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

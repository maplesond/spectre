/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2015  UEA School of Computing Sciences
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
package uk.ac.uea.cmp.spectre.qtools.scale;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.tgac.metaopt.*;
import uk.ac.uea.cmp.spectre.core.ds.quad.quartet.QuartetSystemList;
import uk.ac.uea.cmp.spectre.tools.SpectreTool;

import java.io.File;
import java.io.IOException;

public class Scaling extends SpectreTool {

    private static Logger log = LoggerFactory.getLogger(Scaling.class);

    private static final String OPT_OUTPUT_PREFIX = "output";
    private static final String OPT_OPTIMISER = "optimiser";

    @Override
    public String getName() {
        return "scaler";
    }

    @Override
    public String getDescription() {
        return "Scales input trees to a given range based on their quartet weights (uses \"qmaker\" to get the quartet weights from the trees)";
    }

    @Override
    protected Options createInternalOptions() {

        // Create Options object
        Options options = new Options();

        options.addOption(OptionBuilder.withArgName("file").withLongOpt(OPT_OUTPUT_PREFIX).hasArg()
                .withDescription("The output prefix path, which will be used for all output files").create("o"));

        options.addOption(OptionBuilder.withArgName("string").withLongOpt(OPT_OPTIMISER).hasArg()
                .withDescription("The optimiser to use: " + OptimiserFactory.getInstance().listOperationalOptimisers()).create("p"));

        return options;
    }


    // **** Execution methods ****

    /**
     * Executes scaler using command line input
     *
     * @param commandLine
     * @throws IOException
     */
    @Override
    protected void execute(CommandLine commandLine) throws IOException {

        // All options are required
        File outputPrefix = new File(commandLine.getOptionValue(OPT_OUTPUT_PREFIX));

        String[] args = commandLine.getArgs();

        if (args == null || args.length == 0) {
            throw new IOException("You must specify at least one input file");
        }

        File[] inputFiles = new File[args.length];
        for (int i = 0; i < args.length; i++) {
            inputFiles[i] = new File(args[i].trim());
        }

        // Create the optimiser to use and execute scaler, wrap any optimiser exceptions as io exceptions.
        Optimiser optimiser;
        try {
            optimiser = commandLine.hasOption(OPT_OPTIMISER) ?
                    OptimiserFactory.getInstance().createOptimiserInstance(
                            commandLine.getOptionValue(OPT_OPTIMISER), Objective.ObjectiveType.QUADRATIC) :
                    null;

            this.execute(inputFiles, outputPrefix, optimiser);

        } catch (OptimiserException oe) {
            throw new IOException(oe);
        }
    }

    /**
     * Scales a list of quartet networks loaded from a file and then writes scaled networks to file.
     *
     * @param inputFiles   The file to load
     * @param optimiser    The optimiser to use for scaling
     * @param outputPrefix The location for output files to be created
     * @throws OptimiserException
     * @throws IOException
     */
    public void execute(File[] inputFiles, File outputPrefix, Optimiser optimiser) throws OptimiserException, IOException {

        this.execute(inputFiles, optimiser).saveQWeights(outputPrefix);
    }


    /**
     * Scales a list of quartet networks loaded from a file
     *
     * @param inputFiles The files to load
     * @param optimiser  The optimiser to use for scaling
     * @return A scaled list of quartet networks.
     * @throws OptimiserException
     * @throws IOException
     */
    public QuartetSystemList execute(File[] inputFiles, Optimiser optimiser) throws OptimiserException, IOException {

        return this.execute(new QuartetSystemList(inputFiles), optimiser);
    }

    /**
     * Scales a list of quartet networks loaded from a file
     *
     * @param qList The list of quartet systems to scale
     * @param optimiser  The optimiser to use for scaling
     * @return A scaled list of quartet networks.
     * @throws OptimiserException
     */
    public QuartetSystemList execute(QuartetSystemList qList, Optimiser optimiser) throws OptimiserException {

        // Computes the matrix of coefficients
        ScalingMatrix matrix = new ScalingMatrix(qList);

        if (matrix.isPerfectMatch()) {
            double[] solution = matrix.computeFactorsDirectly();
            if (matrix.isPerfectMatch()) {
                qList.scaleWeights(solution);
            }
        }

        if (!matrix.isPerfectMatch()) {

            matrix.recomputeMatrix();

            //Create the problem from the coefficients and run the solver to get the optimal solution
            Solution solution = new ScalingOptimiser(optimiser).optimise(matrix.getMatrix());

            // Updates quartet weights
            qList.scaleWeights(solution.getVariableValues());
        }

        // Just return this as a convenience to the client.
        return qList;
    }



    /**
     * Main entry point for the scaling program
     *
     * @param args
     */
    public static void main(String[] args) {

        try {
            new Scaling().execute(args);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println(StringUtils.join(e.getStackTrace(), "\n"));
            System.exit(1);
        }
    }
}

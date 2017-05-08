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

package uk.ac.uea.cmp.spectre.tools.ssc;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.MetaInfServices;
import uk.ac.uea.cmp.spectre.core.ds.Sequences;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceCalculatorFactory;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.distance.RandomDistanceGenerator;
import uk.ac.uea.cmp.spectre.core.ds.split.SplitSystem;
import uk.ac.uea.cmp.spectre.core.io.SpectreReader;
import uk.ac.uea.cmp.spectre.core.io.SpectreReaderFactory;
import uk.ac.uea.cmp.spectre.core.io.SpectreWriter;
import uk.ac.uea.cmp.spectre.core.io.SpectreWriterFactory;
import uk.ac.uea.cmp.spectre.core.io.nexus.NexusReader;
import uk.ac.uea.cmp.spectre.core.util.Time;
import uk.ac.uea.cmp.spectre.tools.SpectreTool;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@MetaInfServices
public class SplitSystemComparisonTool extends SpectreTool {

    @Override
    protected Options createInternalOptions() {

        // Create Options object
        Options options = new Options();

        return options;
    }

    private static class Entry {
        String filename;
        int nbTaxa;
        int nbSplits;
        boolean circular;
        boolean compatible;
        boolean full;
        boolean match;

        public Entry() {
            this.filename = "";
            this.nbTaxa = 0;
            this.nbSplits = 0;
            this.circular = false;
            this.compatible = false;
            this.full = false;
            this.match = false;
        }

        public static String header() {
            return StringUtils.join(new String[]{"filename","nb_taxa","nb_splits","circular","compatible","full","match"}, "\t");
        }

        @Override
        public String toString() {
            return StringUtils.join(new String[]{
                    this.filename,
                    Integer.toString(this.nbTaxa),
                    Integer.toString(this.nbSplits),
                    Boolean.toString(this.circular),
                    Boolean.toString(this.compatible),
                    Boolean.toString(this.full),
                    Boolean.toString(this.match)
            }, "\t");
        }
    }


    @Override
    protected void execute(CommandLine commandLine) throws IOException {

        final int nb_files = commandLine.getArgs().length;

        if (nb_files < 1) {
            throw new IOException("Did not specify at least one nexus files containing split systems to analyse / compare.");
        }

        List<Entry> results = new ArrayList<>();

        SplitSystem ref = null;
        for(int i = 0; i < nb_files; i++) {
            File file = new File(commandLine.getArgs()[i]);
            SplitSystem ss = new NexusReader().readSplitSystem(file).makeCanonical();

            Entry e = new Entry();
            e.filename = file.getName();

            if (i == 0) {
                ref = ss;
                e.filename += " (ref)";
            }

            e.nbTaxa = ss.getNbTaxa();
            e.nbSplits = ss.getNbSplits();
            e.circular = ss.isCircular();
            e.compatible = ss.isCompatible();
            e.full = ss.isFull();
            e.match = ref.equals(ss);
            results.add(e);
        }

        System.out.println(Entry.header());
        for(Entry e : results) {
            System.out.println(e.toString());
        }
    }

    @Override
    public String getName() {
        return "splitcompare";
    }

    @Override
    public String getPosArgs() {
        return "(<nexus_file>)+";
    }


    @Override
    public String getDescription() {
        return "Compares two or more nexus files containing split systems.  The first file is considered the reference that " +
                "all further files are compared to.";
    }

    public static void main(String[] args) {

        try {
            new SplitSystemComparisonTool().execute(args);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println(StringUtils.join(e.getStackTrace(), "\n"));
            System.exit(1);
        }
    }
}

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

package uk.ac.uea.cmp.phygen.core.ui.cli;

import org.apache.commons.cli.*;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA. User: Dan Date: 14/05/13 Time: 21:28 To change this template use File | Settings | File
 * Templates.
 */
public abstract class PhygenTool {

    public static final String OPT_HELP = "help";

    protected abstract Options createInternalOptions();
    protected abstract void execute(CommandLine commandLine) throws IOException;

    public Options createOptions() {
        Options options = this.createInternalOptions();
        options.addOption(new Option("?", OPT_HELP, false, "Print this message."));
        return options;
    }

    public void printUsage() {
        new HelpFormatter().printHelp(this.getClass().getSimpleName(), createOptions());
    }

    public CommandLine parse(String[] args) throws ParseException {
        return new PosixParser().parse(createOptions(), args);
    }

    public void execute(String[] args) throws IOException {

        try {
            CommandLine commandLine = this.parse(args);

            if (commandLine.hasOption(OPT_HELP)) {
                printUsage();
            }
            else {
                this.execute(commandLine);
            }
        }
        catch (ParseException p) {
            System.err.println(p.getMessage());
            printUsage();
        }
    }
}

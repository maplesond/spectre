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

package uk.ac.uea.cmp.phygen.tools;

import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import uk.ac.uea.cmp.phygen.tools.chopper.Chopper;
import uk.ac.uea.cmp.phygen.tools.convertor.Convertor;
import uk.ac.uea.cmp.phygen.tools.phycor.PhylipCorrector;
import uk.ac.uea.cmp.phygen.tools.rdg.RandomDistanceGeneratorTool;
import uk.ac.uea.cmp.phygen.tools.scale.Scaling;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: Dan Date: 12/05/13 Time: 19:12 To change this template use File | Settings | File
 * Templates.
 */
public enum ToolsMode {

    RANDOM_DISTANCE_GENERATOR {
        @Override
        public void execute(String[] args) throws IOException, ParseException {
            new RandomDistanceGeneratorTool().execute(args);
        }

        @Override
        public String getAlias() {
            return "RDG";
        }
    },
    PHYLIP_CORRECTOR {
        @Override
        public void execute(String[] args) throws IOException, ParseException {
            new PhylipCorrector().execute(args);
        }

        @Override
        public String getAlias() {
            return "PC";
        }
    },
    CHOPPER {
        @Override
        public void execute(String[] args) throws IOException, ParseException {
            new Chopper().execute(args);
        }

        @Override
        public String getAlias() {
            return "CHP";
        }
    },
    SCALE {
        @Override
        public void execute(String[] args) throws IOException, ParseException {
            new Scaling().execute(args);
        }

        @Override
        public String getAlias() {
            return "SCL";
        }
    },
    CONVERTOR {
        @Override
        public void execute(String[] args) throws IOException, ParseException {
            new Convertor().execute(args);
        }

        @Override
        public String getAlias() {
            return "CNV";
        }
    };

    public String toListString() {
        return this.name() + " (" + this.getAlias() + ")";
    }

    public abstract void execute(String[] args) throws IOException, ParseException;

    public abstract String getAlias();


    // Alias handling
    public static ToolsMode parseName(String name) {

        ToolsMode regular = null;

        try {
            regular = ToolsMode.valueOf(name);
        } catch (IllegalArgumentException e) {
            regular = null;
        }

        if (regular != null) {
            return regular;
        }

        for (ToolsMode tm : ToolsMode.values()) {
            if (tm.getAlias().equalsIgnoreCase(name)) {
                return tm;
            }
        }

        return null;
    }

    public static String listTools() {

        List<String> toolStrings = new ArrayList<String>();

        for (ToolsMode mode : ToolsMode.values()) {
            toolStrings.add(mode.toListString());
        }

        return "[" + StringUtils.join(toolStrings, ", ") + "]";
    }
}

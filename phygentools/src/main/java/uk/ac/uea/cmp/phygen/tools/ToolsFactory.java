/*
 * Phylogenetics Tool suite
 * Copyright (C) 2013  UEA CMP Phylogenetics Group
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

package uk.ac.uea.cmp.phygen.tools;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 28/10/13
 * Time: 22:12
 * To change this template use File | Settings | File Templates.
 */
public class ToolsFactory {

    private static ToolsFactory instance;
    private ServiceLoader<PhygenTool> loader;

    public ToolsFactory() {
        this.loader = ServiceLoader.load(PhygenTool.class);
    }

    public static synchronized ToolsFactory getInstance() {
        if (instance == null) {
            instance = new ToolsFactory();
        }
        return instance;
    }

    public PhygenTool createOptimiserInstance(String identifier) {

        for (PhygenTool phygenTool : loader) {

            if (phygenTool.acceptsIdentifier(identifier)) {

                // Create the appropriate optimiser, based on the objective if required
                return phygenTool;
            }
        }

        return null;
    }

    public String listTools() {

        List<String> toolStrings = new ArrayList<String>();

        for (PhygenTool phygenTool : loader) {
            toolStrings.add(phygenTool.getName());
        }

        return "[" + StringUtils.join(toolStrings, ", ") + "]";
    }

    public String listToolsDescriptions() {

        List<String> toolStrings = new ArrayList<String>();

        for (PhygenTool phygenTool : loader) {
            toolStrings.add(phygenTool.toString() + ": " + phygenTool.getDescription());
        }

        return StringUtils.join(toolStrings, "\n");
    }
}

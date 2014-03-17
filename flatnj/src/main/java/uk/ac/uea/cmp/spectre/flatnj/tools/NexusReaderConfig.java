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

package uk.ac.uea.cmp.spectre.flatnj.tools;

import uk.ac.uea.cmp.spectre.flatnj.fdraw.ViewerConfig;

import java.awt.*;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 *
 * @author balvociute
 */
public class NexusReaderConfig<E> extends NexusReader
{
    String leader = null;
    boolean labels = true;
    boolean trivial = true;
    boolean colorLabels = false;
    String leaderStroke = null;
    Color leaderColor = null;
    Set<Integer> fixed = new HashSet();
    Double ratio = null;

    public NexusReaderConfig()
    {
        block = "viewer";
    }

    @Override
    protected void initializeDataStructures(Dimensions dimensions)
    {
        
    }

    @Override
    protected void parseLine(Format format)
    {
        scannerLC = new Scanner(lineLC);
        matched = scannerLC.findInLine("showtrivial\\s*=\\s*(\\S+)");
        if(matched != null)
        {
            if(Boolean.parseBoolean(scannerLC.match().group(1)))
            {
                trivial = true;
            }
            else
            {
                trivial = false;
            }
        }
        scannerLC = new Scanner(lineLC);
        matched = scannerLC.findInLine("showlabels\\s*=\\s*(\\S+)");
        if(matched != null)
        {
            if(Boolean.parseBoolean(scannerLC.match().group(1)))
            {
                labels = true;
            }
            else
            {
                labels = false;
            }
        }
        scannerLC = new Scanner(lineLC);
        matched = scannerLC.findInLine("colorlabels\\s*=\\s*(\\S+)");
        if(matched != null)
        {
            if(Boolean.parseBoolean(scannerLC.match().group(1)))
            {
                colorLabels = true;
            }
            else
            {
                colorLabels = false;
            }
        }
        scannerLC = new Scanner(lineLC);
        matched = scannerLC.findInLine("leaders\\s*=\\s*(\\S+)");
        if(matched != null)
        {
            leader = scannerLC.match().group(1);
        }
        scannerLC = new Scanner(lineLC);
        matched = scannerLC.findInLine("leaderstroke\\s*=\\s*(\\S+)");
        if(matched != null)
        {
            leaderStroke = scannerLC.match().group(1);
        }
        scannerLC = new Scanner(lineLC);
        matched = scannerLC.findInLine("leadercolor\\s*=\\s*(\\d+)\\s+(\\d+)\\s+(\\d+)");
        if(matched != null)
        {
            leaderColor = new Color(Integer.parseInt(scannerLC.match().group(1)),
                                    Integer.parseInt(scannerLC.match().group(2)),
                                    Integer.parseInt(scannerLC.match().group(3)));
        }
        scannerLC = new Scanner(lineLC);
        matched = scannerLC.findInLine("fix\\s*(\\d+)");
        if(matched != null)
        {
            fixed.add(Integer.parseInt(scannerLC.match().group(1)) - 1);
        }
        scannerLC = new Scanner(lineLC);
        matched = scannerLC.findInLine("ratio\\s*=\\s*(\\.+)");
        if(matched != null)
        {
            try
            {
                ratio = Double.parseDouble(scannerLC.match().group(1));
            }
            catch(NumberFormatException nfe){}
        }
    }

    @Override
    protected Object createObject(Dimensions dimensions, Cycle cycle, Draw draw)
    {
        if(leader != null)
        {
            return new ViewerConfig(new Dimension(dimensions.width, dimensions.height),
                                    leader,
                                    leaderStroke,
                                    leaderColor,
                                    trivial,
                                    labels,
                                    colorLabels,
                                    fixed,
                                    ratio);
        }
        else
        {
            return null;
        }
    }
    
}

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

package uk.ac.uea.cmp.phygen.flatnj.ds;

import java.util.List;

/**
 *
 * @author balvociute
 */
public class Locations
{
    Location[] locations;

    public Locations(Location[] locations)
    {
        this.locations = locations;
    }

    public Locations(List<Location> locationsList)
    {
        locations = new Location[locationsList.size()];
        for (int i = 0; i < locations.length; i++)
        {
            locations[i] = locationsList.get(i);
        }
    }

    public Location[] getLocations()
    {
        return locations;
    }

    public void print()
    {
        for (int i = 0; i < locations.length; i++)
        {
            System.out.println(locations[i].toString());
        }
    }

    public Integer size()
    {
        return locations.length;
    }

    public String[] getTaxa()
    {
        String[] labels = null;
        if(locations[0].getLabel() != null)
        {
            labels = new String[locations.length];
            for (int i = 0; i < locations.length; i++)
            {
                labels[i] = locations[i].getLabel();
            }
        }
        return labels;
    }
    
    public DistanceMatrix toDistanceMatrix()
    {
        double[][] matrix = new double[size()][size()];
        for (int i = 0; i < locations.length; i++)
        {
            matrix[i][i] = 0.0;
            for (int j = i + 1; j < locations.length; j++)
            {
                double d = locations[i].distanceTo(locations[j]);
                matrix[i][j] = d;
                matrix[j][i] = d;
            }
            
        }
        DistanceMatrix dm = new DistanceMatrix(matrix);
        return dm;
    }
    
}
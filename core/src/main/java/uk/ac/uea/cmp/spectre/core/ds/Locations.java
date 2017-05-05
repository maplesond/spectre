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

package uk.ac.uea.cmp.spectre.core.ds;

import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.distance.FlexibleDistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ui.gui.geom.IndexedPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * @author balvociute
 */
public class Locations extends ArrayList<IndexedPoint> {

    public Locations() {
        super();
    }

    public Locations(List<IndexedPoint> locationsList) {
        for (int i = 0; i < locationsList.size(); i++) {
            IndexedPoint ip = locationsList.get(i);
            this.add(new IndexedPoint(ip.getId(), ip.getX(), ip.getY(), ip.getLabel()));
        }
    }

    public String[] getTaxa() {
        String[] labels = null;
        if (this.get(0).getLabel() != null) {
            labels = new String[this.size()];
            for (int i = 0; i < this.size(); i++) {
                labels[i] = this.get(i).getLabel();
            }
        }
        return labels;
    }

    public DistanceMatrix toDistanceMatrix() {
        double[][] matrix = new double[size()][size()];
        IdentifierList taxa = new IdentifierList(size());
        for (int i = 0; i < this.size(); i++) {
            matrix[i][i] = 0.0;
            for (int j = i + 1; j < this.size(); j++) {
                double d = this.get(i).distanceSq(this.get(j));
                matrix[i][j] = d;
                matrix[j][i] = d;
            }

        }
        return new FlexibleDistanceMatrix(taxa, matrix);
    }

}
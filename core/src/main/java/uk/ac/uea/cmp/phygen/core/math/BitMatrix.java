/**
 * Super Q - Computing super networks from partial trees. Copyright (C) 2012 UEA
 * CMP Phylogenetics Group.
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.uea.cmp.phygen.core.math;

import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA. User: Analysis Date: 2004-jun-09 Time: 22:55:09 To
 * change this template use Options | File Templates.
 */
public class BitMatrix {

    public BitMatrix(int rows, int columns) {

        this.rows = rows;
        this.columns = columns;
        data = new boolean[rows][columns];

    }

    public BitMatrix(boolean[][] newData, int rows, int columns) {

        this.rows = rows;
        this.columns = columns;
        data = newData;

    }

    public double elementAt(int row, int column) {

        if (data[row][column]) {

            return 1.0;

        } else {

            return 0.0;

        }

    }

    public void setElementAt(int row, int column, int value) {

        if (value == 1) {

            data[row][column] = true;

        } else {

            data[row][column] = false;

        }

    }

    public BitMatrix cut(LinkedList P) {

        boolean[][] newData = new boolean[rows][P.size()];

        int q = 0;

        for (int c = 0; c < columns; c++) {

            if (P.contains(new Integer(c))) {

                for (int r = 0; r < rows; r++) {

                    newData[r][q] = data[r][c];

                }

                q++;

            }

        }

        return new BitMatrix(newData, rows, P.size());

    }
    boolean[][] data;
    int rows, columns;
}

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

package uk.ac.uea.cmp.spectre.viewer;

import uk.ac.uea.cmp.spectre.core.ds.network.draw.Translocator;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author balvociute
 */
public class LabelPlacementOptimizer8Points extends LabelPlacementOptimizer4Points {

    public LabelPlacementOptimizer8Points() {
        pn = 8;

        preferedQuarters = new int[8][];
        preferedQuarters[0] = new int[]{0, 7, 1, 6, 2, 5, 3, 4};
        preferedQuarters[1] = new int[]{1, 0, 2, 7, 3, 6, 4, 5};
        preferedQuarters[2] = new int[]{2, 3, 1, 4, 0, 5, 7, 6};
        preferedQuarters[3] = new int[]{3, 2, 4, 1, 5, 0, 6, 7};
        preferedQuarters[4] = new int[]{4, 3, 5, 2, 6, 1, 7, 0};
        preferedQuarters[5] = new int[]{5, 6, 4, 7, 3, 0, 2, 1};
        preferedQuarters[6] = new int[]{6, 7, 5, 0, 4, 1, 3, 2};
        preferedQuarters[7] = new int[]{7, 0, 6, 1, 5, 2, 4, 3};
    }

    @Override
    protected void fillInCandidatePositions(ViewerLabel l, int i, Box[] candidatePositions) {
        ViewerPoint p = l.p;

        int j = 0;
        double xPlus = p.getX() + p.width / 2 + defaultOffsetX;
        double xMinus = p.getX() - p.width / 2 - defaultOffsetX - l.label.getWidth();
        double xMiddle = (xPlus + xMinus) / 2;
        double yPlus = p.getY() - p.height / 2 - defaultOffsetY;
        double yMinus = p.getY() + p.height / 2 + defaultOffsetY + l.label.getHeight();
        double yMiddle = (yPlus + yMinus) / 2;

        candidatePositions[i * pn + j++] = new Box(xPlus, yPlus, l.label.getWidth(), l.label.getHeight(), l);
        candidatePositions[i * pn + j++] = new Box(xMiddle, yPlus, l.label.getWidth(), l.label.getHeight(), l);
        candidatePositions[i * pn + j++] = new Box(xMinus, yPlus, l.label.getWidth(), l.label.getHeight(), l);
        candidatePositions[i * pn + j++] = new Box(xMinus, yMiddle, l.label.getWidth(), l.label.getHeight(), l);
        candidatePositions[i * pn + j++] = new Box(xMinus, yMinus, l.label.getWidth(), l.label.getHeight(), l);
        candidatePositions[i * pn + j++] = new Box(xMiddle, yMinus, l.label.getWidth(), l.label.getHeight(), l);
        candidatePositions[i * pn + j++] = new Box(xPlus, yMinus, l.label.getWidth(), l.label.getHeight(), l);
        candidatePositions[i * pn + j] = new Box(xPlus, yMiddle, l.label.getWidth(), l.label.getHeight(), l);

    }

    @Override
    protected void setQuarters(Collection<ViewerLabel> labels, int midX, int midY) {
        Iterator<ViewerLabel> labelIt = labels.iterator();

        double x1b = midX * 1.5;
        double y1b = 2 * midY;
        double x1t = midX * 0.5;
        double y1t = 0;

        double a1 = Translocator.a(x1b, y1b, x1t, y1t);
        double b1 = y1b - x1b * a1;

        double x2b = 2 * midX;
        double y2b = midY * 1.5;
        double x2t = 0;
        double y2t = midY * 0.5;

        double a2 = Translocator.a(x2b, y2b, x2t, y2t);
        double b2 = y2b - x2b * a2;

        double x3b = 2 * midX;
        double y3b = midY * 0.5;
        double x3t = 0;
        double y3t = midY * 1.5;

        double a3 = Translocator.a(x3b, y3b, x3t, y3t);
        double b3 = y3b - x3b * a3;

        double x4b = midX * 1.5;
        double y4b = 0;
        double x4t = midX * 0.5;
        double y4t = 2 * midY;

        double a4 = Translocator.a(x4b, y4b, x4t, y4t);
        double b4 = y4b - x4b * a4;

        while (labelIt.hasNext()) {
            ViewerLabel l = labelIt.next();
            ViewerPoint p = l.p;
            p.quarterNo = 0;

            double x = p.getX();
            double y = p.getY();

            double y1 = a1 * x + b1;
            double y2 = a2 * x + b2;
            double y3 = a3 * x + b3;
            double y4 = a4 * x + b4;

            if (y < y1) {
                if (y > y2) {
                    p.quarterNo = 6;
                } else if (y > y3) {
                    p.quarterNo = 7;
                } else if (y > y4) {
                    p.quarterNo = 0;
                } else {
                    p.quarterNo = 1;
                }
            } else {
                if (y > y4) {
                    p.quarterNo = 5;
                } else if (y > y3) {
                    p.quarterNo = 4;
                } else if (y > y2) {
                    p.quarterNo = 3;
                } else {
                    p.quarterNo = 2;
                }
            }
        }
    }

}

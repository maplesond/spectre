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

package uk.ac.uea.cmp.spectre.core.ds.network.draw;

import uk.ac.uea.cmp.spectre.core.ds.network.Edge;

import java.util.LinkedList;
import java.util.List;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author balvociute
 */
public class AngleCalculatorMaximalArea extends AngleCalculatorSimple {

    @Override
    protected double computeOptimal(List<NetworkBox> boxesSorted) {

        double gap = 0.1;

        double A = 0;
        double B = 0;

        Double minUp = null;
        Double minDown = null;

        for (int i = 0; i < boxesSorted.size(); i++) {
            NetworkBox b = boxesSorted.get(i);
            Edge e1 = b.getE1();
            Edge e2 = b.getE2();

            double alphaSi = getAngle(e1.getTop(), e1.getBot(), e2.getBot());

            minDown = (minDown == null || minDown > alphaSi) ? alphaSi : minDown;
            minUp = (minUp == null || minUp > Math.PI - alphaSi) ? Math.PI - alphaSi : minUp;

            //double a = Math.sqrt((e1.top.x - e1.bot.x) * (e1.top.x - e1.bot.x) + (e1.top.y - e1.bot.y) * (e1.top.y - e1.bot.y));
            double c = Math.sqrt((e1.getBot().getX() - e2.getBot().getX()) * (e1.getBot().getX() - e2.getBot().getX()) +
                    (e1.getBot().getY() - e2.getBot().getY()) * (e1.getBot().getY() - e2.getBot().getY()));
            A += c * Math.cos(alphaSi);
            B += c * Math.sin(alphaSi);

        }
        double deltaAlpha = Math.atan(A / B);

        if (deltaAlpha < 0 && Math.abs(deltaAlpha) > minDown - gap) {
            if (minDown > gap) {
                deltaAlpha = (minDown - gap) * Math.signum(deltaAlpha);
            } else {
                deltaAlpha = 0;
            }
        } else if (deltaAlpha > 0 && deltaAlpha > minUp - gap) {
            if (minUp > gap) {
                deltaAlpha = minUp - gap;
            } else {
                deltaAlpha = 0;
            }
        }

        return deltaAlpha;
    }

    @Override
    protected double computeOptimalCompatible(double bAngleLeft, double bAngleRight) {
        double middle = (bAngleLeft + bAngleRight) / 2;
        return middle - bAngleRight;
    }
}

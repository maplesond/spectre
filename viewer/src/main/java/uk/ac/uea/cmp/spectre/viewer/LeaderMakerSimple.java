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

package uk.ac.uea.cmp.spectre.viewer;

import java.util.*;

/**
 * @author balvociute
 */
class LeaderMakerSimple implements LeaderMaker {

    public LeaderMakerSimple() {
    }

    @Override
    public void placeLeaders(Map<Integer, LabelPlacementOptimizer4Points.Box> fixedPositions,
                             ViewerLabel[] labels,
                             Window drawing) {
        Iterator<Integer> idIt = fixedPositions.keySet().iterator();
        List<Integer[]> slotsLeft = new LinkedList();
        List<Integer[]> slotsRight = new LinkedList();

        List<ViewerLabel> labelsLeft = new LinkedList<>();
        List<ViewerLabel> labelsRight = new LinkedList<>();

        while (idIt.hasNext()) {
            int id = idIt.next();
            if (labels[id].label.movable) {
                LabelPlacementOptimizer4Points.Box b1 = fixedPositions.get(id);
                b1.overlapScore = 0;
                Iterator<LabelPlacementOptimizer4Points.Box> boxIt2 = fixedPositions.values().iterator();
                while (boxIt2.hasNext()) {
                    LabelPlacementOptimizer4Points.Box b2 = boxIt2.next();
                    if (b2 != b1) {
                        b1.overlapScore += LabelPlacementOptimizer4Points.computeOverlap(b1, b2);
                    }
                }
                if (b1.getScore() > 40) {
                    ViewerPoint p = labels[id].p;
                    double xPosition = ((p.getX() < drawing.getCentrePoint().x) ? 10 : drawing.getWidth() - 10 - labels[id].label.getWidth());
                    double yPosition = p.getY() + labels[id].label.getHeight() / 2;

                    List<Integer[]> slots = (xPosition == 10) ? slotsLeft : slotsRight;
                    List<ViewerLabel> labelList = (xPosition == 10) ? labelsLeft : labelsRight;

                    labelList.add(p.l);

                    labels[id].computeOffsets(xPosition, yPosition, Arrays.asList(labels));
                }
            }
        }

        placeLabels(labelsLeft, labels);
        placeLabels(labelsRight, labels);

    }

    private void sortList(List<ViewerLabel> labelsLeft) {
        for (int i = 0; i < labelsLeft.size(); i++) {
            for (int j = i + 1; j < labelsLeft.size(); j++) {
                ViewerLabel l1 = labelsLeft.get(i);
                ViewerLabel l2 = labelsLeft.get(j);
                if (l1 != l2 && l1.p.getY() > l2.p.getY()) {
                    labelsLeft.remove(l2);
                    labelsLeft.add(j, l1);
                    labelsLeft.remove(i);
                    labelsLeft.add(i, l2);
                }
            }
        }
    }

    private void placeLabels(List<ViewerLabel> labelList, ViewerLabel[] labels) {
        sortList(labelList);
        Double index = 0.0;
        for (int i = 0; i < labelList.size(); i++) {
            ViewerLabel l = labelList.get(i);
            if (index < l.getY() - l.label.getHeight()) {
                index = l.getY();
            } else {
                index += l.label.getHeight();
            }
            l.computeOffsets(l.getX(), index, Arrays.asList(labels));
        }
    }

}

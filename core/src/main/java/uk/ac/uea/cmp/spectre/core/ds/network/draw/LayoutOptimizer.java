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

import uk.ac.uea.cmp.spectre.core.ds.network.Network;
import uk.ac.uea.cmp.spectre.core.ds.network.Vertex;

import java.util.LinkedList;
import java.util.TreeSet;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */

/**
 * @author balvociute
 */
public class LayoutOptimizer {

    public Vertex optimize(Vertex v, PermutationSequenceDraw ps, Network network) {
        //Compute split system
        SplitSystemDraw ss = new SplitSystemDraw(ps);

        //Collect all vertices
        LinkedList<Vertex> vertices = v.collectVertices();

        //Initialize array used to store indices of active splits only
        int[] activeSplits = Collector.collectIndicesOfActiveSplits(ps);
        //Initialize array to keep edges involved in each split
        TreeSet[] splitedges = Collector.collectEgdesForTheSplits(ps, v);

        AngleCalculator angleCalculatorSimple = new AngleCalculatorSimple();
        AngleCalculator angleCalculatorPrecise = new AngleCalculatorMaximalArea();

        //Two types of box openers are used. Simple one tries to open boxes by
        //no more than certain constant angle, whereas precise one uses angle
        //which maximises certain function.
        BoxOpener boxOpenerSimple = new BoxOpener(angleCalculatorSimple);
        BoxOpener boxOpenerPrecise = new BoxOpener(angleCalculatorPrecise);

        //CompatibleCorrectors are used to change angles for compatible splits.
        CompatibleCorrector compatibleCorrectorSimple = new CompatibleCorrector(angleCalculatorSimple);
        CompatibleCorrector compatibleCorrectorPrecise = new CompatibleCorrector(angleCalculatorPrecise);

        //First step of the layout optimisation consists of a few iterations of
        //simple box opening followed by a few of more precise one. These two 
        //are repeated a few times. This king of strategy proved to produce
        //networks that look much better rather than using simple or precise
        //alone.

        int trivial = 2 + network.getTrivialEdges().size() / 10;
        int precise = 2 + ps.getNtaxa() / 20;
        int iterations = 2 + ps.getNtaxa() / 50;
        int finish = precise + 5;
        int compatible = 1;// + ps.ntaxa / 40;

        //compatibleCorrectorPrecise.moveTrivial(v, 2 + ((int)(ps.ntaxa * 0.03)), null);
        for (int j = 0; j < iterations; j++) {
            //System.err.print("iteration precise: ");
            for (int i = 0; i < precise; i++) {
                //System.err.print((i + 1) + " ");
                boxOpenerPrecise.openIncompatible(activeSplits, v, ss, vertices, splitedges, network);
            }
            for (int i = 0; i < compatible; i++) {
                //System.err.print((i + 1) + " ");
                compatibleCorrectorPrecise.moveCompatible(v, 1, network);
            }
            //System.err.print("moving trivial: ");
            compatibleCorrectorPrecise.moveTrivial(v, trivial, network);

            //System.err.println();
        }
        //System.err.println("Finishing: ");
        for (int i = 0; i < finish; i++) {
            //System.out.println((i+1) + " ");
            boxOpenerPrecise.openOneIncompatible(activeSplits, v, ss, vertices, splitedges, network);
        }
        return v;
    }
}
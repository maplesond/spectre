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

package uk.ac.uea.cmp.spectre.core.io.nexus.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.spectre.core.ds.quad.quadruple.Quadruple;
import uk.ac.uea.cmp.spectre.core.ds.quad.quadruple.QuadrupleSystem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 01/12/13
 * Time: 21:33
 * To change this template use File | Settings | File Templates.
 */
public class NexusQuadruplesBuilder {

    private static Logger log = LoggerFactory.getLogger(NexusQuadruplesBuilder.class);

    private int expectedNbTaxa;
    private int expectedNbQuadruples;
    private List<Quadruple> quads;


    public NexusQuadruplesBuilder() {
        this.expectedNbTaxa = 0;
        this.expectedNbQuadruples = 0;
        this.quads = new ArrayList<>();
    }


    public QuadrupleSystem createQuadrupleSystem() {

        if (expectedNbQuadruples != this.quads.size()) {
            throw new IllegalStateException("Number of detected quadruples (" + this.quads.size() + ") is not the number we expected (" + this.expectedNbQuadruples + ")");
        }

        QuadrupleSystem qs = new QuadrupleSystem(this.expectedNbTaxa);

        for (Quadruple q : quads) {
            qs.add(q);
        }

        return qs;
    }

    public int getExpectedNbTaxa() {
        return expectedNbTaxa;
    }

    public void setExpectedNbTaxa(int expectedNbTaxa) {
        this.expectedNbTaxa = expectedNbTaxa;
    }

    public int getExpectedNbQuadruples() {
        return expectedNbQuadruples;
    }

    public void setExpectedNbQuadruples(int expectedNbQuadruples) {
        this.expectedNbQuadruples = expectedNbQuadruples;
    }

    public List<Quadruple> getQuads() {
        return quads;
    }

    public void setQuads(List<Quadruple> quads) {
        this.quads = quads;
    }

    public void addQuad(Quadruple q) {
        this.quads.add(q);
    }
}

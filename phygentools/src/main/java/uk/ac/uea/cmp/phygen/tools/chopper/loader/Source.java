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
package uk.ac.uea.cmp.phygen.tools.chopper.loader;

import uk.ac.uea.cmp.phygen.core.ds.Taxa;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetWeights;
import uk.ac.uea.cmp.phygen.core.util.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: Analysis Date: 2004-jul-12 Time: 00:35:38 To
 * change this template use Options | File Templates.
 */
public interface Source extends Service {

    public void load(File file, double weight) throws IOException;

    // make sure qWs exist
    public void process();

    // these are the qWs
    public List<QuartetWeights> getQuartetWeights();

    // list of weights of qWs
    public List<Double> getWeights();

    public double getWSum();

    // note: translate BEFORE processing
    public void translate(Taxa taxa);

    /**
     * straightforward union of taxon names, stored in the input, so clone if necessary
     * @return
     */
    public void addTaxa(Taxa taxa);

    /**
     * Gets the taxa list for each source in the input
     * @return
     */
    public List<Taxa> findTaxaSets();

    public boolean hasMoreSets();

    public QuartetWeights getNextQuartetWeights();

    public double getNextWeight();
}

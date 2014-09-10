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

package uk.ac.uea.cmp.spectre.core.alg.nm;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import uk.ac.uea.cmp.spectre.core.ds.Identifier;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.split.Split;

import java.util.*;

/**
 * Created by dan on 09/09/14.
 */
public class Components2Vertices {

    private Map<Pair<Integer, Integer>, Double> c2v;
    private DistanceMatrix c2c;
    private Map<Identifier, Split> c2vMap;
    private List<Split> vList;
    private List<Identifier> cList;

    public Components2Vertices() {
        this.c2v = new HashMap<>();
        this.c2vMap = new HashMap<>();
        this.vList = new ArrayList<>();
        this.cList = new ArrayList<>();
    }

    public int size() {
        return this.vList.size();
    }

    public Identifier nextComponent() {

        final int id = vList.size() + 1;

        return new Identifier("C" + id, id);
    }

    public void add(Split s) {
        Identifier id = nextComponent();
        this.c2vMap.put(id, s);
        this.vList.add(s);
        this.cList.add(id);

        for(Integer v : s.getASide()) {
            this.c2v.put(new ImmutablePair<>(id.getId(), v), 0.0);
        }
    }

    public Set<Map.Entry<Identifier,Split>> entrySet() {
        return this.c2vMap.entrySet();
    }

    public double getDistance(final int cId, final int vId) {
        return this.c2v.get(new ImmutablePair<>(cId, vId));
    }

    public Split getAt(final int index) {
        return this.vList.get(index);
    }

    public Identifier getIdAt(final int index) {
        return this.cList.get(index);
    }

    public Split get(Identifier id) {
        return this.c2vMap.get(id);
    }

    public List<Split> getVertexList() {
        return this.vList;
    }
}

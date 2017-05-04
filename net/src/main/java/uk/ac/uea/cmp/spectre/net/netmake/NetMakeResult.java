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

package uk.ac.uea.cmp.spectre.net.netmake;

import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.network.Network;
import uk.ac.uea.cmp.spectre.core.ds.split.SplitSystem;
import uk.ac.uea.cmp.spectre.core.io.nexus.Nexus;
import uk.ac.uea.cmp.spectre.core.io.nexus.NexusWriter;

import java.io.File;
import java.io.IOException;


public class NetMakeResult {

    private SplitSystem treeSS;
    private SplitSystem networkSS;
    private DistanceMatrix dm;
    private Network network;
    private Network tree;

    public NetMakeResult(DistanceMatrix dm, SplitSystem treeSS, Network tree, SplitSystem networkSS, Network network) {
        this.dm = dm;
        this.treeSS = treeSS;
        this.networkSS = networkSS;
        this.network = network;
        this.tree = tree;
    }

    public SplitSystem getTreeSS() {
        return treeSS;
    }

    public SplitSystem getNetworkSS() {
        return networkSS;
    }

    public Network getNetwork() {
        return network;
    }

    public Network getTree() {
        return tree;
    }

    public void save(File outputNetwork, File outputTree) throws IOException {

        Nexus nexus = new Nexus();
        nexus.setTaxa(this.dm.getTaxa());
        nexus.setDistanceMatrix(this.dm);
        nexus.setSplitSystem(this.networkSS);
        nexus.setNetwork(this.network);
        new NexusWriter().writeNexusData(outputNetwork, nexus);

        if (this.treeSS != null && outputTree != null) {
            Nexus nextree = new Nexus();
            nexus.setTaxa(this.dm.getTaxa());
            nexus.setDistanceMatrix(this.dm);
            nexus.setSplitSystem(this.treeSS);
            nexus.setNetwork(this.tree);
            new NexusWriter().writeNexusData(outputTree, nextree);
        }
    }
}

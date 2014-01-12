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
package uk.ac.uea.cmp.phygen.core.ds.quartet.load;

import org.kohsuke.MetaInfServices;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetSystem;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetSystemList;
import uk.ac.uea.cmp.phygen.core.io.nexus.NexusReader;

import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA. User: Analysis Date: 2004-jul-11 Time: 23:09:07 To
 * change this template use Options | File Templates.
 */
@MetaInfServices(QLoader.class)
public class NexusQuartetLoader extends AbstractNexusLoader {

    @Override
    public QuartetSystem load(File file) throws IOException {
        return new NexusReader().readQuartets(file);
    }

    @Override
    public QuartetSystemList load(File file, double weight) throws IOException {

        QuartetSystem quartetSystem = new NexusReader().readQuartets(file);

        quartetSystem.setWeight(weight);

        return new QuartetSystemList(quartetSystem);
    }

    @Override
    public String getName() {
        return "nexus:st_quartets";
    }
}

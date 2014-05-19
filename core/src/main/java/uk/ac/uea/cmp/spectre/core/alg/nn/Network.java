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

package uk.ac.uea.cmp.spectre.core.alg.nn;

import uk.ac.uea.cmp.spectre.core.ds.Identifier;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;

import java.util.HashMap;

/**
 * Created by dan on 02/03/14.
 */
class Network extends HashMap<Identifier, IdentifierList> {

    public Network(IdentifierList taxa) {

        for (Identifier t : taxa) {
            IdentifierList newTaxa = new IdentifierList();
            newTaxa.add(t);
            this.put(t, newTaxa);
        }
    }

    public Identifier createNextIdentifier() {

        int maxId = 0;

        for (Identifier t : this.keySet()) {
            maxId = maxId < t.getId() ? t.getId() : maxId;
        }

        return new Identifier(maxId + 1);
    }
}

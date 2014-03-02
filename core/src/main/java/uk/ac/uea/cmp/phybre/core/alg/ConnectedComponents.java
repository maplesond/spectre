package uk.ac.uea.cmp.phybre.core.alg;

import uk.ac.uea.cmp.phybre.core.ds.Identifier;
import uk.ac.uea.cmp.phybre.core.ds.IdentifierList;

import java.util.HashMap;

/**
 * Created by dan on 02/03/14.
 */
class ConnectedComponents extends HashMap<Integer, IdentifierList> {

    public ConnectedComponents(IdentifierList taxa) {

        int i = 1;
        for(Identifier t : taxa) {
            IdentifierList newTaxa = new IdentifierList();
            newTaxa.add(t);
            this.put(i, newTaxa);
        }
    }
}

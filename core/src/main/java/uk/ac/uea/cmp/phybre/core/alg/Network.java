package uk.ac.uea.cmp.phybre.core.alg;

import uk.ac.uea.cmp.phybre.core.ds.Identifier;
import uk.ac.uea.cmp.phybre.core.ds.IdentifierList;

import java.util.HashMap;

/**
 * Created by dan on 02/03/14.
 */
class Network extends HashMap<Identifier, IdentifierList> {

    public Network(IdentifierList taxa) {

        for(Identifier t : taxa) {
            IdentifierList newTaxa = new IdentifierList();
            newTaxa.add(t);
            this.put(t, newTaxa);
        }
    }

    public Identifier createNextIdentifier() {

        int maxId=0;

        for(Identifier t : this.keySet()) {
            maxId = maxId < t.getId() ? t.getId() : maxId;
        }

        return new Identifier(maxId+1);
    }
}

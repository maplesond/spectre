package uk.ac.uea.cmp.spectre.core.alg;

import uk.ac.uea.cmp.spectre.core.ds.Identifier;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;

import java.util.HashMap;

/**
 * Created by dan on 12/03/14.
 */
public class Component2VertexSetMap extends HashMap<Identifier, IdentifierList> {

    public Component2VertexSetMap(IdentifierList taxa) {

        for (Identifier t : taxa) {
            IdentifierList newTaxa = new IdentifierList();
            newTaxa.add(t);
            this.put(t, newTaxa);
        }
    }

    public Identifier createNextIdentifier() {

        int maxId = 0;

        for (Identifier t : this.keySet()) {
            if (maxId < t.getId()) {
                maxId = t.getId();
            }
        }

        return new Identifier(maxId + 1);
    }
}

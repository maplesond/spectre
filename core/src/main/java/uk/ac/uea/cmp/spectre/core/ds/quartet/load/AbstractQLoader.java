package uk.ac.uea.cmp.spectre.core.ds.quartet.load;

import uk.ac.uea.cmp.spectre.core.ds.quartet.QuartetSystemList;

import java.io.File;
import java.io.IOException;

/**
 * Created by dan on 14/12/13.
 */
public abstract class AbstractQLoader implements QLoader {

    /**
     * By default we assume that there is only one quartet network loaded by this QLoader.  That being the case for
     * this method, we just add that network into a new network list and set the weight for the network.
     * @param file The file to load
     * @param weight The weight to be applied to this file
     * @return A quartet network with a single weighted element
     * @throws IOException
     */
    @Override
    public QuartetSystemList load(File file, double weight) throws IOException {

        // Loads the file and adds to list
        QuartetSystemList qnets = new QuartetSystemList(this.load(file));

        // Sets the weight
        qnets.get(0).setWeight(weight);

        // Create a single quartet network based on these quartet weight and add to the list
        return qnets;
    }

}

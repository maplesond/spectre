package uk.ac.uea.cmp.phygen.core.io;

import uk.ac.uea.cmp.phygen.core.ds.distance.DistanceMatrix;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Sarah_2
 * Date: 24/04/13
 * Time: 17:12
 * To change this template use File | Settings | File Templates.
 */
public interface PhygenReader {

    DistanceMatrix read(File input) throws IOException;
}

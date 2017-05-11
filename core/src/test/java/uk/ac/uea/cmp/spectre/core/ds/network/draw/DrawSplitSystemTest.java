package uk.ac.uea.cmp.spectre.core.ds.network.draw;

import org.junit.Before;
import org.junit.Test;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;
import uk.ac.uea.cmp.spectre.core.ds.distance.FlexibleDistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.network.Vertex;
import uk.ac.uea.cmp.spectre.core.ds.split.*;
import uk.ac.uea.cmp.spectre.core.ds.split.circular.ordering.nn.NeighborNetImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by maplesod on 11/05/17.
 */
public class DrawSplitSystemTest {

    SplitSystem ss;

    @Before
    public void setup() {

        final int N = 5;
        List<Split> splits = new ArrayList<>();
        splits.add(new SpectreSplit(new SpectreSplitBlock(new int[]{3}), N, 1.0));
        splits.add(new SpectreSplit(new SpectreSplitBlock(new int[]{1,2,5}), N, 0.0, false, false));
        splits.add(new SpectreSplit(new SpectreSplitBlock(new int[]{1,2}), N, 1.0));
        splits.add(new SpectreSplit(new SpectreSplitBlock(new int[]{2,3,4,5}), N, 1.0));
        splits.add(new SpectreSplit(new SpectreSplitBlock(new int[]{4}), N, 1.0));
        splits.add(new SpectreSplit(new SpectreSplitBlock(new int[]{4,5}), N, 1.0));
        splits.add(new SpectreSplit(new SpectreSplitBlock(new int[]{2,4,5}), N, 0.0, false, false));
        splits.add(new SpectreSplit(new SpectreSplitBlock(new int[]{5}), N, 1.0));
        splits.add(new SpectreSplit(new SpectreSplitBlock(new int[]{2,5}), N, 0.0, false, false));
        splits.add(new SpectreSplit(new SpectreSplitBlock(new int[]{2}), N, 1.0));
        this.ss = new SpectreSplitSystem(new IdentifierList(new int[]{1,3,4,5,2}), splits);
    }


    @Test
    public void computeRegularNetwork() throws Exception {
        DrawSplitSystem d = new DrawSplitSystem(this.ss);
        Vertex v = d.computeRegularNetwork().v;

        assertTrue(v != null);

    }

}
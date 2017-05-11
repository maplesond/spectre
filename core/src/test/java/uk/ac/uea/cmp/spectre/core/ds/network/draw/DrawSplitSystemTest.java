package uk.ac.uea.cmp.spectre.core.ds.network.draw;

import org.junit.Before;
import org.junit.Test;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;
import uk.ac.uea.cmp.spectre.core.ds.network.Vertex;
import uk.ac.uea.cmp.spectre.core.ds.split.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by maplesod on 11/05/17.
 */
public class DrawSplitSystemTest {

    SplitSystem ss;
    SplitSystem collapsed;

    @Before
    public void setup() {

        final int N = 5;
        IdentifierList taxa = new IdentifierList(new int[]{1,3,4,5,2});

        Split s0 = new SpectreSplit(new SpectreSplitBlock(new int[]{1,2,4,5}), N, 1.0);
        Split s1 = new SpectreSplit(new SpectreSplitBlock(new int[]{1,2,5}), N, 0.0, false, false);
        Split s2 = new SpectreSplit(new SpectreSplitBlock(new int[]{1,2}), N, 1.0);
        Split s3 = new SpectreSplit(new SpectreSplitBlock(new int[]{1}), N, 1.0);
        Split s4 = new SpectreSplit(new SpectreSplitBlock(new int[]{1,2,3,5}), N, 1.0);
        Split s5 = new SpectreSplit(new SpectreSplitBlock(new int[]{1,2,3}), N, 1.0);
        Split s6 = new SpectreSplit(new SpectreSplitBlock(new int[]{1,3}), N, 0.0, false, false);
        Split s7 = new SpectreSplit(new SpectreSplitBlock(new int[]{1,2,3,4}), N, 1.0);
        Split s8 = new SpectreSplit(new SpectreSplitBlock(new int[]{1,3,4}), N, 0.0, false, false);
        Split s9 = new SpectreSplit(new SpectreSplitBlock(new int[]{1,3,4,5}), N, 1.0);


        List<Split> splits = new ArrayList<>();
        splits.add(s0);
        splits.add(s1);
        splits.add(s2);
        splits.add(s3);
        splits.add(s4);
        splits.add(s5);
        splits.add(s6);
        splits.add(s7);
        splits.add(s8);
        splits.add(s9);
        this.ss = new SpectreSplitSystem(taxa, splits);

        List<Split> csplits = new ArrayList<>();
        csplits.add(s0);
        csplits.add(s2);
        csplits.add(s3);
        csplits.add(s4);
        csplits.add(s5);
        csplits.add(s7);
        csplits.add(s9);

        this.collapsed = new SpectreSplitSystem(taxa, csplits);
    }


    //@Test
    public void computeRegularNetwork1() throws Exception {
        DrawSplitSystem d = new DrawSplitSystem(this.ss);
        Vertex v = d.computeRegularNetwork().v;

        assertTrue(v != null);

        int nbexedges = v.collectAllExternalEdges(true).size();
        int nbvertices = v.collectVertices().size();

        assertTrue(nbexedges == 14);
        assertTrue(nbvertices == 21);
    }

    //@Test
    public void computeRegularNetworkCollapsed() throws Exception {
        SplitSystem filtered = this.ss.filterByAbsoluteWeight(0.1);
        DrawSplitSystem d = new DrawSplitSystem(filtered);
        Vertex v = d.computeRegularNetwork().v;

        assertTrue(v != null);

        int nbexedges = v.collectAllExternalEdges(true).size();
        int nbvertices = v.collectVertices().size();

        assertTrue(nbexedges == 14);
        assertTrue(nbvertices == 21);   // Same as unfiltered version
    }

    //@Test
    public void computeRegularNetworkCanonical() throws Exception {
        SplitSystem canonical = this.ss.filterByAbsoluteWeight(0.1).makeCanonical();
        DrawSplitSystem d = new DrawSplitSystem(canonical);
        Vertex v = d.computeRegularNetwork().v;

        assertTrue(v != null);

        int nbexedges = v.collectAllExternalEdges(true).size();
        int nbvertices = v.collectVertices().size();

        assertTrue(nbexedges == 14);
        assertTrue(nbvertices == 40);   // The regular network in this configuration will have a lot of vertices
    }

    //@Test
    public void drawMinimalNetwork1() throws Exception {
        DrawSplitSystem d = new DrawSplitSystem(this.ss);
        Vertex v = d.drawSplitSystem();

        assertTrue(v != null);

        int nbexedges = v.collectAllExternalEdges(true).size();
        int nbvertices = v.collectVertices().size();

        assertTrue(nbexedges == 13);
        assertTrue(nbvertices == 20);
    }

    //@Test
    public void drawMinimalNetworkCanonical() throws Exception {
        SplitSystem canonical = this.ss.makeCanonical();
        DrawSplitSystem d = new DrawSplitSystem(canonical);
        Vertex v = d.drawSplitSystem();

        assertTrue(v != null);

        int nbexedges = v.collectAllExternalEdges(true).size();
        int nbvertices = v.collectVertices().size();

        assertTrue(nbexedges == 13);
        assertTrue(nbvertices == 20); // Note that with a minimal network this should be the same as the uncanonical version
    }
}
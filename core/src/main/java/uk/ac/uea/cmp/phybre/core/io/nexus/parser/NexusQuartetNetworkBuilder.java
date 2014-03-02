/*
 * Phylogenetics Tool suite
 * Copyright (C) 2013  UEA CMP Phylogenetics Group
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

package uk.ac.uea.cmp.phybre.core.io.nexus.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.phybre.core.ds.IdentifierList;
import uk.ac.uea.cmp.phybre.core.ds.quartet.GroupedQuartetSystem;
import uk.ac.uea.cmp.phybre.core.ds.quartet.WeightedQuartetGroupMap;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 01/12/13
 * Time: 21:33
 * To change this template use File | Settings | File Templates.
 */
public class NexusQuartetNetworkBuilder {

    private static Logger log = LoggerFactory.getLogger(NexusQuartetNetworkBuilder.class);

    private int expectedNbTaxa;
    private IdentifierList taxa;
    private double weight;
    private WeightedQuartetGroupMap weightedQuartets;


    public NexusQuartetNetworkBuilder() {
        this.expectedNbTaxa = 0;
        this.taxa = null;
        this.weight = 1.0;
        this.weightedQuartets = new WeightedQuartetGroupMap();
    }


    public GroupedQuartetSystem createQuartetNetwork() {

        final int nbTaxa = taxa != null ? taxa.size() : expectedNbTaxa;

        if (expectedNbTaxa != 0 && taxa != null && nbTaxa != expectedNbTaxa) {
            log.warn("Expected number of taxa (" + expectedNbTaxa + ") is different from the number of found taxa (" + nbTaxa + ").");
        }

        return new GroupedQuartetSystem(this.taxa, this.weight, null); //this.weightedQuartets);
    }

    public int getExpectedNbTaxa() {
        return expectedNbTaxa;
    }

    public void setExpectedNbTaxa(int expectedNbTaxa) {
        this.expectedNbTaxa = expectedNbTaxa;
    }

    public void setTaxa(IdentifierList taxa) {
        this.taxa = taxa;
    }

    public IdentifierList getTaxa() {
        return taxa;
    }



    // This is code from QNet... to be implemented

   /*
    WeightedQuartetMap theQuartetWeights = new WeightedQuartetMap();
    Taxa allTaxa = new Taxa();
    List<Taxa> taxaSets = new ArrayList<>();


    boolean useMax = true;

    int N = 0;


    BufferedReader fileInput = new BufferedReader(new FileReader(file));


     //* Keep on reading and tokenizing until... a token is found
     //* beginning with "ntax=" parse its remainder for the number of
     //* taxa.
     //*
     //* Keep on reading until a token is found "TAXLABELS". Then read N
     //* lines which will be the taxon names. We assume there are n choose
     //* 4 quartets.
     //*
     //* Keep on reading and tokenizing until "st_quartets;" is found.
     //* Then proceed to "MATRIX". Then read the quartet lines until a
     //* line starts with ";".

    boolean readingState = true;

    while (readingState) {

        String aLine = fileInput.readLine();
        StringTokenizer sT = new StringTokenizer(aLine);

        while (sT.hasMoreTokens()) {

            String tT = sT.nextToken();

            if (tT.toLowerCase().startsWith("ntax=")) {

                N = Integer.parseInt(tT.substring(5, tT.length() - 1));

                for (int n = 0; n < N; n++) {
                    Taxon newTaxon = new Taxon("", n+1);
                    allTaxa.add(newTaxon);
                    taxaSets.add(new Taxa(newTaxon));
                }

                theQuartetWeights = new WeightedQuartetMap();
                useMax = true;

                readingState = false;
            }
        }
    }

    readingState = true;

    while (readingState) {

        String aLine = fileInput.readLine();
        StringTokenizer sT = new StringTokenizer(aLine);

        while (sT.hasMoreTokens()) {

            String tT = sT.nextToken();

            if (tT.toUpperCase().startsWith("TAXLABELS")) {

                for (int n = 0; n < N; n++) {

                    StringTokenizer aT = new StringTokenizer(fileInput.readLine());

                    String aS = aT.nextToken();

                    while (aT.hasMoreTokens()) {

                        aS = aT.nextToken();
                    }

                    allTaxa.set(n, new Taxon(aS));
                }

                readingState = false;
            }
        }
    }

    readingState = true;

    while (readingState) {

        String aLine = fileInput.readLine();
        StringTokenizer sT = new StringTokenizer(aLine);

        while (sT.hasMoreTokens()) {

            String tT = sT.nextToken();

            if (tT.toLowerCase().startsWith("st_quartets;")) {

                readingState = false;
            }
        }
    }

    readingState = true;

    while (readingState) {

        String aLine = fileInput.readLine();
        StringTokenizer sT = new StringTokenizer(aLine);

        while (sT.hasMoreTokens()) {

            String tT = sT.nextToken();

            if (tT.toUpperCase().startsWith("MATRIX")) {

                boolean quartetState = true;

                while (quartetState) {

                    String bLine = fileInput.readLine();

                    if (bLine.startsWith(";")) {

                        quartetState = false;

                    } else {

                        StringTokenizer bT = new StringTokenizer(bLine);

                        String label = bT.nextToken();
                        double weight = Double.parseDouble(bT.nextToken());
                        int x = Integer.parseInt(bT.nextToken());
                        int y = Integer.parseInt(bT.nextToken());
                        String sC = bT.nextToken();
                        int u = Integer.parseInt(bT.nextToken());
                        String cS = bT.nextToken();
                        int v = Integer.parseInt(cS.substring(0, cS.length() - 1));

                        if (x != y && x != u && x != v && y != u && y != v && u != v) {

                            theQuartetWeights.setWeight(new Quartet(x, y, u, v), weight);

                        }
                    }
                }

                readingState = false;
            }
        }
    }

    return null; //new QuartetNetworkList(new QuartetNetwork(allTaxa, 1.0, theQuartetWeights));
    */
}

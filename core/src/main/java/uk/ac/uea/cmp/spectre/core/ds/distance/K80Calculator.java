/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2017  UEA School of Computing Sciences
 *
 * This program is free software: you can redistribute it and/or modify it under the term of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package uk.ac.uea.cmp.spectre.core.ds.distance;

/**
 * Calculates the Kimura2 parameter between two strings of equal length
 */
public class K80Calculator extends AbstractDistanceMatrixCalculator {

    public static boolean isPurine(char c) {
        return c == 'A' || c == 'a' || c == 'G' || c == 'g';
    }

    public static boolean isPyrimidine(char c) {
        return c == 'C' || c == 'c' || c == 'T' || c == 't' || c == 'U' || c == 'u';
    }


    public static int calcTransitionSites(String s1, String s2) {
        if (s1.length() != s2.length()) {
            throw new IllegalArgumentException("Sequence " + s1 + " and sequence " + s2 + " are different lengths.");
        }

        int ts = 0;
        for (int i = 0; i < s1.length(); i++) {
            char s1i = s1.charAt(i);
            char s2i = s2.charAt(i);
            if (s1i != s2i) {
                if ((isPurine(s1i) && isPurine(s2i)) ||
                        (isPyrimidine(s1i) && isPyrimidine(s2i))) {
                    ts++;
                }
            }
        }

        return ts;
    }

    @Override
    protected double calculateDistance(String s1, String s2) {

        final int hcount = HammingDistanceCalculator.hammingCount(s1, s2);

        final int transition_sites = calcTransitionSites(s1, s2);
        final int transversion_sites = hcount - transition_sites;

        final double dist_ts = (double)transition_sites / (double)s1.length();
        final double dist_tv = (double)transversion_sites / (double)s1.length();

        final double part1 = 0.5 * Math.log(1.0 / (1.0 - 2.0 * dist_ts - dist_tv));
        final double part2 = 0.25 * Math.log(1.0 / (1.0 - 2.0 * dist_tv));

        final double k80 = part1 + part2;

        //System.out.println("K80:" + k80 + " H:" + hcount + " TS:" + transition_sites + " TV:" + transversion_sites + " dts:" + dist_ts + " dtv:" + dist_tv + " P1:" + part1 + " P2:" + part2);

        return k80;
    }
}

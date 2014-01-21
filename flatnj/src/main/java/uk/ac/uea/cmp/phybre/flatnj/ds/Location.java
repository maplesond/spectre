/*
 * Phylogenetics Tool suite
 * Copyright (C) 2013  UEA CMP Phylogenetics Group
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
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

package uk.ac.uea.cmp.phybre.flatnj.ds;

/**
 * @author balvociute
 */
public class Location {
    /**
     * x coordinate of the location.
     */
    private double x;
    /**
     * y coordinate of the location.
     */
    private double y;
    /**
     * Name of the location.
     */
    private String label;

    /**
     * creates Location object with given coordinates and label.
     *
     * @param x     x coordinate of the location.
     * @param y     y coordinate of the location.
     * @param label location`s label.
     */
    public Location(double x, double y, String label) {
        this.x = x;
        this.y = y;
        this.label = label;
    }

    /**
     * creates Location object with given.
     *
     * @param x x coordinate of the location.
     * @param y y coordinate of the location.
     */
    public Location(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        String loc = x + "\t" + y;
        loc = (label == null) ? loc : label + " " + loc;
        return loc;
    }

    /**
     * returns label for this location.
     *
     * @return label
     */
    public String getLabel() {
        return label;
    }

    /**
     * returns x coordinate for this location.
     *
     * @return x
     */
    public double getX() {
        return x;
    }

    /**
     * returns y coordinate for this location.
     *
     * @return y
     */
    public double getY() {
        return y;
    }

    /**
     * estimates euclidean distance from this location to loc.
     *
     * @param loc location to measure distance to.
     * @return distance.
     */
    public double distanceTo(Location loc) {
        double d = Math.sqrt((x - loc.getX()) * (x - loc.getX()) + (y - loc.getY()) * (y - loc.getY()));
        return d;
    }


}

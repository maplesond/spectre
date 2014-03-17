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

package uk.ac.uea.cmp.spectre.flatnj.netvi;

/**
 *
 * @author balvociute
 */
public class Element
{
    protected double x;
    protected double y;

    /**
     * Default constructor.
     */
    public Element()
    {
    }

    /**
     * Constructor with initial values. 
     * 
     * @param x x coordinate.
     * @param y y coordinate.
     */
    public Element(double x, double y)
    {
        this.x = x;
        this.y = y;
    }
    
    /**
     * 
     * @return current x coordinate of the element.
     */
    public double getX()
    {
        return x;
    }

    /**
     * 
     * @return current y coordinate of the element.
     */
    public double getY()
    {
        return y;
    }
    
    /**
     * Sets new x coordinate.
     * @param x new x coordinate.
     */
    public void setX(double x)
    {
        this.x = x;
    }
    
    /**
     * Sets new y coordinate.
     * @param y new y coordinate.
     */
    public void setY(double y)
    {
        this.y = y;
    }
        
    public int getXint()
    {
        return (int) x;
    }
    
    public int getYint()
    {
        return (int) y;
    }
}

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

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author balvociute
 */
class RandomColorGenerator implements ColorGenerator
{
    List<Color> colors;
    Random random;
    
    public RandomColorGenerator()
    {
        random = new Random();
        colors = new LinkedList();
        
        for(int r = 255; r > 100; r -=  30)
        {
            for(int g = 255; g > 100; g -= 30)
            {
                for(int b = 255; b > 100; b -= 30)
                {
                    colors.add(new Color(r, g, b));
                }
            }
        }
    }

    @Override
    public Color nextColor()
    {
        int i = random.nextInt(colors.size());
        return colors.remove(i);
    }
    
    
    
}

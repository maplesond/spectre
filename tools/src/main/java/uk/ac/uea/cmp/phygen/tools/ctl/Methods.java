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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.uea.cmp.phygen.tools.ctl;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public enum Methods {

    FASTME("FastME", true),
    NNET("NNet", true),
    GREEDY("Greedy", true),
    TSP("TSP", true),
    TREE("Tree", true),
    EQUAL("Equal", true),
    PARABOLA("Parabola", false),
    RANDOMNET("RandomNet", true);
    
    private String desc;
    private boolean enabled;
    
    private Methods(String desc, boolean enabled) {
        this.desc = desc;
        this.enabled = enabled;
    }
    
    public String getDescription() {
        return desc;
    }
    
    public boolean getEnabled() {
        return enabled;
    }
    
    public static Methods[] enabledValues() {
        List<Methods> methods = new ArrayList<>();
        
        for(Methods method : Methods.values()) {
            if (method.getEnabled())
                methods.add(method);
        }
        
        return methods.toArray(new Methods[methods.size()]);
    }
}

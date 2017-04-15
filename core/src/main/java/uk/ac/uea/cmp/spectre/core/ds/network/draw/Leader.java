/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2014  UEA School of Computing Sciences
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

package uk.ac.uea.cmp.spectre.core.ds.network.draw;

import java.awt.*;

/**
 * Created by dan on 15/04/17.
 */
public class Leader {
    public enum LeaderType {
        NONE,
        STRAIGHT,
        SLANTED,
        BENDED
    }

    public enum LeaderStroke {
        SOLID {
            @Override
            public Stroke getStroke() {
                return new BasicStroke(1);
            }
        },
        DASHED {
            @Override
            public Stroke getStroke() {
                return new BasicStroke(
                        1.0f,
                        BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER,
                        10.0f,
                        new float[]{5.0f},
                        0.0f);
            }
        },
        DOTTED {
            @Override
            public Stroke getStroke() {
                return new BasicStroke(
                        1.0f,
                        BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER,
                        10.0f,
                        new float[]{1.0f},
                        0.0f);
            }
        };

        public abstract Stroke getStroke();
    }
}

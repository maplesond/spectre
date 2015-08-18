/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2015  UEA School of Computing Sciences
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

package uk.ac.uea.cmp.spectre.net.netmake;

import uk.ac.uea.cmp.spectre.core.ui.gui.StatusTracker;
import uk.ac.uea.cmp.spectre.core.ui.gui.ToolHost;
import uk.ac.uea.cmp.spectre.core.ui.gui.ToolRunner;

public class NetMakeRunner extends ToolRunner {

    public NetMakeRunner(ToolHost host) {
        super(host);
    }

    public NetMake getEngine() {
        if (this.engine instanceof NetMake) {
            NetMake m_engine = NetMake.class.cast(this.engine);
            return m_engine;
        }

        return null;
    }

    public void runNetMake(NetMakeOptions params, StatusTracker tracker) {
        try {
            NetMake m_engine = new NetMake(params, tracker);
            this.run(m_engine);
        } catch (Exception ioe) {
            this.host.showErrorDialog(ioe.getMessage());
        }
    }
}

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

package uk.ac.uea.cmp.spectre.net.netme;

import uk.ac.uea.cmp.spectre.core.ui.gui.StatusTracker;
import uk.ac.uea.cmp.spectre.core.ui.gui.ToolHost;
import uk.ac.uea.cmp.spectre.core.ui.gui.ToolRunner;
import uk.ac.uea.cmp.spectre.net.netmake.NetMake;

/**
 * Created by dan on 09/02/14.
 */
public class NetMERunner extends ToolRunner {

    public NetMERunner(ToolHost host) {
        super(host);
    }

    public NetME getEngine() {
        if (this.engine instanceof NetMake) {
            NetME m_engine = NetME.class.cast(this.engine);
            return m_engine;
        }

        return null;
    }

    public void runNetME(NetMEOptions params, StatusTracker tracker) {
        try {
            NetME m_engine = new NetME(params, tracker);
            this.run(m_engine);
        } catch (Exception ioe) {
            this.host.showErrorDialog(ioe.getMessage());
        }
    }
}

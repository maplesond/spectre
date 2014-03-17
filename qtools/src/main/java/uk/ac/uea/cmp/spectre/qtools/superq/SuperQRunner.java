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
package uk.ac.uea.cmp.spectre.qtools.superq;

import uk.ac.uea.cmp.spectre.core.ui.gui.StatusTracker;
import uk.ac.uea.cmp.spectre.core.ui.gui.ToolHost;
import uk.ac.uea.cmp.spectre.core.ui.gui.ToolRunner;


public class SuperQRunner extends ToolRunner {

    public SuperQRunner(ToolHost host) {
        super(host);
    }

    public SuperQ getEngine() {
        if (this.engine instanceof SuperQ) {
            SuperQ m_engine = SuperQ.class.cast(this.engine);
            return m_engine;
        }

        return null;
    }

    public void runSuperQ(SuperQOptions params, StatusTracker tracker) {
        try {
            SuperQ m_engine = new SuperQ(params, tracker);
            this.run(m_engine);
        } catch (Exception ioe) {
            this.host.showErrorDialog(ioe.getMessage());
        }
    }
}

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
package uk.ac.uea.cmp.phygen.superq.qnet;

import uk.ac.uea.cmp.phygen.core.ui.gui.StatusTracker;
import uk.ac.uea.cmp.phygen.core.ui.gui.ToolHost;
import uk.ac.uea.cmp.phygen.core.ui.gui.ToolRunner;


public class QNetRunner extends ToolRunner {

    public QNetRunner(ToolHost host) {
        super(host);
    }

    public QNet getEngine() {
        if (this.engine instanceof QNet) {
            QNet m_engine = QNet.class.cast(this.engine);
            return m_engine;
        }

        return null;
    }

    public void runSuperQ(QNetOptions params, StatusTracker tracker) {
        try {
            QNet m_engine = new QNet(params, tracker);
            this.run(m_engine);
        } catch (Exception ioe) {
            this.host.showErrorDialog(ioe.getMessage());
        }
    }
}

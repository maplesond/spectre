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

package uk.ac.uea.cmp.spectre.core.ui.gui;

import javax.swing.*;

/**
 * Created by dan on 18/03/14.
 */
public class StatusTrackerWithView extends StatusTracker {

    private JButton view;

    public StatusTrackerWithView(JProgressBar progBar, JLabel status, JButton view) {
        super(progBar, status);

        this.view = view;
    }

    @Override
    public void setFinished(boolean success) {

        super.setFinished(success);

        if (success) {
            this.view.setEnabled(true);
        }
    }

    @Override
    public void reset() {

        super.reset();

        this.view.setEnabled(false);
    }
}

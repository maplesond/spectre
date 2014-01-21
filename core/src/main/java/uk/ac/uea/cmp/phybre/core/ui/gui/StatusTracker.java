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
package uk.ac.uea.cmp.phybre.core.ui.gui;

import javax.swing.*;

public class StatusTracker {

    private JProgressBar progBar;
    private JLabel status;

    public StatusTracker(JProgressBar progBar, JLabel status) {
        this.progBar = progBar;
        this.status = status;
    }

    public void initUnknownRuntime(String message) {
        if (this.progBar != null) {
            this.progBar.setIndeterminate(true);
            this.progBar.setValue(0);
        }

        if (this.status != null) {
            this.status.setText("Status: " + message + "...");
        }
    }

    public void initKnownRuntime(String message, int length) {
        if (this.progBar != null) {
            this.progBar.setMaximum(length);
            this.progBar.setValue(0);
        }

        if (this.status != null) {
            this.status.setText("Status: " + message + "...");
        }
    }

    public void setFinished(boolean success) {
        if (this.progBar != null) {
            this.progBar.setIndeterminate(false);
            this.progBar.setValue(0);
        }

        if (this.status != null) {
            this.status.setText("Status: " + (success ? "completed successfully" : "failed"));
        }
    }

    public void reset() {
        if (this.progBar != null) {
            this.progBar.setIndeterminate(false);
            this.progBar.setValue(0);
        }

        if (this.status != null) {
            this.status.setText("Status: Idle");
        }
    }

    public void increment() {
        if (this.progBar != null) {
            this.progBar.setValue(progBar.getValue() + 1);
        }
    }
}

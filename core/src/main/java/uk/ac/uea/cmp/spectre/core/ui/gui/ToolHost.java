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
package uk.ac.uea.cmp.spectre.core.ui.gui;

/**
 * An interface to be implemented by GUIs which use a ToolRunner to do heavy
 * processing in a separate thread.
 */
public interface ToolHost {

    /**
     * Used to update a host GUI after procedure has executed. Implementor
     * should handle any Exceptions inside this function, and notify user using
     * the showErrorDialog method.
     */
    void update();

    /**
     * Informs host of whether the tool is currently running or not
     *
     * @param running true if currently running, otherwise false
     */
    void setRunningStatus(boolean running);

    /**
     * Requests that host opens an error dialog with the given message
     *
     * @param message Error message to display to user
     */
    void showErrorDialog(String message);
}

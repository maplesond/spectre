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

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public abstract class RunnableTool implements Runnable {
    // Error handling

    private Throwable error;
    // Runtime controls
    private ThreadCompleteListener listener;
    private StatusTracker tracker;
    private volatile boolean continue_run;

    protected RunnableTool() {
        this(null, null);
    }

    protected RunnableTool(StatusTracker tracker) {
        this(tracker, null);
    }

    protected RunnableTool(StatusTracker tracker, ThreadCompleteListener listener) {
        this.error = null;
        this.continue_run = true;

        setTracker(tracker);
        setListener(listener);
    }

    // Error handling routines.
    public boolean failed() {
        return this.error != null ? true : false;
    }

    public String getErrorMessage() {
        return error.getMessage();
    }

    public String getStackTrace() {

        return StringUtils.join(this.error.getStackTrace(), "\n");
    }

    public Throwable getError() {
        return this.error;
    }

    public String getFullErrorMessage() {
        return this.getErrorMessage() + "\n" + this.getStackTrace();

    }

    protected void setError(Throwable throwable) {
        this.error = throwable;
    }

    protected void clearError() {
        this.error = null;
    }

    // Runtime handling routines.
    public void cancelRun() {
        this.continue_run = false;
    }

    public void resetRun() {
        continue_run = true;
    }

    protected void continueRun() throws IOException {
        if (!continue_run) {
            throw new IOException("Run cancelled by user.");
        }
    }

    // Listener handling routines.
    public final void setListener(final ThreadCompleteListener listener) {
        this.listener = listener;
    }

    protected void notifyListener() {
        if (this.listener != null) {
            this.listener.notifyOfThreadCompletion();
        }
    }

    // Tracker handling routines.
    public final void setTracker(final StatusTracker tracker) {
        this.tracker = tracker;
    }

    protected final StatusTracker getTracker() {
        return this.tracker;
    }

    protected void trackerFinished(boolean success) {
        if (this.tracker != null) {
            this.tracker.setFinished(success);
        }
    }

    protected void trackerInitKnownRuntime(String message, int length) {
        if (this.tracker != null) {
            this.tracker.initKnownRuntime(message, length);
        }
    }

    protected void trackerInitUnknownRuntime(String message) {
        if (this.tracker != null) {
            this.tracker.initUnknownRuntime(message);
        }
    }

    protected void trackerReset() {
        if (this.tracker != null) {
            this.tracker.reset();
        }
    }

    protected void trackerIncrement() {
        if (this.tracker != null) {
            this.tracker.increment();
        }
    }
}

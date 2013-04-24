/**
 * Super Q - Computing super networks from partial trees. Copyright (C) 2012 UEA
 * CMP Phylogenetics Group.
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.uea.cmp.phygen.core.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


/**
 * Handles execution of a RunnableTool from a GUI in another thread. Also
 * notifies the GUI of progress and result made during the RunnableTool's run
 * method.
 */
public abstract class ToolRunner implements ThreadCompleteListener {

    static Logger log = LoggerFactory.getLogger(ToolRunner.class);
    protected RunnableTool engine;
    protected Thread thread;
    protected ToolHost host;

    /**
     * Initialises the ToolRunner
     *
     * @param host The host that's running this tool
     */
    protected ToolRunner(ToolHost host) {
        this.host = host;
        this.thread = null;
        this.engine = null;
    }

    /**
     * Check to see if tool is currently running
     *
     * @return true if tool is running, otherwise false
     */
    public boolean getActive() {
        return this.thread.isAlive();
    }

    /**
     * Clears the existing filter engine from memory
     */
    public void reset() {
        this.engine = null;
        this.thread = null;
    }

    /**
     * Instructs the filter tool to stop executing
     */
    public void cancel() {
        if (this.thread != null) {
            this.engine.cancelRun();
        }
    }

    /**
     * Called when engine completes run. If engine fails to complete
     * successfully a request is put out to the host to raise the issue with the
     * user. If it completes successfully the host is asked to update the GUI
     * with the results. In both cases the running status of the GUI is set to
     * false.
     */
    @Override
    public void notifyOfThreadCompletion() {
        if (this.engine.failed()) {
            this.host.showErrorDialog(this.engine.getErrorMessage());
        } else {
            this.host.update();
        }

        this.host.setRunningStatus(false);
    }

    /**
     * Handles running of a tool for derived class. Asks host to set the GUI
     * into running (busy) mode. Then starts a new thread and kicks off the
     * engine in that thread. If an error occurs then the host is signalled to
     * raise an error message with the user and sets the running mode to false.
     *
     * @param engine The RunnableTool to start
     * @throws java.io.IOException Thrown if there were any problems starting the
     * RunnableTool
     */
    protected void run(RunnableTool engine) throws IOException {
        try {
            this.host.setRunningStatus(true);

            this.engine = engine;
            this.engine.setListener(this);

            this.thread = new Thread(this.engine);
            this.thread.start();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);

            this.host.showErrorDialog(ex.getMessage());
            this.host.setRunningStatus(false);
        }
    }
}

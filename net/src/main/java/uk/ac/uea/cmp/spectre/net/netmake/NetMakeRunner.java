package uk.ac.uea.cmp.spectre.net.netmake;

import uk.ac.uea.cmp.spectre.core.ui.gui.StatusTracker;
import uk.ac.uea.cmp.spectre.core.ui.gui.ToolHost;
import uk.ac.uea.cmp.spectre.core.ui.gui.ToolRunner;

/**
 * Created by dan on 09/02/14.
 */
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

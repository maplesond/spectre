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

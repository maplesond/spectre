package uk.ac.uea.cmp.spectre.core.ui.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * Created by dan on 09/03/14.
 */
public class LookAndFeel {

    private static Logger log = LoggerFactory.getLogger(LookAndFeel.class);

    public static final String NIMBUS = "Nimbus";

    public static void setLookAndFeel(String lookAndFeel) {
        try
        {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels())
            {
                if (lookAndFeel.equals(info.getName()))
                {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            log.error("Error setting look and feel for GUI", ex);
        }
    }
}

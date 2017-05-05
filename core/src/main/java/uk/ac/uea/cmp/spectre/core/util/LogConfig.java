package uk.ac.uea.cmp.spectre.core.util;

import org.apache.log4j.*;

/**
 * Created by maplesod on 05/04/17.
 */
public class LogConfig {

    public static void defaultConfig() {
        defaultConfig(false);
    }

    public static void defaultConfig(boolean verbose) {
        BasicConfigurator.configure(new ConsoleAppender(new PatternLayout("%p %d{dd-MMM-yyyy HH:mm:ss} - %m%n")));
        LogManager.getRootLogger().setLevel(verbose ? Level.DEBUG : Level.INFO);
    }
}

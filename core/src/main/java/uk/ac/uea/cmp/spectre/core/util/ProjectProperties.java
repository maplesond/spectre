package uk.ac.uea.cmp.spectre.core.util;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by maplesod on 23/05/17.
 */
public class ProjectProperties {

    private static Logger log = LoggerFactory.getLogger(ProjectProperties.class);

    public static String getVersion() throws IOException {
        Properties prop = new Properties();
        InputStream in = ProjectProperties.class.getResourceAsStream("/general.properties");
        prop.load(in);
        in.close();

        return prop.get("project.version").toString();
    }

    public static String getResourceFile(String filename) {
        return getResourceFile(filename, false);
    }

    public static String getResourceFile(String filename, boolean verbose) {

        try {
            File extDir = new File(ProjectProperties.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());

            // Ok, this can get messy.  There's probably a better way to deal with there is a lot of different ways the
            // project can be configured across different environment, bundles and installers, so let's just wind back a max
            // of 10 levels... if we haven't found it by then, then it's not there.
            for (int i = 0; i < 12; i++) {
                extDir = extDir.getParentFile();
                String dirname = extDir.getName();
                String pardirname = extDir.getParentFile().getName();

                File backfile = new File(extDir, filename);
                if (backfile.exists()) {
                    if (verbose) log.debug("Found resource at: " + backfile.getAbsolutePath());
                    return backfile.getAbsolutePath();
                }

                // Just a quick sanity check to make sure we are not going too far and delving into parts of the file
                // system that we shouldn't
                if (dirname.equalsIgnoreCase("spectre") && !pardirname.equalsIgnoreCase("cmp")) {
                    if (verbose) log.warn("Could not find resource: " + filename);
                    return null;
                }
            }
        }
        catch (Exception e) {
            if (verbose) log.warn("Caught exception when trying to access resource: " + filename + "; " + e.getMessage());
            return null;
        }
        if (verbose) log.warn("Could not find resource: " + filename);
        return null;
    }
}

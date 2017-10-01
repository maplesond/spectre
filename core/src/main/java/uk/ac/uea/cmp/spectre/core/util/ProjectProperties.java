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

        try {
            File extDir = new File(ProjectProperties.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile();
            File etc = new File(extDir, "etc" + File.separator + filename);
            File local1 = new File(extDir, filename);
            File local2 = FileUtils.toFile(ProjectProperties.class.getResource(filename));
            File back1 = new File(extDir.getParentFile(), "etc" + File.separator + filename);
            File back2 = new File(extDir.getParentFile().getParentFile(), "etc" + File.separator + filename);
            File back8 = new File(extDir.getParentFile().getParentFile().getParentFile().getParentFile().getParentFile().getParentFile().getParentFile().getParentFile(), "etc" + File.separator + filename);

            List<File> flist = new ArrayList<>();
            if (etc != null) flist.add(etc);
            if (local1 != null) flist.add(local1);
            if (local2 != null) flist.add(local2);
            if (back1 != null) flist.add(back1);
            if (back2 != null) flist.add(back2);
            if (back8 != null) flist.add(back8);

            for (File f : flist) {
                if (f.exists()) {
                    log.debug("Found resource at: " + f.getAbsolutePath());
                    return f.getAbsolutePath();
                }
            }
        }
        catch (Exception e) {
            log.warn("Caught exception when trying to access resource: " + filename + "; " + e.getMessage());
            return null;
        }
        log.warn("Could not find resource: " + filename);
        return null;
    }
}

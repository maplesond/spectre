package uk.ac.uea.cmp.spectre.core.util;

import uk.ac.uea.cmp.spectre.core.ui.cli.CommandLineHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by maplesod on 23/05/17.
 */
public class ProjectProperties {
    public static String getVersion() throws IOException {
        Properties prop = new Properties();
        InputStream in = ProjectProperties.class.getResourceAsStream("/general.properties");
        prop.load(in);
        in.close();

        return prop.get("project.version").toString();
    }
}

package uk.ac.uea.cmp.spectre.core.io.nexus;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * Created by dan on 12/01/14.
 */
public class NexusFileFilter extends FileFilter {


    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String[] fileExtensions = commonFileExtensions();

        for(String ext : fileExtensions) {
            if (f.getName().endsWith(ext)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String getDescription() {
        return "Nexus";
    }

    public String[] commonFileExtensions() {
        return new String[]{"nex", "nxs", "nexus"};
    }
}

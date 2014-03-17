package uk.ac.uea.cmp.spectre.core.io.qweight;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * Created by dan on 12/01/14.
 */
public class QWeightFileFilter extends FileFilter {


    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String[] fileExtensions = commonFileExtensions();

        for (String ext : fileExtensions) {
            if (f.getName().endsWith(ext)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String getDescription() {
        return "QWeight";
    }

    public String[] commonFileExtensions() {
        return new String[]{"qw", "qweight"};
    }
}

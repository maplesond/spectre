package uk.ac.uea.cmp.spectre.core.ds.quartet.load;

import org.apache.commons.io.FileUtils;
import org.kohsuke.MetaInfServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.spectre.core.ds.quartet.QuartetSystem;
import uk.ac.uea.cmp.spectre.core.ds.quartet.QuartetSystemList;
import uk.ac.uea.cmp.spectre.core.util.SpiFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by dan on 14/12/13.
 */
@MetaInfServices(QLoader.class)
public class ScriptLoader extends AbstractQLoader {

    private static Logger log = LoggerFactory.getLogger(ScriptLoader.class);

    private SpiFactory<QLoader> sourceFactory;

    public ScriptLoader() {
        this.sourceFactory = new SpiFactory<>(QLoader.class);
    }

    @Override
    public QuartetSystem load(File file) throws IOException {
        throw new UnsupportedOperationException("A script loader will return a list of quartet networks, so this method is not supported.");
    }

    @Override
    public QuartetSystemList load(File inputFile, double weight) throws IOException {

        // Create an empty tree
        QuartetSystemList qnets = new QuartetSystemList();

        // Load the script
        List<String> lines = FileUtils.readLines(inputFile);

        // Execute each line of the script
        for (String line : lines) {

            StringTokenizer sT = new StringTokenizer(line.trim());

            if (sT.hasMoreTokens()) {

                // The first token should specify the loader
                String sourceName = sT.nextToken();

                // The second token should be the file to load
                if (sT.hasMoreTokens()) {

                    String sourceFileName = sT.nextToken();
                    File sourceFile = new File(sourceFileName);

                    // If relative path was given, assume we are using the script's relative path rather than relative to
                    // the current working directory
                    if (!sourceFile.isAbsolute() && inputFile.getParent() != null) {
                        sourceFile = new File(inputFile.getParent(), sourceFileName);
                    }

                    // The third token is optional, but if present we multiply all weights in the tree of this file by
                    // the given amount
                    double entryWeight = sT.hasMoreTokens() ?
                            Double.parseDouble(sT.nextToken()) :
                            1.0;

                    // Create a source loader
                    QLoader qLoader = this.sourceFactory.create(sourceName);

                    // Load source and execute qmaker, combine results
                    qnets.addAll(qLoader.load(sourceFile, entryWeight));

                    log.debug("Loaded " + sourceFile.getAbsolutePath());

                } else {

                    log.warn("Script line specified source (" + sourceName + ") but is lacking file name!  Ignoring line.");
                }
            }
        }

        return qnets;
    }

    @Override
    public boolean acceptsExtension(String ext) {
        return ext.equalsIgnoreCase("script") || ext.equalsIgnoreCase("scr") || ext.equalsIgnoreCase("txt");
    }

    @Override
    public String getName() {
        return "script";
    }
}

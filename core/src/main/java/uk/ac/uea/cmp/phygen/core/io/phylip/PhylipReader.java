package uk.ac.uea.cmp.phygen.core.io.phylip;

import org.apache.commons.io.FileUtils;
import uk.ac.uea.cmp.phygen.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.phygen.core.io.PhygenReader;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sarah_2
 * Date: 24/04/13
 * Time: 13:18
 * To change this template use File | Settings | File Templates.
 */
public class PhylipReader implements PhygenReader {

    /**
     * Reads the file specified by this reader and converts it the data into a set
     * of taxa and the distances between taxa.
     * @return The distance matrix, with associated taxa set.
     * @throws IOException Thrown if there were any problems accessing the file.
     */
    public DistanceMatrix read(File file) throws IOException {

        // Validation before reading
        if (file == null) {
            throw new NullPointerException("Must specify a phylip file to read");
        }

        if (!file.exists()) {
            throw new IOException("Phylip file does not exist: " + file.getAbsolutePath());
        }

        if (!file.canRead()) {
            throw new IOException("Phylip file cannot be read: " + file.getAbsolutePath());
        }

        // Load file into line collection
        List<String> inLines = FileUtils.readLines(file);

        // Assume first line contains number of taxa
        String n = inLines.get(0).trim();
        int taxanumber = Integer.parseInt(n);
        DistanceMatrix distanceMatrix = new DistanceMatrix(taxanumber);
        //permutation = new int[taxanumber];
        int runidx2 = 0;
        int runidx = 0;

        // Process the rest of the lines
        for (int i = 1; i < inLines.size(); i++) {

            String aLine = inLines.get(i);

            if (aLine.trim().isEmpty())
                continue;

            int taxaendIdx = aLine.indexOf(" ");

            if (aLine.substring(0, taxaendIdx + 1).equals(" ") == false) {
                runidx2 = 0;
                System.out.println(runidx + " " + taxaendIdx);
                distanceMatrix.setTaxa(runidx, aLine.substring(0, taxaendIdx));

                runidx++;
                aLine = aLine.substring(taxaendIdx + 1);
            }

            String distance = " ";
            int idx = 0;

            aLine = aLine.trim();

            while (aLine.isEmpty() == false) {
                aLine = aLine.trim();
                if (aLine.contains(" ")) {
                    idx = aLine.indexOf(" ");
                    distance = aLine.substring(0, idx);
                    aLine = aLine.substring(idx + 1);
                } else {
                    distance = aLine.substring(0, aLine.length());
                    aLine = aLine.substring(0, 0);
                }


                distanceMatrix.setDistance(runidx - 1, runidx2, Double.parseDouble(distance));

                runidx2++;
            }
        }

        return distanceMatrix;
    }
}

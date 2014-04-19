package uk.ac.uea.cmp.spectre.core.math.stats;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dan on 18/04/14.
 */
public class Report {

    private List<Experiment> experiments;

    public Report() {
        this.experiments = new ArrayList<Experiment>();
    }

    public void addExperiment(Experiment experiment) {
        this.experiments.add(experiment);
    }

    public void save(File reportFile) throws IOException {

        if (experiments == null || experiments.isEmpty())
            return;

        List<String> lines = new ArrayList<String>();

        lines.add(this.experiments.get(0).createHeaderString());

        for(Experiment e : this.experiments) {
            lines.add(e.toTabSeparatedString());
        }

        FileUtils.writeLines(reportFile, lines);
    }
}

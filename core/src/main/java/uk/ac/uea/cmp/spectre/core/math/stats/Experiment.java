/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2014  UEA School of Computing Sciences
 *
 * This program is free software: you can redistribute it and/or modify it under the term of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package uk.ac.uea.cmp.spectre.core.math.stats;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dan on 18/04/14.
 */
public class Experiment {

    private ExperimentDescription description;
    private List<Result> results;

    private Statistics truePositiveStats;   // Hit
    private Statistics falsePositiveStats;  // Type 1 error
    private Statistics falseNegativeStats;  // Type 2 error

    private double recall;
    private double precision;
    private double f1Score;

    public Experiment(ExperimentDescription description) {
        this.description = description;
        this.results = new ArrayList<>();

        this.truePositiveStats = null;
        this.falsePositiveStats = null;
        this.falseNegativeStats = null;

        this.recall = 0.0;
        this.precision = 0.0;
        this.f1Score = 0.0;
    }

    public void addResult(Result result) {
        this.results.add(result);
    }

    public void calcStats() {

        int[] tpsPerVariation = new int[this.results.size()];
        int[] fpsPerVariation = new int[this.results.size()];
        int[] fnsPerVariation = new int[this.results.size()];

        double[] recallPerVariation = new double[this.results.size()];
        double[] precisionPerVariation = new double[this.results.size()];
        double[] f1PerVariation = new double[this.results.size()];

        int i = 0;
        for(Result r : results) {
            tpsPerVariation[i] = r.getTruePositives();
            fpsPerVariation[i] = r.getFalsePositives();
            fnsPerVariation[i] = r.getFalseNegatives();
            recallPerVariation[i] = r.getRecall();
            precisionPerVariation[i] = r.getPrecision();
            f1PerVariation[i] = r.getF1Score();

            i++;
        }

        this.truePositiveStats = new Statistics("TP", tpsPerVariation);
        this.falsePositiveStats = new Statistics("FP", fpsPerVariation);
        this.falseNegativeStats = new Statistics("FN", fnsPerVariation);

        this.recall = Statistics.mean(recallPerVariation);
        this.precision = Statistics.mean(precisionPerVariation);
        this.f1Score = Statistics.mean(f1PerVariation);
    }

    public ExperimentDescription getDescription() {
        return description;
    }

    public List<Result> getResults() {
        return results;
    }


    public Statistics getTruePositiveStats() {
        return truePositiveStats;
    }

    public Statistics getFalsePositiveStats() {
        return falsePositiveStats;
    }

    public Statistics getFalseNegativeStats() {
        return falseNegativeStats;
    }

    public double getRecall() {
        return recall;
    }

    public double getPrecision() {
        return precision;
    }

    public double getF1Score() {
        return f1Score;
    }

    public String createHeaderString() {
        return this.description.createHeaderString() + "\tf1\trecall\tprecision\t" + Statistics.createHeaderString("tp_") + "\t" + Statistics.createHeaderString("fp_") + "\t" + Statistics.createHeaderString("fn_");
    }

    public String toTabSeparatedString() {
        return this.description.toTabSeparatedString() + "\t" + f1Score + "\t" + recall + "\t" + precision + "\t" + truePositivesToTabSeparatedString() + "\t" + falsePositivesToTabSeparatedString() + "\t" + falseNegativesToTabSeparatedString();
    }

    public String truePositivesToTabSeparatedString() {
        if (this.description == null || this.falsePositiveStats == null)
            return "";

        return this.truePositiveStats.createTabSeparatedString();
    }

    public String falsePositivesToTabSeparatedString() {
        if (this.description == null || this.falsePositiveStats == null)
            return "";

        return this.falsePositiveStats.createTabSeparatedString();
    }

    public String falseNegativesToTabSeparatedString() {
        if (this.description == null || this.falseNegativeStats == null)
            return "";

        return this.falseNegativeStats.createTabSeparatedString();
    }

    public void save(File summaryFile) throws IOException {
        List<String> report = new ArrayList<String>();
        report.addAll(this.description.getReportLines());
        report.add("");
        report.add("Recall:\t" + this.recall);
        report.add("Precision:\t" + this.precision);
        report.add("F1 Score:\t" + this.f1Score);
        report.add("");
        report.add("Stat\tTP\tFP\tFN");
        report.add("Mean:\t" + this.truePositiveStats.getMean() + "\t" + this.falsePositiveStats.getMean() + "\t" + this.falseNegativeStats.getMean());
        report.add("Sum:\t" + this.truePositiveStats.getSum() + "\t" + this.falsePositiveStats.getSum() + "\t" + this.falseNegativeStats.getSum());
        report.add("Std Dev:\t" + this.truePositiveStats.getStddev() + "\t" + this.falsePositiveStats.getStddev() + "\t" + this.falseNegativeStats.getStddev());

        FileUtils.writeLines(summaryFile, report);
    }
}

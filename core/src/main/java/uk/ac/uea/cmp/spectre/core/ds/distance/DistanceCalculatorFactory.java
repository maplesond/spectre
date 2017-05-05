package uk.ac.uea.cmp.spectre.core.ds.distance;

import org.apache.commons.lang3.StringUtils;
import uk.ac.uea.cmp.spectre.core.ds.Sequences;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maplesod on 04/05/17.
 */
public enum DistanceCalculatorFactory {
    UNCORRECTED {
        @Override
        public DistanceMatrix createDistanceMatrix(Sequences seqs) {
            return new HammingDistanceCalculator().generateDistances(seqs);
        }

        @Override
        public double calculateDistance(String s1, String s2) {
            return new HammingDistanceCalculator().calculateDistance(s1, s2);
        }
    },
    JUKES_CANTOR {
        @Override
        public DistanceMatrix createDistanceMatrix(Sequences seqs) {
            return new JukesCantorCalculator().generateDistances(seqs);
        }

        @Override
        public double calculateDistance(String s1, String s2) {
            return new JukesCantorCalculator().calculateDistance(s1, s2);
        }
    },
    K80 {
        @Override
        public DistanceMatrix createDistanceMatrix(Sequences seqs) {
            return new K80Calculator().generateDistances(seqs);
        }

        @Override
        public double calculateDistance(String s1, String s2) {
            return new K80Calculator().calculateDistance(s1, s2);
        }
    };

    public abstract DistanceMatrix createDistanceMatrix(Sequences seqs);

    public abstract double calculateDistance(String s1, String s2);

    public static String toListString() {

        List<String> list = new ArrayList<>();

        for (DistanceCalculatorFactory prf : DistanceCalculatorFactory.values()) {
            list.add(prf.toString());
        }

        return "[" + StringUtils.join(list, ", ") + "]";
    }
}

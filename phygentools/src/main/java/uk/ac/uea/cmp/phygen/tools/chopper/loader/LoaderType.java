package uk.ac.uea.cmp.phygen.tools.chopper.loader;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 25/09/13
 * Time: 21:19
 * To change this template use File | Settings | File Templates.
 */
public enum LoaderType {
    SCRIPT {

        public Source getLoader() {
            throw new UnsupportedOperationException("Can't get a loader from SCIPT mode.");
        }

        public String getDescriptiveName() {
            return "script";
        }
    },
    NEWICK {

        public Source getLoader() {
            return new TreeLoader();
        }

        public String getDescriptiveName() {
            return "newick";
        }
    },
    Q_WEIGHTS {

        public Source getLoader() {
            return new QWeightLoader();
        }

        public String getDescriptiveName() {
            return "qweights";
        }
    },
    NEXUS_ST_SPLITS {

        public Source getLoader() {
            return new NexusSplitsLoader();
        }

        public String getDescriptiveName() {
            return "nexus:st_splits";
        }
    },
    NEXUS_ST_QUARTETS {

        public Source getLoader() {
            return new NexusQuartetLoader();
        }

        public String getDescriptiveName() {
            return "nexus:st_quartets";
        }
    },
    NEXUS_TREES {

        public Source getLoader() {
            return new TreeFileLoader();
        }

        public String getDescriptiveName() {
            return "nexus:trees";
        }
    },
    NEXUS_DISTANCES {

        public Source getLoader() {
            return new NexusDistancesLoader();
        }

        public String getDescriptiveName() {
            return "nexus:distances";
        }
    };

    public abstract Source getLoader();

    public abstract String getDescriptiveName();

    public static LoaderType valueOfDescriptiveName(String name) {
        for (LoaderType t : LoaderType.values()) {
            if (t.getDescriptiveName().equalsIgnoreCase(name)) {
                return t;
            }
        }

        throw new IllegalArgumentException("Unknown type");
    }

    public static String listTypes() {
        List<String> typeStrings = new ArrayList<String>();

        for(LoaderType t : LoaderType.values()) {
            typeStrings.add(t.name());
        }

        return "[" + StringUtils.join(typeStrings, ", ") + "]";
    }
}

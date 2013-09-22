package uk.ac.uea.cmp.phygen.core.math.optimise;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 13/09/13
 * Time: 00:00
 * To change this template use File | Settings | File Templates.
 */
public class OptimiserFactory {

    private static OptimiserFactory instance;
    private ServiceLoader<Optimiser> loader;

    public OptimiserFactory() {
        this.loader = ServiceLoader.load(Optimiser.class);
    }

    public static synchronized OptimiserFactory getInstance() {
        if (instance == null) {
            instance = new OptimiserFactory();
        }
        return instance;
    }



    public Optimiser createOptimiserInstance(String name, Objective objective) throws OptimiserException {

        for(Optimiser optimiser : loader) {

            if (optimiser.acceptsIdentifier(name)) {

                // Check if the requested objective is supported and set it if so
                optimiser.setObjective(objective);

                // Initialise the optimiser if necessary
                if (optimiser.requiresInitialisation()) {
                    optimiser.initialise();
                }

                // Check the optimiser is operational
                if (!optimiser.isOperational()) {
                    throw new UnsupportedOperationException(optimiser.getDescription() + " is not operational");
                }

                // Create the appropriate optimiser, based on the objective if required
                return optimiser.hasObjectiveFactory() ? optimiser.getObjectiveFactory().create(objective) : optimiser;
            }
        }

        return null;
    }

    /**
     * Goes through all optimisers found on the classpath and checks to see if they are operational.  Returns a list as
     * a string of all operational optimisers
     *
     * @return
     */
    public String listOperationalOptimisersAsString() {

        List<String> typeStrings = listOperationalOptimisers();

        return "[" + StringUtils.join(typeStrings, ", ") + "]";
    }

    public List<String> listOperationalOptimisers() {

        List<Optimiser> operationalOptimisers = getOperationalOptimisers();

        List<String> typeStrings = new ArrayList<String>();

        for(Optimiser optimiser : operationalOptimisers) {
            typeStrings.add(optimiser.getDescription());
        }

        return typeStrings;
    }


    public List<Optimiser> getOperationalOptimisers() {
        return getOperationalOptimisers(null);
    }

    public List<Optimiser> getOperationalOptimisers(Objective objective) {

        Iterator<Optimiser> it = loader.iterator();

        List<Optimiser> optimiserList = new ArrayList<Optimiser>();

        while (it.hasNext()) {
            Optimiser optimiser = it.next();
            if (optimiser.isOperational() &&
                    (objective == null || (objective != null && optimiser.acceptsObjective(objective))))  {
                optimiserList.add(optimiser);
            }
        }

        return optimiserList;
    }

}

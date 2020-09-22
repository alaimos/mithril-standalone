package com.alaimos.PHENSIM.PathwayEnricher;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.ServiceLoader;

/**
 * This object is used to instantiate Pathway Enricher Objects
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.2.0.0
 * @since 19/08/2020
 */
public class PathwayEnricherFactory {

    private static PathwayEnricherFactory instance = new PathwayEnricherFactory();

    private ServiceLoader<PathwayEnricherInterface> loader;

    private HashMap<String, PathwayEnricherInterface> services = new HashMap<>();

    /**
     * Returns the instance of this object
     *
     * @return instance of this object
     */
    public static PathwayEnricherFactory getInstance() {
        return instance;
    }

    /**
     * Loads all PathwayEnrichers
     */
    private PathwayEnricherFactory() {
        loader = ServiceLoader.load(PathwayEnricherInterface.class);
        loader.forEach(s -> {
            String shortName = s.getShortName();
            if (shortName != null) {
                if (services.containsKey(shortName)) {
                    throw new RuntimeException("Duplicated service name \"" + shortName + "\"");
                }
                services.put(shortName, s);
            }
        });
    }

    /**
     * Gets an enricher by short name
     *
     * @param shortName the name
     * @return the enricher
     */
    @NotNull
    public PathwayEnricherInterface getPathwayEnricher(String shortName) {
        if (!services.containsKey(shortName)) throw new RuntimeException("Unknown enricher \"" + shortName + "\".");
        return services.get(shortName);
    }

    /**
     * Returns an array that contains the names and descriptions of all available enrichers.
     *
     * @return an array
     */
    public String[][] getAllEnrichers() {
        return services.values().stream().map(s -> new String[]{s.getShortName(), s.getDescription()})
                       .toArray(String[][]::new);
    }

}

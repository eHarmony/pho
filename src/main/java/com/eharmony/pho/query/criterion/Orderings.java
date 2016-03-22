package com.eharmony.pho.query.criterion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A List of Ordering criteria.
 * 
 * Encapsulated for type safety and to combat type erasure.
 */
public class Orderings {

    private final List<Ordering> orderings = new ArrayList<Ordering>();

    public Orderings() {
    }

    public Orderings(Ordering... orderings) {
        this.orderings.addAll(Arrays.asList(orderings));
    }

    public void add(Ordering ordering) {
        orderings.add(ordering);
    }

    // Discuss: defensive copy?
    public List<Ordering> get() {
        return orderings;
    }

    @Override
    public String toString() {
        return "Orderings [" + orderings + "]";
    }
}

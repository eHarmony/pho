package com.eharmony.pho.query.criterion;

/**
 * Ordering criterion
 */
public class Ordering implements Criterion, WithProperty {

    public static enum Order {
        ASCENDING, DESCENDING
    }

    private final String propertyName;
    private final Order order;

    public static Ordering asc(String propertyName) {
        return new Ordering(propertyName, Order.ASCENDING);
    }

    public static Ordering desc(String propertyName) {
        return new Ordering(propertyName, Order.DESCENDING);
    }

    public Ordering(String propertyName, Order order) {
        this.propertyName = propertyName;
        this.order = order;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Order getOrder() {
        return order;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((order == null) ? 0 : order.hashCode());
        result = prime * result + ((propertyName == null) ? 0 : propertyName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Ordering other = (Ordering) obj;
        if (order != other.order)
            return false;
        if (propertyName == null) {
            if (other.propertyName != null)
                return false;
        } else if (!propertyName.equals(other.propertyName))
            return false;
        return true;
    }

}

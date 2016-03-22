
package com.eharmony.pho.api;

/**
 * An exception that indicates a problem loading from or storing to a data
 * store. It presents a uniform abstraction across things like
 * {@link java.sql.SQLException}.
 */
public class DataStoreException extends RuntimeException {

    private static final long serialVersionUID = -1363433871972493184L;

    public DataStoreException() {
        super();
    }

    public DataStoreException(String message) {
        super(message);
    }

    public DataStoreException(Throwable cause) {
        super(cause);
    }

    public DataStoreException(String message, Throwable cause) {
        super(message, cause);
    }

}

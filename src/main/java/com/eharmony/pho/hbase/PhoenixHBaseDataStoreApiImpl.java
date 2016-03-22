package com.eharmony.pho.hbase;

import java.sql.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eharmony.pho.api.DataStoreApi;
import com.eharmony.pho.hbase.query.PhoenixHBaseQueryExecutor;
import com.eharmony.pho.hbase.util.PhoenixConnectionManager;
import com.eharmony.pho.query.QuerySelect;
import com.eharmony.pho.query.builder.QueryBuilder;
import com.google.common.base.Preconditions;

/**
 * Datastore api implementation for HBase store. Using apache phoenix (http://phoenix.apache.org/) as sql layer to hbase
 * 
 * @author vvangapandu
 *
 */
public class PhoenixHBaseDataStoreApiImpl implements DataStoreApi {

    private final PhoenixHBaseQueryExecutor queryExecutor;
    private final String connectionUrl;
    private static final Logger logger = LoggerFactory.getLogger(PhoenixHBaseDataStoreApiImpl.class);

    public PhoenixHBaseDataStoreApiImpl(final String connectionUrl, final PhoenixHBaseQueryExecutor queryExecutor)
            throws Exception {
        this.connectionUrl = connectionUrl;
        this.queryExecutor = Preconditions.checkNotNull(queryExecutor);
        // Below code will ensure that connection is string is valid, if not will stop the context loading
        Connection conn = PhoenixConnectionManager.getConnection(connectionUrl);
        if (conn == null) {
            throw new IllegalStateException("unable to create phoenix connection with given url :" + connectionUrl);
        } else {
            closeConnectionSafe(conn);
        }
    }

    public <T> T save(T entity) {
        Connection conn = null;
        try {
            conn = PhoenixConnectionManager.getConnection(connectionUrl);
            T returnEntity = queryExecutor.save(entity, conn);
            conn.commit();
            return returnEntity;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            closeConnectionSafe(conn);
        }
    }

    private void closeConnectionSafe(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (Exception ex) {
            logger.warn("Exception while closing the connection...", ex.getMessage());
        }
    }

    public <T> Iterable<T> save(Iterable<T> entities) {
        Connection conn = null;
        try {
            conn = PhoenixConnectionManager.getConnection(connectionUrl);
            Iterable<T> results = queryExecutor.save(entities, conn);
            conn.commit();
            return results;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            closeConnectionSafe(conn);
        }
    }

    @Override
    public <T> int[] saveBatch(Iterable<T> entities) {
        Connection conn = null;
        try {
            conn = PhoenixConnectionManager.getConnection(connectionUrl);
            int[] results = queryExecutor.saveBatch(entities, conn);
            conn.commit();
            return results;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            closeConnectionSafe(conn);
        }
    }

    public <T, R> Iterable<R> findAll(QuerySelect<T, R> query) {
        Connection conn = null;
        try {
            conn = PhoenixConnectionManager.getConnection(connectionUrl);
            return queryExecutor.find(query, conn);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            closeConnectionSafe(conn);
        }
    }

    public <T, R> R findOne(QuerySelect<T, R> query) {
        Connection conn = null;
        try {
            conn = PhoenixConnectionManager.getConnection(connectionUrl);
            return queryExecutor.findOne(query, conn);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            closeConnectionSafe(conn);
        }
    }

    public <T> Iterable<T> findAllEntities(String key, Class<T> clz, String[] projection) throws Exception {
        Connection conn = null;
        try {
            conn = PhoenixConnectionManager.getConnection(connectionUrl);
            QueryBuilder<T, T> builder = new QueryBuilder<T, T>(clz, clz);
            builder.setReturnFields(projection);
            QuerySelect<T, T> query = builder.build();
            return queryExecutor.find(query, conn);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            closeConnectionSafe(conn);
        }
    }

}

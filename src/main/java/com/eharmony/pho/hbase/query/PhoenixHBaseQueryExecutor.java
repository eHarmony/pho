package com.eharmony.pho.hbase.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eharmony.pho.api.DataStoreException;
import com.eharmony.pho.hbase.mapper.PhoenixProjectedResultMapper;
import com.eharmony.pho.hbase.translator.PhoenixHBaseQueryTranslator;
import com.eharmony.pho.query.QuerySelect;
import com.eharmony.pho.query.QueryUpdate;
import com.eharmony.pho.query.builder.QueryUpdateBuilder;
import com.google.common.base.Preconditions;

/**
 * Executes the query on hbase through phoenix query server after translation using query translator. Select query
 * results will be mapped back to result object using result mapper.
 * 
 * @author vvangapandu
 *
 */
public class PhoenixHBaseQueryExecutor {

    private static final Logger log = LoggerFactory.getLogger(PhoenixHBaseQueryExecutor.class);

    private final PhoenixHBaseQueryTranslator queryTranslator;
    private final PhoenixProjectedResultMapper resultMapper;
    private boolean showSQL = true;

    public PhoenixHBaseQueryExecutor(final PhoenixHBaseQueryTranslator queryTranslator,
            final PhoenixProjectedResultMapper resultMapper) {
        this.queryTranslator = Preconditions.checkNotNull(queryTranslator);
        this.resultMapper = Preconditions.checkNotNull(resultMapper);
    }

    public <T, R> Iterable<R> find(QuerySelect<T, R> query, Connection conn) throws SQLException {
        ResultSet resultSet = null;
        Statement statement = null;
        try {
            String queryStr = queryTranslator.translate(query);
            if (showSQL) {
                log.info("Query String: {}", queryStr);
            }
            statement = conn.createStatement();
            resultSet = statement.executeQuery(queryStr);
            return resultMapper.mapResults(resultSet, query.getReturnType());
        } catch (final Exception hx) {
            throw new DataStoreException(hx.getMessage(), hx);
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
        }
    }

    private void closeStatementSafe(PreparedStatement ps) {
        if (ps != null) {
            try {
                ps.close();
            } catch (Exception ex) {
                log.warn("Exception while closing the PreparedStatement...", ex);
            }
        }
    }

    public <T, R> R findOne(QuerySelect<T, R> query, Connection conn) {
        try {
            Iterable<R> results = find(query, conn);
            if (results != null && results.iterator() != null && results.iterator().hasNext()) {
                return results.iterator().next();
            }
        } catch (final Exception hx) {
            throw new DataStoreException(hx.getMessage(), hx);
        }
        return null;
    }

    @SuppressWarnings("resource")
    public <T> T save(QueryUpdate<T> query, Connection conn) {
        PreparedStatement ps = null;
        try {
            String queryStr = queryTranslator.translate(query);
            if (showSQL) {
                log.info("Query String {}", queryStr);
            }
            ps = conn.prepareStatement(queryStr);
            int result = ps.executeUpdate();
            if (result == 0) {
                throw new DataStoreException("Save Failed for query...");
            }
            return null;
        } catch (final Exception hx) {
            throw new DataStoreException(hx.getMessage(), hx);
        } finally {
            closeStatementSafe(ps);
        }
    }

    @SuppressWarnings("resource")
    public <T> T save(T entity, Connection conn) {
        PreparedStatement ps = null;
        try {
            QueryUpdate<T> query = QueryUpdateBuilder.builderFor(entity).build();
            String queryStr = queryTranslator.translate(query);
            if (showSQL) {
                log.info("Query String {}", queryStr);
            }
            ps = conn.prepareStatement(queryStr);
            int result = ps.executeUpdate();
            if (result == 0) {
                throw new DataStoreException("Save Failed for query...");
            }
            return entity;
        } catch (final Exception hx) {
            throw new DataStoreException(hx.getMessage(), hx);
        } finally {
            closeStatementSafe(ps);
        }
    }

    public <T> Iterable<T> save(Iterable<T> entities, Connection conn) {
        try {
            final List<T> saved = new ArrayList<T>();
            for (final T entity : entities) {
                saved.add(this.save(entity, conn));
            }
            return saved;
        } catch (final Exception hx) {
            throw new DataStoreException(hx.getMessage(), hx);
        }
    }

    @SuppressWarnings("resource")
    public <T> int[] saveBatch(Iterable<T> entities, Connection conn) {
        PreparedStatement ps = null;
        try {
            ps = buildStatementWithBatch(entities, conn);
            return ps.executeBatch();
        } catch (final Exception hx) {
            throw new DataStoreException(hx.getMessage(), hx);
        } finally {
            closeStatementSafe(ps);
        }
    }

    public <T> PreparedStatement buildStatementWithBatch(Iterable<T> entities, Connection conn) throws SQLException {
        PreparedStatement preparedStatement = null;
        for (final T entity : entities) {
            QueryUpdate<T> query = QueryUpdateBuilder.builderFor(entity).build();
            String queryStr = queryTranslator.translate(query);
            if (showSQL) {
                log.info("Query String {}", queryStr);
            }
            if (preparedStatement == null) {
                preparedStatement = conn.prepareStatement(queryStr);
            }
            preparedStatement.addBatch(queryStr);
        }
        return preparedStatement;
    }

    protected PhoenixProjectedResultMapper getMapper() {
        return resultMapper;
    }

    public boolean isShowSQL() {
        return showSQL;
    }

    public void setShowSQL(boolean showSQL) {
        this.showSQL = showSQL;
    }

}

package com.eharmony.pho.hbase.util;

import java.sql.Connection;
import java.sql.DriverManager;
/**
 * Manages the phoenix connections based on JDBC driver
 * 
 * @author vvangapandu
 *
 */
public class PhoenixConnectionManager {

    public static Connection getConnection(final String connectionString) throws Exception {
        Class.forName("org.apache.phoenix.jdbc.PhoenixDriver");
        return DriverManager.getConnection(connectionString);
    }
}

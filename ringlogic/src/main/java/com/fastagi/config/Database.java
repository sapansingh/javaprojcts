package com.fastagi.config;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class Database {

    private static HikariDataSource ds;

    static {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:mysql://192.168.29.72:3306/Synapse_cc");
            config.setUsername("asterisk");
            config.setPassword("asterisk");
            config.setMaximumPoolSize(10);  // max concurrent connections
            config.setMinimumIdle(2);       // minimum idle connections
            config.setIdleTimeout(300000);  // 5 minutes
            config.setConnectionTimeout(30000); // 30 seconds
            config.setLeakDetectionThreshold(60000); // detect leaks > 60s

            ds = new HikariDataSource(config);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize HikariCP pool", e);
        }
    }

    private Database() {}

    public static DataSource getDataSource() {
        return ds;
    }
}

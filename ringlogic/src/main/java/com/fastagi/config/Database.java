package com.fastagi.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Properties;

public class Database {

    private static HikariDataSource ds;

    static {
        try {
            Properties props = new Properties();

            try (InputStream is = Database.class
                    .getClassLoader()
                    .getResourceAsStream("db.properties")) {

                if (is == null) {
                    throw new RuntimeException("db.properties not found in classpath");
                }
                props.load(is);
            }

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(props.getProperty("db.url"));
            config.setUsername(props.getProperty("db.username"));
            config.setPassword(props.getProperty("db.password"));

            config.setMaximumPoolSize(
                    Integer.parseInt(props.getProperty("db.pool.maxSize"))
            );
            config.setMinimumIdle(
                    Integer.parseInt(props.getProperty("db.pool.minIdle"))
            );
            config.setIdleTimeout(
                    Long.parseLong(props.getProperty("db.pool.idleTimeout"))
            );
            config.setConnectionTimeout(
                    Long.parseLong(props.getProperty("db.pool.connectionTimeout"))
            );
            config.setLeakDetectionThreshold(
                    Long.parseLong(props.getProperty("db.pool.leakDetection"))
            );

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

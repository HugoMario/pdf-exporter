package com.usmon.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseManager {

    private static final Logger log = LoggerFactory.getLogger(DatabaseManager.class);

    private final HikariDataSource dataSource;

    public DatabaseManager() {
        Properties props = loadProperties();
        HikariConfig config = new HikariConfig();

        String host     = props.getProperty("db.host", "localhost");
        String port     = props.getProperty("db.port", "1433");
        String database = props.getProperty("db.name");
        String user     = props.getProperty("db.username");
        String password = props.getProperty("db.password");

        String url = String.format(
                "jdbc:sqlserver://%s:%s;databaseName=%s;encrypt=true;trustServerCertificate=true",
                host, port, database);

        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);
        config.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30_000);
        config.setIdleTimeout(600_000);
        config.setMaxLifetime(1_800_000);
        config.setPoolName("SqlServerPool");

        this.dataSource = new HikariDataSource(config);
        log.info("Connection pool initialized — {}", url);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    private Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new IllegalStateException("db.properties not found on classpath");
            }
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load db.properties", e);
        }
        return props;
    }
}

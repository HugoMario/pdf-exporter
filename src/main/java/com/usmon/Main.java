package com.usmon;

import com.usmon.db.DatabaseManager;
import com.usmon.report.ReportExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("PDF Exporter starting...");

        DatabaseManager dbManager = new DatabaseManager();

        try (Connection connection = dbManager.getConnection()) {
            log.info("Database connection established successfully");

            ReportExporter exporter = new ReportExporter();
            exporter.exportToPdf(connection, "reports/sample_report.jrxml", "output/report.pdf");

            log.info("Report exported successfully");
        } catch (Exception e) {
            log.error("Failed to generate report", e);
            System.exit(1);
        }
    }
}

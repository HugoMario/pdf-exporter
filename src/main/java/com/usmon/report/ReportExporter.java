package com.usmon.report;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class ReportExporter {

    private static final Logger log = LoggerFactory.getLogger(ReportExporter.class);

    /**
     * Compiles a .jrxml template, fills it with data from the given connection,
     * and writes a PDF to the specified output path.
     *
     * @param connection  active JDBC connection used as the report data source
     * @param jrxmlPath   classpath-relative path to the .jrxml template
     * @param outputPath  filesystem path where the PDF should be written
     */
    public void exportToPdf(Connection connection, String jrxmlPath, String outputPath) throws JRException {
        log.info("Compiling report template: {}", jrxmlPath);
        JasperReport jasperReport = compileReport(jrxmlPath);

        Map<String, Object> parameters = new HashMap<>();

        log.info("Filling report with data...");
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);

        ensureOutputDirectory(outputPath);

        log.info("Exporting PDF to: {}", outputPath);
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputPath));

        SimplePdfExporterConfiguration config = new SimplePdfExporterConfiguration();
        config.setCompressed(true);
        exporter.setConfiguration(config);

        exporter.exportReport();
        log.info("PDF export complete: {}", outputPath);
    }

    private JasperReport compileReport(String jrxmlPath) throws JRException {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(jrxmlPath);
        if (stream == null) {
            throw new IllegalArgumentException("Report template not found on classpath: " + jrxmlPath);
        }
        return JasperCompileManager.compileReport(stream);
    }

    private void ensureOutputDirectory(String outputPath) {
        File outputFile = new File(outputPath);
        File parent = outputFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }
}

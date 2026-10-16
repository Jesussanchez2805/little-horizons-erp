package com.littlehorizons.erp.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Centralized logging utility for the Little Horizons ERP system.
 * Appends application events, warnings, and severe errors into 'error_log.txt'
 * while also printing formatted output to the console.
 * 
 * Follows the Single Responsibility Principle (SRP) by managing all file-based
 * logging operations in one place.
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public final class AppLogger {

    private static final String LOG_FILE = "error_log.txt";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private AppLogger() {
        // Private constructor to prevent instantiation (Utility class pattern)
    }

    /**
     * Logs an informational message.
     *
     * @param message Description of the event
     */
    public static void info(String message) {
        log("INFO", message, null);
    }

    /**
     * Logs a warning event that does not immediately interrupt system execution.
     *
     * @param message Description of the warning
     */
    public static void warning(String message) {
        log("WARNING", message, null);
    }

    /**
     * Logs a severe error with its associated exception and stack trace.
     *
     * @param message Error description
     * @param throwable Exception caught
     */
    public static void severe(String message, Throwable throwable) {
        log("SEVERE", message, throwable);
    }

    /**
     * Internal synchronized method to write formatted log entries to disk.
     *
     * @param level Log severity level
     * @param message Text message
     * @param throwable Optional exception
     */
    private static synchronized void log(String level, String message, Throwable throwable) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String logLine = String.format("[%s] [%-7s] %s", timestamp, level, message);

        // Write to log file
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(logLine);
            if (throwable != null) {
                throwable.printStackTrace(pw);
            }
        } catch (IOException e) {
            System.err.println("CRITICAL: Failed to write to log file '" + LOG_FILE + "': " + e.getMessage());
        }

        // Print severe messages to standard error for console visibility
        if ("SEVERE".equalsIgnoreCase(level)) {
            System.err.println(logLine);
            if (throwable != null) {
                System.err.println("  Details: " + throwable.getMessage());
            }
        }
    }
}

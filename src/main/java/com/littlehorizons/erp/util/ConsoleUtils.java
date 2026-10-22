package com.littlehorizons.erp.util;

import java.util.List;

/**
 * Console presentation utility for formatting menus, boxes, headers,
 * alerts, and tabular data in a clean, professional aesthetic.
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public final class ConsoleUtils {

    public static final int DEFAULT_WIDTH = 70;

    private ConsoleUtils() {
        // Prevent instantiation
    }

    /**
     * Prints a stylized main banner box for menus and headers.
     *
     * @param title Title text to center
     * @param subtitle Optional subtitle text to center (can be null)
     */
    public static void printHeader(String title, String subtitle) {
        int width = DEFAULT_WIDTH;
        System.out.println();
        System.out.println("╔" + "═".repeat(width - 2) + "╗");
        printCenteredLine(" " + title + " ", width);
        if (subtitle != null && !subtitle.isBlank()) {
            printCenteredLine(" " + subtitle + " ", width);
        }
        System.out.println("╚" + "═".repeat(width - 2) + "╝");
    }

    /**
     * Prints a centered line bounded by double vertical bars.
     */
    private static void printCenteredLine(String text, int width) {
        int innerWidth = width - 2;
        if (text.length() > innerWidth) {
            text = text.substring(0, innerWidth);
        }
        int totalPadding = innerWidth - text.length();
        int leftPadding = totalPadding / 2;
        int rightPadding = totalPadding - leftPadding;
        System.out.println("║" + " ".repeat(leftPadding) + text + " ".repeat(rightPadding) + "║");
    }

    /**
     * Prints a section title divider line.
     *
     * @param title Section name
     */
    public static void printSection(String title) {
        System.out.println();
        System.out.println("─── " + title + " " + "─".repeat(Math.max(0, DEFAULT_WIDTH - title.length() - 5)));
    }

    /**
     * Prints a success alert message.
     *
     * @param message Text to display
     */
    public static void printSuccess(String message) {
        System.out.println("✔ [ÉXITO] " + message);
    }

    /**
     * Prints an error alert message.
     *
     * @param message Text to display
     */
    public static void printError(String message) {
        System.out.println("✖ [ERROR] " + message);
    }

    /**
     * Prints an informational alert message.
     *
     * @param message Text to display
     */
    public static void printInfo(String message) {
        System.out.println("ℹ [INFO] " + message);
    }

    /**
     * Prints a warning alert message.
     *
     * @param message Text to display
     */
    public static void printWarning(String message) {
        System.out.println("⚠ [AVISO] " + message);
    }

    /**
     * Formats and prints tabular data with automated column sizing and borders.
     *
     * @param headers Column titles
     * @param rows Table rows containing string cell values
     */
    public static void printTable(List<String> headers, List<List<String>> rows) {
        if (headers == null || headers.isEmpty()) {
            return;
        }

        int cols = headers.size();
        int[] colWidths = new int[cols];

        for (int i = 0; i < cols; i++) {
            colWidths[i] = headers.get(i).length();
        }

        if (rows != null) {
            for (List<String> row : rows) {
                for (int i = 0; i < cols && i < row.size(); i++) {
                    String cell = row.get(i) != null ? row.get(i) : "";
                    if (cell.length() > colWidths[i]) {
                        colWidths[i] = Math.min(cell.length(), 40); // Cap column width at 40
                    }
                }
            }
        }

        // Top border
        printTableDivider(colWidths, "┌", "┬", "┐");

        // Headers
        System.out.print("│");
        for (int i = 0; i < cols; i++) {
            System.out.print(" " + padRight(headers.get(i), colWidths[i]) + " │");
        }
        System.out.println();

        // Header separator
        printTableDivider(colWidths, "├", "┼", "┤");

        // Data rows
        if (rows == null || rows.isEmpty()) {
            System.out.print("│");
            int totalInner = 0;
            for (int w : colWidths) totalInner += (w + 3);
            totalInner -= 1;
            System.out.println(padCenter("No se encontraron registros", totalInner) + "│");
        } else {
            for (List<String> row : rows) {
                System.out.print("│");
                for (int i = 0; i < cols; i++) {
                    String val = (i < row.size() && row.get(i) != null) ? row.get(i) : "";
                    if (val.length() > colWidths[i]) {
                        val = val.substring(0, colWidths[i] - 3) + "...";
                    }
                    System.out.print(" " + padRight(val, colWidths[i]) + " │");
                }
                System.out.println();
            }
        }

        // Bottom border
        printTableDivider(colWidths, "└", "┴", "┘");
    }

    private static void printTableDivider(int[] widths, String left, String mid, String right) {
        StringBuilder sb = new StringBuilder(left);
        for (int i = 0; i < widths.length; i++) {
            sb.append("─".repeat(widths[i] + 2));
            if (i < widths.length - 1) {
                sb.append(mid);
            }
        }
        sb.append(right);
        System.out.println(sb);
    }

    public static String padRight(String s, int n) {
        if (s == null) s = "";
        return String.format("%-" + n + "s", s);
    }

    public static String padCenter(String s, int n) {
        if (s == null) s = "";
        if (s.length() >= n) return s.substring(0, n);
        int pad = n - s.length();
        int left = pad / 2;
        int right = pad - left;
        return " ".repeat(left) + s + " ".repeat(right);
    }
}

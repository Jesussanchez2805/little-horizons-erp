package com.littlehorizons.erp.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Robust console input reader and validator.
 * Prevents input mismatch exceptions and system crashes by validating
 * user input in retry loops until correct values are provided.
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public final class InputValidator {

    private static final Scanner SCANNER = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private InputValidator() {
        // Prevent instantiation
    }

    /**
     * Reads a non-empty string from the user.
     *
     * @param prompt Message displayed to the user
     * @return Trimmed non-empty string
     */
    public static String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            String line = SCANNER.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            }
            ConsoleUtils.printError("El valor no puede estar vacío. Intente nuevamente.");
        }
    }

    /**
     * Reads an optional string from the user. If blank, returns the default value.
     *
     * @param prompt Message displayed
     * @param defaultValue Value to return if left blank
     * @return User string or defaultValue
     */
    public static String readOptionalString(String prompt, String defaultValue) {
        System.out.print(prompt + (defaultValue != null ? " [" + defaultValue + "]" : "") + ": ");
        String line = SCANNER.nextLine().trim();
        return line.isEmpty() ? defaultValue : line;
    }

    /**
     * Reads an integer within the specified range [min, max].
     *
     * @param prompt Prompt message
     * @param min Minimum acceptable integer
     * @param max Maximum acceptable integer
     * @return Validated integer
     */
    public static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt + " (" + min + "-" + max + "): ");
            String line = SCANNER.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                }
                ConsoleUtils.printError(String.format("El valor debe estar entre %d y %d.", min, max));
            } catch (NumberFormatException e) {
                ConsoleUtils.printError("Formato numérico inválido. Por favor ingrese un número entero.");
            }
        }
    }

    /**
     * Reads a double / decimal number within the specified range [min, max].
     *
     * @param prompt Prompt message
     * @param min Minimum double
     * @param max Maximum double
     * @return Validated double value
     */
    public static double readDouble(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt + " (" + min + " - " + max + "): ");
            String line = SCANNER.nextLine().trim().replace(',', '.');
            try {
                double value = Double.parseDouble(line);
                if (value >= min && value <= max) {
                    return value;
                }
                ConsoleUtils.printError(String.format("El valor debe estar entre %.1f y %.1f.", min, max));
            } catch (NumberFormatException e) {
                ConsoleUtils.printError("Número decimal inválido. Ingrese un valor válido (ej. 4.5).");
            }
        }
    }

    /**
     * Reads a monetary BigDecimal value greater than or equal to zero.
     *
     * @param prompt Prompt message
     * @return Validated BigDecimal
     */
    public static BigDecimal readBigDecimal(String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            String line = SCANNER.nextLine().trim().replace(',', '.');
            try {
                BigDecimal value = new BigDecimal(line);
                if (value.compareTo(BigDecimal.ZERO) >= 0) {
                    return value;
                }
                ConsoleUtils.printError("El monto debe ser mayor o igual a 0.");
            } catch (NumberFormatException e) {
                ConsoleUtils.printError("Monto monetario inválido (ej. 380000.00).");
            }
        }
    }

    /**
     * Reads a valid date formatted as yyyy-MM-dd.
     *
     * @param prompt Prompt message
     * @return Validated LocalDate
     */
    public static LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt + " (AAAA-MM-DD): ");
            String line = SCANNER.nextLine().trim();
            try {
                return LocalDate.parse(line, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                ConsoleUtils.printError("Formato de fecha inválido. Formato esperado AAAA-MM-DD (ej. 2024-03-15).");
            }
        }
    }

    /**
     * Reads an optional date formatted as yyyy-MM-dd. Returns null if blank.
     */
    public static LocalDate readOptionalDate(String prompt) {
        while (true) {
            System.out.print(prompt + " (AAAA-MM-DD o dejar vacío): ");
            String line = SCANNER.nextLine().trim();
            if (line.isEmpty()) {
                return null;
            }
            try {
                return LocalDate.parse(line, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                ConsoleUtils.printError("Formato de fecha inválido. Formato esperado AAAA-MM-DD.");
            }
        }
    }

    /**
     * Reads an email address validating format.
     *
     * @param prompt Prompt message
     * @return Validated email string
     */
    public static String readEmail(String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            String line = SCANNER.nextLine().trim();
            if (EMAIL_PATTERN.matcher(line).matches()) {
                return line;
            }
            ConsoleUtils.printError("Formato de correo electrónico inválido (ej. usuario@dominio.com).");
        }
    }

    /**
     * Reads a boolean yes/no choice.
     *
     * @param prompt Prompt message
     * @return true for Y/yes/S/si, false for N/no
     */
    public static boolean readBoolean(String prompt) {
        while (true) {
            System.out.print(prompt + " (S/N): ");
            String line = SCANNER.nextLine().trim().toUpperCase();
            if (line.startsWith("Y") || line.startsWith("S")) {
                return true;
            }
            if (line.startsWith("N")) {
                return false;
            }
            ConsoleUtils.printError("Por favor ingrese 'S' para Sí o 'N' para No.");
        }
    }

    /**
     * Helper to pause execution until user presses Enter.
     */
    public static void pressEnterToContinue() {
        System.out.println();
        System.out.print("Presione [Enter] para continuar...");
        SCANNER.nextLine();
    }
}

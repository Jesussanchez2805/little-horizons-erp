package com.littlehorizons.erp.util;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.HexFormat;

/**
 * Simulated CUFE generator using SHA-384, the algorithm defined by the DIAN for the real CUFE.
 * The result is a 96-character lowercase hexadecimal string (fits the VARCHAR(100) column).
 * Includes a verification method so a stored CUFE can be validated against the invoice data.
 */
public final class CufeGenerator {

    private static final String ALGORITHM = "SHA-384";
    private static final String SOFTWARE_KEY = "LITTLE_HORIZONS_ERP_KEY";

    private CufeGenerator() {
        // Utility class
    }

    public static String generate(String invoiceNumber, String nit, LocalDate issueDate,
                                  BigDecimal total, String customerDocument) {
        String raw = String.join("|", invoiceNumber, nit, issueDate.toString(),
                total.toPlainString(), customerDocument, SOFTWARE_KEY);
        try {
            byte[] hash = MessageDigest.getInstance(ALGORITHM).digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            // Every Java platform is required to support SHA-384
            throw new IllegalStateException(ALGORITHM + " is not available", e);
        }
    }

    /**
     * @return true when the given CUFE matches the one computed from the invoice data
     */
    public static boolean verify(String cufe, String invoiceNumber, String nit, LocalDate issueDate,
                                 BigDecimal total, String customerDocument) {
        if (cufe == null) {
            return false;
        }
        String expected = generate(invoiceNumber, nit, issueDate, total, customerDocument);
        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                cufe.toLowerCase().getBytes(StandardCharsets.UTF_8));
    }
}

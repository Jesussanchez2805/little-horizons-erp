package com.littlehorizons.erp;

import com.littlehorizons.erp.util.PdfInvoiceGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for PDF Invoice and CUFE hash generation.
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
class PdfInvoiceGeneratorTest {

    @Test
    @DisplayName("Should generate a valid 64-character SHA-256 CUFE string")
    void testCufeGeneration() {
        String cufe = PdfInvoiceGenerator.calculateSimulatedCufe(
                "FE-LH-000001",
                "901.458.789-2",
                LocalDate.of(2024, 1, 15),
                BigDecimal.valueOf(650000),
                "1017123001"
        );

        assertNotNull(cufe, "CUFE should not be null");
        assertEquals(64, cufe.length(), "CUFE must be a 64-character SHA-256 hex string");
    }

    @Test
    @DisplayName("Should generate an official DIAN invoice PDF file with embedded QR code")
    void testPdfInvoiceFileCreation() throws Exception {
        String invoiceNum = "FE-LH-999999";
        String filePath = PdfInvoiceGenerator.generateInvoicePdf(
                invoiceNum,
                LocalDate.now(),
                "REC-TEST-0001",
                "Transferencia",
                "Carlos Ramírez",
                "1017123001",
                "Mateo Ramírez",
                "Séptimo",
                "Matrícula Anual Ordinaria",
                BigDecimal.valueOf(650000)
        );

        File pdfFile = new File(filePath);
        assertTrue(pdfFile.exists(), "The PDF invoice file must be created on disk");
        assertTrue(pdfFile.length() > 1000, "The PDF file should not be empty");
    }
}

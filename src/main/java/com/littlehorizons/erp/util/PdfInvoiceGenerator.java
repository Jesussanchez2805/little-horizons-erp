package com.littlehorizons.erp.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * PDF Electronic Invoice Generator simulating Colombian DIAN specifications.
 * Formats institution details, customer and student info, line items,
 * calculates simulated CUFE (SHA-256 hash), generates a high-resolution QR code,
 * and renders an official PDF invoice.
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public final class PdfInvoiceGenerator {

    private static final String SCHOOL_NAME = "Colegio Internacional Little Horizons S.A.S.";
    private static final String SCHOOL_NIT = "901.458.789-2";
    private static final String SCHOOL_ADDRESS = "Calle 127 # 45-18, Bogotá D.C., Colombia";
    private static final String SCHOOL_PHONE = "+57 (601) 745-8899 / 310 456 7800";
    private static final String SCHOOL_EMAIL = "facturacion@littlehorizons.edu.co";
    private static final String DIAN_RESOLUTION = "Resolución DIAN No. 18764000123456 de 2024-01-10. Habilitación Facturación Electrónica. Rango FE-LH-000001 a FE-LH-100000.";

    private PdfInvoiceGenerator() {
        // Prevent instantiation
    }

    /**
     * Calculates the simulated CUFE (Código Único de Factura Electrónica)
     * using SHA-256 over key transaction attributes.
     *
     * @param invoiceNum Consecutivo
     * @param nit School NIT
     * @param date Issue date
     * @param total Amount
     * @param customerDoc Customer ID
     * @return 64-character hexadecimal SHA-256 string
     */
    public static String calculateSimulatedCufe(String invoiceNum, String nit, LocalDate date, BigDecimal total, String customerDoc) {
        try {
            String raw = String.format("%s|%s|%s|%s|%s|LITTLE_HORIZONS_ERP_KEY", invoiceNum, nit, date.toString(), total.toPlainString(), customerDoc);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            AppLogger.warning("Error computing simulated CUFE hash: " + e.getMessage());
            return "cufe_simulado_" + System.currentTimeMillis();
        }
    }

    /**
     * Generates a QR code image as byte array.
     */
    private static byte[] generateQrCodeBytes(String text, int width, int height) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        return pngOutputStream.toByteArray();
    }

    /**
     * Generates a complete DIAN-compliant PDF invoice and saves it to the invoices folder.
     *
     * @param invoiceNumber Consecutive invoice number (e.g. FE-LH-000123)
     * @param issueDate Date of issue
     * @param receiptNumber Payment receipt number
     * @param paymentMethod Method used (Efectivo, Transferencia, etc.)
     * @param guardianName Full name of paying guardian
     * @param guardianDoc Guardian document number
     * @param studentName Full name of enrolled student
     * @param studentGrade Grade/level of student
     * @param conceptName Description of payment concept
     * @param amount Paid amount
     * @return Absolute or relative file path of the generated PDF
     * @throws Exception If PDF generation fails
     */
    public static String generateInvoicePdf(
            String invoiceNumber,
            LocalDate issueDate,
            String receiptNumber,
            String paymentMethod,
            String guardianName,
            String guardianDoc,
            String studentName,
            String studentGrade,
            String conceptName,
            BigDecimal amount
    ) throws Exception {

        // Ensure target directory exists
        File dir = new File("invoices");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = "factura_" + invoiceNumber.replace("-", "_").toLowerCase() + ".pdf";
        String filePath = "invoices/" + fileName;

        // Calculate simulated CUFE
        String cufe = calculateSimulatedCufe(invoiceNumber, SCHOOL_NIT, issueDate, amount, guardianDoc);

        // QR Code content string following DIAN technical annex structure
        String qrContent = String.format("NumFac:%s;FecFac:%s;NitFac:%s;DocAdq:%s;ValFac:%.2f;CUFE:%s;URL:https://catalogo-vpfe.dian.gov.co/document/searchqr?documentkey=%s",
                invoiceNumber, issueDate, SCHOOL_NIT, guardianDoc, amount.doubleValue(), cufe, cufe);

        // Initialize document
        Document document = new Document(PageSize.LETTER, 36, 36, 36, 36);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        // Fonts
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new Color(18, 52, 86));
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.DARK_GRAY);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.BLACK);
        Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 7, Color.GRAY);
        Font headerCellFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);

        // Currency formatter
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));

        // Header Table (Institution Info vs Invoice Number & Badge)
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{65f, 35f});

        PdfPCell instCell = new PdfPCell();
        instCell.setBorder(Rectangle.NO_BORDER);
        instCell.addElement(new Paragraph(SCHOOL_NAME, titleFont));
        instCell.addElement(new Paragraph("NIT: " + SCHOOL_NIT + " - Régimen Común", subtitleFont));
        instCell.addElement(new Paragraph(SCHOOL_ADDRESS, normalFont));
        instCell.addElement(new Paragraph("Tel: " + SCHOOL_PHONE + " | " + SCHOOL_EMAIL, normalFont));
        headerTable.addCell(instCell);

        PdfPCell invCell = new PdfPCell();
        invCell.setBorder(Rectangle.BOX);
        invCell.setBorderColor(new Color(18, 52, 86));
        invCell.setBackgroundColor(new Color(240, 244, 248));
        invCell.setPadding(8);
        Paragraph invTitle = new Paragraph("FACTURA ELECTRÓNICA DE VENTA", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, new Color(18, 52, 86)));
        invTitle.setAlignment(Element.ALIGN_CENTER);
        Paragraph invNum = new Paragraph(invoiceNumber, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, Color.RED));
        invNum.setAlignment(Element.ALIGN_CENTER);
        Paragraph invDate = new Paragraph("Fecha: " + issueDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), normalFont);
        invDate.setAlignment(Element.ALIGN_CENTER);
        invCell.addElement(invTitle);
        invCell.addElement(invNum);
        invCell.addElement(invDate);
        headerTable.addCell(invCell);

        document.add(headerTable);
        document.add(new Paragraph(" "));

        // Customer & Student Information Table
        PdfPTable clientTable = new PdfPTable(2);
        clientTable.setWidthPercentage(100);
        clientTable.setWidths(new float[]{50f, 50f});

        PdfPCell c1 = new PdfPCell();
        c1.setBorder(Rectangle.BOX);
        c1.setBorderColor(Color.LIGHT_GRAY);
        c1.setPadding(6);
        c1.addElement(new Paragraph("DATOS DEL CLIENTE / ADQUIRIENTE", boldFont));
        c1.addElement(new Paragraph("Nombre: " + guardianName, normalFont));
        c1.addElement(new Paragraph("Identificación: " + guardianDoc, normalFont));
        c1.addElement(new Paragraph("Medio de Pago: " + paymentMethod, normalFont));
        c1.addElement(new Paragraph("No. Recibo de Caja: " + receiptNumber, normalFont));
        clientTable.addCell(c1);

        PdfPCell c2 = new PdfPCell();
        c2.setBorder(Rectangle.BOX);
        c2.setBorderColor(Color.LIGHT_GRAY);
        c2.setPadding(6);
        c2.addElement(new Paragraph("DATOS DEL BENEFICIARIO / ESTUDIANTE", boldFont));
        c2.addElement(new Paragraph("Estudiante: " + studentName, normalFont));
        c2.addElement(new Paragraph("Grado Académico: " + studentGrade, normalFont));
        c2.addElement(new Paragraph("Institución: Colegio Little Horizons", normalFont));
        c2.addElement(new Paragraph("Estado del Servicio: Cancelado", normalFont));
        clientTable.addCell(c2);

        document.add(clientTable);
        document.add(new Paragraph(" "));

        // Item Detail Table
        PdfPTable itemsTable = new PdfPTable(5);
        itemsTable.setWidthPercentage(100);
        itemsTable.setWidths(new float[]{10f, 45f, 15f, 15f, 15f});

        Color headerColor = new Color(18, 52, 86);
        String[] columns = {"Ítem", "Descripción del Concepto", "Cantidad", "Valor Unitario", "Subtotal"};
        for (String col : columns) {
            PdfPCell cell = new PdfPCell(new Phrase(col, headerCellFont));
            cell.setBackgroundColor(headerColor);
            cell.setPadding(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            itemsTable.addCell(cell);
        }

        // 1 Line Item
        PdfPCell cellItem = new PdfPCell(new Phrase("1", normalFont));
        cellItem.setHorizontalAlignment(Element.ALIGN_CENTER);
        itemsTable.addCell(cellItem);

        PdfPCell cellDesc = new PdfPCell(new Phrase(conceptName, normalFont));
        itemsTable.addCell(cellDesc);

        PdfPCell cellQty = new PdfPCell(new Phrase("1", normalFont));
        cellQty.setHorizontalAlignment(Element.ALIGN_CENTER);
        itemsTable.addCell(cellQty);

        PdfPCell cellPrice = new PdfPCell(new Phrase(currencyFormat.format(amount), normalFont));
        cellPrice.setHorizontalAlignment(Element.ALIGN_RIGHT);
        itemsTable.addCell(cellPrice);

        PdfPCell cellSub = new PdfPCell(new Phrase(currencyFormat.format(amount), normalFont));
        cellSub.setHorizontalAlignment(Element.ALIGN_RIGHT);
        itemsTable.addCell(cellSub);

        // Totals
        PdfPCell emptyMerge = new PdfPCell(new Phrase(""));
        emptyMerge.setColspan(3);
        emptyMerge.setBorder(Rectangle.NO_BORDER);

        itemsTable.addCell(emptyMerge);
        PdfPCell subtotalLabel = new PdfPCell(new Phrase("Subtotal:", boldFont));
        subtotalLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
        itemsTable.addCell(subtotalLabel);
        PdfPCell subtotalVal = new PdfPCell(new Phrase(currencyFormat.format(amount), normalFont));
        subtotalVal.setHorizontalAlignment(Element.ALIGN_RIGHT);
        itemsTable.addCell(subtotalVal);

        itemsTable.addCell(emptyMerge);
        PdfPCell ivaLabel = new PdfPCell(new Phrase("IVA (0% Exento):", boldFont));
        ivaLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
        itemsTable.addCell(ivaLabel);
        PdfPCell ivaVal = new PdfPCell(new Phrase(currencyFormat.format(0), normalFont));
        ivaVal.setHorizontalAlignment(Element.ALIGN_RIGHT);
        itemsTable.addCell(ivaVal);

        itemsTable.addCell(emptyMerge);
        PdfPCell totalLabel = new PdfPCell(new Phrase("TOTAL A PAGAR:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, new Color(18, 52, 86))));
        totalLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalLabel.setBackgroundColor(new Color(240, 244, 248));
        itemsTable.addCell(totalLabel);
        PdfPCell totalVal = new PdfPCell(new Phrase(currencyFormat.format(amount), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK)));
        totalVal.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalVal.setBackgroundColor(new Color(240, 244, 248));
        itemsTable.addCell(totalVal);

        document.add(itemsTable);
        document.add(new Paragraph(" "));

        // DIAN Electronic Compliance Footer (CUFE and QR Code)
        PdfPTable dianTable = new PdfPTable(2);
        dianTable.setWidthPercentage(100);
        dianTable.setWidths(new float[]{75f, 25f});

        PdfPCell dianTextCell = new PdfPCell();
        dianTextCell.setBorder(Rectangle.BOX);
        dianTextCell.setBorderColor(Color.LIGHT_GRAY);
        dianTextCell.setPadding(6);
        dianTextCell.addElement(new Paragraph("INFORMACIÓN TRIBUTARIA Y CUMPLIMIENTO DIAN", boldFont));
        dianTextCell.addElement(new Paragraph(DIAN_RESOLUTION, smallFont));
        dianTextCell.addElement(new Paragraph("CUFE: " + cufe, smallFont));
        dianTextCell.addElement(new Paragraph("Servicio educativo excluido de IVA según Estatuto Tributario art. 476.", smallFont));
        dianTextCell.addElement(new Paragraph("Representación gráfica de factura electrónica generada automáticamente por ERP Little Horizons.", smallFont));
        dianTable.addCell(dianTextCell);

        // Generate QR Image and attach to PDF
        byte[] qrBytes = generateQrCodeBytes(qrContent, 120, 120);
        Image qrImage = Image.getInstance(qrBytes);
        qrImage.setAlignment(Element.ALIGN_CENTER);
        qrImage.scaleToFit(90, 90);

        PdfPCell qrCell = new PdfPCell();
        qrCell.setBorder(Rectangle.BOX);
        qrCell.setBorderColor(Color.LIGHT_GRAY);
        qrCell.setPadding(4);
        qrCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        qrCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        qrCell.addElement(qrImage);
        dianTable.addCell(qrCell);

        document.add(dianTable);

        document.close();
        AppLogger.info("Electronic invoice PDF successfully generated at: " + filePath);
        return filePath;
    }
}

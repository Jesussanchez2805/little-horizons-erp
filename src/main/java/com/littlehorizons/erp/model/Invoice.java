package com.littlehorizons.erp.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Domain entity representing an electronic invoice (Factura Electrónica).
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public class Invoice {

    private int id;
    private String invoiceNumber;
    private int paymentId;
    private LocalDate issueDate;
    private String cufe;
    private String pdfFileName;
    private String filePath;

    // Relational join fields
    private String studentName;
    private String guardianName;
    private String receiptNumber;
    private BigDecimal amount;

    public Invoice() {
    }

    public Invoice(int id, String invoiceNumber, int paymentId, LocalDate issueDate,
                   String cufe, String pdfFileName, String filePath) {
        this.id = id;
        this.invoiceNumber = invoiceNumber;
        this.paymentId = paymentId;
        this.issueDate = issueDate;
        this.cufe = cufe;
        this.pdfFileName = pdfFileName;
        this.filePath = filePath;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public String getCufe() { return cufe; }
    public void setCufe(String cufe) { this.cufe = cufe; }

    public String getPdfFileName() { return pdfFileName; }
    public void setPdfFileName(String pdfFileName) { this.pdfFileName = pdfFileName; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getGuardianName() { return guardianName; }
    public void setGuardianName(String guardianName) { this.guardianName = guardianName; }

    public String getReceiptNumber() { return receiptNumber; }
    public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    @Override
    public String toString() {
        return String.format("[%s] Issued: %s | Guardian: %s | Total: $%,.2f | PDF: %s",
                invoiceNumber, issueDate, guardianName != null ? guardianName : "N/A",
                amount != null ? amount.doubleValue() : 0.0, pdfFileName);
    }
}

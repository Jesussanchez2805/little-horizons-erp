package com.littlehorizons.erp.controller;

import com.littlehorizons.erp.dao.FinancialDAO;
import com.littlehorizons.erp.dao.FinancialDAOImpl;
import com.littlehorizons.erp.dao.StudentDAO;
import com.littlehorizons.erp.dao.StudentDAOImpl;
import com.littlehorizons.erp.model.*;
import com.littlehorizons.erp.util.AppLogger;
import com.littlehorizons.erp.util.PdfInvoiceGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controller orchestrating tuition payments, receipt generation,
 * defaulter identification, and DIAN-compliant PDF Electronic Invoices.
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public class FinancialController {

    private final FinancialDAO financialDAO;
    private final StudentDAO studentDAO;

    public FinancialController() {
        this(new FinancialDAOImpl(), new StudentDAOImpl());
    }

    public FinancialController(FinancialDAO financialDAO, StudentDAO studentDAO) {
        this.financialDAO = financialDAO;
        this.studentDAO = studentDAO;
    }

    public List<PaymentConcept> getAllConcepts() {
        return financialDAO.findAllConcepts();
    }

    public Optional<PaymentConcept> getConceptById(int id) {
        return financialDAO.findConceptById(id);
    }

    /**
     * Records a financial payment and optionally generates the DIAN electronic invoice PDF.
     *
     * @param studentId Student ID
     * @param guardianId Paying guardian ID (optional, can be null)
     * @param conceptId Concept ID
     * @param amount Paid amount in COP
     * @param method Payment method ('Efectivo', 'Transferencia', etc.)
     * @param year Academic year
     * @param period Academic period ('I', 'II', 'III', 'IV', 'Anual')
     * @param notes Transaction observations
     * @param generatePdf Whether to generate the DIAN PDF invoice immediately
     * @return Processed Payment entity
     */
    public Optional<Payment> processPayment(
            int studentId,
            Integer guardianId,
            int conceptId,
            BigDecimal amount,
            String method,
            Integer year,
            String period,
            String notes,
            boolean generatePdf
    ) {
        String receiptNumber = financialDAO.generateNextReceiptNumber();

        Payment payment = new Payment();
        payment.setStudentId(studentId);
        payment.setGuardianId(guardianId);
        payment.setConceptId(conceptId);
        payment.setAmountPaid(amount);
        payment.setPaymentDate(LocalDate.now());
        payment.setPaymentMethod(method);
        payment.setReceiptNumber(receiptNumber);
        payment.setAcademicYear(year);
        payment.setAcademicPeriod(period);
        payment.setNotes(notes);

        Payment recorded = financialDAO.recordPayment(payment);
        if (recorded == null) {
            return Optional.empty();
        }

        // If PDF generation requested, generate electronic invoice
        if (generatePdf) {
            try {
                generateInvoiceForPayment(recorded);
            } catch (Exception e) {
                AppLogger.severe("Could not automatically generate invoice PDF for payment: " + recorded.getId(), e);
            }
        }

        return Optional.of(recorded);
    }

    /**
     * Generates a DIAN Electronic Invoice PDF and stores invoice metadata in the database.
     */
    public Optional<Invoice> generateInvoiceForPayment(Payment payment) throws Exception {
        // Fetch full payment details
        Optional<Payment> fullPaymentOpt = financialDAO.findPaymentById(payment.getId());
        if (fullPaymentOpt.isEmpty()) {
            return Optional.empty();
        }
        Payment p = fullPaymentOpt.get();

        String invoiceNumber = financialDAO.generateNextInvoiceNumber();
        LocalDate issueDate = p.getPaymentDate() != null ? p.getPaymentDate() : LocalDate.now();

        String guardianName = p.getGuardianName() != null ? p.getGuardianName() : "Acudiente General";
        String guardianDoc = p.getGuardianDocNumber() != null ? p.getGuardianDocNumber() : "222222222222";
        String studentName = p.getStudentName() != null ? p.getStudentName() : "Estudiante General";
        String studentGrade = "Colegio Little Horizons";
        String conceptName = p.getConceptName() != null ? p.getConceptName() : "Concepto Educativo";

        // Generate PDF on disk with simulated CUFE and QR Code
        String filePath = PdfInvoiceGenerator.generateInvoicePdf(
                invoiceNumber,
                issueDate,
                p.getReceiptNumber(),
                p.getPaymentMethod(),
                guardianName,
                guardianDoc,
                studentName,
                studentGrade,
                conceptName,
                p.getAmountPaid()
        );

        String cufe = PdfInvoiceGenerator.calculateSimulatedCufe(
                invoiceNumber, "901.458.789-2", issueDate, p.getAmountPaid(), guardianDoc
        );

        String pdfFileName = "factura_" + invoiceNumber.replace("-", "_").toLowerCase() + ".pdf";

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(invoiceNumber);
        invoice.setPaymentId(p.getId());
        invoice.setIssueDate(issueDate);
        invoice.setCufe(cufe);
        invoice.setPdfFileName(pdfFileName);
        invoice.setFilePath(filePath);

        Invoice created = financialDAO.createInvoice(invoice);
        return Optional.ofNullable(created);
    }

    public List<Payment> getStudentPaymentHistory(int studentId) {
        return financialDAO.findPaymentsByStudent(studentId);
    }

    public List<Payment> getGuardianPaymentHistory(int guardianId) {
        return financialDAO.findPaymentsByGuardian(guardianId);
    }

    public List<Payment> getAllPayments() {
        return financialDAO.findAllPayments();
    }

    public List<Student> getDefaulters(int year, String period) {
        return financialDAO.findDefaulterStudents(year, period);
    }

    public List<Invoice> getAllInvoices() {
        return financialDAO.findAllInvoices();
    }

    public Optional<Invoice> getInvoiceByPayment(int paymentId) {
        return financialDAO.findInvoiceByPaymentId(paymentId);
    }
}

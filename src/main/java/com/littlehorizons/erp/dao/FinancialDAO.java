package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Invoice;
import com.littlehorizons.erp.model.Payment;
import com.littlehorizons.erp.model.PaymentConcept;
import com.littlehorizons.erp.model.Student;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Financial operations:
 * Concepts, Payments, Electronic Invoices, and Defaulters identification.
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public interface FinancialDAO {
    // Concepts
    List<PaymentConcept> findAllConcepts();
    Optional<PaymentConcept> findConceptById(int id);
    Optional<PaymentConcept> findConceptByCode(String code);

    // Payments
    Payment recordPayment(Payment payment);
    Optional<Payment> findPaymentById(int id);
    Optional<Payment> findPaymentByReceipt(String receiptNumber);
    List<Payment> findPaymentsByStudent(int studentId);
    List<Payment> findPaymentsByGuardian(int guardianId);
    List<Payment> findAllPayments();
    String generateNextReceiptNumber();

    // Defaulters (Morosos)
    List<Student> findDefaulterStudents(int year, String period);

    // Invoices
    Invoice createInvoice(Invoice invoice);
    Optional<Invoice> findInvoiceById(int id);
    Optional<Invoice> findInvoiceByPaymentId(int paymentId);
    List<Invoice> findAllInvoices();
    String generateNextInvoiceNumber();
}

package com.littlehorizons.erp.view;

import com.littlehorizons.erp.controller.FinancialController;
import com.littlehorizons.erp.model.Invoice;
import com.littlehorizons.erp.model.Payment;
import com.littlehorizons.erp.model.PaymentConcept;
import com.littlehorizons.erp.model.Student;
import com.littlehorizons.erp.util.ConsoleUtils;
import com.littlehorizons.erp.util.InputValidator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Console view for the Financial Management module:
 * Payments, Concept catalogs, DIAN Electronic Invoices in PDF, and Defaulters.
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public class FinancialView {

    private final FinancialController financialController;

    public FinancialView(FinancialController financialController) {
        this.financialController = financialController;
    }

    public void displayMenu() {
        boolean back = false;
        while (!back) {
            ConsoleUtils.printHeader("MODULE 5: FINANCIAL & INVOICING MANAGEMENT", "Little Horizons Educational ERP");
            System.out.println(" 1. Browse Payment Concepts (Catálogo de Conceptos)");
            System.out.println(" 2. Process & Record Payment (Registro de Pagos)");
            System.out.println(" 3. Generate DIAN Electronic Invoice PDF for Existing Payment");
            System.out.println(" 4. View Student Payment History");
            System.out.println(" 5. View Guardian Payment History");
            System.out.println(" 6. Report of Defaulter Students (Identificación de Morosos)");
            System.out.println(" 7. View All Generated Electronic Invoices (Facturas Emitidas)");
            System.out.println(" 0. Return to Main Menu");
            System.out.println();

            int choice = InputValidator.readInt("Select an option", 0, 7);

            switch (choice) {
                case 1 -> browseConcepts();
                case 2 -> processPayment();
                case 3 -> generateInvoiceForPayment();
                case 4 -> viewStudentPaymentHistory();
                case 5 -> viewGuardianPaymentHistory();
                case 6 -> viewDefaultersReport();
                case 7 -> viewAllInvoices();
                case 0 -> back = true;
            }
        }
    }

    private void browseConcepts() {
        ConsoleUtils.printSection("PAYMENT CONCEPTS CATALOG");
        List<PaymentConcept> concepts = financialController.getAllConcepts();

        List<String> headers = List.of("ID", "Code", "Concept Name", "Base Amount (COP)", "Recurring", "Active");
        List<List<String>> rows = new ArrayList<>();
        for (PaymentConcept c : concepts) {
            rows.add(List.of(
                    String.valueOf(c.getId()),
                    c.getCode(),
                    c.getName(),
                    String.format("$%,.2f", c.getBaseAmount().doubleValue()),
                    c.isRecurring() ? "YES (Monthly)" : "NO (One-off)",
                    c.isActive() ? "Active" : "Inactive"
            ));
        }
        ConsoleUtils.printTable(headers, rows);
        InputValidator.pressEnterToContinue();
    }

    private void processPayment() {
        ConsoleUtils.printSection("PROCESS & RECORD PAYMENT");
        int studentId = InputValidator.readInt("Enter Student ID", 1, 999999);
        int guardianId = InputValidator.readInt("Enter Paying Guardian ID", 1, 999999);

        // Display concepts
        List<PaymentConcept> concepts = financialController.getAllConcepts();
        System.out.println("\nSelect Payment Concept:");
        for (PaymentConcept c : concepts) {
            System.out.printf("  [%d] %-28s (Base: $%,.2f)%n", c.getId(), c.getName(), c.getBaseAmount().doubleValue());
        }

        int conceptId = InputValidator.readInt("Concept ID", 1, 999999);
        Optional<PaymentConcept> conceptOpt = financialController.getConceptById(conceptId);
        BigDecimal defaultAmount = conceptOpt.map(PaymentConcept::getBaseAmount).orElse(BigDecimal.valueOf(380000));

        System.out.printf("Base amount for selected concept is: $%,.2f%n", defaultAmount.doubleValue());
        boolean custom = InputValidator.readBoolean("Do you wish to enter a personalized/discounted amount?");
        BigDecimal amount = defaultAmount;
        if (custom) {
            amount = InputValidator.readBigDecimal("Enter customized amount to pay (COP)");
        }

        System.out.println("Select Payment Method:");
        System.out.println("1. Efectivo");
        System.out.println("2. Transferencia");
        System.out.println("3. Tarjeta Débito");
        System.out.println("4. Tarjeta Crédito");
        System.out.println("5. Cheque");
        int m = InputValidator.readInt("Option", 1, 5);
        String[] methods = {"Efectivo", "Transferencia", "Tarjeta Débito", "Tarjeta Crédito", "Cheque"};
        String method = methods[m - 1];

        int year = InputValidator.readInt("Academic Year", 2020, 2035);
        String period = InputValidator.readNonEmptyString("Academic Period (I, II, III, IV, Anual)");
        String notes = InputValidator.readOptionalString("Payment remarks", "Pago regular de pensión/concepto");

        boolean genPdf = InputValidator.readBoolean("Generate DIAN Electronic Invoice PDF automatically?");

        Optional<Payment> res = financialController.processPayment(
                studentId, guardianId, conceptId, amount, method, year, period, notes, genPdf
        );

        if (res.isPresent()) {
            Payment p = res.get();
            ConsoleUtils.printSuccess("Payment recorded successfully!");
            System.out.println("  • Receipt Number: " + p.getReceiptNumber());
            System.out.println("  • Payment ID:     " + p.getId());
            System.out.printf("  • Amount Paid:    $%,.2f COP%n", p.getAmountPaid().doubleValue());

            if (genPdf) {
                ConsoleUtils.printSuccess(">> Official DIAN Electronic Invoice PDF generated in 'invoices/' directory! <<");
            }
        } else {
            ConsoleUtils.printError("Failed to record payment.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void generateInvoiceForPayment() {
        ConsoleUtils.printSection("GENERATE DIAN INVOICE PDF FOR PAYMENT");
        int paymentId = InputValidator.readInt("Enter Payment ID", 1, 999999);

        try {
            Payment dummy = new Payment();
            dummy.setId(paymentId);
            Optional<Invoice> inv = financialController.generateInvoiceForPayment(dummy);
            if (inv.isPresent()) {
                Invoice i = inv.get();
                ConsoleUtils.printSuccess("Invoice PDF generated successfully!");
                System.out.println("  • Invoice Number:  " + i.getInvoiceNumber());
                System.out.println("  • Simulated CUFE:  " + i.getCufe());
                System.out.println("  • PDF File Path:   " + i.getFilePath());
                System.out.println("  • DIAN QR Code:    Embedded successfully into PDF");
            } else {
                ConsoleUtils.printError("Payment not found or invoice could not be created.");
            }
        } catch (Exception e) {
            ConsoleUtils.printError("Error generating PDF: " + e.getMessage());
        }
        InputValidator.pressEnterToContinue();
    }

    private void viewStudentPaymentHistory() {
        ConsoleUtils.printSection("STUDENT PAYMENT HISTORY");
        int studentId = InputValidator.readInt("Enter Student ID", 1, 999999);

        List<Payment> list = financialController.getStudentPaymentHistory(studentId);
        if (list.isEmpty()) {
            ConsoleUtils.printWarning("No payment records found for Student ID " + studentId);
        } else {
            List<String> headers = List.of("Receipt No.", "Date", "Concept", "Amount (COP)", "Method", "Period");
            List<List<String>> rows = new ArrayList<>();
            for (Payment p : list) {
                rows.add(List.of(
                        p.getReceiptNumber(),
                        p.getPaymentDate().toString(),
                        p.getConceptName() != null ? p.getConceptName() : String.valueOf(p.getConceptId()),
                        String.format("$%,.2f", p.getAmountPaid().doubleValue()),
                        p.getPaymentMethod(),
                        p.getAcademicPeriod() != null ? p.getAcademicPeriod() : ""
                ));
            }
            ConsoleUtils.printTable(headers, rows);
        }
        InputValidator.pressEnterToContinue();
    }

    private void viewGuardianPaymentHistory() {
        ConsoleUtils.printSection("GUARDIAN PAYMENT HISTORY");
        int guardianId = InputValidator.readInt("Enter Guardian ID", 1, 999999);

        List<Payment> list = financialController.getGuardianPaymentHistory(guardianId);
        if (list.isEmpty()) {
            ConsoleUtils.printWarning("No payments found for Guardian ID " + guardianId);
        } else {
            List<String> headers = List.of("Receipt No.", "Date", "Student", "Concept", "Amount (COP)", "Method");
            List<List<String>> rows = new ArrayList<>();
            for (Payment p : list) {
                rows.add(List.of(
                        p.getReceiptNumber(),
                        p.getPaymentDate().toString(),
                        p.getStudentName() != null ? p.getStudentName() : String.valueOf(p.getStudentId()),
                        p.getConceptName() != null ? p.getConceptName() : "",
                        String.format("$%,.2f", p.getAmountPaid().doubleValue()),
                        p.getPaymentMethod()
                ));
            }
            ConsoleUtils.printTable(headers, rows);
        }
        InputValidator.pressEnterToContinue();
    }

    private void viewDefaultersReport() {
        ConsoleUtils.printSection("DEFAULTERS REPORT (ESTUDIANTES EN MORA)");
        int year = InputValidator.readInt("Academic Year", 2020, 2035);
        String period = InputValidator.readNonEmptyString("Academic Period (I, II, III, IV)");

        List<Student> defaulters = financialController.getDefaulters(year, period);
        if (defaulters.isEmpty()) {
            ConsoleUtils.printSuccess("Great news! There are zero defaulters for period " + period + " " + year);
        } else {
            ConsoleUtils.printWarning(String.format("Found %d students with pending tuition for %s %d:",
                    defaulters.size(), period, year));

            List<String> headers = List.of("ID", "Document", "Student Full Name", "Phone", "Email", "Status");
            List<List<String>> rows = new ArrayList<>();
            for (Student s : defaulters) {
                rows.add(List.of(
                        String.valueOf(s.getId()),
                        s.getDocumentType() + " " + s.getDocumentNumber(),
                        s.getFullName(),
                        s.getPhone() != null ? s.getPhone() : "N/A",
                        s.getEmail() != null ? s.getEmail() : "N/A",
                        s.getStatus()
                ));
            }
            ConsoleUtils.printTable(headers, rows);
        }
        InputValidator.pressEnterToContinue();
    }

    private void viewAllInvoices() {
        ConsoleUtils.printSection("ELECTRONIC INVOICES ISSUED (FACTURAS ELECTRÓNICAS DIAN)");
        List<Invoice> list = financialController.getAllInvoices();

        if (list.isEmpty()) {
            ConsoleUtils.printWarning("No electronic invoices generated yet.");
        } else {
            List<String> headers = List.of("Invoice No.", "Issue Date", "Guardian", "Receipt", "Amount (COP)", "PDF File");
            List<List<String>> rows = new ArrayList<>();
            for (Invoice i : list) {
                rows.add(List.of(
                        i.getInvoiceNumber(),
                        i.getIssueDate().toString(),
                        i.getGuardianName() != null ? i.getGuardianName() : "N/A",
                        i.getReceiptNumber() != null ? i.getReceiptNumber() : "N/A",
                        String.format("$%,.2f", i.getAmount() != null ? i.getAmount().doubleValue() : 0.0),
                        i.getPdfFileName()
                ));
            }
            ConsoleUtils.printTable(headers, rows);
            ConsoleUtils.printInfo("All PDF invoices are archived in the local 'invoices/' project directory.");
        }
        InputValidator.pressEnterToContinue();
    }
}

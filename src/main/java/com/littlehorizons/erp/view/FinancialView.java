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
            ConsoleUtils.printHeader("MÓDULO 5: GESTIÓN FINANCIERA Y FACTURACIÓN", "Little Horizons Educational ERP");
            System.out.println(" 1. Explorar Catálogo de Conceptos de Pago");
            System.out.println(" 2. Procesar y Registrar Pago");
            System.out.println(" 3. Generar Factura Electrónica DIAN en PDF para un Pago");
            System.out.println(" 4. Ver Historial de Pagos del Estudiante");
            System.out.println(" 5. Ver Historial de Pagos del Acudiente");
            System.out.println(" 6. Reporte de Estudiantes en Mora");
            System.out.println(" 7. Ver Facturas Electrónicas Emitidas");
            System.out.println(" 0. Volver al Menú Principal");
            System.out.println();

            int choice = InputValidator.readInt("Seleccione una opción", 0, 7);

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
        ConsoleUtils.printSection("CATÁLOGO DE CONCEPTOS DE PAGO");
        List<PaymentConcept> concepts = financialController.getAllConcepts();

        List<String> headers = List.of("ID", "Código", "Concepto", "Monto Base (COP)", "Recurrente", "Estado");
        List<List<String>> rows = new ArrayList<>();
        for (PaymentConcept c : concepts) {
            rows.add(List.of(
                    String.valueOf(c.getId()),
                    c.getCode(),
                    c.getName(),
                    String.format("$%,.2f", c.getBaseAmount().doubleValue()),
                    c.isRecurring() ? "SÍ (Mensual)" : "NO (Único)",
                    c.isActive() ? "Activo" : "Inactivo"
            ));
        }
        ConsoleUtils.printTable(headers, rows);
        InputValidator.pressEnterToContinue();
    }

    private void processPayment() {
        ConsoleUtils.printSection("PROCESAR Y REGISTRAR PAGO");
        int studentId = InputValidator.readInt("Ingrese ID del Estudiante", 1, 999999);
        int guardianId = InputValidator.readInt("Ingrese ID del Acudiente Pagador", 1, 999999);

        // Display concepts
        List<PaymentConcept> concepts = financialController.getAllConcepts();
        System.out.println("\nSeleccione Concepto de Pago:");
        for (PaymentConcept c : concepts) {
            System.out.printf("  [%d] %-28s (Base: $%,.2f)%n", c.getId(), c.getName(), c.getBaseAmount().doubleValue());
        }

        int conceptId = InputValidator.readInt("ID del Concepto", 1, 999999);
        Optional<PaymentConcept> conceptOpt = financialController.getConceptById(conceptId);
        BigDecimal defaultAmount = conceptOpt.map(PaymentConcept::getBaseAmount).orElse(BigDecimal.valueOf(380000));

        System.out.printf("El monto base para el concepto seleccionado es: $%,.2f%n", defaultAmount.doubleValue());
        boolean custom = InputValidator.readBoolean("¿Desea ingresar un monto personalizado o con descuento?");
        BigDecimal amount = defaultAmount;
        if (custom) {
            amount = InputValidator.readBigDecimal("Ingrese monto personalizado a pagar (COP)");
        }

        System.out.println("Seleccione Método de Pago:");
        System.out.println("1. Efectivo");
        System.out.println("2. Transferencia");
        System.out.println("3. Tarjeta Débito");
        System.out.println("4. Tarjeta Crédito");
        System.out.println("5. Cheque");
        int m = InputValidator.readInt("Opción", 1, 5);
        String[] methods = {"Efectivo", "Transferencia", "Tarjeta Débito", "Tarjeta Crédito", "Cheque"};
        String method = methods[m - 1];

        int year = InputValidator.readInt("Año Académico", 2020, 2035);
        String period = InputValidator.readNonEmptyString("Periodo Académico (I, II, III, IV, Anual)");
        String notes = InputValidator.readOptionalString("Observaciones del pago", "Pago regular de pensión/concepto");

        boolean genPdf = InputValidator.readBoolean("¿Generar Factura Electrónica DIAN en PDF automáticamente?");

        Optional<Payment> res = financialController.processPayment(
                studentId, guardianId, conceptId, amount, method, year, period, notes, genPdf
        );

        if (res.isPresent()) {
            Payment p = res.get();
            ConsoleUtils.printSuccess("¡Pago registrado exitosamente!");
            System.out.println("  • Número de Recibo: " + p.getReceiptNumber());
            System.out.println("  • ID de Pago:        " + p.getId());
            System.out.printf("  • Monto Pagado:      $%,.2f COP%n", p.getAmountPaid().doubleValue());

            if (genPdf) {
                ConsoleUtils.printSuccess(">> ¡Factura Electrónica oficial DIAN en PDF generada en el directorio 'invoices/'! <<");
            }
        } else {
            ConsoleUtils.printError("Error al registrar el pago.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void generateInvoiceForPayment() {
        ConsoleUtils.printSection("GENERAR FACTURA ELECTRÓNICA DIAN EN PDF");
        int paymentId = InputValidator.readInt("Ingrese ID del Pago", 1, 999999);

        try {
            Payment dummy = new Payment();
            dummy.setId(paymentId);
            Optional<Invoice> inv = financialController.generateInvoiceForPayment(dummy);
            if (inv.isPresent()) {
                Invoice i = inv.get();
                ConsoleUtils.printSuccess("¡Factura en PDF generada exitosamente!");
                System.out.println("  • Número de Factura: " + i.getInvoiceNumber());
                System.out.println("  • CUFE Simulado:     " + i.getCufe());
                System.out.println("  • Ruta del Archivo:  " + i.getFilePath());
                System.out.println("  • Código QR DIAN:    Incrustado exitosamente en el PDF");
            } else {
                ConsoleUtils.printError("Pago no encontrado o no se pudo generar la factura.");
            }
        } catch (Exception e) {
            ConsoleUtils.printError("Error al generar PDF: " + e.getMessage());
        }
        InputValidator.pressEnterToContinue();
    }

    private void viewStudentPaymentHistory() {
        ConsoleUtils.printSection("HISTORIAL DE PAGOS DEL ESTUDIANTE");
        int studentId = InputValidator.readInt("Ingrese ID del Estudiante", 1, 999999);

        List<Payment> list = financialController.getStudentPaymentHistory(studentId);
        if (list.isEmpty()) {
            ConsoleUtils.printWarning("No se encontraron pagos para el Estudiante ID " + studentId);
        } else {
            List<String> headers = List.of("N° Recibo", "Fecha", "Concepto", "Monto (COP)", "Método", "Periodo");
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
        ConsoleUtils.printSection("HISTORIAL DE PAGOS DEL ACUDIENTE");
        int guardianId = InputValidator.readInt("Ingrese ID del Acudiente", 1, 999999);

        List<Payment> list = financialController.getGuardianPaymentHistory(guardianId);
        if (list.isEmpty()) {
            ConsoleUtils.printWarning("No se encontraron pagos para el Acudiente ID " + guardianId);
        } else {
            List<String> headers = List.of("N° Recibo", "Fecha", "Estudiante", "Concepto", "Monto (COP)", "Método");
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
        ConsoleUtils.printSection("REPORTE DE ESTUDIANTES EN MORA");
        int year = InputValidator.readInt("Año Académico", 2020, 2035);
        String period = InputValidator.readNonEmptyString("Periodo Académico (I, II, III, IV)");

        List<Student> defaulters = financialController.getDefaulters(year, period);
        if (defaulters.isEmpty()) {
            ConsoleUtils.printSuccess("¡Excelente noticia! No hay estudiantes en mora para el periodo " + period + " " + year);
        } else {
            ConsoleUtils.printWarning(String.format("Se encontraron %d estudiantes con pensión pendiente para %s %d:",
                    defaulters.size(), period, year));

            List<String> headers = List.of("ID", "Documento", "Nombre del Estudiante", "Teléfono", "Correo Electrónico", "Estado");
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
        ConsoleUtils.printSection("FACTURAS ELECTRÓNICAS EMITIDAS (DIAN)");
        List<Invoice> list = financialController.getAllInvoices();

        if (list.isEmpty()) {
            ConsoleUtils.printWarning("Aún no se han generado facturas electrónicas.");
        } else {
            List<String> headers = List.of("N° Factura", "Fecha Emisión", "Acudiente", "N° Recibo", "Monto (COP)", "Archivo PDF");
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
            ConsoleUtils.printInfo("Todas las facturas PDF se encuentran archivadas en la carpeta local 'invoices/' del proyecto.");
        }
        InputValidator.pressEnterToContinue();
    }
}

package com.littlehorizons.erp.view;

import com.littlehorizons.erp.controller.ReportController;
import com.littlehorizons.erp.util.ConsoleUtils;
import com.littlehorizons.erp.util.InputValidator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Console view for the Reports and Institutional Analytics module.
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public class ReportView {

    private final ReportController reportController;

    public ReportView(ReportController reportController) {
        this.reportController = reportController;
    }

    public void displayMenu() {
        boolean back = false;
        while (!back) {
            ConsoleUtils.printHeader("MÓDULO 7: REPORTES Y ESTADÍSTICAS INSTITUCIONALES", "Little Horizons Educational ERP");
            System.out.println(" 1. Tablero Ejecutivo Institucional");
            System.out.println(" 2. Ranking de Excelencia Académica (por Promedio GPA)");
            System.out.println(" 3. Desglose de Ingresos Financieros por Concepto");
            System.out.println(" 4. Distribución del Inventario de Recursos por Estado");
            System.out.println(" 0. Volver al Menú Principal");
            System.out.println();

            int choice = InputValidator.readInt("Seleccione una opción", 0, 4);

            switch (choice) {
                case 1 -> viewExecutiveDashboard();
                case 2 -> viewTopStudentsRanking();
                case 3 -> viewRevenueBreakdown();
                case 4 -> viewResourceBreakdown();
                case 0 -> back = true;
            }
        }
    }

    private void viewExecutiveDashboard() {
        ConsoleUtils.printSection("TABLERO EJECUTIVO INSTITUCIONAL");
        Map<String, Object> metrics = reportController.getExecutiveDashboardMetrics();

        List<String> headers = List.of("Métrica Institucional", "Valor / Total");
        List<List<String>> rows = new ArrayList<>();

        metrics.forEach((key, val) -> {
            String formatted;
            if (val instanceof BigDecimal) {
                formatted = String.format("$%,.2f COP", ((BigDecimal) val).doubleValue());
            } else {
                formatted = String.valueOf(val);
            }
            rows.add(List.of(key, formatted));
        });

        ConsoleUtils.printTable(headers, rows);
        InputValidator.pressEnterToContinue();
    }

    private void viewTopStudentsRanking() {
        ConsoleUtils.printSection("RANKING DE EXCELENCIA ACADÉMICA");
        int top = InputValidator.readInt("¿Cuántos estudiantes destacados desea visualizar?", 3, 50);

        List<Map.Entry<String, Double>> ranking = reportController.getTopStudentsByGpa(top);
        if (ranking.isEmpty()) {
            ConsoleUtils.printWarning("No hay calificaciones registradas para generar el ranking.");
        } else {
            List<String> headers = List.of("Puesto", "Datos del Estudiante", "Promedio Acumulado");
            List<List<String>> rows = new ArrayList<>();
            int pos = 1;
            for (Map.Entry<String, Double> entry : ranking) {
                String medal = "";
                if (pos == 1) medal = " [1° - Oro]";
                else if (pos == 2) medal = " [2° - Plata]";
                else if (pos == 3) medal = " [3° - Bronce]";

                rows.add(List.of(
                        "#" + pos + medal,
                        entry.getKey(),
                        String.format("%.2f / 5.0", entry.getValue())
                ));
                pos++;
            }
            ConsoleUtils.printTable(headers, rows);
        }
        InputValidator.pressEnterToContinue();
    }

    private void viewRevenueBreakdown() {
        ConsoleUtils.printSection("DESGLOSE DE INGRESOS POR CONCEPTO DE PAGO");
        Map<String, BigDecimal> map = reportController.getRevenueByConcept();

        List<String> headers = List.of("Concepto de Pago", "Ingresos Totales (COP)");
        List<List<String>> rows = new ArrayList<>();
        BigDecimal grandTotal = BigDecimal.ZERO;

        for (Map.Entry<String, BigDecimal> entry : map.entrySet()) {
            rows.add(List.of(
                    entry.getKey(),
                    String.format("$%,.2f", entry.getValue().doubleValue())
            ));
            grandTotal = grandTotal.add(entry.getValue());
        }

        ConsoleUtils.printTable(headers, rows);
        ConsoleUtils.printSuccess(String.format("Gran Total de Ingresos Registrados: $%,.2f COP", grandTotal.doubleValue()));
        InputValidator.pressEnterToContinue();
    }

    private void viewResourceBreakdown() {
        ConsoleUtils.printSection("DISTRIBUCIÓN DEL INVENTARIO DE RECURSOS POR ESTADO");
        Map<String, Long> map = reportController.getResourceInventoryBreakdown();

        List<String> headers = List.of("Estado del Inventario", "Cantidad");
        List<List<String>> rows = new ArrayList<>();
        for (Map.Entry<String, Long> entry : map.entrySet()) {
            rows.add(List.of(entry.getKey(), String.valueOf(entry.getValue())));
        }

        ConsoleUtils.printTable(headers, rows);
        InputValidator.pressEnterToContinue();
    }
}

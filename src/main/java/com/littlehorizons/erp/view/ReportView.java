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
            ConsoleUtils.printHeader("MODULE 7: REPORTS & INSTITUTIONAL STATISTICS", "Little Horizons Educational ERP");
            System.out.println(" 1. Institutional Executive Dashboard");
            System.out.println(" 2. Top Academic Students Ranking (by GPA)");
            System.out.println(" 3. Financial Revenue Breakdown by Payment Concept");
            System.out.println(" 4. Resource Inventory Status Distribution");
            System.out.println(" 0. Return to Main Menu");
            System.out.println();

            int choice = InputValidator.readInt("Select an option", 0, 4);

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
        ConsoleUtils.printSection("EXECUTIVE INSTITUTIONAL DASHBOARD");
        Map<String, Object> metrics = reportController.getExecutiveDashboardMetrics();

        List<String> headers = List.of("Institutional Metric", "Value / Count");
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
        ConsoleUtils.printSection("TOP ACADEMIC STUDENTS RANKING");
        int top = InputValidator.readInt("How many top students to display?", 3, 50);

        List<Map.Entry<String, Double>> ranking = reportController.getTopStudentsByGpa(top);
        if (ranking.isEmpty()) {
            ConsoleUtils.printWarning("No grades recorded to build ranking.");
        } else {
            List<String> headers = List.of("Rank", "Student Details", "Cumulative GPA");
            List<List<String>> rows = new ArrayList<>();
            int pos = 1;
            for (Map.Entry<String, Double> entry : ranking) {
                String medal = "";
                if (pos == 1) medal = " [1st - Gold]";
                else if (pos == 2) medal = " [2nd - Silver]";
                else if (pos == 3) medal = " [3rd - Bronze]";

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
        ConsoleUtils.printSection("REVENUE BREAKDOWN BY PAYMENT CONCEPT");
        Map<String, BigDecimal> map = reportController.getRevenueByConcept();

        List<String> headers = List.of("Payment Concept", "Total Revenue (COP)");
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
        ConsoleUtils.printSuccess(String.format("Grand Total Recorded Revenue: $%,.2f COP", grandTotal.doubleValue()));
        InputValidator.pressEnterToContinue();
    }

    private void viewResourceBreakdown() {
        ConsoleUtils.printSection("RESOURCE INVENTORY DISTRIBUTION BY STATUS");
        Map<String, Long> map = reportController.getResourceInventoryBreakdown();

        List<String> headers = List.of("Inventory Status", "Count");
        List<List<String>> rows = new ArrayList<>();
        for (Map.Entry<String, Long> entry : map.entrySet()) {
            rows.add(List.of(entry.getKey(), String.valueOf(entry.getValue())));
        }

        ConsoleUtils.printTable(headers, rows);
        InputValidator.pressEnterToContinue();
    }
}

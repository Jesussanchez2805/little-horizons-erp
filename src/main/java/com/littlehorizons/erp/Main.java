package com.littlehorizons.erp;

import com.littlehorizons.erp.util.AppLogger;
import com.littlehorizons.erp.util.ConsoleUtils;
import com.littlehorizons.erp.util.DatabaseConnection;
import com.littlehorizons.erp.view.MainMenuView;

/**
 * Application Entrypoint for the Little Horizons Educational ERP.
 * Initializes logging, performs database connectivity diagnostic checks,
 * and launches the top-level console interface.
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public class Main {

    public static void main(String[] args) {
        AppLogger.info("Starting Little Horizons Educational ERP System v1.0.0...");

        // Startup banner
        ConsoleUtils.printHeader(
                "LITTLE HORIZONS EDUCATIONAL ERP",
                "Colegio Internacional Little Horizons S.A.S. - Sistema de Gestión"
        );

        // Pre-flight database connectivity check
        try {
            boolean connected = DatabaseConnection.getInstance().testConnection();
            if (connected) {
                ConsoleUtils.printSuccess("Conexión exitosa con la base de datos MySQL (erp_little_horizons).");
            } else {
                ConsoleUtils.printWarning("Aviso: No se pudo verificar la conexión inmediata con MySQL.");
                ConsoleUtils.printInfo("Recuerda que puedes configurar credenciales en la opción 8 del menú principal");
                ConsoleUtils.printInfo("o editando el archivo 'src/main/resources/db.properties'.");
            }
        } catch (Exception e) {
            AppLogger.warning("Initial database check reported: " + e.getMessage());
        }

        // Launch interactive console menu
        try {
            MainMenuView menu = new MainMenuView();
            menu.start();
        } catch (Exception e) {
            AppLogger.severe("Uncaught critical exception in main loop", e);
            ConsoleUtils.printError("Ha ocurrido un error crítico no controlado: " + e.getMessage());
        } finally {
            AppLogger.info("Little Horizons Educational ERP closed.");
        }
    }
}

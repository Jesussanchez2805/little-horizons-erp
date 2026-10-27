/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package testexamen.view;

/**
 *
 * @author camper
 */

import testexamen.controller.*;
import com.littlehorizons.erp.util.ConsoleUtils;
import com.littlehorizons.erp.util.DatabaseConnection;
import com.littlehorizons.erp.util.InputValidator;

/**
 * Main interactive menu for the Little Horizons Educational ERP.
 * Dispatches navigation to each individual functional module view.
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public class mainMenuView {

    private final TeacherView teacherView;

    public mainMenuView() {

        TeacherController teacherController = new TeacherController();


        this.teacherView = new TeacherView(teacherController);
    }
    /**
     * Main application loop.
     */
    public void start() {
        boolean exit = false;

        while (!exit) {
            System.out.println();
            System.out.println("╔══════════════════════════════════════════════════════════════╗");
            System.out.println("║   ERP COLEGIO INTERNACIONAL LITTLE HORIZONS                  ║");
            System.out.println("║   EXPORTADOR DE MATERIAS ASIGNADAS - AÑO 2025                ║");
            System.out.println("╚══════════════════════════════════════════════════════════════╝");
            System.out.println();

            System.out.println(" 1. Gestión de Profesores y Carga Académica");
            System.out.println(" 2. Configuración de Base de Datos");
            System.out.println(" 3. Salir del Sistema");
            System.out.println();

            int option = InputValidator.readInt("Seleccione una opción", 1, 10);

            switch (option) {
                case 1 -> teacherView.displayMenu();
                case 2 -> configureDatabase();
                case 3 -> {
                    ConsoleUtils
                            .printInfo("Gracias por utilizar Little Horizons ERP. Cerrando sesión de forma segura...");
                    exit = true;
                }
            }
        }
    }

    private void configureDatabase() {
        ConsoleUtils.printSection("CONFIGURACIÓN DE CONEXIÓN A BASE DE DATOS");
        DatabaseConnection db = DatabaseConnection.getInstance();
        System.out.println("Host actual:          " + db.getHost());
        System.out.println("Puerto actual:        " + db.getPort());
        System.out.println("Base de datos actual: " + db.getDatabase());
        System.out.println("Usuario actual:       " + db.getUser());
        System.out.println();

        boolean test = db.testConnection();
        if (test) {
            ConsoleUtils.printSuccess("Conexión con MySQL activa y verificada exitosamente.");
        } else {
            ConsoleUtils.printWarning("No se pudo conectar a MySQL con las credenciales actuales.");
        }

        boolean change = InputValidator.readBoolean("¿Desea ingresar nuevas credenciales de MySQL en caliente?");
        if (change) {
            String host = InputValidator.readNonEmptyString("Host (ej. localhost)");
            int port = InputValidator.readInt("Puerto", 1024, 65535);
            String database = InputValidator.readNonEmptyString("Nombre de base de datos (ej. erp_little_horizons)");
            String user = InputValidator.readNonEmptyString("Usuario (ej. root)");
            String password = InputValidator.readOptionalString("Contraseña", "");

            db.setCredentials(host, port, database, user, password);
            if (db.testConnection()) {
                ConsoleUtils.printSuccess("¡Conexión establecida con éxito con las nuevas credenciales!");
            } else {
                ConsoleUtils
                        .printError("No se pudo conectar con los datos ingresados. Verifique el servicio de MySQL.");
            }
        }
        InputValidator.pressEnterToContinue();
    }
}

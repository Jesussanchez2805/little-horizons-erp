package com.littlehorizons.erp.view;

import com.littlehorizons.erp.controller.*;
import com.littlehorizons.erp.util.ConsoleUtils;
import com.littlehorizons.erp.util.DatabaseConnection;
import com.littlehorizons.erp.util.InputValidator;

/**
 * Main interactive menu for the Little Horizons Educational ERP.
 * Dispatches navigation to each individual functional module view.
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public class MainMenuView {

    private final StudentView studentView;
    private final AcademicView academicView;
    private final TeacherView teacherView;
    private final ExtracurricularView extracurricularView;
    private final FinancialView financialView;
    private final ResourceView resourceView;
    private final ReportView reportView;

    public MainMenuView() {
        StudentController studentController = new StudentController();
        AcademicController academicController = new AcademicController();
        TeacherController teacherController = new TeacherController();
        ExtracurricularController extracurricularController = new ExtracurricularController();
        FinancialController financialController = new FinancialController();
        ResourceController resourceController = new ResourceController();
        ReportController reportController = new ReportController();

        this.studentView = new StudentView(studentController);
        this.academicView = new AcademicView(academicController);
        this.teacherView = new TeacherView(teacherController);
        this.extracurricularView = new ExtracurricularView(extracurricularController);
        this.financialView = new FinancialView(financialController);
        this.resourceView = new ResourceView(resourceController);
        this.reportView = new ReportView(reportController);
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
            System.out.println("║   Sistema Central de Planificación y Gestión Educativa       ║");
            System.out.println("╚══════════════════════════════════════════════════════════════╝");
            System.out.println();
            System.out.println(" 1. Gestión de Estudiantes y Matrículas (Students & Guardians)");
            System.out.println(" 2. Gestión Académica (Courses, Grades 0.0-5.0 & Attendance)");
            System.out.println(" 3. Gestión de Profesores y Carga Académica (Faculty)");
            System.out.println(" 4. Actividades Extracurriculares (Clubs & Events)");
            System.out.println(" 5. Gestión Financiera (Payments & DIAN PDF Invoices)");
            System.out.println(" 6. Gestión de Recursos (Library, Labs & Loan System)");
            System.out.println(" 7. Reportes y Estadísticas (Executive Analytics & GPA)");
            System.out.println(" 8. Configuración de Base de Datos (Database Connection)");
            System.out.println(" 9. Salir del Sistema (Exit)");
            System.out.println();

            int option = InputValidator.readInt("Seleccione una opción", 1, 9);

            switch (option) {
                case 1 -> studentView.displayMenu();
                case 2 -> academicView.displayMenu();
                case 3 -> teacherView.displayMenu();
                case 4 -> extracurricularView.displayMenu();
                case 5 -> financialView.displayMenu();
                case 6 -> resourceView.displayMenu();
                case 7 -> reportView.displayMenu();
                case 8 -> configureDatabase();
                case 9 -> {
                    ConsoleUtils.printInfo("Gracias por utilizar Little Horizons ERP. Cerrando sesión de forma segura...");
                    exit = true;
                }
            }
        }
    }

    private void configureDatabase() {
        ConsoleUtils.printSection("CONFIGURACIÓN DE CONEXIÓN A BASE DE DATOS");
        DatabaseConnection db = DatabaseConnection.getInstance();
        System.out.println("Current host:     " + db.getHost());
        System.out.println("Current port:     " + db.getPort());
        System.out.println("Current database: " + db.getDatabase());
        System.out.println("Current user:     " + db.getUser());
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
                ConsoleUtils.printError("No se pudo conectar con los datos ingresados. Verifique el servicio de MySQL.");
            }
        }
        InputValidator.pressEnterToContinue();
    }
}

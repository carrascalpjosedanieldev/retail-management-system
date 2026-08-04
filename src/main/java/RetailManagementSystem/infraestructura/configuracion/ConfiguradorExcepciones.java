package RetailManagementSystem.infraestructura.configuracion;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public class ConfiguradorExcepciones {

    public static void inicializarManejadorGlobal() {
        Thread.setDefaultUncaughtExceptionHandler((hilo, excepcion) -> {
            System.err.println("🚨 ERROR FATAL NO CONTROLADO en el hilo [" + hilo.getName() + "]");
            excepcion.printStackTrace();
            Platform.runLater(() -> mostrarAlertaGenerica());
        });
    }

    private static void mostrarAlertaGenerica() {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error Interno");
        alerta.setHeaderText("Fallo Crítico del Sistema");
        alerta.setContentText("Ocurrió un error inesperado al procesar la solicitud.\n"
                + "El problema ha sido registrado. Por favor, contacte a soporte técnico.");
        alerta.showAndWait();
    }

}


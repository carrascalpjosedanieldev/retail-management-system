package RetailManagementSystem.vista.configuracion;

import RetailManagementSystem.vista.excepciones.CargarVistaException;
import RetailManagementSystem.vista.utilidades.GestorAlertas;

import javafx.application.Platform;
import javafx.stage.Stage;

public class ConfiguradorExcepciones {

    public static void inicializarManejadorGlobal(Stage stagePrincipal) {
        Thread.setDefaultUncaughtExceptionHandler((hilo, excepcion) -> {
            System.err.println("🚨 ERROR FATAL en el hilo [" + hilo.getName() + "]");
            excepcion.printStackTrace();
            Throwable causaRaiz = obtenerCausaRaiz(excepcion);
            Platform.runLater(() -> {
                if (causaRaiz instanceof CargarVistaException errorVista) {
                    String mensaje = "Ocurrió un Problema al Intentar Abrir la Vista.\n" +
                            "Ruta Solicitada: " + errorVista.getRutaSolicitada() + "\n\n" +
                            "Si el problema persiste, contacte al Creador Original 😎 Jose Daniel 😎.";
                    GestorAlertas.mostrarAlertaError(
                            stagePrincipal ,"Error de Navegación",
                            "NO se pudo Cargar la Pantalla", mensaje
                    );
                } else {
                    GestorAlertas.mostrarAlertaError(
                            stagePrincipal, "Error Interno",
                            "Fallo Crítico del Sistema",
                            "Ocurrió un Error Inesperado al Procesar la Solicitud.\n" +
                                    "El Problema ha sido Registrado. Verifique su Conexión y Notifíquele al Administrador el Error."
                    );
                }
            });
        });
    }

    public static Throwable obtenerCausaRaiz(Throwable e) {
        Throwable causa = e;
        while (causa.getCause() != null && causa != causa.getCause()) {
            causa = causa.getCause();
        }
        return causa;
    }

}


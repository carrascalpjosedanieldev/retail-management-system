package RetailManagementSystem;

// PARA EXPORTAR EL PROYECTO FÁCILMENTE:
// Get-ChildItem -Recurse -Filter *.java | Get-Content | Out-File proyecto_completo.txt

import RetailManagementSystem.infraestructura.inyeccion.ContenedorDependencias;
import RetailManagementSystem.vista.configuracion.ConfiguradorExcepciones;
import RetailManagementSystem.vista.utilidades.CargadorVistas;
import RetailManagementSystem.vista.utilidades.GestorAlertas;
import RetailManagementSystem.vista.utilidades.RutasVista;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stagePrincipal){
        ConfiguradorExcepciones.inicializarManejadorGlobal(stagePrincipal);
        ContenedorDependencias.inicializar();
        try {
            FXMLLoader loader = CargadorVistas.obtenerLoaderConfigurado(RutasVista.MENU_PRINCIPAL_VIEW);
            Parent root = loader.load();
            stagePrincipal.setOnCloseRequest(event -> {
                event.consume();
                GestorAlertas.mostrarAlertaSalirDelSistema(stagePrincipal);
            });
            Scene escena = new Scene(root, 1280, 720);
            stagePrincipal.setTitle("Sistema de Gestión de Tienda - JavaFX");
            stagePrincipal.setScene(escena);
            stagePrincipal.setResizable(true);
            stagePrincipal.setMinWidth(1024);
            stagePrincipal.setMinHeight(600);
            stagePrincipal.show();
        } catch (Exception e) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.initOwner(stagePrincipal);
            alerta.setTitle("Error Crítico de Inicialización");
            alerta.setHeaderText("NO se pudo Iniciar la Aplicación");
            alerta.setContentText("""
                    Ocurrió un fallo grave al cargar la pantalla principal del sistema.
                    
                    Por favor, verifique los archivos de vista o contacte al Soporte Técnico o al Creador Original 😎 Jose Daniel 😎.
                    """);
            alerta.getDialogPane().setPrefSize(480, 200);
            alerta.showAndWait();
        }
    }


}//===================================================================================================================//


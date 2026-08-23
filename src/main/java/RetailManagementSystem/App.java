package RetailManagementSystem;

// PARA EXPORTAR EL PROYECTO FÁCILMENTE:
// Get-ChildItem -Recurse -Filter *.java | Get-Content | Out-File proyecto_completo.txt

import RetailManagementSystem.infraestructura.inyeccion.ContenedorDependencias;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.vista.configuracion.ConfiguradorExcepciones;
import RetailManagementSystem.vista.utilidades.CargadorVistas;
import RetailManagementSystem.vista.utilidades.GestorAlertas;
import RetailManagementSystem.vista.utilidades.RutasVista;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.net.URL;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stagePrincipal){
        ConfiguradorExcepciones.inicializarManejadorGlobal(stagePrincipal);
        ContenedorDependencias.inicializar();
        PermisosApp.inicializarYValidarSincronizacionPermisos();
        try {
            FXMLLoader loader = CargadorVistas.obtenerLoaderConfigurado(RutasVista.LOGIN_PRINCIPAL_VIEW);
            Parent root = loader.load();
            stagePrincipal.setOnCloseRequest(event -> {
                event.consume();
                GestorAlertas.mostrarAlertaSalirDelSistema(stagePrincipal);
            });
            Scene escena = new Scene(root, 600, 600);
            stagePrincipal.setTitle("Sistema de Gestión de Tienda - JavaFX");
            stagePrincipal.setScene(escena);
            stagePrincipal.setResizable(false);
            stagePrincipal.setMinWidth(600);
            stagePrincipal.setMinHeight(600);
            stagePrincipal.show();
        } catch (Exception e) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            if (stagePrincipal != null) {
                alerta.initOwner(stagePrincipal);
            }
            alerta.setTitle("Error Crítico de Inicialización");
            alerta.setHeaderText("NO se pudo Iniciar la Aplicación");
            alerta.setContentText("""
                    Ocurrió un fallo grave al cargar la pantalla principal del sistema.
                    
                    Por favor, verifique los archivos de vista o contacte al Soporte Técnico o al Creador Original 😎 Jose Daniel 😎.
                    
                    """ + "Detalle:  " + e.getMessage());
            DialogPane panelAlerta = alerta.getDialogPane();
            URL urlCss = GestorAlertas.class.getResource(RutasVista.ESTILOS_CSS_ALERTA_DE_NAVEGACION);
            if (urlCss != null) {
                panelAlerta.getStylesheets().add(urlCss.toExternalForm());
            }
            panelAlerta.setMinHeight(Region.USE_PREF_SIZE);
            alerta.showAndWait();
        }
    }


}//===================================================================================================================//


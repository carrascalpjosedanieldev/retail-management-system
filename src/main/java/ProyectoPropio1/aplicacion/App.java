package ProyectoPropio1.aplicacion;

// PARA EXPORTAR EL PROYECTO FÁCILMENTE:
// Get-ChildItem -Recurse -Filter *.java | Get-Content | Out-File proyecto_completo.txt

import ProyectoPropio1.servicios.aplicacion.ServicioConfiguraciones;
import ProyectoPropio1.utilidades.FabricaServicios;
import ProyectoPropio1.utilidades.RutasVista;

import ProyectoPropio1.vista.controladores.MenuPrincipalControlador;
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
        try {
            ServicioConfiguraciones servicioConfiguraciones = FabricaServicios.obtenerServicioConfiguraciones();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(RutasVista.MENU_PRINCIPAL_VIEW));
            loader.setControllerFactory(claseControlador -> {
                if (claseControlador == MenuPrincipalControlador.class) {
                    return new MenuPrincipalControlador(servicioConfiguraciones);
                }
                try {
                    return claseControlador.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            Parent root = loader.load();
            MenuPrincipalControlador controlador = loader.getController();
            stagePrincipal.setOnCloseRequest(event -> {
                event.consume();
                controlador.salirDeSistema();
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
            alerta.setTitle("Error Crítico de Inicialización");
            alerta.setHeaderText("No se pudo iniciar la aplicación");
            alerta.setContentText("""
                    Ocurrió un fallo grave al cargar la pantalla principal del sistema.
                    
                    Por favor, verifique los archivos de vista o contacte al Soporte Técnico o al Creador Original 😎 Jose Daniel 😎..""");
            alerta.getDialogPane().setPrefSize(480, 200);
            alerta.showAndWait();
        }
    }

}//===================================================================================================================//


package ProyectoPropio1.vista.controladores;

import ProyectoPropio1.utilidades.RutasVista;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class GestionarTiendaControlador {

    @FXML
    private Button btnSalir;

    private void cambiarVentana(ActionEvent event, String ruta){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            javafx.scene.control.Alert alerta = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alerta.setTitle("Error de Navegación");
            alerta.setHeaderText("No se pudo cargar la pantalla");
            alerta.setContentText("Ruta no encontrada: " + ruta + "\nRevisa la consola para más detalles.");
            try {
                alerta.getDialogPane().getStylesheets().add(
                        java.util.Objects.requireNonNull(
                                getClass().getResource(RutasVista.ESTILOS_CSS_GESTIONAR_TIENDA)
                        ).toExternalForm()
                );
            } catch (NullPointerException cssEx) {
                System.out.println("No se encontró el CSS para la alerta de error.");
            }
            alerta.showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    void abrirConfiguraciones(ActionEvent event) {

    }

    @FXML
    void abrirDescuentos(ActionEvent event) {
        cambiarVentana(event, RutasVista.GESTIONAR_DESCUENTOS_VIEW);
    }

    @FXML
    void abrirImpuestos(ActionEvent event) {
        cambiarVentana(event, RutasVista.GESTIONAR_IMPUESTOS_VIEW);
    }

    @FXML
    void abrirInventarios(ActionEvent event) {
        cambiarVentana(event, RutasVista.GESTIONAR_INVENTARIOS_VIEW);
    }

    @FXML
    void abrirServicios(ActionEvent event) {
        cambiarVentana(event, RutasVista.GESTIONAR_SERVICIOS_VIEW);
    }

    @FXML
    void volverAlMenu(ActionEvent event) {
        cambiarVentana(event, RutasVista.MENU_PRINCIPAL_VIEW);

    }

}


package ProyectoPropio1.vista.controladores.gestionarTienda;

import ProyectoPropio1.utilidades.RutasVista;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;

import java.net.URL;

public class GestionarTiendaControlador {

    @FXML
    private Button btnSalir;

    private void cambiarVentana(ActionEvent event, String ruta){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error de Navegación");
            alerta.setHeaderText("NO se pudo Cargar la Pantalla");
            alerta.setContentText("Ocurrió un Problema al Intentar Abrir la Vista.\n" +
                    "Ruta Solicitada: " + ruta + "\n" +
                    "Si el problema persiste, contacte al Administrador o al Creador Original 😎 Jose Daniel 😎.");
            DialogPane panelAlerta = alerta.getDialogPane();
            panelAlerta.setPrefSize(500, 280);
            URL urlCss = getClass().getResource(RutasVista.ESTILOS_CSS_GESTIONAR_TIENDA);
            if (urlCss != null) {
                panelAlerta.getStylesheets().add(urlCss.toExternalForm());
            } else {
                panelAlerta.setPrefSize(500, 180);
            }
            alerta.showAndWait();
        }
    }

    @FXML
    void abrirConfiguraciones(ActionEvent event) {
        cambiarVentana(event, RutasVista.GESTIONAR_CONFIGURACIONES_VIEW);
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

}//===================================================================================================================//


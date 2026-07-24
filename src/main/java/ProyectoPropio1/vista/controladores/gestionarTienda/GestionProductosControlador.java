package ProyectoPropio1.vista.controladores.gestionarTienda;

import ProyectoPropio1.utilidades.CargadorVistas;
import ProyectoPropio1.utilidades.RutasVista;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;

public class GestionProductosControlador {

    //ATRIBUTOS:

    @FXML public VBox tabGeneral;
    @FXML public VBox tabRopa;
    @FXML public VBox tabPerecedero;
    @FXML private TabPane tabPaneProductos;
    @FXML private TabGeneralProductosControlador tabGeneralController;
    @FXML private TabRopaControlador tabRopaController;
    @FXML private TabPerecederosControlador tabPerecederoController;

    //MÉTODOS:

    @FXML
    public void initialize() { }

    public void inicializarConInventario(int idInventarioRecibido) {
        if (tabGeneralController != null) {
            tabGeneralController.recibirIdInventario(idInventarioRecibido);
        }
        if (tabRopaController != null) {
            tabRopaController.recibirIdInventario(idInventarioRecibido);
        }
        if (tabPerecederoController != null) {
            tabPerecederoController.recibirIdInventario(idInventarioRecibido);
        }
    }


    @FXML
    void volverAlPanel(ActionEvent event) {
        try {
            Stage stageActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
            CargadorVistas.cambiarPantalla(stageActual, RutasVista.GESTIONAR_INVENTARIOS_VIEW);
        } catch (Exception e) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error de Navegación");
            alerta.setHeaderText(null);
            alerta.setContentText("Ocurrió un Problema al Intentar Volver al Panel de Gestión.\n" +
                    "Detalle: " + e.getMessage());
            alerta.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            DialogPane pane = alerta.getDialogPane();
            pane.setMinHeight(180);
            pane.setMinWidth(400);
            URL urlCss = getClass().getResource(RutasVista.ESTILOS_CSS_PRODUCTOS);
            if (urlCss != null) {
                pane.getStylesheets().add(urlCss.toExternalForm());
            }
            alerta.showAndWait();
        }
    }


}//===================================================================================================================//



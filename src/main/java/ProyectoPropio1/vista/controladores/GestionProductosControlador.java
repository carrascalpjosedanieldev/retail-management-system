package ProyectoPropio1.vista.controladores;

import ProyectoPropio1.utilidades.RutasVista;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class GestionProductosControlador {

    public VBox tabGeneral;

    public VBox tabRopa;

    public VBox tabPerecedero;

    @FXML
    private TabPane tabPaneProductos;

    @FXML
    private TabGeneralProductosControlador tabGeneralController;

    @FXML
    private TabRopaControlador tabRopaController;

    @FXML
    private TabPerecederosControlador tabPerecederoController;

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource(RutasVista.GESTIONAR_INVENTARIOS_VIEW));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}



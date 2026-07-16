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

    @FXML
    void abrirConfiguraciones(ActionEvent event) {

    }

    @FXML
    void abrirDescuentos(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(RutasVista.GESTIONAR_DESCUENTOS_VIEW));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void abrirImpuestos(ActionEvent event) {

    }

    @FXML
    void abrirInventarios(ActionEvent event) {

    }

    @FXML
    void abrirServicios(ActionEvent event) {

    }

    @FXML
    void volverAlMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(RutasVista.MENU_PRINCIPAL_VIEW));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

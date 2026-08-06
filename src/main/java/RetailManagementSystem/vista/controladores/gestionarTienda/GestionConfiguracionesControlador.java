package RetailManagementSystem.vista.controladores.gestionarTienda;

import RetailManagementSystem.vista.excepciones.CargarVistaException;
import RetailManagementSystem.vista.utilidades.CargadorVistas;
import RetailManagementSystem.vista.utilidades.RutasVista;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;


public class GestionConfiguracionesControlador {

    //MÉTODOS:


    @FXML
    void abrirConfiguracionNombre(ActionEvent event) {
        abrirConfiguracionNombre();
    }

    private void abrirConfiguracionNombre(){
        String rutaFxml = RutasVista.EDITAR_NOMBRE_TIENDA_VIEW;
        try {
            FXMLLoader loader = CargadorVistas.obtenerLoaderConfigurado(rutaFxml);
            Parent root = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Configuración de Tienda");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setResizable(false);
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();
        } catch (IOException | IllegalStateException e) {
            throw new CargarVistaException(rutaFxml, "Fallo al abrir ventana de edición", e);
        }
    }

    @FXML
    public void volverPanelGestion(ActionEvent event) {
        Stage stageActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
        CargadorVistas.cambiarPantalla(stageActual, RutasVista.GESTIONAR_TIENDA_VIEW);
    }


}//===================================================================================================================//


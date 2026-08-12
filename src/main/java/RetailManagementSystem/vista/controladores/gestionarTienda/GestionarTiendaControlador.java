package RetailManagementSystem.vista.controladores.gestionarTienda;

import RetailManagementSystem.vista.utilidades.CargadorVistas;
import RetailManagementSystem.vista.utilidades.RutasVista;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class GestionarTiendaControlador {

    @FXML
    private Button btnSalir;

    private void cambiarVentana(ActionEvent event, String ruta){
        Stage stageActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
        CargadorVistas.cambiarPantalla(stageActual, ruta);
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
    public void abrirPoliticasVencimiento(ActionEvent event) {
        cambiarVentana(event, RutasVista.GESTIONAR_POLITICAS_V_VIEW);
    }

    @FXML
    void volverAlMenu(ActionEvent event) {
        cambiarVentana(event, RutasVista.MENU_PRINCIPAL_VIEW);

    }

}//===================================================================================================================//


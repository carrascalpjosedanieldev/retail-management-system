package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarInventarios.gestionarProductos;

import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarInventarios.gestionarProductos.tabGeneral.TabGeneralProductosControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarInventarios.gestionarProductos.tabPerecedero.TabPerecederosControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarInventarios.gestionarProductos.tabRopa.TabRopaControlador;
import RetailManagementSystem.vista.utilidades.CargadorVistas;
import RetailManagementSystem.vista.utilidades.RutasVista;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
        Stage stageActual = (Stage) tabPaneProductos.getScene().getWindow();
        CargadorVistas.cambiarPantalla(stageActual, RutasVista.GESTIONAR_INVENTARIOS_VIEW);
    }


}//===================================================================================================================//



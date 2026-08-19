package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarConfiguraciones;

import RetailManagementSystem.vista.utilidades.CargadorVistas;
import RetailManagementSystem.vista.utilidades.RutasVista;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

public class GestionConfiguracionesControlador {

    //MÉTODOS:

    @FXML
    void abrirConfiguracionNombre(ActionEvent event) {
        CargadorVistas.abrirModalSinInyeccion(
                RutasVista.EDITAR_NOMBRE_TIENDA_VIEW,
                "Configuración de Tienda",
                ((Node) event.getSource()).getScene().getWindow()
                );
    }


    @FXML
    public void abrirGestionRoles(ActionEvent event) {
        Stage stageActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
        CargadorVistas.cambiarPantalla(stageActual, RutasVista.GESTION_ROLES_VIEW);
    }


    @FXML
    void abrirGestionPermisos(ActionEvent event){
        Stage stageActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
        CargadorVistas.cambiarPantalla(stageActual, RutasVista.PERMISOS_VISTA_VIEW);
    }


    @FXML
    public void volverPanelGestion(ActionEvent event) {
        Stage stageActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
        CargadorVistas.cambiarPantalla(stageActual, RutasVista.GESTIONAR_TIENDA_VIEW);
    }

}//===================================================================================================================//


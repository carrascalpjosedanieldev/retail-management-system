package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarConfiguraciones;

import RetailManagementSystem.vista.utilidades.CargadorVistas;
import RetailManagementSystem.vista.utilidades.RutasVista;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Window;

public class GestionConfiguracionesControlador {

    //ATRIBUTOS:

    @FXML
    private Button btnSalir;

    //MÉTODOS:

    private Window getVentana(){
        return btnSalir.getScene() != null ? btnSalir.getScene().getWindow() : null;
    }


    @FXML
    void abrirConfiguracionNombre(ActionEvent event) {
        CargadorVistas.abrirModalSinInyeccion(
                RutasVista.EDITAR_NOMBRE_TIENDA_VIEW,
                "Configuración de Tienda",
                getVentana()
                );
    }


    @FXML
    public void abrirGestionRoles(ActionEvent event) {
        CargadorVistas.cambiarPantalla(getVentana(), RutasVista.GESTION_ROLES_VIEW);
    }


    @FXML
    void abrirGestionPermisos(ActionEvent event){
        CargadorVistas.cambiarPantalla(getVentana(), RutasVista.PERMISOS_VISTA_VIEW);
    }


    @FXML
    public void volverPanelGestion(ActionEvent event) {
        CargadorVistas.cambiarPantalla(getVentana(), RutasVista.GESTIONAR_TIENDA_VIEW);
    }

}//===================================================================================================================//


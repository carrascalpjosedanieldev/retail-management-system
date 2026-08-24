package RetailManagementSystem.vista.controladores.gestionarTienda;

import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.vista.utilidades.CargadorVistas;
import RetailManagementSystem.vista.utilidades.RutasVista;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Window;

public class GestionarTiendaControlador {

    //ATRIBUTOS:

    @FXML private Button btnSalir;

    private UsuarioDTOCompleto usuarioActual;

    //MÉTODOS:

    public void cargarUsuario(UsuarioDTOCompleto usuarioActual){
        if (usuarioActual == null){
            throw new IllegalArgumentException("Usuario Nulo, Error al Recibir el Usuario");
        }
        this.usuarioActual = usuarioActual;
    }

    private Window getVentana(){
        return btnSalir.getScene() != null ? btnSalir.getScene().getWindow() : null;
    }

    private void cambiarVentana(String ruta){
        CargadorVistas.cambiarPantalla(getVentana(), ruta);
    }

    @FXML
    void abrirConfiguraciones(ActionEvent event) {
        cambiarVentana(RutasVista.GESTIONAR_CONFIGURACIONES_VIEW);
    }

    @FXML
    void abrirDescuentos(ActionEvent event) {
        cambiarVentana(RutasVista.GESTIONAR_DESCUENTOS_VIEW);
    }

    @FXML
    void abrirImpuestos(ActionEvent event) {
        cambiarVentana(RutasVista.GESTIONAR_IMPUESTOS_VIEW);
    }

    @FXML
    void abrirInventarios(ActionEvent event) {
        cambiarVentana(RutasVista.GESTIONAR_INVENTARIOS_VIEW);
    }

    @FXML
    void abrirServicios(ActionEvent event) {
        cambiarVentana(RutasVista.GESTIONAR_SERVICIOS_VIEW);
    }

    @FXML
    public void abrirPoliticasVencimiento(ActionEvent event) {
        cambiarVentana(RutasVista.GESTIONAR_POLITICAS_V_VIEW);
    }

    @FXML
    void volverAlMenu(ActionEvent event) {
        cambiarVentana(RutasVista.MENU_PRINCIPAL_VIEW);
    }

}//===================================================================================================================//


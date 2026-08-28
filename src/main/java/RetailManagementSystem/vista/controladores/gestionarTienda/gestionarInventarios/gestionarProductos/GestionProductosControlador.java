package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarInventarios.gestionarProductos;

import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.dominio.excepciones.autenticacionYSeguridad.AccesoDenegadoException;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.infraestructura.seguridad.ValidadorSeguridad;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarInventarios.GestionInventariosControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarInventarios.gestionarProductos.tabGeneral.TabGeneralProductosControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarInventarios.gestionarProductos.tabPerecedero.TabPerecederosControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarInventarios.gestionarProductos.tabRopa.TabRopaControlador;
import RetailManagementSystem.vista.utilidades.CargadorVistas;
import RetailManagementSystem.vista.utilidades.GestorAlertas;
import RetailManagementSystem.vista.utilidades.RutasVista;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.util.List;

public class GestionProductosControlador {

    //ATRIBUTOS:

    @FXML public VBox tabGeneral;
    @FXML public VBox tabRopa;
    @FXML public VBox tabPerecedero;
    @FXML private TabPane tabPaneProductos;
    @FXML private TabGeneralProductosControlador tabGeneralController;
    @FXML private TabRopaControlador tabRopaController;
    @FXML private TabPerecederosControlador tabPerecederoController;

    private UsuarioDTOCompleto usuarioActual;

    //MÉTODOS:

    @FXML
    public void initialize() { }

    public void inicializarConInventarioYUsuario(UsuarioDTOCompleto usuarioActual, int idInventarioRecibido) {
        ValidadorSeguridad.exigirAlgunPermiso(usuarioActual, List.of(
                PermisosApp.VER_PRODUCTOS,
                PermisosApp.REGISTRAR_PRODUCTOS,
                PermisosApp.EDITAR_PRODUCTO,
                PermisosApp.MANEJAR_STOCK_PRODUCTO,
                PermisosApp.TRASLADAR_PRODUCTOS,
                PermisosApp.CAMBIAR_ESTADO_PRODUCTO
        ));
        this.usuarioActual = usuarioActual;
        if (tabGeneralController != null) {
            tabGeneralController.recibirIdInventarioYUsuario(this.usuarioActual, idInventarioRecibido);
        }
        if (tabRopaController != null) {
            tabRopaController.recibirIdInventarioYUsuario(this.usuarioActual, idInventarioRecibido);
        }
        if (tabPerecederoController != null) {
            tabPerecederoController.recibirIdInventarioYUsuario(this.usuarioActual, idInventarioRecibido);
        }
    }

    private Window getVentana(){
        return tabPaneProductos.getScene() != null ? tabPaneProductos.getScene().getWindow() : null;
    }


    @FXML
    private void volverAlPanel(ActionEvent event) {
        volverAGestionInventarios();
    }

    private void volverAGestionInventarios(){
        try {
            CargadorVistas.cambiarPantallaInyectada(
                    getVentana(),
                    RutasVista.GESTIONAR_INVENTARIOS_VIEW,
                    (GestionInventariosControlador c) -> {
                        c.cargarDatos(this.usuarioActual);
                    }
            );
        } catch (AccesoDenegadoException ex){
            GestorAlertas.mostrarAlertaAccesoDenegado(getVentana(), ex);
        }
    }


}//===================================================================================================================//



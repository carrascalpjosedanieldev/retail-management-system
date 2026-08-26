package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarInventarios.gestionarProductos;

import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.dominio.excepciones.autenticacionYSeguridad.AccesoDenegadoException;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
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
        if (usuarioActual == null){
            throw new IllegalArgumentException("Usuario Nulo, Error al Recibir el Usuario");
        }
        if (!usuarioActual.tienePermiso(PermisosApp.VER_PRODUCTOS) ||
            !usuarioActual.tienePermiso(PermisosApp.ADMINISTRAR_PRODUCTOS)){
            GestorAlertas.mostrarAlertaError(
                    getVentana(),
                    "Acceso Denegado", "Privilegios Insuficientes",
                    "NO tienes los Permisos Necesarios para Ver o Gestionar los Productos."
            );
            volverAGestionInventarios();
            return;
        }
        this.usuarioActual = usuarioActual;
        try {
            if (tabGeneralController != null) {
                tabGeneralController.recibirIdInventarioYUsuario(this.usuarioActual, idInventarioRecibido);
            }
            if (tabRopaController != null) {
                tabRopaController.recibirIdInventarioYUsuario(this.usuarioActual, idInventarioRecibido);
            }
            if (tabPerecederoController != null) {
                tabPerecederoController.recibirIdInventarioYUsuario(this.usuarioActual, idInventarioRecibido);
            }
        } catch (AccesoDenegadoException e) {
            GestorAlertas.mostrarAlertaError(
                    getVentana(),
                    "Acceso Denegado", "Privilegios Insuficientes",
                    e.getMessage()
            );
        }

    }

    private Window getVentana(){
        return tabPaneProductos.getScene() != null ? tabPaneProductos.getScene().getWindow() : null;
    }


    @FXML
    void volverAlPanel(ActionEvent event) {
        volverAGestionInventarios();
    }

    private void volverAGestionInventarios(){
        CargadorVistas.cambiarPantallaInyectada(
                getVentana(),
                RutasVista.GESTIONAR_INVENTARIOS_VIEW,
                (GestionInventariosControlador c) -> {
                    c.cargarDatos(this.usuarioActual);
                }
        );
    }


}//===================================================================================================================//



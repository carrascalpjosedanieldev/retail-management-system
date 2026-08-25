package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarConfiguraciones;

import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.vista.controladores.gestionarTienda.GestionarTiendaControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarConfiguraciones.editarTienda.EdicionTiendaControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarConfiguraciones.gestionRoles.GestionRolesControlador;
import RetailManagementSystem.vista.utilidades.CargadorVistas;
import RetailManagementSystem.vista.utilidades.GestorAlertas;
import RetailManagementSystem.vista.utilidades.RutasVista;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Window;

public class GestionConfiguracionesControlador {

    //ATRIBUTOS:

    @FXML private Button btnEditarNombre;
    @FXML private Button btnGestionRoles;
    @FXML private Button btnGestionPermisos;
    @FXML private Button btnSalir;

    private UsuarioDTOCompleto usuarioActual;

    //MÉTODOS:

    public void cargarUsuario(UsuarioDTOCompleto usuarioActual){
        if (usuarioActual == null){
            throw new IllegalArgumentException("Usuario Nulo, Error al Recibir el Usuario");
        }
        if (
            !usuarioActual.tienePermiso(PermisosApp.EDITAR_PERFIL_DE_TIENDA) &&
            !usuarioActual.tienePermiso(PermisosApp.GESTIONAR_ROLES) &&
            !usuarioActual.tienePermiso(PermisosApp.GESTIONAR_PERMISOS)
        ) {
            GestorAlertas.mostrarAlertaError(
                    getVentana(),
                    "Acceso Denegado", "Privilegios Insuficientes",
                    "NO tienes los Permisos Necesarios para entrar a Configuraciones."
            );
            volverAGestionarTienda();
            return;
        }
        this.usuarioActual = usuarioActual;
        protegerBoton(btnEditarNombre, PermisosApp.EDITAR_PERFIL_DE_TIENDA);
        protegerBoton(btnGestionRoles, PermisosApp.GESTIONAR_ROLES);
        protegerBoton(btnGestionPermisos, PermisosApp.GESTIONAR_PERMISOS);
    }

    private void protegerBoton(Button boton, String permisoRequerido) {
        boolean tieneAcceso = this.usuarioActual.tienePermiso(permisoRequerido);
        boton.setVisible(tieneAcceso);
        boton.setManaged(tieneAcceso);
    }

    private Window getVentana(){
        return btnSalir.getScene() != null ? btnSalir.getScene().getWindow() : null;
    }


    @FXML
    void abrirConfiguracionNombre(ActionEvent event) {
        CargadorVistas.abrirModalConInyeccion(
                RutasVista.EDITAR_NOMBRE_TIENDA_VIEW,
                "Configuración de Tienda",
                getVentana(),
                (EdicionTiendaControlador c) -> {
                    c.cargarUsuario(this.usuarioActual);
                }
        );
    }


    @FXML
    public void abrirGestionRoles(ActionEvent event) {
        CargadorVistas.cambiarPantallaInyectada(
                getVentana(),
                RutasVista.GESTION_ROLES_VIEW,
                (GestionRolesControlador c) -> {
                    c.cargarUsuario(this.usuarioActual);
                }
        );
    }


    @FXML
    void abrirGestionPermisos(ActionEvent event){
        CargadorVistas.cambiarPantalla(getVentana(), RutasVista.PERMISOS_VISTA_VIEW);
    }


    @FXML
    public void volverPanelGestion(ActionEvent event) {
        volverAGestionarTienda();
    }

    private void volverAGestionarTienda(){
        CargadorVistas.cambiarPantallaInyectada(
                getVentana(),
                RutasVista.GESTIONAR_TIENDA_VIEW,
                (GestionarTiendaControlador c) -> {
                    c.cargarUsuario(this.usuarioActual);
                }
        );
    }

}//===================================================================================================================//


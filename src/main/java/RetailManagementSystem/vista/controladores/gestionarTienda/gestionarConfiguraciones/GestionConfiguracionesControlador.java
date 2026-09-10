package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarConfiguraciones;

import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.dominio.excepciones.autenticacionYSeguridad.AccesoDenegadoException;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.infraestructura.seguridad.ValidadorSeguridad;
import RetailManagementSystem.vista.controladores.gestionarTienda.GestionarTiendaControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarConfiguraciones.editarTienda.EdicionTiendaControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarConfiguraciones.gestionPermisos.GestionPermisosControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarConfiguraciones.gestionRoles.GestionRolesControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarConfiguraciones.politicasDeBloqueo.EditarPoliticasBloqueoControlador;
import RetailManagementSystem.vista.utilidades.CargadorVistas;
import RetailManagementSystem.vista.utilidades.GestorAlertas;
import RetailManagementSystem.vista.utilidades.RutasVista;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Window;

import java.util.List;

public class GestionConfiguracionesControlador {

    //ATRIBUTOS:

    @FXML private Button btnEditarNombre;
    @FXML private Button btnGestionRoles;
    @FXML private Button btnGestionPermisos;
    @FXML private Button btnPoliticasDeBloqueo;
    @FXML private Button btnSalir;

    private UsuarioDTOCompleto usuarioActual;

    //MÉTODOS:

    public void cargarUsuario(UsuarioDTOCompleto usuarioActual){
        ValidadorSeguridad.exigirAlgunPermiso(usuarioActual, List.of(
                PermisosApp.EDITAR_PERFIL_DE_TIENDA,
                PermisosApp.REGISTRAR_ROLES,
                PermisosApp.EDITAR_ROLES,
                PermisosApp.ADMINISTRAR_PERMISOS_DE_ROLES,
                PermisosApp.GESTIONAR_PERMISOS
        ));
        this.usuarioActual = usuarioActual;
        protegerBoton(btnEditarNombre, PermisosApp.EDITAR_PERFIL_DE_TIENDA);
        protegerBoton(btnGestionRoles, PermisosApp.EDITAR_ROLES);
        protegerBoton(btnGestionPermisos, PermisosApp.GESTIONAR_PERMISOS);
        protegerBoton(btnPoliticasDeBloqueo, PermisosApp.EDITAR_POLITICAS_DE_BLOQUEO);
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
    private void abrirConfiguracionNombre(ActionEvent event) {
        try {
            CargadorVistas.abrirModalConInyeccion(
                    RutasVista.EDITAR_NOMBRE_TIENDA_VIEW,
                    "Configuración de Tienda",
                    getVentana(),
                    (EdicionTiendaControlador c) -> {
                        c.cargarUsuario(this.usuarioActual);
                    }
            );
        } catch (AccesoDenegadoException ex){
            GestorAlertas.mostrarAlertaAccesoDenegado(getVentana(), ex);
        }
    }


    @FXML
    public void abrirGestionRoles(ActionEvent event) {
        try {
            CargadorVistas.cambiarPantallaInyectada(
                    getVentana(),
                    RutasVista.GESTION_ROLES_VIEW,
                    (GestionRolesControlador c) -> {
                        c.cargarUsuario(this.usuarioActual);
                    }
            );
        } catch (AccesoDenegadoException ex){
            GestorAlertas.mostrarAlertaAccesoDenegado(getVentana(), ex);
        }
    }


    @FXML
    private void abrirGestionPermisos(ActionEvent event){
        try {
            CargadorVistas.cambiarPantallaInyectada(
                    getVentana(),
                    RutasVista.PERMISOS_VISTA_VIEW,
                    (GestionPermisosControlador c) -> {
                        c.cargarUsuario(this.usuarioActual);
                    }
            );
        } catch (AccesoDenegadoException ex){
            GestorAlertas.mostrarAlertaAccesoDenegado(getVentana(), ex);
        }
    }


    @FXML
    private void abrirPoliticasDeBloqueo(ActionEvent event) {
        try {
            CargadorVistas.abrirModalConInyeccion(
                    RutasVista.EDITAR_POLITICAS_DE_BLOQUEO_VIEW,
                    "Gestion Políticas de Bloqueo",
                    getVentana(),
                    (EditarPoliticasBloqueoControlador c)->{
                        c.cargarDatos(this.usuarioActual);
                    }
            );
        } catch (AccesoDenegadoException ex){
            GestorAlertas.mostrarAlertaAccesoDenegado(getVentana(), ex);
        }
    }


    @FXML
    private void volverPanelGestion(ActionEvent event) {
        try {
            CargadorVistas.cambiarPantallaInyectada(
                    getVentana(),
                    RutasVista.GESTIONAR_TIENDA_VIEW,
                    (GestionarTiendaControlador c) -> {
                        c.cargarUsuario(this.usuarioActual);
                    }
            );
        } catch (AccesoDenegadoException ex){
            GestorAlertas.mostrarAlertaAccesoDenegado(getVentana(), ex);
        }
    }

}//===================================================================================================================//


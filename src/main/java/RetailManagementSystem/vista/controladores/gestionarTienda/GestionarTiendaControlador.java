package RetailManagementSystem.vista.controladores.gestionarTienda;

import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarConfiguraciones.GestionConfiguracionesControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarDescuentos.GestionDescuentosControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarImpuestos.GestionImpuestosControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarInventarios.GestionInventariosControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarPoliticasV.GestionPoliticasVencimientoControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarServicios.GestionServiciosControlador;
import RetailManagementSystem.vista.controladores.menuPrincipal.MenuPrincipalControlador;
import RetailManagementSystem.vista.utilidades.CargadorVistas;
import RetailManagementSystem.vista.utilidades.RutasVista;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Window;

import java.util.List;

public class GestionarTiendaControlador {

    //ATRIBUTOS:

    @FXML private Button btnSalir;
    @FXML private Button btnConfiguraciones;
    @FXML private Button btnGestionDescuentos;
    @FXML private Button btnGestionImpuestos;
    @FXML private Button btnGestionInventarios;
    @FXML private Button btnGestionPoliticasV;
    @FXML private Button btnGestionServicios;

    private UsuarioDTOCompleto usuarioActual;

    //MÉTODOS:

    public void cargarUsuario(UsuarioDTOCompleto usuarioActual){
        if (usuarioActual == null){
            throw new IllegalArgumentException("Usuario Nulo, Error al Recibir el Usuario");
        }
        this.usuarioActual = usuarioActual;
        configurarVisibilidadModulos();
    }

    private boolean tieneAccesoAlModulo(List<String> permisosDelModulo) {
        return permisosDelModulo.stream()
                .anyMatch(permiso -> this.usuarioActual.tienePermiso(permiso));
    }

    private void configurarVisibilidadModulos() {
        boolean accesoConfiguraciones = tieneAccesoAlModulo(List.of(
                PermisosApp.EDITAR_PERFIL_DE_TIENDA,
                PermisosApp.VER_ROLES,
                PermisosApp.REGISTRAR_ROLES,
                PermisosApp.EDITAR_ROLES,
                PermisosApp.ADMINISTRAR_PERMISOS_DE_ROLES,
                PermisosApp.VER_PERMISOS,
                PermisosApp.GESTIONAR_PERMISOS
        ));
        btnConfiguraciones.setVisible(accesoConfiguraciones);
        btnConfiguraciones.setManaged(accesoConfiguraciones);
        boolean accesoDescuentos = tieneAccesoAlModulo(List.of(
                PermisosApp.VER_DESCUENTOS,
                PermisosApp.REGISTRAR_DESCUENTOS,
                PermisosApp.MODIFICAR_DESCUENTOS,
                PermisosApp.CAMBIAR_ESTADO_DESCUENTOS
        ));
        btnGestionDescuentos.setVisible(accesoDescuentos);
        btnGestionDescuentos.setManaged(accesoDescuentos);
        boolean accesoImpuestos = tieneAccesoAlModulo(List.of(
                PermisosApp.VER_IMPUESTOS
        ));
        btnGestionImpuestos.setVisible(accesoImpuestos);
        btnGestionImpuestos.setManaged(accesoImpuestos);
        boolean accesoInventarios = tieneAccesoAlModulo(List.of(
                PermisosApp.VER_INVENTARIOS,
                PermisosApp.ADMINISTRAR_INVENTARIOS,
                PermisosApp.VER_PRODUCTOS,
                PermisosApp.ADMINISTRAR_PRODUCTOS,
                PermisosApp.TRASLADAR_PRODUCTOS
        ));
        btnGestionInventarios.setVisible(accesoInventarios);
        btnGestionInventarios.setManaged(accesoInventarios);
        boolean accesoPoliticasV = tieneAccesoAlModulo(List.of(
                PermisosApp.POLITICAS_DE_VENCIMIENTO
        ));
        btnGestionPoliticasV.setVisible(accesoPoliticasV);
        btnGestionPoliticasV.setManaged(accesoPoliticasV);
        boolean accesoServicios = tieneAccesoAlModulo(List.of(
                PermisosApp.VER_SERVICIOS,
                PermisosApp.ADMINISTRAR_SERVICIOS
        ));
        btnGestionServicios.setVisible(accesoServicios);
        btnGestionServicios.setManaged(accesoServicios);
    }

    private Window getVentana(){
        return btnSalir.getScene() != null ? btnSalir.getScene().getWindow() : null;
    }


    @FXML
    private void abrirConfiguraciones(ActionEvent event) {
        CargadorVistas.cambiarPantallaInyectada(
                getVentana(),
                RutasVista.GESTIONAR_CONFIGURACIONES_VIEW,
                (GestionConfiguracionesControlador c) -> {
                    c.cargarUsuario(this.usuarioActual);
                }
        );
    }


    @FXML
    private void abrirDescuentos(ActionEvent event) {
        CargadorVistas.cambiarPantallaInyectada(
                getVentana(),
                RutasVista.GESTIONAR_DESCUENTOS_VIEW,
                (GestionDescuentosControlador c) -> {
                    c.cargarUsuario(this.usuarioActual);
                }
        );
    }


    @FXML
    private void abrirImpuestos(ActionEvent event) {
        CargadorVistas.cambiarPantallaInyectada(
                getVentana(),
                RutasVista.GESTIONAR_IMPUESTOS_VIEW,
                (GestionImpuestosControlador c) -> {
                    c.cargarDatos(this.usuarioActual);
                }
        );
    }


    @FXML
    private void abrirInventarios(ActionEvent event) {
        CargadorVistas.cambiarPantallaInyectada(
                getVentana(),
                RutasVista.GESTIONAR_INVENTARIOS_VIEW,
                (GestionInventariosControlador c) -> {
                    c.cargarDatos(this.usuarioActual);
                }
        );
    }


    @FXML
    private void abrirServicios(ActionEvent event) {
        CargadorVistas.cambiarPantallaInyectada(
                getVentana(),
                RutasVista.GESTIONAR_SERVICIOS_VIEW,
                (GestionServiciosControlador c) -> {
                    c.cargarDatos(this.usuarioActual);
                }
        );
    }


    @FXML
    private void abrirPoliticasVencimiento(ActionEvent event) {
        CargadorVistas.cambiarPantallaInyectada(
                getVentana(),
                RutasVista.GESTIONAR_POLITICAS_V_VIEW,
                (GestionPoliticasVencimientoControlador c) -> {
                    c.cargarDatos(this.usuarioActual);
                }
        );
    }


    @FXML
    private void volverAlMenu(ActionEvent event) {
        CargadorVistas.cambiarPantallaInyectada(
                getVentana(),
                RutasVista.MENU_PRINCIPAL_VIEW,
                (MenuPrincipalControlador c) -> {
                    c.recibirUsuarioActual(this.usuarioActual);
                }
        );
    }

}//===================================================================================================================//


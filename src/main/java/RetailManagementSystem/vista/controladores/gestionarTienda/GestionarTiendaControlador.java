package RetailManagementSystem.vista.controladores.gestionarTienda;

import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarConfiguraciones.GestionConfiguracionesControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarDescuentos.GestionDescuentosControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarImpuestos.GestionImpuestosControlador;
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
                PermisosApp.GESTIONAR_ROLES,
                PermisosApp.GESTIONAR_PERMISOS
        ));
        btnConfiguraciones.setVisible(accesoConfiguraciones);
        btnConfiguraciones.setManaged(accesoConfiguraciones);
    }

    private Window getVentana(){
        return btnSalir.getScene() != null ? btnSalir.getScene().getWindow() : null;
    }


    private void cambiarVentana(String ruta){
        CargadorVistas.cambiarPantalla(getVentana(), ruta);
    }


    @FXML
    void abrirConfiguraciones(ActionEvent event) {
        CargadorVistas.cambiarPantallaInyectada(
                getVentana(),
                RutasVista.GESTIONAR_CONFIGURACIONES_VIEW,
                (GestionConfiguracionesControlador c) -> {
                    c.cargarUsuario(this.usuarioActual);
                }
        );
    }


    @FXML
    void abrirDescuentos(ActionEvent event) {
        CargadorVistas.cambiarPantallaInyectada(
                getVentana(),
                RutasVista.GESTIONAR_DESCUENTOS_VIEW,
                (GestionDescuentosControlador c) -> {
                    c.cargarUsuario(this.usuarioActual);
                }
        );
    }


    @FXML
    void abrirImpuestos(ActionEvent event) {
        CargadorVistas.cambiarPantallaInyectada(
                getVentana(),
                RutasVista.GESTIONAR_IMPUESTOS_VIEW,
                (GestionImpuestosControlador c) -> {

                }
        );
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
        CargadorVistas.cambiarPantallaInyectada(
                getVentana(),
                RutasVista.MENU_PRINCIPAL_VIEW,
                (MenuPrincipalControlador c) -> {
                    c.recibirUsuarioActual(this.usuarioActual);
                }
        );
    }

}//===================================================================================================================//


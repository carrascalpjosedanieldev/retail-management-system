package RetailManagementSystem.vista.controladores.menuPrincipal;

import RetailManagementSystem.aplicacion.dto.seguridad.RolDTO;
import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.servicios.ServicioConfiguraciones;
import RetailManagementSystem.infraestructura.configuracion.InformacionAplicacion;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.vista.controladores.gestionarTienda.GestionarTiendaControlador;
import RetailManagementSystem.vista.controladores.gestionarUsuarios.GestionUsuariosControlador;
import RetailManagementSystem.vista.controladores.puntoDeVenta.PanelDeControlControlador;
import RetailManagementSystem.vista.utilidades.CargadorVistas;
import RetailManagementSystem.vista.utilidades.GestorAlertas;
import RetailManagementSystem.vista.utilidades.RutasVista;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Window;
import javafx.util.Duration;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MenuPrincipalControlador {

    //ATRIBUTOS:

    @FXML private Button btnSalir;
    @FXML private Label lblNombreTienda;
    @FXML private Label lblNombreUsuario;
    @FXML private Label lblReloj;
    @FXML private Label lblRoles;
    @FXML private Label lblVersion;
    @FXML private Button btnGestionarTienda;
    @FXML private Button btnPuntoVenta;
    @FXML private Button btnGestionarUsuarios;

    private UsuarioDTOCompleto usuarioActual;

    private final ServicioConfiguraciones servicioConfiguraciones;

    //CONSTRUCTOR:

    public MenuPrincipalControlador(ServicioConfiguraciones servicioConfiguraciones) {
        this.servicioConfiguraciones = servicioConfiguraciones;
    }

    //MÉTODOS:

    public void recibirUsuarioActual(UsuarioDTOCompleto usuarioActual){
        if (usuarioActual == null){
            throw new IllegalArgumentException("Usuario Nulo, Error al Recibir el Usuario");
        }
        this.usuarioActual = usuarioActual;
        lblNombreUsuario.setText(usuarioActual.getNombreCompleto());
        if (usuarioActual.roles().isEmpty()){
            lblRoles.setText("Sin Roles");
        } else {
            StringBuilder roles = new StringBuilder();
            for (RolDTO rol:usuarioActual.roles()){
                roles.append(rol.nombre());
                roles.append(System.lineSeparator());
            }
            lblRoles.setText(roles.toString());
        }
        configurarVisibilidadModulos();
    }

    private boolean tieneAccesoAlModulo(List<String> permisosDelModulo) {
        return permisosDelModulo.stream()
                .anyMatch(permiso -> this.usuarioActual.tienePermiso(permiso));
    }

    private void configurarVisibilidadModulos() {
        boolean accesoPuntoVenta = tieneAccesoAlModulo(List.of(
                PermisosApp.PROCESAR_VENTA,
                PermisosApp.VER_HISTORIAL_VENTAS
        ));
        btnPuntoVenta.setVisible(accesoPuntoVenta);
        btnPuntoVenta.setManaged(accesoPuntoVenta);
        boolean accesoUsuarios = tieneAccesoAlModulo(List.of(
                PermisosApp.GESTIONAR_USUARIOS
        ));
        btnGestionarUsuarios.setVisible(accesoUsuarios);
        btnGestionarUsuarios.setManaged(accesoUsuarios);
        boolean accesoTienda = tieneAccesoAlModulo(List.of(
                PermisosApp.VER_INVENTARIOS,
                PermisosApp.ADMINISTRAR_INVENTARIOS,
                PermisosApp.VER_PRODUCTOS,
                PermisosApp.ADMINISTRAR_PRODUCTOS,
                PermisosApp.TRASLADAR_PRODUCTOS,
                PermisosApp.VER_SERVICIOS,
                PermisosApp.REGISTRAR_SERVICIOS,
                PermisosApp.VER_IMPUESTOS,
                PermisosApp.VER_DESCUENTOS,
                PermisosApp.VER_POLITICAS_V,
                PermisosApp.EDITAR_PERFIL_DE_TIENDA,
                PermisosApp.EDITAR_ROLES,
                PermisosApp.GESTIONAR_PERMISOS
        ));
        btnGestionarTienda.setVisible(accesoTienda);
        btnGestionarTienda.setManaged(accesoTienda);
    }

    private Window getVentana(){
        return btnSalir.getScene() != null ? btnSalir.getScene().getWindow() : null;
    }


    @FXML
    public void initialize() {
        iniciarReloj();
        cargarNombreTienda();
        cargarVersionTienda();
    }

    private void cargarVersionTienda(){
        lblVersion.setText("Cargando...");
        CompletableFuture.supplyAsync(
                InformacionAplicacion::obtenerVersion
        ).thenAccept(version->{
            Platform.runLater(()->{
                lblVersion.setText("Mi Tienda " + version);
            });
        }).exceptionally(ex->{
            Platform.runLater(() -> {
                lblVersion.setText("Versión --");
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                GestorAlertas.mostrarAlertaError(
                        getVentana(), "Error de Carga", "Error al Cargar la Version",
                        "NO se pudo Cargar la Versión de la Tienda: " + causa.getMessage() + ".\n" +
                                "Contacte al Administrador o Verifica tu Conexión."
                );
            });
            return null;
        });
    }

    private void iniciarReloj() {
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("hh:mm a");
        Timeline reloj = new Timeline(
                new KeyFrame(Duration.seconds(1), evento -> {
                    LocalTime horaActual = LocalTime.now();
                    lblReloj.setText(horaActual.format(formatoHora));
                })
        );
        reloj.setCycleCount(Timeline.INDEFINITE);
        reloj.play();
    }

    private void cargarNombreTienda() {
        lblNombreTienda.setText("Cargando...");
        CompletableFuture.supplyAsync(
                this.servicioConfiguraciones::obtenerNombreTienda
        ).thenAccept(nombreTienda -> {
            Platform.runLater(() -> {
                if (nombreTienda != null && !nombreTienda.isBlank()) {
                    lblNombreTienda.setText(nombreTienda);
                } else {
                    lblNombreTienda.setText("Mi Tienda");
                }
            });
        }).exceptionally(ex -> {
            Platform.runLater(() -> {
                lblNombreTienda.setText("Tienda (Modo Offline)");
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                GestorAlertas.mostrarAlertaError(
                        getVentana(), "Error de Carga",
                        "Error al Obtener el Nombre de la Tienda",
                        "NO se pudo Leer la Configuración Local: " + causa.getMessage() + "\n" +
                                "Verifica tu conexión para seguir utilizando la App."
                );
            });
            return null;
        });
    }


    @FXML
    public void abrirPuntoDeVenta(ActionEvent event) {
        CargadorVistas.cambiarPantallaInyectada(
                getVentana(),
                RutasVista.PANEL_DE_CONTROL_POS_VIEW,
                (PanelDeControlControlador c) -> {
                    c.cargarUsuario(this.usuarioActual);
                }
        );
    }


    @FXML
    void abrirGestionarTienda(ActionEvent event) {
        CargadorVistas.cambiarPantallaInyectada(
                getVentana(),
                RutasVista.GESTIONAR_TIENDA_VIEW,
                (GestionarTiendaControlador c) -> {
                    c.cargarUsuario(this.usuarioActual);
                }
        );
    }

    @FXML
    void abrirGestionarUsuarios(ActionEvent event){
        CargadorVistas.cambiarPantallaInyectada(
                getVentana(),
                RutasVista.GESTIONAR_USUARIOS_VIEW,
                (GestionUsuariosControlador c) -> {
                    c.cargarUsuario(this.usuarioActual);
                }
        );
    }

    @FXML
    public void cerrarSesion(ActionEvent event) {
        if (!GestorAlertas.mostrarConfirmacion(getVentana(), "Cerrar Sesión?", null,
                "Estas Seguro de que quieres cerrar Sesión?")){
            return;
        }
        CargadorVistas.cambiarPantallaConTamanoPequeno(
                getVentana(), RutasVista.LOGIN_PRINCIPAL_VIEW
        );
    }

}//===================================================================================================================//


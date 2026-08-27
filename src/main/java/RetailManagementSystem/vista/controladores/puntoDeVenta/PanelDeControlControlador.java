package RetailManagementSystem.vista.controladores.puntoDeVenta;

import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorHistoricoDeVentas;
import RetailManagementSystem.infraestructura.configuracion.InformacionAplicacion;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.vista.controladores.menuPrincipal.MenuPrincipalControlador;
import RetailManagementSystem.vista.utilidades.CargadorVistas;
import RetailManagementSystem.vista.utilidades.FormateadorNumeros;
import RetailManagementSystem.vista.utilidades.GestorAlertas;
import RetailManagementSystem.vista.utilidades.RutasVista;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Window;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class PanelDeControlControlador {

    //ATRIBUTOS:

    @FXML public Button btnNuevaVenta;
    @FXML public Button btnHistorialVentas;
    @FXML public Button btnVolver;
    @FXML public Label lblUltimaVenta;
    @FXML public Label lblCantidadFacturas;
    @FXML public Label lblTotalVentasHoy;
    @FXML public Label lblReloj;
    @FXML public Label lblVersion;
    @FXML private Label lblNombreCajero;

    private UsuarioDTOCompleto usuarioActual;

    private final OrquestadorHistoricoDeVentas orquestadorHistoricoDeVentas;

    //CONTROLADOR:

    public PanelDeControlControlador(OrquestadorHistoricoDeVentas orquestadorHistoricoDeVentas) {
        this.orquestadorHistoricoDeVentas = orquestadorHistoricoDeVentas;
    }

    //MÉTODOS:

    public void cargarUsuario(UsuarioDTOCompleto usuarioActual){
        if (usuarioActual == null){
            throw new IllegalArgumentException("Usuario Nulo, Error al Recibir el Usuario");
        }
        this.usuarioActual = usuarioActual;
        lblNombreCajero.setText(usuarioActual.getNombreCompleto());
        protegerBoton(btnNuevaVenta, PermisosApp.PROCESAR_VENTA);
        protegerBoton(btnHistorialVentas, PermisosApp.VER_HISTORIAL_VENTAS);
    }

    private void protegerBoton(Button boton, String permisoRequerido) {
        boolean tieneAcceso = this.usuarioActual.tienePermiso(permisoRequerido);
        boton.setVisible(tieneAcceso);
        boton.setManaged(tieneAcceso);
    }

    private Window getVentana(){
        return btnVolver.getScene() != null ? btnVolver.getScene().getWindow() : null;
    }


    @FXML
    public void initialize() {
        iniciarReloj();
        cargarMetricasDelDia();
        cargarVersionTienda();
    }

    private void iniciarReloj() {
        DateTimeFormatter formatoFechaHora = DateTimeFormatter.ofPattern(
                "EEEE, dd 'de' MMMM 'de' yyyy - hh:mm:ss a",
                Locale.of("es", "CO")
        );
        Timeline relojTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1), evento -> {
                    LocalDateTime ahora = LocalDateTime.now();
                    String fechaFormateada = ahora.format(formatoFechaHora);
                    fechaFormateada = fechaFormateada.substring(0, 1).toUpperCase() + fechaFormateada.substring(1);
                    lblReloj.setText(fechaFormateada);
                })
        );
        relojTimeline.setCycleCount(Animation.INDEFINITE);
        relojTimeline.play();
    }

    private void cargarMetricasDelDia() {
        lblCantidadFacturas.setText("...");
        lblTotalVentasHoy.setText("Calculando...");
        lblUltimaVenta.setText("Cargando...");
        CompletableFuture.supplyAsync(
                this.orquestadorHistoricoDeVentas::obtenerResumenHoy
        ).thenAccept(resumenVentaDia ->
            Platform.runLater(()->{
                lblCantidadFacturas.setText(String.valueOf(resumenVentaDia.cantidadFacturas()));
                lblTotalVentasHoy.setText(FormateadorNumeros.formatoMoneda(resumenVentaDia.totalVentas()));
                lblUltimaVenta.setText(FormateadorNumeros.formatoMoneda(resumenVentaDia.ultimaVenta()));
            })
        ).exceptionally(ex->{
            Platform.runLater(()->{
                lblCantidadFacturas.setText("0");
                lblTotalVentasHoy.setText("$ 0.00");
                lblUltimaVenta.setText("$ 0.00");
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                GestorAlertas.mostrarAlertaError(
                        getVentana(), "Error de Conexión",
                        "No se pudieron cargar las métricas de hoy",
                        "Se asignaron valores en cero. Se ha registrado el error: " + causa.getMessage()
                );
            });
            return null;
        });
    }

    private void cargarVersionTienda(){
        lblVersion.setText("Cargando...");
        CompletableFuture.supplyAsync(
                InformacionAplicacion::obtenerVersion
        ).thenAccept(version->
            Platform.runLater(()->
                lblVersion.setText("Mi Tienda " + version)
            )
        ).exceptionally(ex->{
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


    @FXML
    private void abrirNuevaVenta(ActionEvent event) {
        CargadorVistas.cambiarPantallaInyectada(
                getVentana(),
                RutasVista.MENU_DE_VENTAS_VIEW,
                (MenuDeVentasControlador c) -> {
                    c.cargarUsuario(this.usuarioActual);
                }
        );
    }


    @FXML
    private void abrirHistorialVentas(ActionEvent event) {
        CargadorVistas.abrirModalConInyeccion(
                RutasVista.HISTORIAL_VENTAS_VIEW,
                "Generar Reporte de Recaudo", getVentana(),
                (HistorialVentasControlador c) -> {
                    c.cargarUsuario(this.usuarioActual);
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


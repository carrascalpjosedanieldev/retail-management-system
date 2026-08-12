package RetailManagementSystem.vista.controladores.menuPrincipal;

import RetailManagementSystem.aplicacion.servicios.ServicioConfiguraciones;
import RetailManagementSystem.infraestructura.configuracion.InformacionAplicacion;
import RetailManagementSystem.vista.utilidades.CargadorVistas;
import RetailManagementSystem.vista.utilidades.GestorAlertas;
import RetailManagementSystem.vista.utilidades.RutasVista;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

public class MenuPrincipalControlador {

    //ATRIBUTOS:

    @FXML public Button btnSalir;
    @FXML public Label lblVersion;
    @FXML private Label lblReloj;
    @FXML private Label lblNombreTienda;

    private final ServicioConfiguraciones servicioConfiguraciones;

    //CONSTRUCTOR:

    public MenuPrincipalControlador(ServicioConfiguraciones servicioConfiguraciones) {
        this.servicioConfiguraciones = servicioConfiguraciones;
    }

    //MÉTODOS:

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
                        "Error de Carga", "Error al Cargar la Version",
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
                        "Error de Carga",
                        "Error al Obtener el Nombre de la Tienda",
                        "NO se pudo Leer la Configuración Local: " + causa.getMessage() + "\n" +
                                "Vertica tu conexión para seguir utilizando la App."
                );
            });
            return null;
        });
    }


    @FXML
    public void abrirPuntoDeVenta(ActionEvent event) {
        Stage stageActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
        CargadorVistas.cambiarPantalla(stageActual, RutasVista.PANEL_DE_CONTROL_POS_VIEW);
    }


    @FXML
    void abrirGestionarTienda(ActionEvent event) {
        Stage stageActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
        CargadorVistas.cambiarPantalla(stageActual, RutasVista.GESTIONAR_TIENDA_VIEW);
    }


    @FXML
    void salirDelSistema(ActionEvent event) {
        GestorAlertas.mostrarAlertaSalirDelSistema();
    }


}//===================================================================================================================//


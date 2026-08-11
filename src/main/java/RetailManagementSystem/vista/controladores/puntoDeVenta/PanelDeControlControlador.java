package RetailManagementSystem.vista.controladores.puntoDeVenta;

import RetailManagementSystem.aplicacion.dto.ventas.ResumenVentaDiaDTO;
import RetailManagementSystem.aplicacion.ensambladores.EnsambladorDTOFactura;
import RetailManagementSystem.aplicacion.servicios.ServicioFacturas;
import RetailManagementSystem.infraestructura.configuracion.InformacionAplicacion;
import RetailManagementSystem.vista.utilidades.CargadorVistas;
import RetailManagementSystem.vista.utilidades.FormateadorNumeros;
import RetailManagementSystem.vista.utilidades.GestorAlertas;
import RetailManagementSystem.vista.utilidades.RutasVista;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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

    private final ServicioFacturas servicioFacturas;

    private final EnsambladorDTOFactura ensambladorDTOFactura;

    //CONTROLADOR:

    public PanelDeControlControlador(ServicioFacturas servicioFacturas, EnsambladorDTOFactura ensambladorDTOFactura) {
        this.servicioFacturas = servicioFacturas;
        this.ensambladorDTOFactura = ensambladorDTOFactura;
    }

    //MÉTODOS:

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
        Task<ResumenVentaDiaDTO> tareaMetricas = new Task<>() {
            @Override
            protected ResumenVentaDiaDTO call() throws Exception {
                return ensambladorDTOFactura.ensamblarResumenVentaDia(
                        servicioFacturas.obtenerResumenHoy()
                );
            }
        };
        tareaMetricas.setOnSucceeded(evento -> {
            ResumenVentaDiaDTO resumen = tareaMetricas.getValue();
            lblCantidadFacturas.setText(String.valueOf(resumen.cantidadFacturas()));
            lblTotalVentasHoy.setText(FormateadorNumeros.formatoMoneda(resumen.totalVentas()));
            lblUltimaVenta.setText(FormateadorNumeros.formatoMoneda(resumen.ultimaVenta()));
        });
        tareaMetricas.setOnFailed(evento -> {
            lblCantidadFacturas.setText("0");
            lblTotalVentasHoy.setText("$ 0.00");
            lblUltimaVenta.setText("$ 0.00");
            Throwable errorReal = tareaMetricas.getException();
            GestorAlertas.mostrarAlertaError(
                    "Error de Conexión", "No se pudieron cargar las métricas de hoy",
                    "Se asignaron valores en cero. Se ha registrado el error: " +
                            errorReal.getClass().getSimpleName()
            );
        });
        Thread hilo = new Thread(tareaMetricas);
        hilo.setDaemon(true);
        hilo.start();
    }

    private void cargarVersionTienda(){
        try {
            String version = InformacionAplicacion.obtenerVersion();
            lblVersion.setText("Mi Tienda " + version);
        } catch (RuntimeException e) {
            lblVersion.setText("Versión --");
            GestorAlertas.mostrarAlertaError(
                    "Error de Carga", "Error al Cargar la Version",
                    "No se pudo cargar la versión de la tienda. Contacte a soporte."
            );
        }
    }


    @FXML
    public void abrirNuevaVenta(ActionEvent event) {
        Stage stageActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
        CargadorVistas.cambiarPantalla(stageActual, RutasVista.MENU_DE_VENTAS_VIEW);
    }


    @FXML
    public void abrirHistorialVentas(ActionEvent event) {
        Parent root = CargadorVistas.cargarVista(RutasVista.HISTORIAL_VENTAS_VIEW);
        Stage modalStage = new Stage();
        modalStage.setTitle("Generar Reporte de Recaudo");
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.initStyle(StageStyle.DECORATED);
        Scene scene = new Scene(root);
        modalStage.setScene(scene);
        modalStage.showAndWait();
    }


    @FXML
    public void volverAlMenu(ActionEvent event) {
        Stage stageActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
        CargadorVistas.cambiarPantalla(stageActual, RutasVista.MENU_PRINCIPAL_VIEW);
    }


}//===================================================================================================================//


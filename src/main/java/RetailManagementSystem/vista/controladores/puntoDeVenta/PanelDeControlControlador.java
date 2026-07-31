package RetailManagementSystem.vista.controladores.puntoDeVenta;

import RetailManagementSystem.aplicacion.dto.ResumenVentaDiaDTO;
import RetailManagementSystem.aplicacion.ensambladores.EnsambladorDTOFactura;
import RetailManagementSystem.aplicacion.servicios.ServicioFacturas;
import RetailManagementSystem.infraestructura.configuracion.InformacionAplicacion;
import RetailManagementSystem.vista.utilidades.CargadorVistas;
import RetailManagementSystem.vista.utilidades.FormateadorNumeros;
import RetailManagementSystem.vista.utilidades.RutasVista;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.net.URL;
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

    private void mostrarAlertaErrorNavegacion(String rutaSolicitada) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error de Navegación");
        alerta.setHeaderText("NO se pudo Cargar la Pantalla");
        alerta.setContentText("Ocurrió un Problema al Intentar Abrir la Vista.\n" +
                "Ruta Solicitada: " + rutaSolicitada + "\n" +
                "Si el problema persiste, contacte al Administrador o al Creador Original 😎 Jose Daniel 😎.");
        DialogPane panelAlerta = alerta.getDialogPane();
        panelAlerta.setPrefSize(500, 280);
        URL urlCss = getClass().getResource(RutasVista.ESTILOS_CSS_PANEL_DE_CONTROL_POS);
        if (urlCss != null) {
            panelAlerta.getStylesheets().add(urlCss.toExternalForm());
        } else {
            panelAlerta.setPrefSize(500, 180);
        }
        alerta.showAndWait();
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
        try {
            ResumenVentaDiaDTO resumen = this.ensambladorDTOFactura.ensamblarResumenVentaDia(
                    this.servicioFacturas.obtenerResumenHoy()
            );
            lblCantidadFacturas.setText(String.valueOf(resumen.cantidadFacturas()));
            lblTotalVentasHoy.setText(FormateadorNumeros.formatoMoneda(resumen.totalVentas()));
            lblUltimaVenta.setText(FormateadorNumeros.formatoMoneda(resumen.ultimaVenta()));
        } catch (Exception e) {
            lblCantidadFacturas.setText("0");
            lblTotalVentasHoy.setText("$ 0.00");
            lblUltimaVenta.setText("$ 0.00");
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error al Cargar las Métricas");
            alerta.setHeaderText(null);
            alerta.setContentText("Error:  " + e.getMessage());
            DialogPane pane = alerta.getDialogPane();
            pane.setMinHeight(Region.USE_PREF_SIZE);
            URL urlCss = getClass().getResource(RutasVista.ESTILOS_CSS_PANEL_DE_CONTROL_POS);
            if (urlCss != null) {
                pane.getStylesheets().add(urlCss.toExternalForm());
            }
            alerta.showAndWait();
        }
    }

    private void cargarVersionTienda(){
        try {
            String version = InformacionAplicacion.obtenerVersion();
            lblVersion.setText("Mi Tienda " + version);
        } catch (Exception e) {
            lblVersion.setText("Versión --");
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error de Carga");
            alerta.setHeaderText("Error al Cargar la Version");
            alerta.setContentText("NO se pudo Cargar la Versión en la Vista: " + e.getMessage());
            DialogPane panelAlerta = alerta.getDialogPane();
            URL urlCss = getClass().getResource(RutasVista.ESTILOS_CSS_PANEL_DE_CONTROL_POS);
            if (urlCss != null) {
                panelAlerta.getStylesheets().add(urlCss.toExternalForm());
            }
            alerta.showAndWait();
        }
    }


    @FXML
    public void abrirNuevaVenta(ActionEvent event) {
        try {
            Stage stageActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
            CargadorVistas.cambiarPantalla(stageActual, RutasVista.MENU_DE_VENTAS_VIEW);
        } catch (Exception e) {
            mostrarAlertaErrorNavegacion(RutasVista.MENU_DE_VENTAS_VIEW);
        }
    }


    @FXML
    public void abrirHistorialVentas(ActionEvent event) {
        try {
            Parent root = CargadorVistas.cargarVista(RutasVista.HISTORIAL_VENTAS_VIEW);
            Stage modalStage = new Stage();
            modalStage.setTitle("Generar Reporte de Recaudo");
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initStyle(StageStyle.DECORATED);
            Scene scene = new Scene(root);
            modalStage.setScene(scene);
            modalStage.showAndWait();
        } catch (Exception e) {
            mostrarAlertaErrorNavegacion(RutasVista.HISTORIAL_VENTAS_VIEW);
        }
    }


    @FXML
    public void volverAlMenu(ActionEvent event) {
        try {
            Stage stageActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
            CargadorVistas.cambiarPantalla(stageActual, RutasVista.MENU_PRINCIPAL_VIEW);
        } catch (Exception e) {
            mostrarAlertaErrorNavegacion(RutasVista.MENU_PRINCIPAL_VIEW);
        }
    }


}//===================================================================================================================//


package RetailManagementSystem.vista.controladores.puntoDeVenta;

import RetailManagementSystem.aplicacion.dto.ResumenVentaDiaDTO;
import RetailManagementSystem.aplicacion.ensambladores.EnsambladorDTOFactura;
import RetailManagementSystem.aplicacion.servicios.ServicioFacturas;
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

    private final ServicioFacturas servicioFacturas;

    private final EnsambladorDTOFactura ensambladorDTOFactura;

    //CONTROLADOR:

    public PanelDeControlControlador(ServicioFacturas servicioFacturas, EnsambladorDTOFactura ensambladorDTOFactura) {
        this.servicioFacturas = servicioFacturas;
        this.ensambladorDTOFactura = ensambladorDTOFactura;
    }

    //MÉTODOS:

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        DialogPane pane = alerta.getDialogPane();
        pane.setMinHeight(Region.USE_PREF_SIZE);
        URL urlCss = getClass().getResource(RutasVista.ESTILOS_CSS_PANEL_DE_CONTROL_POS);
        if (urlCss != null) {
            pane.getStylesheets().add(urlCss.toExternalForm());
        }
        alerta.showAndWait();
    }


    @FXML
    public void initialize() {
        iniciarReloj();
        cargarMetricasDelDia();
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
            mostrarAlerta(Alert.AlertType.ERROR, "Error al Cargar las Métricas",
                    "Error:  " + e.getMessage());
        }
    }


    @FXML
    public void abrirNuevaVenta(ActionEvent event) {
        try {
            Stage stageActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
            CargadorVistas.cambiarPantalla(stageActual, RutasVista.MENU_DE_VENTAS_VIEW);
        } catch (Exception e) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error de Navegación");
            alerta.setHeaderText("NO se pudo Cargar la Pantalla");
            alerta.setContentText("Ocurrió un Problema al Intentar Abrir la Vista.\n" +
                    "Ruta Solicitada: " + RutasVista.MENU_DE_VENTAS_VIEW + "\n" +
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
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error de Navegación");
            alerta.setHeaderText("NO se pudo Cargar la Pantalla");
            alerta.setContentText("Ocurrió un Problema al Intentar Abrir la Vista.\n" +
                    "Ruta Solicitada: " + RutasVista.HISTORIAL_VENTAS_VIEW + "\n" +
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
    }


    @FXML
    public void volverAlMenu(ActionEvent event) {
        try {
            Stage stageActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
            CargadorVistas.cambiarPantalla(stageActual, RutasVista.MENU_PRINCIPAL_VIEW);
        } catch (Exception e) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error de Navegación");
            alerta.setHeaderText("NO se pudo Cargar la Pantalla");
            alerta.setContentText("Ocurrió un Problema al Intentar Abrir la Vista.\n" +
                    "Ruta Solicitada: " + RutasVista.MENU_PRINCIPAL_VIEW + "\n" +
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
    }


}//===================================================================================================================//


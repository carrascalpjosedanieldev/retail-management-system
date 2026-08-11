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

import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

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
        try {
            String nombreTienda = this.servicioConfiguraciones.obtenerNombreTienda();
            if (nombreTienda != null && !nombreTienda.isBlank()) {
                lblNombreTienda.setText(nombreTienda);
            } else {
                lblNombreTienda.setText("Mi Tienda");
            }
        } catch (RuntimeException e) {
            lblNombreTienda.setText("Tienda (Modo Offline)");
            GestorAlertas.mostrarAlertaError(
                    "Error de Carga",
                    "Error al Obtener el Nombre de la Tienda",
                    "NO se pudo Leer la Configuración Local: " + e.getMessage()
            );
        }
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
        salirDeSistema();
    }

    public void salirDeSistema(){
        Label iconoAmigable = new Label("👋");
        iconoAmigable.setStyle("-fx-font-size: 45px; -fx-padding: 0 10 0 10;");
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmar Salida");
        alerta.setHeaderText(null);
        alerta.setContentText("¿Estás Seguro de que deseas Salir del Sistema?");
        alerta.setGraphic(iconoAmigable);
        DialogPane panelAlerta = alerta.getDialogPane();
        URL urlCss = getClass().getResource(RutasVista.ESTILOS_CSS_MENU_PRINCIPAL);
        if (urlCss != null) {
            panelAlerta.getStylesheets().add(urlCss.toExternalForm());
        }
        Optional<ButtonType> respuesta = alerta.showAndWait();
        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            Platform.exit();
            System.exit(0);
        }
    }


}//===================================================================================================================//


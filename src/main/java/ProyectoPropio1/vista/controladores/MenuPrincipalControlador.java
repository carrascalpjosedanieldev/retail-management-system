package ProyectoPropio1.vista.controladores;

import ProyectoPropio1.servicios.aplicacion.servicios.ServicioConfiguraciones;
import ProyectoPropio1.utilidades.CargadorVistas;
import ProyectoPropio1.utilidades.RutasVista;

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
import java.util.concurrent.CompletableFuture;

public class MenuPrincipalControlador {

    //ATRIBUTOS:

    @FXML public Button btnSalir;
    @FXML private Label lblReloj;
    @FXML private Label lblNombreTienda;

    private final ServicioConfiguraciones servicioConfiguraciones;

    //CONSTRUCTOR:

    public MenuPrincipalControlador(ServicioConfiguraciones servicioConfiguraciones) {
        this.servicioConfiguraciones = servicioConfiguraciones;
    }

    //MÉTODOS:

    private Optional<ButtonType> mostrarAlerta(
            Alert.AlertType tipo, String titulo, String cabecera, String contenido,
            Node iconoPersonalizado, boolean esAlertaError
    ) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(cabecera);
        alerta.setContentText(contenido);
        if (iconoPersonalizado != null) {
            alerta.setGraphic(iconoPersonalizado);
        }
        DialogPane panelAlerta = alerta.getDialogPane();
        if (esAlertaError) {
            panelAlerta.setPrefSize(500, 250);
        }
        URL urlCss = getClass().getResource(RutasVista.ESTILOS_CSS_MENU_PRINCIPAL);
        if (urlCss != null) {
            panelAlerta.getStylesheets().add(urlCss.toExternalForm());
        } else if (esAlertaError) {
            panelAlerta.setPrefSize(500, 180);
        }
        return alerta.showAndWait();
    }


    @FXML
    public void initialize() {
        iniciarReloj();
        cargarNombreTienda();
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
        CompletableFuture.supplyAsync(() -> {
                    return this.servicioConfiguraciones.obtenerValorConfiguracion(RutasVista.NOMBRE_TIENDA_CLAVE);
                }).thenAcceptAsync(nombreTienda -> {
                    if (nombreTienda != null && !nombreTienda.isBlank()) {
                        lblNombreTienda.setText(nombreTienda);
                    } else {
                        lblNombreTienda.setText("Mi Tienda");
                    }
                }, Platform::runLater)
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        lblNombreTienda.setText("Tienda (Modo Offline)");
                        System.err.println("Error al Cargar el Nombre de la Tienda: " + ex.getMessage());
                    });
                    return null;
                });
    }


    @FXML
    void abrirGestionarTienda(ActionEvent event) {
        try {
            Stage stageActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
            CargadorVistas.cambiarPantalla(stageActual, RutasVista.GESTIONAR_TIENDA_VIEW);
        } catch (Exception e) {
            String mensaje = "Ocurrió un Problema al Cargar la Vista de Gestión de Tienda.\n" +
                    "Si el Problema persiste, contacte al Administrador o al Creador Original 😎 Jose Daniel 😎.";
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Navegación",
                    "NO se pudo Abrir la Pantalla", mensaje, null, true);
        }
    }


    @FXML
    void salirDelSistema(ActionEvent event) {
        salirDeSistema();
    }

    public void salirDeSistema(){
        Label iconoAmigable = new Label("👋");
        iconoAmigable.setStyle("-fx-font-size: 45px; -fx-padding: 0 10 0 10;");
        Optional<ButtonType> respuesta = mostrarAlerta(
                Alert.AlertType.CONFIRMATION, "Confirmar Salida",
                null, "¿Estás Seguro de que deseas Salir del Sistema?",
                iconoAmigable, false
        );
        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            Platform.exit();
            System.exit(0);
        }
    }


}//===================================================================================================================//


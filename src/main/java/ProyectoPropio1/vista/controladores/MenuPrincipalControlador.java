package ProyectoPropio1.vista.controladores;

import ProyectoPropio1.utilidades.RutasVista;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
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
    @FXML private Label lblReloj;

    //MÉTODOS:

    @FXML
    public void initialize() {
        iniciarReloj();
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

    @FXML
    void abrirGestionarTienda(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(RutasVista.GESTIONAR_TIENDA_VIEW));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            String mensaje = "Ocurrió un problema al cargar la vista de Gestión de Tienda.\n" +
                    "Si el problema persiste, contacte al Administrador o al Creador Original 😎 Jose Daniel 😎.";
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Navegación",
                    "No se pudo abrir la pantalla", mensaje, null, true);
        }
    }

    @FXML
    void salirDelSistema(ActionEvent event) {
        salirDelSistema();
    }

    public void salirDelSistema(){
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

}

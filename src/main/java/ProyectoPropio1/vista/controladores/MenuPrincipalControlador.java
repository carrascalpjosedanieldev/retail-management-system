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
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;


import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class MenuPrincipalControlador {

    @FXML
    public Button btnSalir;

    @FXML
    private Label lblReloj;

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

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Error al cargar la pantalla de Gestión de Tienda:");
            e.printStackTrace();
        }
    }

    @FXML
    void salirDelSistema(ActionEvent event) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmar Salida");
        alerta.setHeaderText(null);
        alerta.setContentText("¿Estás Seguro de que deseas Salir del Sistema?");

        javafx.scene.control.DialogPane panelAlerta = alerta.getDialogPane();

        String rutaCss = getClass().getResource(RutasVista.ESTILOS_CSS_MENU_PRINCIPAL).toExternalForm();
        panelAlerta.getStylesheets().add(rutaCss);

        Label iconoAmigable = new Label("👋");
        iconoAmigable.setStyle("-fx-font-size: 45px; -fx-padding: 0 10 0 10;");
        alerta.setGraphic(iconoAmigable);

        Optional<ButtonType> respuesta = alerta.showAndWait();

        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            Platform.exit();
            System.exit(0);
        }
    }

}

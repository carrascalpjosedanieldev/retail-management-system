package ProyectoPropio1.vista.controladores.gestionarTienda;

import ProyectoPropio1.dominio.Tienda;
import ProyectoPropio1.servicios.aplicacion.ServicioConfiguraciones;
import ProyectoPropio1.utilidades.CargadorVistas;
import ProyectoPropio1.utilidades.RutasVista;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Optional;

public class GestionConfiguracionesControlador {

    //ATRIBUTOS:

    private final ServicioConfiguraciones servicioConfiguraciones;

    //CONSTRUCTOR:

    public GestionConfiguracionesControlador(ServicioConfiguraciones servicioConfiguraciones) {
        this.servicioConfiguraciones = servicioConfiguraciones;
    }

    //MÉTODOS:

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        DialogPane panelAlerta = alerta.getDialogPane();
        panelAlerta.setMinHeight(Region.USE_PREF_SIZE);
        switch (tipo) {
            case INFORMATION:
                panelAlerta.getStyleClass().add("alerta-info");
                break;
            case WARNING:
                panelAlerta.getStyleClass().add("alerta-warning");
                break;
            case ERROR:
                panelAlerta.getStyleClass().add("alerta-error");
                break;
            default:
                break;
        }
        URL urlCss = getClass().getResource(RutasVista.ESTILOS_CSS_CONFIGURACIONES);
        if (urlCss != null) {
            panelAlerta.getStylesheets().add(urlCss.toExternalForm());
        }
        alerta.showAndWait();
    }


    @FXML
    void abrirConfiguracionNombre(ActionEvent event) {
        abrirConfiguracionNombre();
    }

    private void abrirConfiguracionNombre(){
        Dialog<String> dialogClave = new Dialog<>();
        URL urlCss = getClass().getResource(RutasVista.ESTILOS_CSS_CONFIGURACIONES);
        if (urlCss != null) {
            dialogClave.getDialogPane().getStylesheets().add(urlCss.toExternalForm());
        }
        dialogClave.getDialogPane().getStyleClass().add("dialog-edicion");
        dialogClave.setTitle("Autenticación Requerida");
        dialogClave.setHeaderText("Por Seguridad, Ingresa la Clave Maestra para Ver y Editar.");
        ButtonType btnAceptar = new ButtonType("Verificar", ButtonBar.ButtonData.OK_DONE);
        dialogClave.getDialogPane().getButtonTypes().addAll(btnAceptar, ButtonType.CANCEL);
        PasswordField txtClaveOculta = new PasswordField();
        txtClaveOculta.setPromptText("Contraseña ...");
        txtClaveOculta.setPrefWidth(350);
        TextField txtClaveVisible = new TextField();
        txtClaveVisible.setPromptText("Contraseña ...");
        txtClaveVisible.setPrefWidth(350);
        txtClaveVisible.setVisible(false);
        txtClaveVisible.setManaged(false);
        txtClaveVisible.textProperty().bindBidirectional(txtClaveOculta.textProperty());
        StackPane stackClave = new StackPane(txtClaveOculta, txtClaveVisible);
        ToggleButton btnVer = new ToggleButton("👁");
        btnVer.getStyleClass().add("btn-ver-clave");
        btnVer.setOnAction(e -> {
            boolean mostrar = btnVer.isSelected();
            txtClaveVisible.setVisible(mostrar);
            txtClaveVisible.setManaged(mostrar);
            txtClaveOculta.setVisible(!mostrar);
            txtClaveOculta.setManaged(!mostrar);
        });
        HBox boxClave = new HBox(5, stackClave, btnVer);
        GridPane gridClave = new GridPane();
        gridClave.setHgap(10);
        gridClave.setVgap(10);
        gridClave.add(new Label("Clave:"), 0, 0);
        gridClave.add(boxClave, 1, 0);
        dialogClave.getDialogPane().setContent(gridClave);
        dialogClave.getDialogPane().setPrefWidth(500);
        Platform.runLater(txtClaveOculta::requestFocus);
        Button btnAceptarNode = (Button) dialogClave.getDialogPane().lookupButton(btnAceptar);
        btnAceptarNode.addEventFilter(ActionEvent.ACTION, evt -> {
            String claveIngresada = txtClaveOculta.getText();
            if (!claveIngresada.equals(RutasVista.NOMBRE_TIENDA_CLAVE)) {
                mostrarAlerta(Alert.AlertType.ERROR, "Acceso Denegado",
                        "La Clave Ingresada es Incorrecta. Intenta de nuevo.");
                evt.consume();
            }
        });
        dialogClave.setResultConverter(dialogButton -> {
            if (dialogButton == btnAceptar) {
                return txtClaveOculta.getText();
            }
            return null;
        });
        Button btnCancelarClave = (Button) dialogClave.getDialogPane().lookupButton(ButtonType.CANCEL);
        if (btnCancelarClave != null) btnCancelarClave.getStyleClass().add("btn-cancelar");
        Optional<String> resultadoClave = dialogClave.showAndWait();
        if (resultadoClave.isPresent()) {
            mostrarPanelEdicionTienda();
        }
    }

    private void mostrarPanelEdicionTienda() {
        Dialog<String[]> dialogEdicion = new Dialog<>();
        URL urlCss = getClass().getResource(RutasVista.ESTILOS_CSS_CONFIGURACIONES);
        if (urlCss != null) {
            dialogEdicion.getDialogPane().getStylesheets().add(urlCss.toExternalForm());
        }
        dialogEdicion.getDialogPane().getStyleClass().add("dialog-edicion");
        dialogEdicion.setTitle("Editar Detalles de la Tienda");
        dialogEdicion.setHeaderText("Modifica la información general de tu negocio");
        ButtonType btnGuardar = new ButtonType("Guardar Cambios", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialogEdicion.getDialogPane().getButtonTypes().addAll(btnGuardar, btnCancelar);
        String nombreActual = this.servicioConfiguraciones.obtenerValorConfiguracion(RutasVista.NOMBRE_TIENDA_CLAVE);
        String descripcionActual = this.servicioConfiguraciones.obtenerDescripcionConfiguracion(RutasVista.NOMBRE_TIENDA_CLAVE);
        TextField txtNombre = new TextField(nombreActual);
        txtNombre.setPromptText("Ej: Supermercado Central");
        txtNombre.setPrefWidth(500);
        TextArea txtDescripcion = new TextArea(descripcionActual);
        txtDescripcion.setPromptText("Breve descripción del negocio...");
        txtDescripcion.setWrapText(true);
        txtDescripcion.setPrefWidth(500);
        txtDescripcion.setPrefRowCount(4);
        GridPane gridInfo = new GridPane();
        gridInfo.setVgap(10);
        gridInfo.add(new Label("Nombre de la Tienda:"), 0, 0);
        gridInfo.add(txtNombre, 0, 1);
        Label lblDesc = new Label("Descripción:");
        lblDesc.setStyle("-fx-padding: 10 0 0 0;");
        gridInfo.add(lblDesc, 0, 2);
        gridInfo.add(txtDescripcion, 0, 3);
        dialogEdicion.getDialogPane().setContent(gridInfo);
        dialogEdicion.getDialogPane().setPrefWidth(550);
        Platform.runLater(txtNombre::selectAll);
        Button btnGuardarNode = (Button) dialogEdicion.getDialogPane().lookupButton(btnGuardar);
        btnGuardarNode.addEventFilter(ActionEvent.ACTION, evt -> {
            String nuevoNombre = txtNombre.getText().trim();
            String nuevaDescripcion = txtDescripcion.getText().trim();
            if (nuevoNombre.isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Dato Inválido",
                        "El Nombre de la Tienda NO puede estar Vacío.");
                evt.consume();
                return;
            }
            try {
                Tienda tienda = Tienda.crearNueva(nombreActual);
                this.servicioConfiguraciones.cambiarNombreYDescripcionTienda(
                        RutasVista.NOMBRE_TIENDA_CLAVE, nuevoNombre, tienda, nuevaDescripcion
                );
                mostrarAlerta(Alert.AlertType.INFORMATION, "Cambios Guardados",
                        "La Información de la Tienda se Actualizó con Éxito.");
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error Crítico",
                        "NO se Actualizaron los Datos:\n" +
                                "Error: " + e.getMessage());
                evt.consume();
            }
        });
        Button btnCancelarEdicion = (Button) dialogEdicion.getDialogPane().lookupButton(btnCancelar);
        if (btnCancelarEdicion != null) btnCancelarEdicion.getStyleClass().add("btn-cancelar");
        dialogEdicion.showAndWait();
    }


    @FXML
    public void volverPanelGestion(ActionEvent event) {
        try {
            Stage stageActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
            CargadorVistas.cambiarPantalla(stageActual, RutasVista.GESTIONAR_TIENDA_VIEW);
        } catch (Exception e) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error de Navegación",
                    "Ocurrió un problema al intentar volver al panel de Gestión.\n" +
                            "Si el problema persiste, contacte al Administrador o al Creador Original 😎 Jose Daniel 😎."
            );
        }
    }


}//===================================================================================================================//


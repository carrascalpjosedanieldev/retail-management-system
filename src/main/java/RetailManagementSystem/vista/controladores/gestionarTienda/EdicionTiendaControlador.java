package RetailManagementSystem.vista.controladores.gestionarTienda;

import RetailManagementSystem.aplicacion.servicios.ServicioConfiguraciones;
import RetailManagementSystem.vista.utilidades.GestorAlertas;
import RetailManagementSystem.vista.utilidades.RutasVista;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.net.URL;

public class EdicionTiendaControlador {

    //ATRIBUTOS:

    @FXML private TextField txtNombre;
    @FXML private TextArea txtDescripcion;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    private final ServicioConfiguraciones servicioConfiguraciones;

    //CONSTRUCTOR:

    public EdicionTiendaControlador(ServicioConfiguraciones servicioConfiguraciones) {
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
        URL urlCss = getClass().getResource(RutasVista.ESTILOS_CSS_CONFIGURACIONES);
        if (urlCss != null) {
            panelAlerta.getStylesheets().add(urlCss.toExternalForm());
        }
        alerta.showAndWait();
    }

    @FXML
    public void initialize() {
        txtNombre.setText(this.servicioConfiguraciones.obtenerNombreTienda());
        txtDescripcion.setText(this.servicioConfiguraciones.obtenerDescripcionTienda());
    }

    @FXML
    private void guardarCambios(ActionEvent event) {
        String nuevoNombre = txtNombre.getText().trim();
        String nuevaDescripcion = txtDescripcion.getText().trim();
        if (nuevoNombre.isEmpty()) {
            GestorAlertas.mostrarError("Dato Inválido", "Campo Requerido",
                    "El Nombre de la Tienda NO puede estar vacío.");
            txtNombre.requestFocus();
            return;
        }
        try {
            this.servicioConfiguraciones.cambiarNombreYDescripcionTienda(nuevoNombre, nuevaDescripcion);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Cambios Guardados",
                    "La Información de la Tienda se Actualizó con Éxito.");
            cerrarVentana(event);
        } catch (IllegalArgumentException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Error en los Datos",
                    "Error:  " + e.getMessage());
        }
    }

    @FXML
    private void cerrarVentana(ActionEvent event) {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }


}//===================================================================================================================//


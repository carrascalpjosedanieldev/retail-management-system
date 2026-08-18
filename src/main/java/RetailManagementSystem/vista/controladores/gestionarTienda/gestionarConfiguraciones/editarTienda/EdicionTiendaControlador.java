package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarConfiguraciones.editarTienda;

import RetailManagementSystem.aplicacion.servicios.ServicioConfiguraciones;
import RetailManagementSystem.vista.utilidades.GestorAlertas;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Window;

public class EdicionTiendaControlador {

    //ATRIBUTOS:

    @FXML private TextField txtNombre;
    @FXML private TextArea txtDescripcion;
    @FXML private Button btnCancelar;

    private final ServicioConfiguraciones servicioConfiguraciones;

    //CONSTRUCTOR:

    public EdicionTiendaControlador(ServicioConfiguraciones servicioConfiguraciones) {
        this.servicioConfiguraciones = servicioConfiguraciones;
    }

    //MÉTODOS:

    private Window getVentana(){
        return btnCancelar.getScene().getWindow();
    }


    @FXML
    public void initialize() {
        txtNombre.setText(this.servicioConfiguraciones.obtenerNombreTienda());
        txtDescripcion.setText(this.servicioConfiguraciones.obtenerDescripcionTienda());
        Platform.runLater(()->btnCancelar.requestFocus());
    }

    @FXML
    private void guardarCambios(ActionEvent event) {
        String nuevoNombre = txtNombre.getText().trim();
        String nuevaDescripcion = txtDescripcion.getText().trim();
        if (nuevoNombre.isEmpty()) {
            GestorAlertas.mostrarAlertaError(
                    getVentana(), "Dato Inválido", "Campo Requerido",
                    "El Nombre de la Tienda NO puede estar vacío."
            );
            txtNombre.requestFocus();
            return;
        }
        try {
            this.servicioConfiguraciones.cambiarNombreYDescripcionTienda(nuevoNombre, nuevaDescripcion);
            GestorAlertas.mostrarAlertaInformacion(
                    getVentana(), "Cambios Guardados", null,
                    "La Información de la Tienda se Actualizó con Éxito."
            );
            cerrarVentana(event);
        } catch (IllegalArgumentException e) {
            GestorAlertas.mostrarAlertaError(
                    getVentana(), "Error en los Datos", null,
                    "Error:  " + e.getMessage()
            );
        }
    }

    @FXML
    private void cerrarVentana(ActionEvent event) {
        Stage stage = (Stage) getVentana();
        stage.close();
    }


}//===================================================================================================================//


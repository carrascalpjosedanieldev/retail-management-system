package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarConfiguraciones.editarTienda;

import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.servicios.ServicioConfiguraciones;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.infraestructura.seguridad.ValidadorSeguridad;
import RetailManagementSystem.vista.utilidades.GestorAlertas;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Window;

import java.util.concurrent.CompletableFuture;

public class EdicionTiendaControlador {

    //ATRIBUTOS:

    @FXML private TextField txtNombre;
    @FXML private TextArea txtDescripcion;
    @FXML private Button btnCancelar;

    private final ServicioConfiguraciones servicioConfiguraciones;

    private UsuarioDTOCompleto usuarioActual;

    //CONSTRUCTOR:

    public EdicionTiendaControlador(ServicioConfiguraciones servicioConfiguraciones) {
        this.servicioConfiguraciones = servicioConfiguraciones;
    }

    //MÉTODOS:

    public void cargarUsuario(UsuarioDTOCompleto usuarioActual){
        ValidadorSeguridad.exigirPermiso(usuarioActual, PermisosApp.EDITAR_PERFIL_DE_TIENDA);
        this.usuarioActual = usuarioActual;
    }

    private Window getVentana(){
        return btnCancelar.getScene() != null ? btnCancelar.getScene().getWindow() : null;
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
        CompletableFuture.runAsync(()->
                this.servicioConfiguraciones.cambiarNombreYDescripcionTienda(
                        this.usuarioActual, nuevoNombre, nuevaDescripcion
                )
        ).thenRun(()->
            Platform.runLater(()->{
                GestorAlertas.mostrarAlertaInformacion(
                        getVentana(), "Cambios Guardados", null,
                        "La Información de la Tienda se Actualizó con Éxito."
                );
                cerrarPantalla();
            })
        ).exceptionally(ex->{
            Platform.runLater(()->{
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                if (causa instanceof  IllegalArgumentException){
                    GestorAlertas.mostrarAlertaError(
                            getVentana(), "Error en los Datos", null,
                            "Error:  " + causa.getMessage()
                    );
                } else {
                    GestorAlertas.mostrarAlertaError(
                            getVentana(), "Error Crítico",
                            "NO se pudo Completar la Acción.",
                            "Notifícale al Administrador este Error:\n" + causa.getMessage()
                    );
                }
            });
            return null;
        });
    }

    private void cerrarPantalla(){
        Window ventana = getVentana();
        if (ventana != null) {
            ventana.hide();
        }
    }


    @FXML
    private void cerrarVentana(ActionEvent event) {
        cerrarPantalla();
    }


}//===================================================================================================================//


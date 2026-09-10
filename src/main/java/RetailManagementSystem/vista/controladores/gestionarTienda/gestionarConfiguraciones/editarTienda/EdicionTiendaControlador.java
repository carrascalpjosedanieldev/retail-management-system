package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarConfiguraciones.editarTienda;

import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorConfiguraciones;
import RetailManagementSystem.dominio.excepciones.autenticacionYSeguridad.AccesoDenegadoException;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.infraestructura.seguridad.ValidadorSeguridad;
import RetailManagementSystem.vista.configuracion.ConfiguradorExcepciones;
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

    private final OrquestadorConfiguraciones orquestadorConfiguraciones;

    private UsuarioDTOCompleto usuarioActual;

    //CONSTRUCTOR:

    public EdicionTiendaControlador(OrquestadorConfiguraciones orquestadorConfiguraciones) {
        this.orquestadorConfiguraciones = orquestadorConfiguraciones;
    }

    //MÉTODOS:

    public void cargarUsuario(UsuarioDTOCompleto usuarioActual){
        ValidadorSeguridad.exigirPermiso(usuarioActual, PermisosApp.EDITAR_PERFIL_DE_TIENDA);
        this.usuarioActual = usuarioActual;
        cargarDatos();
    }

    private void cargarDatos(){
        CompletableFuture.supplyAsync(
                this.orquestadorConfiguraciones::obtenerDatosTienda
        ).thenAccept(datosTienda ->
            Platform.runLater(()->{
                txtNombre.setText(datosTienda.valor());
                txtDescripcion.setText(datosTienda.descripcion());
                btnCancelar.requestFocus();
            })
        ).exceptionally(ex->{
            Platform.runLater(()->{
                Throwable causa = ConfiguradorExcepciones.obtenerCausaRaiz(ex);
                GestorAlertas.mostrarAlertaError(
                        getVentana(), "Error Crítico",
                        "NO se pudo Cargar los Datos de la Tienda.",
                        "Se Cerrara la Ventana por Seguridad. Notifícale al Administrador este Error:\n" +
                                causa.getMessage()
                );
                cerrarPantalla();
            });
            return null;
        });
    }

    private Window getVentana(){
        return btnCancelar.getScene() != null ? btnCancelar.getScene().getWindow() : null;
    }


    @FXML
    private void guardarCambios(ActionEvent event) {
        guardarCambios();
    }

    private void guardarCambios(){
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
                this.orquestadorConfiguraciones.cambiarNombreYDescripcionTienda(
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
                            getVentana(), "Error en los Datos Ingresados", null,
                            "Error:  " + causa.getMessage()
                    );
                } else if (causa instanceof AccesoDenegadoException) {
                    GestorAlertas.mostrarAlertaError(
                            getVentana(), "Permisos Insuficientes",
                            "NO Puedes estar en esta Pantalla.",
                            "Se Cerrara la Ventana Actual\n" + causa.getMessage()
                    );
                    cerrarPantalla();
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


    @FXML
    private void cerrarVentana(ActionEvent event) {
        cerrarPantalla();
    }

    private void cerrarPantalla(){
        Window ventana = getVentana();
        if (ventana != null) {
            ventana.hide();
        }
    }

}//===================================================================================================================//


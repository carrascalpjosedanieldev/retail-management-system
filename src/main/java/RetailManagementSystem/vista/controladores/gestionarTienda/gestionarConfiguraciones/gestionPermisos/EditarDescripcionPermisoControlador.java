package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarConfiguraciones.gestionPermisos;

import RetailManagementSystem.aplicacion.dto.seguridad.PermisoDTO;
import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorPermisos;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.infraestructura.seguridad.ValidadorSeguridad;
import RetailManagementSystem.vista.configuracion.ConfiguradorExcepciones;
import RetailManagementSystem.vista.utilidades.GestorAlertas;
import RetailManagementSystem.vista.utilidades.UtilidadesLista;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Window;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EditarDescripcionPermisoControlador {

    //ATRIBUTOS:

    @FXML private Button btnCancelar;
    @FXML private Button btnGuardar;
    @FXML private Label lblNombrePermiso;
    @FXML private TextArea txtDescripcion;

    private final OrquestadorPermisos orquestadorPermisos;

    private ObservableList<PermisoDTO> listaObservable;

    private UsuarioDTOCompleto usuarioActual;

    private PermisoDTO seleccionado;

    //CONSTRUCTOR:

    public EditarDescripcionPermisoControlador(OrquestadorPermisos orquestadorPermisos) {
        this.orquestadorPermisos = orquestadorPermisos;
    }

    //MÉTODOS:

    public void cargarDatos(
            UsuarioDTOCompleto usuarioActual, ObservableList<PermisoDTO> listaObservable, PermisoDTO seleccionado
    ) {
        ValidadorSeguridad.exigirAlgunPermiso(usuarioActual, List.of(
                PermisosApp.GESTIONAR_PERMISOS
        ));
        this.usuarioActual = usuarioActual;
        this.listaObservable = listaObservable;
        this.seleccionado = seleccionado;
        lblNombrePermiso.setText(seleccionado.nombre());
        txtDescripcion.setText(seleccionado.descripcion());
    }

    private Window getVentana(){
        return btnGuardar.getScene() != null ? btnGuardar.getScene().getWindow() : null;
    }


    @FXML
    void guardarCambios(ActionEvent event) {
        guardarCambios();
    }

    private void guardarCambios(){
        String descripcion = txtDescripcion.getText();
        if (descripcion.isBlank()){
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(),
                    "Campo Vacío",
                    "Escribe una Descripción",
                    "Por favor, Escribe una Descripción Clara sobre el Permiso seleccionado."
            );
            return;
        }
        CompletableFuture.runAsync(()->
                this.orquestadorPermisos.cambiarDescripcionPermiso(
                        this.usuarioActual, this.seleccionado.idPermiso(), descripcion
                )
        ).thenRun(()->
                Platform.runLater(()->{
                    GestorAlertas.mostrarAlertaInformacion(
                            getVentana(), "Éxito", null,
                            "El Permiso se ha Actualizado con Éxito."
                    );
                    PermisoDTO actualizado = new PermisoDTO(
                            seleccionado.idPermiso(),
                            seleccionado.nombre(),
                            descripcion,
                            seleccionado.modulo(),
                            seleccionado.activo()
                    );
                    UtilidadesLista.reemplazarPorIdentidad(
                            listaObservable,
                            actualizado,
                            item -> item.idPermiso() == actualizado.idPermiso()
                    );
                    cerrarModal();
                })
        ).exceptionally(ex->{
            Platform.runLater(()->{
                Throwable causa = ConfiguradorExcepciones.obtenerCausaRaiz(ex);
                GestorAlertas.mostrarAlertaError(
                        getVentana(), "Error Critico",
                        "NO se pudo Completar la Acción.",
                        "Notificale al Administrador este Error:\n" + causa.getMessage()
                );
            });
            return null;
        });
    }


    @FXML
    void cerrarVentana(ActionEvent event) {
        cerrarModal();
    }

    private void cerrarModal(){
        Window ventana = getVentana();
        if (ventana != null){
            ventana.hide();
        }
    }

}//===================================================================================================================//


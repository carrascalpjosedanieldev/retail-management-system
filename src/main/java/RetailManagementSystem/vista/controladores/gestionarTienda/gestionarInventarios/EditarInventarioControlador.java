package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarInventarios;

import RetailManagementSystem.aplicacion.dto.gestion.InventarioDTO;
import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorInventarioProducto;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.vista.utilidades.GestorAlertas;

import RetailManagementSystem.vista.utilidades.UtilidadesLista;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Window;

import java.util.concurrent.CompletableFuture;

public class EditarInventarioControlador {

    //ATRIBUTOS:

    @FXML private Button btnActualizar;
    @FXML private Button btnCancelar;
    @FXML private Label lblNombreInv;
    @FXML private TextField txtCapacidad;
    @FXML private TextField txtNombre;

    private InventarioDTO datosInventario;

    private ObservableList<InventarioDTO> listaObservable;

    private final OrquestadorInventarioProducto orquestadorInventarioProducto;

    private UsuarioDTOCompleto usuarioActual;

    //CONSTRUCTOR:

    public EditarInventarioControlador(OrquestadorInventarioProducto orquestadorInventarioProducto) {
        this.orquestadorInventarioProducto = orquestadorInventarioProducto;
    }

    //MÉTODOS:

    public void cargarDatos(
            UsuarioDTOCompleto usuarioActual, InventarioDTO datosInventario,
            ObservableList<InventarioDTO> listaObservable
    ) {
        if (usuarioActual == null){
            throw new IllegalArgumentException("Usuario Nulo, Error al Recibir el Usuario");
        }
        if (!usuarioActual.tienePermiso(PermisosApp.VER_INVENTARIOS) ||
            !usuarioActual.tienePermiso(PermisosApp.ADMINISTRAR_INVENTARIOS)){
            GestorAlertas.mostrarAlertaError(
                    getVentana(),
                    "Acceso Denegado", "Privilegios Insuficientes",
                    "NO tienes los Permisos Necesarios para Ver o Gestionar los Inventarios."
            );
            return;
        }
        this.usuarioActual = usuarioActual;
        if (datosInventario == null) {
            throw new IllegalArgumentException("NO puedes editar un Inventario Vacío.");
        }
        this.datosInventario = datosInventario;
        this.listaObservable = listaObservable;
        lblNombreInv.setText(datosInventario.nombre());
        txtNombre.setText(datosInventario.nombre());
        txtCapacidad.setText(String.valueOf(datosInventario.capacidadMaxima()));
        Platform.runLater(()-> btnCancelar.requestFocus());
    }

    private Window getVentana(){
        return btnCancelar.getScene() != null ? btnCancelar.getScene().getWindow() : null;
    }


    @FXML
    private void accionActualizar(ActionEvent event) {
        String nuevoNombre = txtNombre.getText().trim();
        if (nuevoNombre.isEmpty()) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Campos Vacíos", null,
                    "El Nombre del Inventario NO puede estar Vacío."
            );
            return;
        }
        if (nuevoNombre.equalsIgnoreCase(datosInventario.nombre())) {
            return;
        }
        CompletableFuture.supplyAsync(()->
                this.orquestadorInventarioProducto.actualizarInventario(
                        this.usuarioActual, this.datosInventario.idInventario(), nuevoNombre
                )
        ).thenAccept(inventarioActualizado->
            Platform.runLater(()->{
                UtilidadesLista.reemplazarPorIdentidad(
                        listaObservable,
                        inventarioActualizado,
                        item-> item.idInventario().equals(inventarioActualizado.idInventario())
                );
                GestorAlertas.mostrarAlertaInformacion(
                        getVentana(), "Éxito", null,
                        "El Inventario se ha Actualizado con Éxito."
                );
                cerrarPantalla();
            })
        ).exceptionally(ex->{
            Platform.runLater(() -> {
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                if (causa instanceof IllegalArgumentException){
                    GestorAlertas.mostrarAlertaError(
                            getVentana(), "Error en los Datos Ingresados",
                            "NO se pudo Completar la Acción.",
                            "Error:  " + causa.getMessage()
                    );
                } else {
                    GestorAlertas.mostrarAlertaError(
                          getVentana(), "Error Critico",
                           "NO se pudo Completar la Acción.",
                          "Notificale al Administrador este Error:\n" + causa.getMessage()
                    );
                    cerrarPantalla();
                }
            });
            return null;
        });
    }

    private void cerrarPantalla(){
        Window ventana = getVentana();
        if (ventana != null){
            ventana.hide();
        }
    }

    @FXML
    private void accionCancelar(ActionEvent event) {
        cerrarPantalla();
    }

}//===================================================================================================================//


package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarInventarios;

import RetailManagementSystem.aplicacion.dto.gestion.InventarioDTO;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorInventarioProducto;
import RetailManagementSystem.vista.utilidades.GestorAlertas;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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

    //CONSTRUCTOR:

    public EditarInventarioControlador(OrquestadorInventarioProducto orquestadorInventarioProducto) {
        this.orquestadorInventarioProducto = orquestadorInventarioProducto;
    }

    //MÉTODOS:

    public void cargarDatos(InventarioDTO datosInventario, ObservableList<InventarioDTO> listaObservable){
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

    @FXML
    void accionActualizar(ActionEvent event) {
        String nuevoNombre = txtNombre.getText().trim();
        if (nuevoNombre.isEmpty()) {
            GestorAlertas.mostrarAlertaWarning(
                    "Campos Vacíos", null,
                    "El Nombre del Inventario NO puede estar Vacío."
            );
            return;
        }
        if (nuevoNombre.equalsIgnoreCase(datosInventario.nombre())) {
            return;
        }
        CompletableFuture.supplyAsync(()->
                this.orquestadorInventarioProducto.actualizarInventario(this.datosInventario.idInventario(), nuevoNombre)
        ).thenAccept(inventarioActualizado->{
            Platform.runLater(()->{
                int indice = listaObservable.indexOf(this.datosInventario);
                listaObservable.set(indice, inventarioActualizado);
                GestorAlertas.mostrarAlertaInformacion(
                        "Éxito", null,
                        "El Inventario se ha Actualizado con Éxito."
                );
                cerrarPantalla();
            });
        }).exceptionally(ex->{
            Platform.runLater(() -> {
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                if (causa instanceof IllegalArgumentException){
                    GestorAlertas.mostrarAlertaError(
                            "Error en los Datos Ingresados",
                            "NO se pudo Completar la Acción.",
                            "Error:  " + causa.getMessage()
                    );
                } else {
                    GestorAlertas.mostrarAlertaError(
                          "Error Critico",
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
        Stage stageActual = (Stage) lblNombreInv.getScene().getWindow();
        stageActual.close();
    }

    @FXML
    void accionCancelar(ActionEvent event) {
        cerrarPantalla();
    }

}//===================================================================================================================//


package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarInventarios;

import RetailManagementSystem.aplicacion.dto.gestion.InventarioDTO;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorInventarioProducto;
import RetailManagementSystem.vista.utilidades.GestorAlertas;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.concurrent.CompletableFuture;

public class CrearInventarioControlador {

    //ATRIBUTOS:

    @FXML private Button btnCancelar;
    @FXML private TextField txtCapacidad;
    @FXML private TextField txtNombre;

    private ObservableList<InventarioDTO> listaObservable;

    private final OrquestadorInventarioProducto orquestadorInventarioProducto;

    //CONSTRUCTOR:

    public CrearInventarioControlador(OrquestadorInventarioProducto orquestadorInventarioProducto) {
        this.orquestadorInventarioProducto = orquestadorInventarioProducto;
    }

    //MÉTODOS:

    public void cargarDatos(ObservableList<InventarioDTO> listaObservable){
        this.listaObservable = listaObservable;
        Platform.runLater(()->btnCancelar.requestFocus());
    }


    @FXML
    void accionGuardar(ActionEvent event) {
        String nombre = txtNombre.getText().trim();
        String capacidadTexto = txtCapacidad.getText().trim();
        if (nombre.isEmpty() || capacidadTexto.isEmpty()) {
            GestorAlertas.mostrarAlertaWarning(
                    "Campos Vacíos", null,
                    "El Nombre y la Capacidad son Obligatorios."
            );
            return;
        }
        int capacidad;
        try {
            capacidad = Integer.parseInt(capacidadTexto);
            if (capacidad <= 0) {
                GestorAlertas.mostrarAlertaWarning(
                        "Dato Inválido", null,
                        "La Capacidad debe ser un Número Positivo."
                );
                return;
            }
        } catch (NumberFormatException e) {
            GestorAlertas.mostrarAlertaWarning(
                    "Número Inválido", null,
                    "La Capacidad debe ser un Número Entero (ej. 500), Sin Letras ni Decimales."
            );
            return;
        }
        CompletableFuture.supplyAsync(()->
                this.orquestadorInventarioProducto.registrarInventario(nombre, capacidad)
        ).thenAccept(inventarioRegistrado->
            Platform.runLater(()->{
                this.listaObservable.add(inventarioRegistrado);
                GestorAlertas.mostrarAlertaInformacion(
                        "Éxito", null,
                        "El Inventario ha sido Creado Correctamente."
                );
                cerrarPantalla();
            })
        ).exceptionally(ex->{
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
                }
                btnCancelar.requestFocus();
            });
            return null;
        });

    }

    private void cerrarPantalla(){
        Stage stageActual = (Stage) btnCancelar.getScene().getWindow();
        stageActual.close();
    }


    @FXML
    void accionCancelar(ActionEvent event) {
        cerrarPantalla();
    }

}//===================================================================================================================//


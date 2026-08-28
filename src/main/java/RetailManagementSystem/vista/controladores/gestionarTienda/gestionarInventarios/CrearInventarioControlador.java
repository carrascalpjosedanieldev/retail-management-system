package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarInventarios;

import RetailManagementSystem.aplicacion.dto.gestion.InventarioDTO;
import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorInventarioProducto;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.infraestructura.seguridad.ValidadorSeguridad;
import RetailManagementSystem.vista.utilidades.GestorAlertas;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Window;

import java.util.concurrent.CompletableFuture;

public class CrearInventarioControlador {

    //ATRIBUTOS:

    @FXML private Button btnCancelar;
    @FXML private TextField txtCapacidad;
    @FXML private TextField txtNombre;

    private ObservableList<InventarioDTO> listaObservable;

    private final OrquestadorInventarioProducto orquestadorInventarioProducto;

    private UsuarioDTOCompleto usuarioActual;

    //CONSTRUCTOR:

    public CrearInventarioControlador(OrquestadorInventarioProducto orquestadorInventarioProducto) {
        this.orquestadorInventarioProducto = orquestadorInventarioProducto;
    }

    //MÉTODOS:

    public void cargarDatos(UsuarioDTOCompleto usuarioActual, ObservableList<InventarioDTO> listaObservable){
        ValidadorSeguridad.exigirPermiso(usuarioActual, PermisosApp.REGISTRAR_INVENTARIOS);
        this.usuarioActual = usuarioActual;
        this.listaObservable = listaObservable;
        Platform.runLater(()->btnCancelar.requestFocus());
    }

    private Window getVentana(){
        return btnCancelar.getScene() != null ? btnCancelar.getScene().getWindow() : null;
    }


    @FXML
    private void accionGuardar(ActionEvent event) {
        String nombre = txtNombre.getText().trim();
        String capacidadTexto = txtCapacidad.getText().trim();
        if (nombre.isEmpty() || capacidadTexto.isEmpty()) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Campos Vacíos", null,
                    "El Nombre y la Capacidad son Obligatorios."
            );
            return;
        }
        int capacidad;
        try {
            capacidad = Integer.parseInt(capacidadTexto);
            if (capacidad <= 0) {
                GestorAlertas.mostrarAlertaWarning(
                        getVentana(), "Dato Inválido", null,
                        "La Capacidad debe ser un Número Positivo."
                );
                return;
            }
        } catch (NumberFormatException e) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Número Inválido", null,
                    "La Capacidad debe ser un Número Entero (ej. 500), Sin Letras ni Decimales."
            );
            return;
        }
        CompletableFuture.supplyAsync(()->
                this.orquestadorInventarioProducto.registrarInventario(this.usuarioActual, nombre, capacidad)
        ).thenAccept(inventarioRegistrado->
            Platform.runLater(()->{
                this.listaObservable.add(inventarioRegistrado);
                GestorAlertas.mostrarAlertaInformacion(
                        getVentana(), "Éxito", null,
                        "El Inventario ha sido Creado Correctamente."
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
                }
                btnCancelar.requestFocus();
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


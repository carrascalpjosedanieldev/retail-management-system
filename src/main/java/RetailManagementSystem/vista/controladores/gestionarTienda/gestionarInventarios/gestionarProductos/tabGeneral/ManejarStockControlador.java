package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarInventarios.gestionarProductos.tabGeneral;

import RetailManagementSystem.aplicacion.dto.ventas.ProductoResumenDTO;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorInventarioProducto;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorProductos;
import RetailManagementSystem.dominio.excepciones.CapacidadInventarioExcedidaException;
import RetailManagementSystem.dominio.excepciones.StockInsuficienteException;
import RetailManagementSystem.vista.utilidades.GestorAlertas;
import RetailManagementSystem.vista.utilidades.UtilidadesLista;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

public class ManejarStockControlador {

    //ATRIBUTOS:

    @FXML private Button btnCancelar;
    @FXML private Button btnGuardar;
    @FXML private ToggleGroup grupoAccion;
    @FXML private ToggleButton tglReponer;
    @FXML private ToggleButton tglRetirar;
    @FXML private Label lblStockActual;
    @FXML public Label lblVistaPrevia;
    @FXML private TextField txtCantidad;

    private ProductoResumenDTO seleccionado;

    private int idInventario;

    private ObservableList<ProductoResumenDTO> listaObservable;

    private final OrquestadorInventarioProducto orquestadorInventarioProducto;

    private final OrquestadorProductos orquestadorProductos;

    //CONSTRUCTOR:

    public ManejarStockControlador(
            OrquestadorInventarioProducto orquestadorInventarioProducto, OrquestadorProductos orquestadorProductos
    ) {
        this.orquestadorInventarioProducto = orquestadorInventarioProducto;
        this.orquestadorProductos = orquestadorProductos;
    }

    //MÉTODOS:

    public void cargarDatos(
            ProductoResumenDTO producto, int idInventario, ObservableList<ProductoResumenDTO> listaObservable
    ) {
        this.seleccionado = producto;
        this.idInventario = idInventario;
        this.listaObservable = listaObservable;
        txtCantidad.clear();
        tglReponer.setSelected(true);
        lblStockActual.setText(String.valueOf(producto.stock()));
    }


    @FXML
    public void initialize() {
        grupoAccion.selectedToggleProperty().addListener((obs, viejoBoton, nuevoBoton) -> {
            if (nuevoBoton == null) {
                viejoBoton.setSelected(true);
            } else {
                actualizarVistaPrevia();
            }
        });
        txtCantidad.textProperty().addListener((obs, old, newValue) -> actualizarVistaPrevia());
        Platform.runLater(()->btnCancelar.requestFocus());
    }

    private void actualizarVistaPrevia() {
        if (this.seleccionado == null) {
            return;
        }
        try {
            String texto = txtCantidad.getText().trim();
            if (texto.isBlank()) {
                lblVistaPrevia.setText("Ingrese la cantidad");
                btnGuardar.setDisable(true);
                marcarLabelComoError(false);
                return;
            }
            int cantidad = Integer.parseInt(texto);
            if (cantidad <= 0) {
                lblVistaPrevia.setText("La cantidad debe ser mayor a cero");
                btnGuardar.setDisable(true);
                marcarLabelComoError(true);
                return;
            }
            int nuevoStock = tglReponer.isSelected() ?
                    seleccionado.stock() + cantidad :
                    seleccionado.stock() - cantidad;
            if (nuevoStock < 0) {
                lblVistaPrevia.setText("Error: El stock no puede ser negativo (" + nuevoStock + ")");
                btnGuardar.setDisable(true);
                marcarLabelComoError(true);
            } else {
                lblVistaPrevia.setText("Nuevo Stock estimado: " + nuevoStock);
                btnGuardar.setDisable(false);
                marcarLabelComoError(false);
            }
        } catch (NumberFormatException ex) {
            lblVistaPrevia.setText("Ingrese una cantidad válida");
            btnGuardar.setDisable(true);
            marcarLabelComoError(false);
        }
    }

    private void marcarLabelComoError(boolean esError) {
        if (esError) {
            lblVistaPrevia.getStyleClass().remove("lbl-vista-previa");
            if (!lblVistaPrevia.getStyleClass().contains("lbl-vista-previa-error")) {
                lblVistaPrevia.getStyleClass().add("lbl-vista-previa-error");
            }
        } else {
            lblVistaPrevia.getStyleClass().remove("lbl-vista-previa-error");
            if (!lblVistaPrevia.getStyleClass().contains("lbl-vista-previa")) {
                lblVistaPrevia.getStyleClass().add("lbl-vista-previa");
            }
        }
    }


    @FXML
    void accionGuardar(ActionEvent event) {
        String textoCantidad = txtCantidad.getText().trim();
        if (textoCantidad.isEmpty()) {
            GestorAlertas.mostrarAlertaWarning(
                    "Error de Validación", null,
                    "La Cantidad NO puede estar vacía."
            );
            return;
        }
        try {
            int cantidad = Integer.parseInt(textoCantidad);
            if (cantidad <= 0) {
                GestorAlertas.mostrarAlertaWarning(
                        "Error de Validación", null,
                        "La Cantidad debe ser Mayor a Cero."
                );
                return;
            }
            boolean esReposicion = tglReponer.isSelected();
            CompletableFuture.supplyAsync(()->{
                if (esReposicion){
                    return this.orquestadorInventarioProducto.validarEspacioInventarioYAumentarStockProducto(
                            this.idInventario, cantidad, seleccionado.codigoProducto(), LocalDate.now()
                    );
                } else {
                    return this.orquestadorProductos.reducirStockDeProductoDeInventario(
                            this.idInventario, seleccionado.codigoProducto(), cantidad, LocalDate.now()
                    );
                }
            }).thenAccept(actualizado->
                Platform.runLater(()->{
                    UtilidadesLista.reemplazarPorIdentidad(
                            listaObservable,
                            actualizado,
                            item -> item.codigoProducto().equals(actualizado.codigoProducto())
                    );
                    GestorAlertas.mostrarAlertaInformacion(
                            "Éxito", null,
                            "El Stock se ha Actualizado Correctamente."
                    );
                    cerrarPantalla();
                })
            ).exceptionally(ex->{
                Platform.runLater(()->{
                    Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                    if (causa instanceof IllegalArgumentException || causa instanceof IllegalStateException){
                        GestorAlertas.mostrarAlertaWarning(
                                "NO se pudo Completar la Acción", null,
                                "Error: " + causa.getMessage()
                        );
                    } else if (
                            causa instanceof CapacidadInventarioExcedidaException || causa instanceof StockInsuficienteException
                    ) {
                        GestorAlertas.mostrarAlertaWarning(
                                "Operación Rechazada", null,
                                "Error: " + causa.getMessage()
                        );
                    } else {
                        GestorAlertas.mostrarAlertaError(
                                "Error Critico", null,
                                "Verifica tu Conexión y Notificale este error al Administrador:\n" +
                                        causa.getMessage()
                        );
                    }
                });
                return null;
            });
        } catch (NumberFormatException e) {
            GestorAlertas.mostrarAlertaWarning(
                    "Número Inválido", null,
                    "Por favor Ingresa un Número Entero Válido."
            );
        }
    }

    private void cerrarPantalla() {
        Stage stageActual = (Stage) txtCantidad.getScene().getWindow();
        stageActual.close();
    }


    @FXML
    void accionCancelar(ActionEvent event) {
        cerrarPantalla();
    }

}//===================================================================================================================//


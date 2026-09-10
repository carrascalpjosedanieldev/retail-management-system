package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarInventarios.gestionarProductos.tabGeneral;

import RetailManagementSystem.aplicacion.dto.gestion.InventarioDTO;
import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.dto.ventas.ProductoResumenDTO;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorGestionStock;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorInventarios;
import RetailManagementSystem.dominio.excepciones.reglasDeNegocio.CapacidadInventarioExcedidaException;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.infraestructura.seguridad.ValidadorSeguridad;
import RetailManagementSystem.vista.utilidades.GestorAlertas;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Window;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MoverProductoAOtroInventarioControlador {

    //ATRIBUTOS:

    @FXML private Button btnCancelar;
    @FXML private ComboBox<InventarioDTO> comboInventarios;
    @FXML private Label lblInfo;
    @FXML private Label lblNombreProducto;

    private int idInventario;

    private ProductoResumenDTO seleccionado;

    private ObservableList<ProductoResumenDTO> listaObservable;

    private final OrquestadorGestionStock orquestadorGestionStock;

    private final OrquestadorInventarios orquestadorInventarios;

    private UsuarioDTOCompleto usuarioActual;

    //CONSTRUCTOR:

    public MoverProductoAOtroInventarioControlador(
            OrquestadorGestionStock orquestadorGestionStock, OrquestadorInventarios orquestadorInventarios
    ) {
        this.orquestadorGestionStock = orquestadorGestionStock;
        this.orquestadorInventarios = orquestadorInventarios;
    }

    //MÉTODOS:

    public void cargarDatos(
            UsuarioDTOCompleto usuarioActual, int idInventario, ProductoResumenDTO seleccionado,
            ObservableList<ProductoResumenDTO> listaObservable
    ) {
        ValidadorSeguridad.exigirPermiso(usuarioActual, PermisosApp.TRASLADAR_PRODUCTOS);
        this.usuarioActual = usuarioActual;
        this.idInventario = idInventario;
        this.seleccionado = seleccionado;
        this.listaObservable = listaObservable;
        lblNombreProducto.setText(seleccionado.nombre());
        lblInfo.setText("Se moverá la referencia completa y sus " + seleccionado.stock() + " unidades disponibles.");
        Platform.runLater(()->btnCancelar.requestFocus());
        cargarDatosComboBox();
    }

    private Window getVentana(){
        return btnCancelar.getScene() != null ? btnCancelar.getScene().getWindow() : null;
    }


    @FXML
    public void initialize(){
        comboInventarios.setConverter(new StringConverter<InventarioDTO>() {
            @Override
            public String toString(InventarioDTO inv) { return inv != null ? inv.nombre() : ""; }
            @Override
            public InventarioDTO fromString(String string) { return null; }
        });
    }

    private void cargarDatosComboBox(){
        CompletableFuture.supplyAsync(
                this.orquestadorInventarios::obtenerTodosLosInventarios
        ).thenAccept(listaInventarios->
            Platform.runLater(()->{
                List<InventarioDTO> inventariosFiltrados = new ArrayList<>(listaInventarios);
                inventariosFiltrados.removeIf(inv -> inv.idInventario() == this.idInventario);
                comboInventarios.getItems().addAll(inventariosFiltrados);
            })
        ).exceptionally(ex->{
            Platform.runLater(()->{
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                GestorAlertas.mostrarAlertaError(
                        getVentana(), "Error", null,
                        "NO se Pudieron Cargar los Inventarios.\n" +
                                "Detalle:  " + causa.getMessage()
                );
                cerrarPantalla();
            });
            return null;
        });
    }


    @FXML
    private void moverProducto(ActionEvent event) {
        if (comboInventarios.getValue() == null) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Error de Validación", null,
                    "Debe seleccionar un Inventario de destino."
            );
            return;
        }
        InventarioDTO inventarioDestino = comboInventarios.getValue();
        CompletableFuture.runAsync(()->
                this.orquestadorGestionStock.validarEspacioInventarioYMoverProducto(
                        this.usuarioActual, this.idInventario, inventarioDestino.idInventario(),
                        seleccionado.codigoProducto()
                )
        ).thenRun(()->
            Platform.runLater(()->{
                listaObservable.remove(seleccionado);
                GestorAlertas.mostrarAlertaInformacion(
                        getVentana(), "Éxito", null,
                        "El Producto ha sido Movido Exitosamente al Inventario: " + inventarioDestino.nombre()
                );
                cerrarPantalla();
            })
        ).exceptionally(ex->{
            Platform.runLater(()->{
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                if (causa instanceof IllegalArgumentException){
                    GestorAlertas.mostrarAlertaError(
                            getVentana(), "NO se pudo Completar la Acción", null,
                            "Error:  " + causa.getMessage()
                    );
                } else if (causa instanceof CapacidadInventarioExcedidaException){
                    GestorAlertas.mostrarAlertaError(
                            getVentana(), "Capacidad Excedida", null,
                            "El Inventario -" + inventarioDestino.nombre() + "- NO puede recibir esa Cantidad.\n" +
                                    causa.getMessage()
                    );
                } else {
                    GestorAlertas.mostrarAlertaError(
                            getVentana(), "Error Critico", null,
                            "Verifica tu Conexión y Notificale este Error al Administrador:\n" +
                                    causa.getMessage()
                    );
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
    private void cancelar(ActionEvent event) {
        cerrarPantalla();
    }

}//===================================================================================================================//


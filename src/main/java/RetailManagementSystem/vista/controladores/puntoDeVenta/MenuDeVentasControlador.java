package RetailManagementSystem.vista.controladores.puntoDeVenta;

import RetailManagementSystem.aplicacion.dto.ventas.FacturaDTO;
import RetailManagementSystem.aplicacion.dto.ventas.ItemCarritoDTO;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorVentas;
import RetailManagementSystem.dominio.entidades.ventas.SesionVenta;
import RetailManagementSystem.dominio.excepciones.*;
import RetailManagementSystem.vista.utilidades.CargadorVistas;
import RetailManagementSystem.vista.utilidades.FormateadorNumeros;
import RetailManagementSystem.vista.utilidades.GestorAlertas;
import RetailManagementSystem.vista.utilidades.RutasVista;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.beans.binding.Bindings;
import javafx.stage.Window;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MenuDeVentasControlador {

    //ATRIBUTOS:

    @FXML private Button btnVolver;
    @FXML private TextField txtCodigo;
    @FXML private Button btnAgregar;
    @FXML private TableView<ItemCarritoDTO> tablaCarrito;
    @FXML private TableColumn<ItemCarritoDTO, String> colCodigo;
    @FXML private TableColumn<ItemCarritoDTO, String> colDescripcion;
    @FXML private TableColumn<ItemCarritoDTO, Integer> colCantidad;
    @FXML private TableColumn<ItemCarritoDTO, BigDecimal> colPrecio;
    @FXML private TableColumn<ItemCarritoDTO, BigDecimal> colSubtotal;
    @FXML private Label lblSubtotal;
    @FXML private Label lblImpuestos;
    @FXML private Label lblTotalGeneral;
    @FXML private Button btnAumentarCant;
    @FXML private Button btnReducirCant;
    @FXML private Button btnEliminarItem;
    @FXML private Button btnCancelarVenta;
    @FXML private Button btnProcesarVenta;

    private final OrquestadorVentas orquestadorVentas;

    private ObservableList<ItemCarritoDTO> listaCarrito;

    private SesionVenta sesionVenta;

    //CONSTRUCTOR:

    public MenuDeVentasControlador(OrquestadorVentas orquestadorVentas) {
        this.orquestadorVentas = orquestadorVentas;
    }

    //MÉTODOS:

    private LocalDate obtenerFecha(){
        return LocalDate.now();
    }

    private Window getVentana(){
        return btnVolver.getScene().getWindow();
    }


    @FXML
    public void initialize() {
        listaCarrito = FXCollections.observableArrayList();
        tablaCarrito.setItems(listaCarrito);
        configurarColumnas();
        configurarHabilitacionBotones();
        configurarBarraBusqueda();
        this.sesionVenta = this.orquestadorVentas.abrirVentaSesion();
        actualizarTotales();
    }

    private void configurarColumnas(){
        colCodigo.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().codigoArticulo())
        );
        colDescripcion.setCellValueFactory(cellData -> {
            ItemCarritoDTO item = cellData.getValue();
            String descripcionArmada = item.tipoItem() + ": " + item.nombreArticulo();
            return new SimpleStringProperty(descripcionArmada);
        });
        colCantidad.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                cellData.getValue().cantidad())
        );
        colPrecio.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                cellData.getValue().precioUnitario())
        );
        colPrecio.setCellFactory(columna -> crearCeldaMoneda());
        colSubtotal.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                cellData.getValue().subtotal())
        );
        colSubtotal.setCellFactory(columna -> crearCeldaMoneda());
    }

    private TableCell<ItemCarritoDTO, BigDecimal> crearCeldaMoneda(){
        return new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal monto, boolean empty) {
                super.updateItem(monto, empty);
                if (empty || monto == null) {
                    setText(null);
                } else {
                    setText(FormateadorNumeros.formatoMoneda(monto));
                }
            }
        };
    }

    private void configurarHabilitacionBotones(){
        btnAumentarCant.setDisable(true);
        btnReducirCant.setDisable(true);
        btnEliminarItem.setDisable(true);
        tablaCarrito.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean haySeleccion = (newSelection != null);
            btnAumentarCant.setDisable(!haySeleccion);
            btnReducirCant.setDisable(!haySeleccion);
            btnEliminarItem.setDisable(!haySeleccion);
        });
        btnProcesarVenta.disableProperty().bind(Bindings.isEmpty(listaCarrito));
        btnCancelarVenta.disableProperty().bind(Bindings.isEmpty(listaCarrito));
    }

    private void configurarBarraBusqueda(){
        txtCodigo.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                agregarItem(null);
            }
        });
        Platform.runLater(() -> txtCodigo.requestFocus());
    }

    private void actualizarTotales() {
        lblSubtotal.setText("$ 0.00");
        lblImpuestos.setText("$ 0.00");
        lblTotalGeneral.setText("$ 0.00");
    }


    @FXML
    public void volverAlMenu(ActionEvent event) {
        if (listaCarrito != null && !listaCarrito.isEmpty()) {
            boolean respuesta = GestorAlertas.mostrarConfirmacion(
                    getVentana(), "Venta en Curso",
                    "¿Está Seguro de que desea Salir?",
                    """
                    Tiene ítems en el Carrito. Si sale Ahora, se Cancelará la Venta y Perderá Todo el Progreso.
                    
                    ¿Desea Continuar y Salir?"""
            );
            if (!respuesta) {
                return;
            }
            CompletableFuture.supplyAsync(()->
                    this.orquestadorVentas.cancelarCompraTotal(this.sesionVenta, obtenerFecha())
            ).thenAccept(carritoActualizado->
                Platform.runLater(()->{
                    actualizarTablaYTotales(carritoActualizado.carritoItems());
                    GestorAlertas.mostrarAlertaInformacion(
                            getVentana(), "Venta Cancelada", null,
                            "Se ha Cancelado la Venta y vaciado el Carrito con Éxito."
                    );
                })
            ).exceptionally(ex->{
                Platform.runLater(()->{
                    Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                    manejarErrorCritico(causa);
                });
                return null;
            });
        }
        Stage stageActual = (Stage) getVentana();
        CargadorVistas.cambiarPantalla(stageActual, RutasVista.PANEL_DE_CONTROL_POS_VIEW);
    }


    @FXML
    public void agregarItem(ActionEvent event) {
        agregarItem();
    }

    private void agregarItem(){
        String codigo = txtCodigo.getText().trim();
        if (codigo.isEmpty()) {
            return;
        }
        CompletableFuture.supplyAsync(()->
            this.orquestadorVentas.agregarItemAlCarrito(this.sesionVenta, codigo, obtenerFecha())
        ).thenAccept(carritoActualizado ->
            Platform.runLater(()->{
                actualizarTablaYTotales(carritoActualizado.carritoItems());
                txtCodigo.clear();
            })
        ).exceptionally(ex->{
            Platform.runLater(()->{
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                if (causa instanceof ProductoVencidoException){
                    GestorAlertas.mostrarAlertaError(
                            getVentana(), "Producto Vencido", null,
                            causa.getMessage() + " NO sera agregado al Carrito"
                    );
                } else if (causa instanceof StockInsuficienteException){
                    GestorAlertas.mostrarAlertaError(
                            getVentana(), "Stock Insuficiente", null,
                            "Error:  " + causa.getMessage()
                    );
                } else if (causa instanceof ProductoNoDisponibleException ||
                        causa instanceof ServicioNoDisponibleExeption){
                    GestorAlertas.mostrarAlertaError(
                            getVentana(), "Item NO Disponible", null,
                            causa.getMessage() + " NO sera agregado al Carrito"
                    );
                } else if (causa instanceof ProductoNoEncontradoException ||
                        causa instanceof ServicioNoEncontradoException) {
                    GestorAlertas.mostrarAlertaError(
                            getVentana(), "Item NO Encontrado", null,
                            "Error:  " + causa.getMessage()
                    );
                } else if (causa instanceof IllegalArgumentException){
                    GestorAlertas.mostrarAlertaError(
                            getVentana(), "Error en los Datos Ingresados", null,
                            "Error:  " + causa.getMessage()
                    );
                } else {
                    this.manejarErrorCritico(causa);
                }
            });
            return null;
        }).whenComplete((resultado, excepcion)->
            Platform.runLater(this::restaurarFocoCodigo)
        );
    }

    private void actualizarTablaYTotales(List<ItemCarritoDTO> itemsDelCarrito) {
        if (itemsDelCarrito == null || itemsDelCarrito.isEmpty()){
            listaCarrito.clear();
            actualizarTotales();
            return;
        }
        listaCarrito.setAll(itemsDelCarrito);
        BigDecimal subtotalVenta = BigDecimal.ZERO;
        BigDecimal impuestos = BigDecimal.ZERO;
        for (ItemCarritoDTO item : itemsDelCarrito) {
            subtotalVenta = subtotalVenta.add(item.subtotal());
            impuestos = impuestos.add(
                    item.impuestos().multiply(BigDecimal.valueOf(item.cantidad()))
            );
        }
        BigDecimal totalGeneral = subtotalVenta;
        subtotalVenta = subtotalVenta.subtract(impuestos);
        lblSubtotal.setText(FormateadorNumeros.formatoMoneda(subtotalVenta));
        lblImpuestos.setText(FormateadorNumeros.formatoMoneda(impuestos));
        lblTotalGeneral.setText(FormateadorNumeros.formatoMoneda(totalGeneral));
    }

    private void manejarErrorCritico(Throwable causa) {
        GestorAlertas.mostrarAlertaError(
                getVentana(), "Error Crítico",
                "NO se pudo Completar la Acción.",
                "Notifícale al Administrador este Error:\n" + causa.getMessage()
        );
    }

    private void restaurarFocoCodigo() {
        Platform.runLater(() -> txtCodigo.requestFocus());
    }


    @FXML
    public void eliminarItemSeleccionado(ActionEvent event) {
        eliminarItemSeleccionado();
    }

    private void eliminarItemSeleccionado(){
        ItemCarritoDTO itemSeleccionado = tablaCarrito.getSelectionModel().getSelectedItem();
        if (itemSeleccionado == null) {
            return;
        }
        boolean respuesta = GestorAlertas.mostrarConfirmacion(
                getVentana(), "Confirmar Eliminación",
                "¿Eliminar ítem?",
                "¿Está seguro de que desea retirar -" + itemSeleccionado.nombreArticulo() + "- del carrito?"
        );
        if (!respuesta) {
            txtCodigo.requestFocus();
            return;
        }
        CompletableFuture.supplyAsync(()->
            this.orquestadorVentas.eliminarItemDelCarrito(
                    this.sesionVenta, itemSeleccionado.codigoArticulo(), obtenerFecha()
            )
        ).thenAccept(carritoActualizado->
            Platform.runLater(()->
                actualizarTablaYTotales(carritoActualizado.carritoItems())
            )
        ).exceptionally(ex->{
            Platform.runLater(()->{
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                if (causa instanceof IllegalArgumentException) {
                    GestorAlertas.mostrarAlertaError(
                            getVentana(), "Error en el Proceso", null,
                            "Error:  " + causa.getMessage()
                    );
                } else {
                    manejarErrorCritico(causa);
                }
            });
            return null;
        }).whenComplete((resultado, excepcion)->
            Platform.runLater(this::restaurarFocoCodigo)
        );
    }


    @FXML
    public void cancelarVenta(ActionEvent event) {
        cancelarVenta();
    }

    private void cancelarVenta(){
        if (listaCarrito == null || listaCarrito.isEmpty()) {
            return;
        }
        boolean respuesta = GestorAlertas.mostrarConfirmacion(
                getVentana(), "Cancelar Venta",
                "¿Desea Cancelar Toda la Venta?",
                "Se Eliminarán Todos los Productos del Carrito. Esta Acción NO se puede Deshacer."
        );
        if (!respuesta){
            txtCodigo.requestFocus();
            return;
        }
        CompletableFuture.supplyAsync(()->
                this.orquestadorVentas.cancelarCompraTotal(this.sesionVenta, obtenerFecha())
        ).thenAccept(carritoActualizado->
            Platform.runLater(()->{
                actualizarTablaYTotales(carritoActualizado.carritoItems());
                GestorAlertas.mostrarAlertaInformacion(
                        getVentana(), "Venta Cancelada", null,
                        "Se ha Cancelado la Venta y vaciado el Carrito con Éxito."
                );
            })
        ).exceptionally(ex->{
            Platform.runLater(()->{
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                manejarErrorCritico(causa);
            });
            return null;
        }).whenComplete((resultado, excepcion)->
            Platform.runLater(()->{
                txtCodigo.clear();
                txtCodigo.requestFocus();
            })
        );
    }


    @FXML
    public void aumentarCantidadSeleccionada(ActionEvent event) {
        gestionarCambioCantidad(true);
    }

    @FXML
    public void reducirCantidadSeleccionada(ActionEvent event) {
        gestionarCambioCantidad(false);
    }

    private void gestionarCambioCantidad(boolean esAumento) {
        ItemCarritoDTO itemSeleccionado = tablaCarrito.getSelectionModel().getSelectedItem();
        if (itemSeleccionado == null) {
            return;
        }
        String titulo = esAumento ? "Aumentar" : "Reducir";
        DialogoCantidadControlador controladorDialogo = CargadorVistas.abrirModalInyectada(
                RutasVista.DIALOGO_CANTIDAD_VIEW,
                titulo, getVentana(),
                (DialogoCantidadControlador c)->{
                    c.configurarDialogo(titulo, 1, itemSeleccionado.nombreArticulo());
                }
        );
        if (controladorDialogo == null){
            restaurarFocoCodigo();
            return;
        }
        if (!controladorDialogo.isConfirmado()) {
            restaurarFocoCodigo();
            return;
        }
        int cantidad = controladorDialogo.getCantidadFinal();
        CompletableFuture.supplyAsync(() -> {
            if (esAumento) {
                return this.orquestadorVentas.aumentarCantidadItem(
                        this.sesionVenta, itemSeleccionado.codigoArticulo(), cantidad, obtenerFecha());
            } else {
                return this.orquestadorVentas.reducirCantidadItem(
                        this.sesionVenta, itemSeleccionado.codigoArticulo(), cantidad, obtenerFecha());
            }
        }).thenAccept(carritoActualizado ->
                Platform.runLater(() -> actualizarTablaYTotales(carritoActualizado.carritoItems()))
        ).exceptionally(ex -> {
            Platform.runLater(() -> {
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                if (causa instanceof IllegalArgumentException){
                    GestorAlertas.mostrarAlertaError(
                            getVentana(), "Entrada Inválida", null,
                            "Por favor, Ingrese un Número Entero Válido."
                    );
                } else if (causa instanceof StockInsuficienteException) {
                    GestorAlertas.mostrarAlertaError(
                            getVentana(), "Stock Insuficiente", null,
                            "Error:  " + causa.getMessage()
                    );
                } else {
                    manejarErrorCritico(causa);
                }
            });
            return null;
        }).whenComplete((resultado, excepcion) -> restaurarFocoCodigo());
    }


    @FXML
    public void procesarVenta(ActionEvent event) {
        procesarVenta();
    }

    private void procesarVenta(){
        if (listaCarrito == null || listaCarrito.isEmpty()) {
            return;
        }
        String total = lblTotalGeneral.getText().trim();
        boolean respuesta = GestorAlertas.mostrarConfirmacion(
                getVentana(), "Confirmar Venta",
                "¿Finalizar y Registrar la Venta?",
                "Se Registrará la Venta por un Total de " + total + ".\n¿Está seguro de continuar?"
        );
        if (!respuesta){
            txtCodigo.requestFocus();
            return;
        }
        CompletableFuture.supplyAsync(()->
                this.orquestadorVentas.procesarVentaYObtenerFactura(this.sesionVenta, obtenerFecha())
        ).thenAccept(facturaGenerada ->
            Platform.runLater(()->{
                mostrarVentanaFactura(facturaGenerada);
                actualizarTotales();
                actualizarTablaYTotales(null);
            })
        ).exceptionally(ex->{
            Platform.runLater(()->{
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                if (causa instanceof CarritoVacioException){
                    GestorAlertas.mostrarAlertaError(
                            getVentana(), "Carrito Vacío", null,
                            "Error:  " + causa.getMessage()
                    );
                } else if (causa instanceof StockInsuficienteException){
                    GestorAlertas.mostrarAlertaError(
                            getVentana(), "Stock Insuficiente", null,
                            "Lo Sentimos, volviendo a Verificar el Stock por seguridad nos dimos cuenta de esto:\n" +
                                    causa.getMessage() + "\n" +
                                    "No te preocupes el carrito esta Intacto pero debes modificarlo."
                    );
                } else {
                    manejarErrorCritico(causa);
                }
            });
            return null;
        }).whenComplete((resultado, excepcion)->{
            txtCodigo.clear();
            restaurarFocoCodigo();
        });

    }

    private void mostrarVentanaFactura(FacturaDTO factura) {
        CargadorVistas.abrirModalInyectada(
                RutasVista.FACTURA_GENERADA_VIEW,
                "Factura Generada - " + factura.numeroFactura(), getVentana(),
                (FacturaGeneradaControlador c)->{
                    c.cargarFactura(factura);
                }
        );
    }


}//===================================================================================================================//


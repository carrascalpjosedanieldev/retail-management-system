package RetailManagementSystem.vista.controladores.puntoDeVenta;

import RetailManagementSystem.aplicacion.dto.ventas.FacturaDTO;
import RetailManagementSystem.aplicacion.dto.ventas.ItemCarritoDTO;
import RetailManagementSystem.aplicacion.dto.ventas.VistaPreviaCarritoDTO;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorVentas;
import RetailManagementSystem.dominio.entidades.ventas.SesionVenta;
import RetailManagementSystem.dominio.excepciones.*;
import RetailManagementSystem.vista.excepciones.CargarVistaException;
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
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.beans.binding.Bindings;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        URL urlCss = getClass().getResource(RutasVista.ESTILOS_CSS_MENU_DE_VENTAS);
        if (urlCss != null) {
            alert.getDialogPane().getStylesheets().add(urlCss.toExternalForm());
        }
        alert.showAndWait();
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
                    "Venta en Curso",
                    "¿Está Seguro de que desea Salir?",
                    """
                    Tiene ítems en el Carrito. Si sale Ahora, se Cancelará la Venta y Perderá Todo el Progreso.
                    
                    ¿Desea Continuar y Salir?"""
            );
            if (!respuesta) {
                return;
            }
            this.orquestadorVentas.cancelarCompraTotal(this.sesionVenta);
        }
        Stage stageActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
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
        ).thenAccept(carritoActualizado -> {
            Platform.runLater(()->{
                actualizarTablaYTotales(carritoActualizado.carritoItems());
                txtCodigo.clear();
            });
        }).exceptionally(ex->{
            Platform.runLater(()->{
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                if (causa instanceof ProductoVencidoException){
                    GestorAlertas.mostrarAlertaError(
                            "Producto Vencido", null,
                            causa.getMessage() + " NO sera agregado al Carrito"
                    );
                } else if (causa instanceof StockInsuficienteException){
                    GestorAlertas.mostrarAlertaError(
                            "Stock Insuficiente", null,
                            "Error:  " + causa.getMessage()
                    );
                } else if (causa instanceof ProductoNoDisponibleException || causa instanceof ServicioNoDisponibleExeption){
                    GestorAlertas.mostrarAlertaError(
                            "Item NO Disponible", null,
                            causa.getMessage() + " NO sera agregado al Carrito"
                    );
                } else if (causa instanceof ProductoNoEncontradoException || causa instanceof ServicioNoEncontradoException) {
                    GestorAlertas.mostrarAlertaError(
                            "Item NO Encontrado", null,
                            "Error:  " + causa.getMessage()
                    );
                } else if (causa instanceof IllegalArgumentException){
                    GestorAlertas.mostrarAlertaError(
                            "Error en los Datos Ingresados", null,
                            "Error:  " + causa.getMessage()
                    );
                } else {
                    this.manejarErrorCritico(causa);
                }
            });
            return null;
        }).whenComplete((resultado, excepcion)->{
            Platform.runLater(this::restaurarFocoCodigo);
        });
    }

    private void actualizarTablaYTotales(List<ItemCarritoDTO> itemsDelCarrito) {
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
                "Error Crítico",
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
                "Confirmar Eliminación",
                "¿Eliminar ítem?",
                "¿Está seguro de que desea retirar -" + itemSeleccionado.nombreArticulo() +
                        "- del carrito?"
        );
        if (!respuesta) {
            txtCodigo.requestFocus();
            return;
        }
        CompletableFuture.supplyAsync(()->
            this.orquestadorVentas.eliminarItemDelCarrito(
                    this.sesionVenta, itemSeleccionado.codigoArticulo(), obtenerFecha()
            )
        ).thenAccept(carritoActualizado->{
            Platform.runLater(()->{
                actualizarTablaYTotales(carritoActualizado.carritoItems());
            });
        }).exceptionally(ex->{
            Platform.runLater(()->{
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                if (causa instanceof IllegalArgumentException) {
                    GestorAlertas.mostrarAlertaError(
                            "Error en el Proceso", null,
                            "Error:  " + causa.getMessage()
                    );
                } else {
                    manejarErrorCritico(causa);
                }
            });
            return null;
        }).whenComplete((resultado, excepcion)->{
            Platform.runLater(this::restaurarFocoCodigo);
        });
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
                "Cancelar Venta",
                "¿Desea Cancelar Toda la Venta?",
                "Se Eliminarán Todos los Productos del Carrito. Esta Acción NO se puede Deshacer."
        );
        if (!respuesta){
            txtCodigo.requestFocus();
            return;
        }
        CompletableFuture.runAsync(()->
                this.orquestadorVentas.cancelarCompraTotal(this.sesionVenta)
        ).thenRun(()->{
            Platform.runLater(()->{
                actualizarTotales();
                GestorAlertas.mostrarAlertaInformacion(
                        "Venta Cancelada", null,
                        "Se ha Cancelado la Venta y vaciado el Carrito con Éxito."
                );
            });
        }).exceptionally(ex->{
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
        aumentarCantidadSeleccionada();
    }

    private void aumentarCantidadSeleccionada(){
        ItemCarritoDTO itemSeleccionado = tablaCarrito.getSelectionModel().getSelectedItem();
        if (itemSeleccionado == null) {
            return;
        }
        TextInputDialog dialogo = new TextInputDialog("1");
        dialogo.setTitle("Aumentar Cantidad");
        dialogo.setHeaderText("Aumentar unidades de: " + itemSeleccionado.nombreArticulo());
        dialogo.setContentText("Ingrese la cantidad adicional a agregar:");
        URL urlCss = getClass().getResource(RutasVista.ESTILOS_CSS_MENU_DE_VENTAS);
        if (urlCss != null) {
            dialogo.getDialogPane().getStylesheets().add(urlCss.toExternalForm());
        }
        Optional<String> resultado = dialogo.showAndWait();
        if (resultado.isPresent()) {
            String entrada = resultado.get().trim();
            try {
                int cantidadAAumentar = Integer.parseInt(entrada);
                if (cantidadAAumentar <= 0) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Cantidad Inválida",
                            "La Cantidad a Aumentar debe ser Mayor a Cero (0).");
                    return;
                }
                VistaPreviaCarritoDTO vistaPreviaCarrito = this.orquestadorVentas.aumentarCantidadItem(
                        this.sesionVenta, itemSeleccionado.codigoArticulo(), cantidadAAumentar, obtenerFecha()
                );
                actualizarTablaYTotales(vistaPreviaCarrito.carritoItems());
            } catch (NumberFormatException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Entrada Inválida",
                        "Por favor, Ingrese un Número Entero Válido.");
            } catch (StockInsuficienteException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Stock Insuficiente",
                        "Error:  " + e.getMessage());
            } catch (ProductoNoDisponibleException | ServicioNoDisponibleExeption e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Item NO Disponible",
                        e.getMessage() + " NO sera agregado al Carrito");
            } catch (ProductoVencidoException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Producto Vencido",
                        e.getMessage() + " NO sera agregado al Carrito");
            } catch (ProductoNoEncontradoException | ServicioNoEncontradoException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Item NO Encontrado",
                        "Error:  " + e.getMessage());
            } finally {
                txtCodigo.requestFocus();
            }
        } else {
            txtCodigo.requestFocus();
        }
    }


    @FXML
    public void reducirCantidadSeleccionada(ActionEvent event) {
        reducirCantidadSeleccionada();
    }

    private void reducirCantidadSeleccionada(){
        ItemCarritoDTO itemSeleccionado = tablaCarrito.getSelectionModel().getSelectedItem();
        if (itemSeleccionado == null) {
            return;
        }
        TextInputDialog dialogo = new TextInputDialog("1");
        dialogo.setTitle("Reducir Cantidad");
        dialogo.setHeaderText("Reducir unidades de: " + itemSeleccionado.nombreArticulo());
        dialogo.setContentText("Ingrese la cantidad a reducir:");
        URL urlCss = getClass().getResource(RutasVista.ESTILOS_CSS_MENU_DE_VENTAS);
        if (urlCss != null) {
            dialogo.getDialogPane().getStylesheets().add(urlCss.toExternalForm());
        }
        Optional<String> resultado = dialogo.showAndWait();
        if (resultado.isPresent()) {
            String entrada = resultado.get().trim();
            try {
                int cantidadAReducir = Integer.parseInt(entrada);
                if (cantidadAReducir <= 0) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Cantidad Inválida",
                            "La cantidad a reducir debe ser mayor a cero (0).");
                    return;
                }
                VistaPreviaCarritoDTO vistaPreviaCarrito = this.orquestadorVentas.reducirCantidadItem(
                        this.sesionVenta, itemSeleccionado.codigoArticulo(), cantidadAReducir, obtenerFecha()
                );
                actualizarTablaYTotales(vistaPreviaCarrito.carritoItems());
            } catch (NumberFormatException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Entrada Inválida",
                        "Por favor, ingrese un número entero válido.");
            } catch (StockInsuficienteException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Stock Insuficiente",
                        "Error:  " + e.getMessage());
            } catch (IllegalArgumentException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Error en los Datos Ingresados",
                        "Error:  " + e.getMessage());
            } finally {
                txtCodigo.requestFocus();
            }
        } else {
            txtCodigo.requestFocus();
        }
    }


    @FXML
    public void procesarVenta(ActionEvent event) {
        procesarVenta();
    }

    private void procesarVenta(){
        if (listaCarrito == null || listaCarrito.isEmpty()) {
            return;
        }

        int total = 0;

        boolean respuesta = GestorAlertas.mostrarConfirmacion(
                "Confirmar Venta",
                "¿Finalizar y registrar la venta?",
                "Se registrará la venta por un total de " + total + ".\n¿Está seguro de continuar?"
        );
        if (!respuesta){
            txtCodigo.requestFocus();
            return;
        }
        try {
            FacturaDTO facturaGenerada = this.orquestadorVentas.procesarVentaYObtenerFactura(this.sesionVenta, obtenerFecha());
            mostrarVentanaFactura(facturaGenerada);
            actualizarTotales();
        } catch (CarritoVacioException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Carrito Vacío",
                    "Error:  " + e.getMessage());
        } catch (StockInsuficienteException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Stock Insuficiente",
                    "Lo Sentimos, volviendo a Verificar el Stock por seguridad nos dimos cuenta de esto:\n" +
                            e.getMessage() + "\n" +
                            "No te preocupes el carrito esta intacto");
        } finally {
            txtCodigo.clear();
            txtCodigo.requestFocus();
        }
    }

    private void mostrarVentanaFactura(FacturaDTO factura) {
        String rutaFxml = RutasVista.FACTURA_GENERADA_VIEW;
        try {
            FXMLLoader loader = CargadorVistas.obtenerLoaderConfigurado(rutaFxml);
            Parent root = loader.load();
            FacturaGeneradaControlador controlador = loader.getController();
            controlador.cargarFactura(factura);
            Stage stageFactura = new Stage();
            stageFactura.setTitle("Factura Generada - " + factura.numeroFactura());
            stageFactura.initModality(Modality.APPLICATION_MODAL);
            stageFactura.setResizable(false);
            Scene escenaFactura = new Scene(root);
            URL urlCss = getClass().getResource(RutasVista.ESTILOS_CSS_FACTURA_GENERADA);
            if (urlCss != null) {
                escenaFactura.getStylesheets().add(urlCss.toExternalForm());
            }
            stageFactura.setScene(escenaFactura);
            stageFactura.showAndWait();
        } catch (IOException e) {
            throw new CargarVistaException(rutaFxml, "NO se pudo Cargar el Archivo FXML.", e);
        }
    }


}//===================================================================================================================//


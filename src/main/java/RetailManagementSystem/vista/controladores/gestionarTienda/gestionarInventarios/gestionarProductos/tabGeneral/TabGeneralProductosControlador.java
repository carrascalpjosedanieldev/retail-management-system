package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarInventarios.gestionarProductos.tabGeneral;

import RetailManagementSystem.aplicacion.dto.ventas.ProductoResumenDTO;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorProductos;
import RetailManagementSystem.dominio.excepciones.*;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorInventarioProducto;
import RetailManagementSystem.vista.excepciones.CargarVistaException;
import RetailManagementSystem.vista.utilidades.CargadorVistas;
import RetailManagementSystem.vista.utilidades.FormateadorNumeros;
import RetailManagementSystem.vista.utilidades.GestorAlertas;
import RetailManagementSystem.vista.utilidades.RutasVista;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

public class TabGeneralProductosControlador {

    //ATRIBUTOS:

    @FXML private TableView<ProductoResumenDTO> tablaProductos;
    @FXML private TableColumn<ProductoResumenDTO, String> colCodigo;
    @FXML private TableColumn<ProductoResumenDTO, String> colNombre;
    @FXML private TableColumn<ProductoResumenDTO, BigDecimal> colValor;
    @FXML private TableColumn<ProductoResumenDTO, Integer> colStock;
    @FXML private TableColumn<ProductoResumenDTO, String> colEstado;
    @FXML private TextField txtBuscar;
    @FXML private ToggleGroup grupoFiltroEstado;
    @FXML private ToggleButton btnFiltroTodos;
    @FXML private ToggleButton btnFiltroDisponibles;
    @FXML private ToggleButton btnFiltroNoDisponibles;

    private int idInventario;

    private final OrquestadorInventarioProducto orquestadorInventarioProducto;

    private final OrquestadorProductos orquestadorProductos;

    private final ObservableList<ProductoResumenDTO> listaObservable = FXCollections.observableArrayList();

    private FilteredList<ProductoResumenDTO> listaFiltrada;

    //CONSTRUCTOR:

    public TabGeneralProductosControlador(
            OrquestadorInventarioProducto orquestadorInventarioProducto, OrquestadorProductos orquestadorProductos
    ) {
        this.orquestadorInventarioProducto = orquestadorInventarioProducto;
        this.orquestadorProductos = orquestadorProductos;
    }

    //MÉTODOS:

    public void recibirIdInventario(int idInventario) {
        if (idInventario <= 0){
            GestorAlertas.mostrarAlertaWarning(
                    "ID del Inventario Invalido", null,
                    "El ID recibido NO es Valido."
            );
            return;
        }
        this.idInventario = idInventario;
        cargarDatosTabla();
    }


    @FXML
    public void initialize() {
        configurarColumnas();
        configurarFiltros();
    }

    private void cargarDatosTabla() {
        CompletableFuture.supplyAsync(()->
                this.orquestadorProductos.obtenerResumenProductosDeInventario(this.idInventario, LocalDate.now())
        ).thenAccept(resumenProductos->{
            Platform.runLater(()->{
                listaObservable.clear();
                listaObservable.addAll(resumenProductos);
            });
        }).exceptionally(ex->{
            Platform.runLater(()->{

            });
            return null;
        });
    }

    private void configurarColumnas() {
        colCodigo.setCellValueFactory(celda -> new SimpleStringProperty(
                celda.getValue().codigoProducto())
        );
        colCodigo.setCellFactory(columna -> new TableCell<>() {
            private final Tooltip tooltipFlotante = new Tooltip();
            {
                tooltipFlotante.getStyleClass().add("tooltip-codigo");
                getStyleClass().add("codigo-copiable");
                tooltipFlotante.setShowDelay(Duration.millis(100));
                setAlignment(Pos.CENTER);
            }
            @Override
            protected void updateItem(String codigo, boolean empty) {
                super.updateItem(codigo, empty);
                if (empty || codigo == null) {
                    setText(null);
                    setTooltip(null);
                    setOnMouseClicked(null);
                    setStyle("");
                } else {
                    setText(codigo);
                    tooltipFlotante.setText(codigo + "\n(Clic para copiar)");
                    setTooltip(tooltipFlotante);
                    setOnMouseClicked(evt -> {
                        ClipboardContent contenido = new ClipboardContent();
                        contenido.putString(codigo);
                        Clipboard.getSystemClipboard().setContent(contenido);
                    });
                }
            }
        });
        colNombre.setCellValueFactory(celda -> new SimpleStringProperty(
                celda.getValue().nombre())
        );
        colStock.setCellValueFactory(celda -> new SimpleIntegerProperty(
                celda.getValue().stock()).asObject()
        );
        colStock.setCellFactory(columna -> new TableCell<>() {
            {
                setAlignment(Pos.CENTER);
            }
            @Override
            protected void updateItem(Integer stock, boolean empty) {
                super.updateItem(stock, empty);
                getStyleClass().removeAll("stock-sin-existencias", "stock-bajo", "stock-normal");
                if (empty || stock == null) {
                    setText(null);
                    return;
                }
                setText(String.valueOf(stock));
                if (stock <= 0) {
                    getStyleClass().add("stock-sin-existencias");
                } else if (stock <= 5) {
                    getStyleClass().add("stock-bajo");
                } else {
                    getStyleClass().add("stock-normal");
                }
            }
        });
        colValor.setCellValueFactory(celda -> new SimpleObjectProperty<>(
                celda.getValue().valorVenta())
        );
        colValor.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal precio, boolean empty) {
                super.updateItem(precio, empty);
                if (empty || precio == null) {
                    setText(null);
                } else {
                    setText(FormateadorNumeros.formatoMoneda(precio));
                }
            }
        });
        colEstado.setCellValueFactory(celda -> {
            boolean esActivo = celda.getValue().activo();
            String estado = esActivo ? "Disponible" : "NO Disponible";
            return new SimpleStringProperty(estado);
        });
        colEstado.setCellFactory(col -> new TableCell<>() {
            {
                setAlignment(Pos.CENTER);
            }
            @Override
            protected void updateItem(String estado, boolean empty) {
                super.updateItem(estado, empty);
                getStyleClass().removeAll("estado-disponible", "estado-no-disponible");
                if (empty || estado == null) {
                    setText(null);
                    return;
                }
                setText(estado);
                if (estado.equalsIgnoreCase("Disponible")) {
                    getStyleClass().add("estado-disponible");
                } else {
                    getStyleClass().add("estado-no-disponible");
                }
            }
        });
    }

    private void configurarFiltros() {
        listaFiltrada = new FilteredList<>(listaObservable, b -> true);
        Runnable aplicarFiltros = () -> {
            listaFiltrada.setPredicate(producto -> {
                String textoBusqueda = txtBuscar.getText() != null ? txtBuscar.getText().toLowerCase().trim() : "";
                boolean coincideTexto = producto.nombre().toLowerCase().contains(textoBusqueda) ||
                        producto.codigoProducto().toLowerCase().contains(textoBusqueda);
                ToggleButton btnSeleccionado = (ToggleButton) grupoFiltroEstado.getSelectedToggle();
                boolean coincideEstado = true;
                if (btnSeleccionado == btnFiltroDisponibles) {
                    coincideEstado = producto.activo();
                } else if (btnSeleccionado == btnFiltroNoDisponibles) {
                    coincideEstado = !producto.activo();
                }
                return coincideTexto && coincideEstado;
            });
        };
        txtBuscar.textProperty().addListener((observable, viejoValor, nuevoValor) -> aplicarFiltros.run());
        grupoFiltroEstado.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                grupoFiltroEstado.selectToggle(oldVal);
            } else {
                aplicarFiltros.run();
            }
        });
        SortedList<ProductoResumenDTO> listaOrdenada = new SortedList<>(listaFiltrada);
        listaOrdenada.comparatorProperty().bind(tablaProductos.comparatorProperty());
        tablaProductos.setItems(listaOrdenada);
    }


    @FXML
    void abrirSelectorNuevoProducto(ActionEvent event) {
        String rutaFxml = RutasVista.CREAR_PRODUCTO_VIEW;
        try {
            FXMLLoader loader = CargadorVistas.obtenerLoaderConfigurado(rutaFxml);
            Parent root = loader.load();
            CrearProductoControlador controlador = loader.getController();
            controlador.recibirIdInventario(this.idInventario);
            Stage modalStage = new Stage();
            modalStage.setTitle("Crear Nuevo Producto");
            modalStage.setScene(new Scene(root));
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setResizable(false);
            Stage ventanaPadre = (Stage) ((Node) event.getSource()).getScene().getWindow();
            modalStage.initOwner(ventanaPadre);
            modalStage.showAndWait();
            cargarDatosTabla();
        } catch (IOException e) {
            throw new CargarVistaException(rutaFxml, "No se pudo cargar el archivo FXML.", e);
        }
    }


    @FXML
    void cambiarEstadoProducto(ActionEvent event) {
        cambiarEstadoProducto();
    }

    private void cambiarEstadoProducto(){
        ProductoResumenDTO seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            GestorAlertas.mostrarAlertaWarning(
                    "Atención", null,
                    "Por favor, Selecciona un Producto para Cambiar su Estado."
            );
            return;
        }
        String textoNuevoEstado = seleccionado.activo() ? "NO DISPONIBLE" : "DISPONIBLE";
        if (!GestorAlertas.mostrarConfirmacion(
                "Confirmar cambio de activo",
                "Vas a modificar el producto: " + seleccionado.nombre(),
                "¿Estás Seguro de que Deseas Marcar este Producto como " + textoNuevoEstado + "?"
        )){
            return;
        }
        CompletableFuture.runAsync(()->
                this.orquestadorProductos.cambiarEstadoProducto(this.idInventario, seleccionado.codigoProducto())
        ).thenRun(()->
            Platform.runLater(()->{
                ProductoResumenDTO actualizado = new ProductoResumenDTO(
                        seleccionado.codigoProducto(),
                        seleccionado.nombre(),
                        seleccionado.valorVenta(),
                        seleccionado.stock(),
                        !seleccionado.activo()
                );
                int indice = listaObservable.indexOf(seleccionado);
                listaObservable.set(indice, actualizado);
                GestorAlertas.mostrarAlertaInformacion(
                        "Éxito", null,
                        "El Estado del Producto - " + seleccionado.nombre() + " - ha sido Actualizado con Éxito."
                );
            })
        ).exceptionally(ex->{
            Platform.runLater(()->{
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                GestorAlertas.mostrarAlertaError(
                        "NO se pudo Completar la Acción", null,
                        "Error:  " + causa.getMessage()
                );
            });
            return null;
        });
    }


    @FXML
    public void abrirManejarStock(ActionEvent event) {
        ProductoResumenDTO productoSeleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (productoSeleccionado == null) {
            GestorAlertas.mostrarAlertaWarning(
                    "Atención", null,
                    "Seleccione un Producto Primero."
            );
            return;
        }
        String rutaFxml = RutasVista.MANEJAR_STOCK_PRODUCTO_VIEW;
        try {
            FXMLLoader loader = CargadorVistas.obtenerLoaderConfigurado(rutaFxml);
            Parent root = loader.load();
            ManejarStockControlador controlador = loader.getController();
            controlador.cargarDatos(productoSeleccionado, this.idInventario, listaObservable);
            Stage modalStage = new Stage();
            modalStage.setTitle("Manejar Stock Producto");
            modalStage.setScene(new Scene(root));
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setResizable(false);
            Stage ventanaPadre = (Stage) ((Node) event.getSource()).getScene().getWindow();
            modalStage.initOwner(ventanaPadre);
            modalStage.showAndWait();
            cargarDatosTabla();
        } catch (IOException e) {
            throw new CargarVistaException(rutaFxml, "NO se pudo Cargar el Archivo FXML.", e);
        }
    }


    @FXML
    public void abrirMoverAOtroInventario(ActionEvent event) {
        ProductoResumenDTO seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            GestorAlertas.mostrarAlertaWarning(
                    "Atención", null,
                    "Seleccione un Producto de la tabla para moverlo."
            );
            return;
        }
        String rutaFxml = RutasVista.MOVER_PRODUCTO_INVENTARIO_VIEW;
        try {
            FXMLLoader loader = CargadorVistas.obtenerLoaderConfigurado(rutaFxml);
            Parent root = loader.load();
            MoverProductoAOtroInventarioControlador controlador = loader.getController();
            controlador.cargarDatos(this.idInventario, seleccionado, listaObservable);
            Stage modalStage = new Stage();
            modalStage.setTitle("Mover Producto");
            modalStage.setScene(new Scene(root));
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setResizable(false);
            Stage ventanaPadre = (Stage) ((Node) event.getSource()).getScene().getWindow();
            modalStage.initOwner(ventanaPadre);
            modalStage.showAndWait();
            cargarDatosTabla();
        } catch (IOException e) {
            throw new CargarVistaException(rutaFxml, "NO se pudo Cargar el Archivo FXML.", e);
        }
    }


}//===================================================================================================================//


package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarInventarios.gestionarProductos.tabGeneral;

import RetailManagementSystem.aplicacion.dto.gestion.InventarioDTO;
import RetailManagementSystem.aplicacion.dto.ventas.ProductoResumenDTO;
import RetailManagementSystem.dominio.excepciones.*;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorInventarioProducto;
import RetailManagementSystem.aplicacion.servicios.ServicioInventario;
import RetailManagementSystem.aplicacion.servicios.ServicioProductos;
import RetailManagementSystem.aplicacion.ensambladores.EnsambladorDTOInventario;
import RetailManagementSystem.aplicacion.ensambladores.EnsambladorDTOProducto;
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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
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

    private final ServicioProductos servicioProductos;
    private final ServicioInventario servicioInventario;

    private final EnsambladorDTOProducto ensambladorDTOProducto;
    private final EnsambladorDTOInventario ensambladorDTOInventario;

    private final OrquestadorInventarioProducto orquestadorInventarioProducto;

    private final ObservableList<ProductoResumenDTO> listaObservable = FXCollections.observableArrayList();

    private FilteredList<ProductoResumenDTO> listaFiltrada;

    //CONSTRUCTOR:

    public TabGeneralProductosControlador(
            ServicioProductos servicioProductos, ServicioInventario servicioInventario,
            EnsambladorDTOProducto ensambladorDTOProducto, EnsambladorDTOInventario ensambladorDTOInventario
    ) {
        this.servicioProductos = servicioProductos;
        this.servicioInventario = servicioInventario;
        this.ensambladorDTOProducto = ensambladorDTOProducto;
        this.ensambladorDTOInventario = ensambladorDTOInventario;
        this.orquestadorInventarioProducto = new OrquestadorInventarioProducto(
                servicioProductos, servicioInventario, ensambladorDTOProducto, ensambladorDTOInventario
        );
    }

    //MÉTODOS:

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        DialogPane pane = alerta.getDialogPane();
        pane.setMinHeight(Region.USE_PREF_SIZE);
        URL urlCss = getClass().getResource(RutasVista.ESTILOS_CSS_PRODUCTOS);
        if (urlCss != null) {
            pane.getStylesheets().add(urlCss.toExternalForm());
        }
        alerta.showAndWait();
    }

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
                this.ensambladorDTOProducto.ensamblarDetalleProductosResumen(
                this.servicioProductos.obtenerProductosDeInventario(this.idInventario), LocalDate.now()
                )
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
                this.servicioProductos.cambiarEstadoProducto(this.idInventario, seleccionado.codigoProducto())
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
        moverAOtroInventario();
    }

    private void moverAOtroInventario(){
        ProductoResumenDTO productoSeleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (productoSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención",
                    "Seleccione un Producto de la tabla para moverlo.");
            return;
        }
        Dialog<ButtonType> dialog = new Dialog<>();
        URL urlCss = getClass().getResource(RutasVista.ESTILOS_CSS_PRODUCTOS);
        if (urlCss != null) {
            dialog.getDialogPane().getStylesheets().add(urlCss.toExternalForm());
        }
        dialog.setTitle("Mover Producto de Inventario");
        dialog.setHeaderText("Mover: " + productoSeleccionado.nombre());
        Label lblInfo = new Label("Se moverá la referencia completa y sus " + productoSeleccionado.stock() + " unidades disponibles.");
        lblInfo.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
        Label lblDestino = new Label("Seleccione el Inventario de Destino:");
        ComboBox<InventarioDTO> comboInventarios = new ComboBox<>();
        comboInventarios.setPromptText("Elegir Inventario...");
        comboInventarios.getStyleClass().add("combo-box-personalizado");
        comboInventarios.setPrefWidth(250);
        try {
            List<InventarioDTO> lista = this.ensambladorDTOInventario.ensamblarDetalleInventarioGeneral(
                    servicioInventario.obtenerTodosLosInventarios()
            );
            for (InventarioDTO inventarioDTO:lista){
                if (inventarioDTO.idInventario() == this.idInventario){
                    lista.remove(inventarioDTO);
                    break;
                }
            }
            comboInventarios.getItems().addAll(lista);
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "NO se Pudieron Cargar los Inventarios.");
            return;
        }
        comboInventarios.setConverter(new StringConverter<InventarioDTO>() {
            @Override
            public String toString(InventarioDTO inv) { return inv != null ? inv.nombre() : ""; }
            @Override
            public InventarioDTO fromString(String string) { return null; }
        });
        VBox contenido = new VBox(15, lblInfo, lblDestino, comboInventarios);
        contenido.setPadding(new Insets(20));
        dialog.getDialogPane().setContent(contenido);
        ButtonType btnMover = new ButtonType("Mover Producto", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnMover, ButtonType.CANCEL);
        Button botonFisicoMover = (Button) dialog.getDialogPane().lookupButton(btnMover);
        botonFisicoMover.setStyle("-fx-background-color: #ef4444; -fx-border-color: #b91c1c;");
        botonFisicoMover.addEventFilter(ActionEvent.ACTION, evt -> {
            if (comboInventarios.getValue() == null) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Validación",
                        "Debe seleccionar un Inventario de destino.");
                evt.consume();
            }
        });
        dialog.showAndWait().ifPresent(resultado -> {
            if (resultado.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                InventarioDTO inventarioDestino = comboInventarios.getValue();
                try {
                    this.orquestadorInventarioProducto.validarEspacioInventarioYMoverProducto(
                            this.idInventario, inventarioDestino.idInventario(),
                            productoSeleccionado.codigoProducto(), productoSeleccionado.stock()
                    );
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                            "El Producto ha sido Movido Exitosamente al Inventario: " + inventarioDestino.nombre());
                    cargarDatosTabla();
                } catch (IllegalArgumentException e) {
                    mostrarAlerta(Alert.AlertType.WARNING, "NO se pudo Completar la Accion",
                            "Error:  " + e.getMessage());
                } catch (InventarioNoEncontradoException e) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Inventario NO Encontrado",
                            "Error:  " + e.getMessage());
                } catch (ProductoNoEncontradoException e) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Producto NO Encontrado",
                            "Error:  " + e.getMessage());
                } catch (CapacidadInventarioExcedidaException e) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Capacidad Excedida",
                            "El Inventario -" + inventarioDestino.nombre() + "- NO puede recibir esa Cantidad.\n" +
                                    e.getMessage());
                }
            }
        });
    }


}//===================================================================================================================//


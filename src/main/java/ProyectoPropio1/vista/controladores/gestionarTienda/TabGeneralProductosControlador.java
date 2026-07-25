package ProyectoPropio1.vista.controladores.gestionarTienda;

import ProyectoPropio1.dto.InventarioDTO;
import ProyectoPropio1.dto.ProductoResumenDTO;
import ProyectoPropio1.excepciones.CapacidadInventarioExcedidaException;
import ProyectoPropio1.servicios.aplicacion.servicios.ServicioInventario;
import ProyectoPropio1.servicios.aplicacion.servicios.ServicioProductos;
import ProyectoPropio1.servicios.aplicacion.ensambladores.EnsambladorDTOInventario;
import ProyectoPropio1.servicios.aplicacion.ensambladores.EnsambladorDTOProducto;
import ProyectoPropio1.utilidades.CargadorVistas;
import ProyectoPropio1.utilidades.FormateadorNumeros;
import ProyectoPropio1.utilidades.RutasVista;

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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;

public class TabGeneralProductosControlador {

    //ATRIBUTOS:

    @FXML private TableView<ProductoResumenDTO> tablaProductos;
    @FXML private TableColumn<ProductoResumenDTO, String> colCodigo;
    @FXML private TableColumn<ProductoResumenDTO, String> colNombre;
    @FXML private TableColumn<ProductoResumenDTO, BigDecimal> colValor;
    @FXML private TableColumn<ProductoResumenDTO, Integer> colStock;
    @FXML private TableColumn<ProductoResumenDTO, Boolean> colEstado;
    @FXML private TextField txtBuscar;
    @FXML private ToggleGroup grupoFiltroEstado;
    @FXML private ToggleButton btnFiltroTodos;
    @FXML private ToggleButton btnFiltroDisponibles;
    @FXML private ToggleButton btnFiltroNoDisponibles;

    private int idInventario;

    private final ServicioInventario servicioInventario;
    private final ServicioProductos servicioProductos;

    private final EnsambladorDTOProducto ensambladorDTOProducto;
    private final EnsambladorDTOInventario ensambladorDTOInventario;

    private final ObservableList<ProductoResumenDTO> listaObservable = FXCollections.observableArrayList();

    private FilteredList<ProductoResumenDTO> listaFiltrada;

    //CONSTRUCTOR:

    public TabGeneralProductosControlador(
            ServicioInventario servicioInventario, ServicioProductos servicioProductos,
            EnsambladorDTOProducto ensambladorDTOProducto, EnsambladorDTOInventario ensambladorDTOInventario
    ) {
        this.servicioInventario = servicioInventario;
        this.servicioProductos = servicioProductos;
        this.ensambladorDTOProducto = ensambladorDTOProducto;
        this.ensambladorDTOInventario = ensambladorDTOInventario;
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
        this.idInventario = idInventario;
        cargarDatosTabla();
    }


    @FXML
    public void initialize() {
        configurarColumnas();
        configurarFiltros();
        cargarDatosTabla();
    }

    private void cargarDatosTabla() {
        List<ProductoResumenDTO> resumenProductos = this.ensambladorDTOProducto.ensamblarDetalleProductosResumen(
                this.servicioProductos.obtenerProductosDeInventario(this.idInventario), LocalDate.now()
        );
        listaObservable.clear();
        listaObservable.addAll(resumenProductos);
    }

    private void configurarColumnas() {
        colCodigo.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().codigoProducto()));
        colCodigo.setCellFactory(columna -> new TableCell<>() {
            private final Tooltip tooltipFlotante = new Tooltip();
            {
                tooltipFlotante.setStyle("-fx-background-color: #1e293b; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 5px 10px;");
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
                    setStyle("-fx-cursor: hand;");
                    setOnMouseClicked(evt -> {
                        ClipboardContent contenido = new ClipboardContent();
                        contenido.putString(codigo);
                        Clipboard.getSystemClipboard().setContent(contenido);
                    });
                }
            }
        });
        colNombre.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().nombre()));
        colStock.setCellValueFactory(celda -> new SimpleIntegerProperty(celda.getValue().stock()).asObject());
        colStock.setCellFactory(columna -> new TableCell<>() {
            {
                setAlignment(Pos.CENTER);
            }
            @Override
            protected void updateItem(Integer stock, boolean empty) {
                super.updateItem(stock, empty);
                if (empty || stock == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.valueOf(stock));
                    if (stock <= 0) {
                        setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                    } else if (stock <= 5) {
                        setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
                    }
                }
            }
        });
        colValor.setCellValueFactory(celda -> new SimpleObjectProperty<>(celda.getValue().valorVenta()));
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
        colEstado.setCellValueFactory(celda -> new SimpleBooleanProperty(celda.getValue().disponible()).asObject());
        colEstado.setCellFactory(col -> new TableCell<>() {
            {
                setAlignment(Pos.CENTER);
            }
            @Override
            protected void updateItem(Boolean disponible, boolean empty) {
                super.updateItem(disponible, empty);
                if (empty || disponible == null) {
                    setText(null);
                    setStyle("");
                } else {
                    if (disponible) {
                        setText("Disponible");
                        setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
                    } else {
                        setText("No Disponible");
                        setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                    }
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
                    coincideEstado = producto.disponible();
                } else if (btnSeleccionado == btnFiltroNoDisponibles) {
                    coincideEstado = !producto.disponible();
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
        try {
            FXMLLoader loader = CargadorVistas.obtenerLoaderConfigurado(RutasVista.CREAR_PRODUCTO_VIEW);
            Parent root = loader.load();
            CrearProductoControlador controlador = loader.getController();
            controlador.recibirIdInventario(this.idInventario);
            Stage modalStage = new Stage();
            modalStage.setTitle("Crear Nuevo Producto");
            modalStage.setScene(new Scene(root));
            modalStage.initModality(Modality.WINDOW_MODAL);
            Stage ventanaPadre = (Stage) ((Node) event.getSource()).getScene().getWindow();
            modalStage.initOwner(ventanaPadre);
            modalStage.showAndWait();
            cargarDatosTabla();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "NO se pudo Abrir la Ventana de Creación de Producto.");
        }
    }


    @FXML
    void cambiarEstadoProducto(ActionEvent event) {
        cambiarEstadoProducto();
    }

    private void cambiarEstadoProducto(){
        ProductoResumenDTO seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención",
                    "Por favor, Selecciona un Producto para Cambiar su Estado.");
            return;
        }
        String textoNuevoEstado = seleccionado.disponible() ? "NO DISPONIBLE" : "DISPONIBLE";
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar cambio de estado");
        confirmacion.setHeaderText("Vas a modificar el producto: " + seleccionado.nombre());
        confirmacion.setContentText("¿Estás Seguro de que Deseas Marcar este Producto como " + textoNuevoEstado + "?");
        DialogPane pane = confirmacion.getDialogPane();
        pane.setMinHeight(Region.USE_PREF_SIZE);
        URL urlCss = getClass().getResource(RutasVista.ESTILOS_CSS_PRODUCTOS);
        if (urlCss != null) {
            pane.getStylesheets().add(urlCss.toExternalForm());
        }
        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                try {
                    this.servicioProductos.cambiarEstadoProducto(this.idInventario, seleccionado.codigoProducto());
                    cargarDatosTabla();
                } catch (Exception e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error",
                            "NO se pudo Cambiar el Estado: " + e.getMessage());
                }
            }
        });
    }


    @FXML
    public void abrirManejarStock(ActionEvent event) {
        abrirManejarStock();
    }

    private void abrirManejarStock(){
        ProductoResumenDTO productoSeleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (productoSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención",
                    "Seleccione un Producto Primero.");
            return;
        }
        Dialog<ButtonType> dialog = new Dialog<>();
        URL urlCss = getClass().getResource(RutasVista.ESTILOS_CSS_PRODUCTOS);
        if (urlCss != null) {
            dialog.getDialogPane().getStylesheets().add(urlCss.toExternalForm());
        }
        dialog.setTitle("Ajustar Stock - " + productoSeleccionado.nombre());
        dialog.setHeaderText("Stock Actual: " + productoSeleccionado.stock() + " Unidades");
        ToggleGroup grupoAccion = new ToggleGroup();
        ToggleButton btnReponer = new ToggleButton("⬆ Reponer (Entrada)");
        btnReponer.getStyleClass().addAll("toggle-stock", "toggle-reponer");
        btnReponer.setToggleGroup(grupoAccion);
        btnReponer.setSelected(true);
        ToggleButton btnRetirar = new ToggleButton("⬇ Retirar (Salida)");
        btnRetirar.getStyleClass().addAll("toggle-stock", "toggle-retirar");
        btnRetirar.setToggleGroup(grupoAccion);
        HBox boxBotones = new HBox(0, btnReponer, btnRetirar);
        boxBotones.setAlignment(Pos.CENTER);
        TextField txtCantidad = new TextField();
        txtCantidad.setPromptText("Cantidad a ajustar...");
        txtCantidad.getStyleClass().add("input-cantidad");
        Label lblVistaPrevia = new Label("Nuevo Stock estimado: " + productoSeleccionado.stock());
        lblVistaPrevia.getStyleClass().add("lbl-vista-previa");
        Runnable actualizarVistaPrevia = () -> {
            try {
                int cantidad = txtCantidad.getText().isBlank() ? 0 : Integer.parseInt(txtCantidad.getText().trim());
                if (cantidad < 0) {
                    throw new NumberFormatException();
                }
                int nuevoStock = btnReponer.isSelected()
                        ? productoSeleccionado.stock() + cantidad
                        : productoSeleccionado.stock() - cantidad;
                lblVistaPrevia.setText("Nuevo Stock estimado: " + nuevoStock);
                if (nuevoStock < 0) {
                    lblVistaPrevia.getStyleClass().setAll("lbl-vista-previa-error");
                    lblVistaPrevia.setText("Error: El stock no puede ser negativo (" + nuevoStock + ")");
                } else {
                    lblVistaPrevia.getStyleClass().setAll("lbl-vista-previa");
                }
            } catch (NumberFormatException ex) {
                lblVistaPrevia.setText("Ingrese una cantidad válida");
                lblVistaPrevia.getStyleClass().setAll("lbl-vista-previa");
            }
        };
        txtCantidad.textProperty().addListener((obs, old, newValue) -> actualizarVistaPrevia.run());
        grupoAccion.selectedToggleProperty().addListener((obs, old, newValue) -> actualizarVistaPrevia.run());
        VBox contenido = new VBox(15, new Label("Seleccione la acción:"), boxBotones, new Label("Cantidad a ajustar:"), txtCantidad, lblVistaPrevia);
        contenido.setPadding(new Insets(20));
        dialog.getDialogPane().setContent(contenido);
        ButtonType btnGuardar = new ButtonType("Guardar Cambios", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);
        ButtonType btnTipoGuardar = dialog.getDialogPane().getButtonTypes().stream()
                .filter(b -> b.getButtonData() == ButtonBar.ButtonData.OK_DONE)
                .findFirst().orElse(null);
        if (btnTipoGuardar != null) {
            Button botonFisicoGuardar = (Button) dialog.getDialogPane().lookupButton(btnTipoGuardar);
            botonFisicoGuardar.addEventFilter(ActionEvent.ACTION, evt -> {
                String textoCantidad = txtCantidad.getText().trim();
                if (textoCantidad.isEmpty()) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error de Validación",
                            "La Cantidad NO puede estar vacía.");
                    evt.consume();
                    return;
                }
                try {
                    int cantidad = Integer.parseInt(textoCantidad);
                    if (cantidad <= 0) {
                        mostrarAlerta(Alert.AlertType.ERROR, "Error de Validación",
                                "La Cantidad debe ser Mayor a Cero.");
                        evt.consume();
                        return;
                    }
                    if (btnRetirar.isSelected()) {
                        int stockResultante = productoSeleccionado.stock() - cantidad;
                        if (stockResultante < 0) {
                            mostrarAlerta(Alert.AlertType.ERROR, "Error de Operación",
                                    "NO puedes Retirar más Unidades de las Disponibles.\n" +
                                            "\nStock actual: " + productoSeleccionado.stock());
                            evt.consume();
                        }
                    }
                } catch (NumberFormatException e) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Número Inválido",
                            "Por favor Ingresa un Número Entero Válido.");
                    evt.consume();
                }
            });
        }
        dialog.showAndWait().ifPresent(resultado -> {
            if (resultado.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                int cantidadFinal = Integer.parseInt(txtCantidad.getText().trim());
                boolean esRepocision = btnReponer.isSelected();
                try {
                    if (esRepocision) {
                        this.servicioInventario.verificarEspacioDisponible(this.idInventario, cantidadFinal);
                        this.servicioProductos.aumentarStockDeProductoDeInventario(
                                this.idInventario, productoSeleccionado.codigoProducto(), cantidadFinal
                        );
                    } else {
                        this.servicioProductos.reducirStockDeProductoDeInventario(
                                this.idInventario, productoSeleccionado.codigoProducto(), cantidadFinal);
                    }
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                            "El Stock se ha Actualizado Correctamente.");
                    cargarDatosTabla();
                } catch (CapacidadInventarioExcedidaException e) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Capacidad Excedida",
                            "Error:  " + e.getMessage());
                } catch (Exception e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error Crítico",
                            "NO se pudo Actualizar el Stock en la Base de Datos.\n" +
                                    "Error: " + e.getMessage());
                }
            }
        });
    }


    @FXML
    public void abrirMoverAOtroInventario(ActionEvent event) {
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
        Label lblInfo = new Label("Se moverá la referencia completa y sus "
                + productoSeleccionado.stock() + " unidades disponibles.");
        lblInfo.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
        Label lblDestino = new Label("Seleccione el Inventario de destino:");
        ComboBox<InventarioDTO> comboInventarios = new ComboBox<>();
        comboInventarios.setPromptText("Elegir inventario...");
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
                    this.servicioInventario.verificarEspacioDisponible(
                            inventarioDestino.idInventario(), productoSeleccionado.stock()
                    );
                    this.servicioProductos.moverProductoAInventario(
                            this.idInventario, inventarioDestino.idInventario(), productoSeleccionado.codigoProducto()
                    );
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                            "El Producto ha sido Movido Exitosamente al Inventario: " + inventarioDestino.nombre()); // Ajusta según tu DTO
                    cargarDatosTabla();
                } catch (CapacidadInventarioExcedidaException e) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Capacidad Excedida",
                            "El Inventario -" + inventarioDestino.nombre() + "- NO puede recibir esa Cantidad.\n" +
                                    e.getMessage());
                } catch (Exception e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error Crítico",
                            "NO se pudo Mover el Producto.\n" + e.getMessage());
                }
            }
        });
    }


}//===================================================================================================================//


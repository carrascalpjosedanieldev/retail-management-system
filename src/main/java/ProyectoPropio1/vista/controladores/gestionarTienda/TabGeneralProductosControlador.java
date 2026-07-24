package ProyectoPropio1.vista.controladores.gestionarTienda;

import ProyectoPropio1.dto.ProductoResumenDTO;
import ProyectoPropio1.servicios.aplicacion.ServicioProductos;
import ProyectoPropio1.servicios.ensambladores.EnsambladorDTOProducto;
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
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

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

    private final ServicioProductos servicioProductos;

    private final EnsambladorDTOProducto ensambladorDTOProducto;

    private final ObservableList<ProductoResumenDTO> listaObservable = FXCollections.observableArrayList();

    private FilteredList<ProductoResumenDTO> listaFiltrada;

    //CONSTRUCTOR:

    public TabGeneralProductosControlador(ServicioProductos servicioProductos,
                                          EnsambladorDTOProducto ensambladorDTOProducto) {
        this.servicioProductos = servicioProductos;
        this.ensambladorDTOProducto = ensambladorDTOProducto;
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


}//===================================================================================================================//


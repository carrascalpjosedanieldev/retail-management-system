package ProyectoPropio1.vista.controladores.gestionarTienda;

import ProyectoPropio1.dominio.enums.Talla;
import ProyectoPropio1.dto.DatosTotalesProductoRopaDTO;
import ProyectoPropio1.dto.DescuentoDTO;
import ProyectoPropio1.dto.ImpuestoDTO;
import ProyectoPropio1.servicios.aplicacion.ServicioProductos;
import ProyectoPropio1.servicios.ensambladores.EnsambladorDTOProducto;
import ProyectoPropio1.utilidades.CargadorVistas;
import ProyectoPropio1.utilidades.FormateadorNumeros;
import ProyectoPropio1.utilidades.RutasVista;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
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

public class TabRopaControlador {

    //ATRIBUTOS:

    @FXML private TextField txtBuscar;
    @FXML private TableView<DatosTotalesProductoRopaDTO> tablaRopa;
    @FXML private TableColumn<DatosTotalesProductoRopaDTO, String> colCodigo;
    @FXML private TableColumn<DatosTotalesProductoRopaDTO, String> colNombre;
    @FXML private TableColumn<DatosTotalesProductoRopaDTO, Talla> colTalla;
    @FXML private TableColumn<DatosTotalesProductoRopaDTO, BigDecimal> colCompra;
    @FXML private TableColumn<DatosTotalesProductoRopaDTO, BigDecimal> colGanancia;
    @FXML private TableColumn<DatosTotalesProductoRopaDTO, ImpuestoDTO> colImpuesto;
    @FXML private TableColumn<DatosTotalesProductoRopaDTO, DescuentoDTO> colDescuento;
    @FXML private TableColumn<DatosTotalesProductoRopaDTO, BigDecimal> colVentaFinal;
    @FXML private TableColumn<DatosTotalesProductoRopaDTO, Integer> colStock;
    @FXML private TableColumn<DatosTotalesProductoRopaDTO, String> colEstado;

    private int idInventario;

    private final ServicioProductos servicioProductos;

    private final EnsambladorDTOProducto ensambladorDTOProducto;

    private final ObservableList<DatosTotalesProductoRopaDTO> listaObservable = FXCollections.observableArrayList();

    private FilteredList<DatosTotalesProductoRopaDTO> listaFiltrada;

    //CONSTRUCTOR:

    public TabRopaControlador(ServicioProductos servicioProductos, EnsambladorDTOProducto ensambladorDTOProducto) {
        this.servicioProductos = servicioProductos;
        this.ensambladorDTOProducto = ensambladorDTOProducto;
    }

    //MÉTODOS:

    public void recibirIdInventario(int idInventario) {
        this.idInventario = idInventario;
        cargarDatosTabla();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        DialogPane pane = alerta.getDialogPane();
        pane.setMinHeight(Region.USE_PREF_SIZE);
        URL urlCss = getClass().getResource(RutasVista.ESTILOS_CSS_EDITAR_ROPA);
        if (urlCss != null) {
            pane.getStylesheets().add(urlCss.toExternalForm());
        }
        alerta.showAndWait();
    }

    private void cargarDatosTabla() {
        try {
            LocalDate fechaActual = LocalDate.now();
            List<DatosTotalesProductoRopaDTO> listaRopa = this.ensambladorDTOProducto.ensamblarDetalleProductosRopa(
                    this.servicioProductos.obtenerProductosRopaDeInventario(this.idInventario), fechaActual
            );
            listaObservable.clear();
            listaObservable.setAll(listaRopa);
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al Cargar Datos",
                    "NO se pudo Cargar la Lista de Ropa.\n" +
                            "Detalle: " + e.getMessage()
            );
        }
    }


    @FXML
    public void initialize() {
        configurarColumnas();
        configurarFiltro();
    }

    private void configurarColumnas() {
        colCodigo.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().codigo()));
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
                    String codigoCorto = codigo.length() > 10 ? codigo.substring(0, 10) + "..." : codigo;
                    setText(codigoCorto);
                    tooltipFlotante.setText(codigo + "\n(Clic para copiar)");
                    setTooltip(tooltipFlotante);
                    setStyle("-fx-cursor: hand; -fx-text-fill: #3b82f6;");
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
        colEstado.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().disponible()));
        colTalla.setCellValueFactory(celda -> new SimpleObjectProperty<>(celda.getValue().talla()));
        colTalla.setCellFactory(col -> new TableCell<>() {
            {
                setAlignment(Pos.CENTER);
            }
            @Override
            protected void updateItem(Talla talla, boolean empty) {
                super.updateItem(talla, empty);
                setText((empty || talla == null) ? null : talla.name());
            }
        });
        colCompra.setCellValueFactory(celda -> new SimpleObjectProperty<>(celda.getValue().valorCompra()));
        colCompra.setCellFactory(col -> crearCeldaMoneda());
        colVentaFinal.setCellValueFactory(celda -> new SimpleObjectProperty<>(celda.getValue().valorVentaFinal()));
        colVentaFinal.setCellFactory(col -> crearCeldaMoneda());
        colGanancia.setCellValueFactory(celda -> new SimpleObjectProperty<>(celda.getValue().porcentajeGanancia()));
        colGanancia.setCellFactory(col -> new TableCell<>() {
            {
                setAlignment(Pos.CENTER);
            }
            @Override
            protected void updateItem(BigDecimal ganancia, boolean empty) {
                super.updateItem(ganancia, empty);
                setText((empty || ganancia == null) ? null : ganancia.toPlainString() + "%");
            }
        });
        colImpuesto.setCellValueFactory(celda -> new SimpleObjectProperty<>(celda.getValue().datosImpuesto()));
        colImpuesto.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(ImpuestoDTO imp, boolean empty) {
                super.updateItem(imp, empty);
                setText((empty || imp == null) ? null : imp.nombre() + " (" + imp.porcentaje() + "%)");
            }
        });
        colDescuento.setCellValueFactory(celda -> new SimpleObjectProperty<>(celda.getValue().datosDescuento()));
        colDescuento.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(DescuentoDTO desc, boolean empty) {
                super.updateItem(desc, empty);
                setText((empty || desc == null) ? null : desc.nombre() + " (" + desc.porcentaje() + "%)");
            }
        });
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
        colEstado.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().disponible()));
        colEstado.setCellFactory(columna -> new TableCell<>() {
            {
                setAlignment(Pos.CENTER);
            }
            @Override
            protected void updateItem(String estado, boolean empty) {
                super.updateItem(estado, empty);
                if (empty || estado == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(estado);
                    setStyle(estado.equalsIgnoreCase("Activo") || estado.equalsIgnoreCase("Disponible")
                            ? "-fx-text-fill: #10b981; -fx-font-weight: bold;"
                            : "-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                }
            }
        });
    }

    private TableCell<DatosTotalesProductoRopaDTO, BigDecimal> crearCeldaMoneda() {
        return new TableCell<>() {
            {
                setAlignment(Pos.CENTER);
            }
            @Override
            protected void updateItem(BigDecimal precio, boolean empty) {
                super.updateItem(precio, empty);
                setText((empty || precio == null) ? null : FormateadorNumeros.formatoMoneda(precio));
            }
        };
    }

    private void configurarFiltro() {
        listaFiltrada = new FilteredList<>(listaObservable, b -> true);
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> {
            listaFiltrada.setPredicate(ropa -> {
                if (newVal == null || newVal.isBlank()) return true;
                String filtro = newVal.toLowerCase().trim();
                return ropa.nombre().toLowerCase().contains(filtro) ||
                        ropa.codigo().toLowerCase().contains(filtro) ||
                        ropa.talla().name().toLowerCase().contains(filtro) ||
                        ropa.disponible().toLowerCase().contains(filtro);
            });
        });
        SortedList<DatosTotalesProductoRopaDTO> listaOrdenada = new SortedList<>(listaFiltrada);
        listaOrdenada.comparatorProperty().bind(tablaRopa.comparatorProperty());
        tablaRopa.setItems(listaOrdenada);
    }


    @FXML
    void abrirEditorRopa(ActionEvent event) {
        DatosTotalesProductoRopaDTO productoSeleccionado = tablaRopa.getSelectionModel().getSelectedItem();
        if (productoSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección requerida",
                    "Por favor, Seleccione una Prenda de Ropa en la Tabla para Editarla.");
            return;
        }
        try {
            FXMLLoader loader = CargadorVistas.obtenerLoaderConfigurado(RutasVista.EDITAR_ROPA_VIEW);
            Parent root = loader.load();
            EditarRopaControlador controladorEditor = loader.getController();
            controladorEditor.cargarDatosProducto(productoSeleccionado, this.idInventario);
            Stage stageEditor = new Stage();
            stageEditor.setScene(new Scene(root));
            stageEditor.setTitle("Editar Prenda de Ropa");
            stageEditor.initModality(Modality.WINDOW_MODAL);
            Stage ventanaPadre = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stageEditor.initOwner(ventanaPadre);
            stageEditor.setResizable(false);
            stageEditor.showAndWait();
            cargarDatosTabla();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Interfaz",
                    "NO se pudo Abrir la Ventana de Edición.\nError: " + e.getMessage());
        }
    }


    @FXML
    void cambiarEstadoProducto(ActionEvent event) {
        cambiarEstadoProducto();
    }

    private void cambiarEstadoProducto(){
        DatosTotalesProductoRopaDTO productoSeleccionado = tablaRopa.getSelectionModel().getSelectedItem();
        if (productoSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección Requerida",
                    "Por favor, Seleccione una Prenda de Ropa en la Tabla para Cambiar su Estado.");
            return;
        }
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Cambio de Estado");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Está Seguro que desea Cambiar el Estado del Producto:\n"
                + productoSeleccionado.codigo() + " - " + productoSeleccionado.nombre() + "?");
        DialogPane pane = confirmacion.getDialogPane();
        pane.setMinHeight(Region.USE_PREF_SIZE);
        URL urlCss = getClass().getResource(RutasVista.ESTILOS_CSS_EDITAR_ROPA);
        if (urlCss != null) {
            pane.getStylesheets().add(urlCss.toExternalForm());
        }
        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                try {
                    this.servicioProductos.cambiarEstadoProducto(
                            this.idInventario,
                            productoSeleccionado.codigo()
                    );
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Estado Actualizado",
                            "El Estado del Producto se Actualizó Correctamente.");
                    cargarDatosTabla();
                } catch (Exception e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error en la Actualización",
                            "Ocurrió un Error al Intentar cambiar el Estado del Producto.\n" +
                                    "Error: " + e.getMessage());
                }
            }
        });
    }


}//===================================================================================================================//


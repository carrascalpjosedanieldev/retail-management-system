package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarInventarios.gestionarProductos;

import RetailManagementSystem.dominio.enums.Talla;
import RetailManagementSystem.aplicacion.dto.comercial.DatosTotalesProductoRopaDTO;
import RetailManagementSystem.aplicacion.dto.gestion.DescuentoDTO;
import RetailManagementSystem.aplicacion.dto.gestion.ImpuestoDTO;
import RetailManagementSystem.aplicacion.servicios.ServicioProductos;
import RetailManagementSystem.aplicacion.ensambladores.EnsambladorDTOProducto;
import RetailManagementSystem.vista.excepciones.CargarVistaException;
import RetailManagementSystem.vista.utilidades.CargadorVistas;
import RetailManagementSystem.vista.utilidades.FormateadorNumeros;
import RetailManagementSystem.vista.utilidades.GestorAlertas;
import RetailManagementSystem.vista.utilidades.RutasVista;

import javafx.application.Platform;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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


    private void cargarDatosTabla() {
        try {
            LocalDate fechaActual = LocalDate.now();
            List<DatosTotalesProductoRopaDTO> listaRopa = this.ensambladorDTOProducto.ensamblarDetalleProductosRopa(
                    this.servicioProductos.obtenerProductosRopaDeInventario(this.idInventario), fechaActual
            );
            listaObservable.clear();
            listaObservable.setAll(listaRopa);
        } catch (RuntimeException e) {
            GestorAlertas.mostrarAlertaError(
                    "Error Crítico de Carga",
                    "No se pudieron cargar los datos del inventario.",
                    "Ocurrió un error al cargar los productos ropa. La ventana se cerrará por seguridad.\nDetalle: " + e.getMessage()
            );
            if (tablaRopa != null && tablaRopa.getScene() != null) {
                Stage stageActual = (Stage) tablaRopa.getScene().getWindow();
                stageActual.close();
            }
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
        colNombre.setCellValueFactory(celda -> new SimpleStringProperty(
                celda.getValue().nombre())
        );
        colStock.setCellValueFactory(celda -> new SimpleIntegerProperty(
                celda.getValue().stock()).asObject()
        );
        colTalla.setCellValueFactory(celda -> new SimpleObjectProperty<>(
                celda.getValue().talla())
        );
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
        colEstado.setCellValueFactory(celda -> {
            boolean esActivo = celda.getValue().activo();
            String estado = esActivo ? "Activo" : "Inactivo";
            return new SimpleStringProperty(estado);
        });
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
                        ropa.talla().name().toLowerCase().contains(filtro);
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
            GestorAlertas.mostrarAlertaWarning(
                    "Selección requerida", null,
                    "Por favor, Seleccione una Prenda de Ropa en la Tabla para Editarla."
            );
            return;
        }
        String rutaFxml = RutasVista.EDITAR_ROPA_VIEW;
        try {
            FXMLLoader loader = CargadorVistas.obtenerLoaderConfigurado(rutaFxml);
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
        } catch (IOException | IllegalStateException e) {
            throw new CargarVistaException(rutaFxml, "No se pudo cargar el archivo FXML.", e);
        }
    }


    @FXML
    void cambiarEstadoProducto(ActionEvent event) {
        cambiarEstadoProducto();
    }

    private void cambiarEstadoProducto(){
        DatosTotalesProductoRopaDTO seleccionado = tablaRopa.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            GestorAlertas.mostrarAlertaWarning(
                    "Selección Requerida", null,
                    "Por favor, Seleccione una Prenda de Ropa en la Tabla para Cambiar su Estado."
            );
            return;
        }

        if (!GestorAlertas.mostrarConfirmacion(
                "Confirmar Cambio de Estado", null,
                "¿Está Seguro que desea Cambiar el Estado del Producto:\n"
                        + seleccionado.codigo() + " - " + seleccionado.nombre() + "?"
        )){
            return;
        }
        CompletableFuture.runAsync(()->
                this.servicioProductos.cambiarEstadoProducto(this.idInventario, seleccionado.codigo())
        ).thenRun(()->
            Platform.runLater(()->{
                DatosTotalesProductoRopaDTO actualizado = new DatosTotalesProductoRopaDTO(
                        seleccionado.codigo(),
                        seleccionado.nombre(),
                        seleccionado.valorCompra(),
                        seleccionado.porcentajeGanancia(),
                        seleccionado.valorVentaFinal(),
                        seleccionado.stock(),
                        seleccionado.datosImpuesto(),
                        seleccionado.datosDescuento(),
                        seleccionado.talla(),
                        !seleccionado.activo()
                );
                int indice = listaObservable.indexOf(seleccionado);
                listaObservable.set(indice, actualizado);
                GestorAlertas.mostrarAlertaInformacion(
                        "Estado Actualizado", null,
                        "El Estado del Producto se Actualizó Correctamente."
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


}//===================================================================================================================//


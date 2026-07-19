package ProyectoPropio1.vista.controladores;

import ProyectoPropio1.dominio.enums.Talla;
import ProyectoPropio1.dto.DatosTotalesProductoRopaDTO;
import ProyectoPropio1.dto.DescuentoDTO;
import ProyectoPropio1.dto.ImpuestoDTO;
import ProyectoPropio1.servicios.aplicacion.ServicioProductos;
import ProyectoPropio1.servicios.ensambladores.EnsambladorDTOProducto;
import ProyectoPropio1.utilidades.FabricaEnsambladores;
import ProyectoPropio1.utilidades.FabricaServicios;
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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class TabRopaControlador {

    @FXML
    private TextField txtBuscar;

    @FXML
    private TableView<DatosTotalesProductoRopaDTO> tablaRopa;

    @FXML
    private TableColumn<DatosTotalesProductoRopaDTO, String> colCodigo;

    @FXML
    private TableColumn<DatosTotalesProductoRopaDTO, String> colNombre;

    @FXML
    private TableColumn<DatosTotalesProductoRopaDTO, Talla> colTalla;

    @FXML
    private TableColumn<DatosTotalesProductoRopaDTO, BigDecimal> colCompra;

    @FXML
    private TableColumn<DatosTotalesProductoRopaDTO, BigDecimal> colGanancia;

    @FXML
    private TableColumn<DatosTotalesProductoRopaDTO, ImpuestoDTO> colImpuesto;

    @FXML
    private TableColumn<DatosTotalesProductoRopaDTO, DescuentoDTO> colDescuento;

    @FXML
    private TableColumn<DatosTotalesProductoRopaDTO, BigDecimal> colVentaFinal;

    @FXML
    private TableColumn<DatosTotalesProductoRopaDTO, Integer> colStock;

    @FXML
    private TableColumn<DatosTotalesProductoRopaDTO, String> colEstado;

    private int idInventario;

    private final ObservableList<DatosTotalesProductoRopaDTO> listaObservable = FXCollections.observableArrayList();

    private FilteredList<DatosTotalesProductoRopaDTO> listaFiltrada;

    private final NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(Locale.of("es", "CO"));

    private final ServicioProductos servicioProductos = FabricaServicios.obtenerServicioProductos();

    private final EnsambladorDTOProducto ensambladorDTOProducto = FabricaEnsambladores.obtenerEnsambladorDTOProducto();

    private LocalDate obtenerFecha(){
        return LocalDate.now();
    }

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
        pane.setMinHeight(180);
        pane.setMinWidth(400);
        try {
            alerta.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource(RutasVista.ESTILOS_CSS_PRODUCTOS)).toExternalForm());
        } catch (Exception ignored) {}
        alerta.showAndWait();
    }

    private void cargarDatosTabla() {
        try {
            List<DatosTotalesProductoRopaDTO> listaRopa = this.ensambladorDTOProducto.ensamblarDetalleProductosRopa(
                    this.servicioProductos.obtenerProductosRopaDeInventario(this.idInventario), obtenerFecha()
            );
            listaObservable.clear();
            listaObservable.setAll(listaRopa);
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al cargar datos",
                    "No se pudo cargar la lista de ropa.\nDetalle: " + e.getMessage()
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
        colCodigo.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String codigo, boolean empty) {
                super.updateItem(codigo, empty);

                if (empty || codigo == null) {
                    setText(null);
                    setTooltip(null);
                } else {
                    String codigoCorto = codigo.length() > 8 ? codigo.substring(0, 8) + "..." : codigo;
                    setText(codigoCorto);
                    Tooltip tooltipCompleto = new Tooltip(codigo);
                    tooltipCompleto.setStyle("-fx-font-size: 13px; -fx-background-color: #1e293b;");
                    setTooltip(tooltipCompleto);
                }
            }
        });
        colNombre.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().nombre()));
        colStock.setCellValueFactory(celda -> new SimpleIntegerProperty(celda.getValue().stock()).asObject());
        colEstado.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().disponible()));
        colTalla.setCellValueFactory(celda -> new SimpleObjectProperty<>(celda.getValue().talla()));
        colTalla.setCellFactory(col -> new TableCell<>() {
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
    }

    private TableCell<DatosTotalesProductoRopaDTO, BigDecimal> crearCeldaMoneda() {
        return new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal precio, boolean empty) {
                super.updateItem(precio, empty);
                setText((empty || precio == null) ? null : formatoMoneda.format(precio));
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
                    "Por favor, seleccione una prenda de ropa en la tabla para editarla.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(RutasVista.EDITAR_ROPA_VIEW));
            Parent root = loader.load();
            EditarRopaControlador controladorEditor = loader.getController();
            controladorEditor.cargarDatosProducto(productoSeleccionado, this.idInventario);
            Stage stageEditor = new Stage();
            stageEditor.setScene(new Scene(root));
            stageEditor.setTitle("Editar Prenda de Ropa");
            stageEditor.initModality(Modality.APPLICATION_MODAL);
            stageEditor.setResizable(false);
            stageEditor.showAndWait();
            cargarDatosTabla();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Interfaz",
                    "No se pudo abrir la ventana de edición.\nDetalle: " + e.getMessage());
        }
    }

    @FXML
    void cambiarEstadoProducto(ActionEvent event) {
        DatosTotalesProductoRopaDTO productoSeleccionado = tablaRopa.getSelectionModel().getSelectedItem();
        if (productoSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección requerida",
                    "Por favor, seleccione una prenda de ropa en la tabla para cambiar su estado.");
            return;
        }
        try {
            servicioProductos.cambiarEstadoProducto(this.idInventario, productoSeleccionado.codigo());
            cargarDatosTabla();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al cambiar estado",
                    "No se pudo actualizar el estado del producto.\nDetalle: " + e.getMessage());
        }
    }

}

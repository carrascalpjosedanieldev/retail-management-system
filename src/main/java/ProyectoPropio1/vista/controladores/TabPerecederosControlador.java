package ProyectoPropio1.vista.controladores;

import ProyectoPropio1.dto.DatosTotalesProductoPerecederoDTO;
import ProyectoPropio1.servicios.aplicacion.ServicioProductos;
import ProyectoPropio1.servicios.ensambladores.EnsambladorDTOProducto;
import ProyectoPropio1.utilidades.FabricaEnsambladores;
import ProyectoPropio1.utilidades.FabricaServicios;
import ProyectoPropio1.utilidades.RutasVista;
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

public class TabPerecederosControlador {

    @FXML
    private TextField txtBuscar;

    @FXML
    private TableView<DatosTotalesProductoPerecederoDTO> tablaPerecederos;

    @FXML
    private TableColumn<DatosTotalesProductoPerecederoDTO, String> colCodigo;

    @FXML
    private TableColumn<DatosTotalesProductoPerecederoDTO, String> colNombre;

    @FXML
    private TableColumn<DatosTotalesProductoPerecederoDTO, LocalDate> colFechaVencimiento;

    @FXML
    private TableColumn<DatosTotalesProductoPerecederoDTO, String> colPoliticaVencimiento;

    @FXML
    private TableColumn<DatosTotalesProductoPerecederoDTO, String> colEstaVencido;

    @FXML
    private TableColumn<DatosTotalesProductoPerecederoDTO, BigDecimal> colCompra;

    @FXML
    private TableColumn<DatosTotalesProductoPerecederoDTO, BigDecimal> colGanancia;

    @FXML
    private TableColumn<DatosTotalesProductoPerecederoDTO, String> colImpuesto;

    @FXML
    private TableColumn<DatosTotalesProductoPerecederoDTO, String> colDescuento;

    @FXML
    private TableColumn<DatosTotalesProductoPerecederoDTO, BigDecimal> colVentaFinal;

    @FXML
    private TableColumn<DatosTotalesProductoPerecederoDTO, Integer> colStock;

    @FXML
    private TableColumn<DatosTotalesProductoPerecederoDTO, String> colDisponible;

    private int idInventario;

    private final NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(Locale.of("es", "CO"));

    private final ObservableList<DatosTotalesProductoPerecederoDTO> listaMaestraPerecederos = FXCollections.observableArrayList();

    private FilteredList<DatosTotalesProductoPerecederoDTO> listaFiltrada;

    private final ServicioProductos servicioProductos = FabricaServicios.obtenerServicioProductos();

    private final EnsambladorDTOProducto ensambladorDTOProducto = FabricaEnsambladores.obtenerEnsambladorDTOProducto();

    public void recibirIdInventario(int idInventario) {
        this.idInventario = idInventario;
        cargarDatosTabla();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    public void initialize() {
        configurarColumnas();
        configurarFiltro();
    }

    private void configurarColumnas() {
        colCodigo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().codigo()));
        colNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().nombre()));
        colEstaVencido.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().estaVencido()));
        colStock.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().stock()));
        colDisponible.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().disponible()));
        colFechaVencimiento.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().fechaVencimiento()));
        colPoliticaVencimiento.setCellValueFactory(cellData -> {
            var politica = cellData.getValue().datosPoliticaVencimiento();
            return new SimpleStringProperty(politica != null ? politica.nombrePolitica() : "Sin Política");
        });
        colImpuesto.setCellValueFactory(cellData -> {
            var impuesto = cellData.getValue().datosImpuesto();
            return new SimpleStringProperty(impuesto != null ? impuesto.nombre() + " (" + impuesto.porcentaje() + "%)" : "Sin Impuesto");
        });
        colDescuento.setCellValueFactory(cellData -> {
            var descuento = cellData.getValue().datosDescuento();
            return new SimpleStringProperty(descuento != null ? descuento.nombre() + " (" + descuento.porcentaje() + "%)" : "Sin Descuento");
        });
        colCompra.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().valorCompra()));
        colGanancia.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().porcentajeGanancia()));
        colVentaFinal.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().valorVentaFinal()));
        formatearColumnaMoneda(colCompra);
        formatearColumnaMoneda(colVentaFinal);
    }

    private void formatearColumnaMoneda(TableColumn<DatosTotalesProductoPerecederoDTO, BigDecimal> columna) {
        columna.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal precio, boolean empty) {
                super.updateItem(precio, empty);
                if (empty || precio == null) {
                    setText(null);
                } else {
                    setText(formatoMoneda.format(precio));
                }
            }
        });
    }

    private void configurarFiltro() {
        listaFiltrada = new FilteredList<>(listaMaestraPerecederos, p -> true);
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            listaFiltrada.setPredicate(producto -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }
                String textoBusqueda = newValue.toLowerCase().trim();
                boolean coincideCodigo = producto.codigo() != null && producto.codigo().toLowerCase().contains(textoBusqueda);
                boolean coincideNombre = producto.nombre() != null && producto.nombre().toLowerCase().contains(textoBusqueda);
                return coincideCodigo || coincideNombre;
            });
        });
        SortedList<DatosTotalesProductoPerecederoDTO> listaOrdenada = new SortedList<>(listaFiltrada);
        listaOrdenada.comparatorProperty().bind(tablaPerecederos.comparatorProperty());
        tablaPerecederos.setItems(listaOrdenada);
    }

    private LocalDate obtenerFecha(){
        return LocalDate.now();
    }

    private void cargarDatosTabla() {
        try {
            List<DatosTotalesProductoPerecederoDTO> datosBD = this.ensambladorDTOProducto.ensamblarDetalleProductosPerecedero(
                    this.servicioProductos.obtenerProductosPerecederoDeInventario(this.idInventario), obtenerFecha()
            );
            listaMaestraPerecederos.clear();
            if (datosBD != null && !datosBD.isEmpty()) {
                listaMaestraPerecederos.addAll(datosBD);
            }
            tablaPerecederos.refresh();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Carga",
                    "Ocurrió un error al cargar los productos perecederos de la base de datos.\nDetalle: " + e.getMessage());
        }
    }

    @FXML
    void abrirEditorPerecedero(ActionEvent event) {
        DatosTotalesProductoPerecederoDTO productoSeleccionado = tablaPerecederos.getSelectionModel().getSelectedItem();
        if (productoSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección requerida",
                    "Por favor, seleccione un producto perecedero en la tabla para editarlo.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(RutasVista.EDITAR_PERECEDERO_VIEW));
            Parent root = loader.load();
            EditarPerecederoControlador controladorEditor = loader.getController();
            controladorEditor.cargarDatosProducto(productoSeleccionado, this.idInventario);
            Stage stageEditor = new Stage();
            stageEditor.setScene(new Scene(root));
            stageEditor.setTitle("Editar Producto Perecedero");
            stageEditor.initModality(Modality.APPLICATION_MODAL);
            stageEditor.setResizable(false);
            stageEditor.showAndWait();
            cargarDatosTabla();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Interfaz",
                    "No se pudo abrir la ventana de edición.\nDetalle: " + e.getMessage());
        }
    }

    @FXML
    void cambiarEstadoProducto(ActionEvent event) {
        DatosTotalesProductoPerecederoDTO productoSeleccionado = this.tablaPerecederos.getSelectionModel().getSelectedItem();
        if (productoSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección Requerida",
                    "Por favor, seleccione un producto perecedero de la tabla para cambiar su estado.");
            return;
        }
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Cambio de Estado");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Está seguro que desea cambiar el estado del producto:\n"
                + productoSeleccionado.codigo() + " - " + productoSeleccionado.nombre() + "?");
        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                try {
                    this.servicioProductos.cambiarEstadoProducto(
                            this.idInventario,
                            productoSeleccionado.codigo()
                    );
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Estado Actualizado",
                            "El estado del producto se actualizó correctamente.");
                    cargarDatosTabla();
                } catch (Exception e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error en la Actualización",
                            "Ocurrió un error al intentar cambiar el estado del producto.\nDetalle: " + e.getMessage());
                }
            }
        });
    }

}

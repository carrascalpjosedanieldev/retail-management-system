package ProyectoPropio1.vista.controladores;

import ProyectoPropio1.dto.ProductoResumenDTO;
import ProyectoPropio1.servicios.aplicacion.ServicioProductos;
import ProyectoPropio1.servicios.ensambladores.EnsambladorDTOProducto;
import ProyectoPropio1.utilidades.FabricaEnsambladores;
import ProyectoPropio1.utilidades.FabricaServicios;
import ProyectoPropio1.utilidades.RutasVista;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class TabGeneralProductosControlador {

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

    private final ServicioProductos servicioProductos = FabricaServicios.obtenerServicioProductos();

    private final EnsambladorDTOProducto ensambladorDTOProducto = FabricaEnsambladores.obtenerEnsambladorDTOProducto();

    private final ObservableList<ProductoResumenDTO> listaObservable = FXCollections.observableArrayList();

    private FilteredList<ProductoResumenDTO> listaFiltrada;


    private LocalDate obtenerFecha(){
        return LocalDate.now();
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


    @FXML
    public void initialize() {
        configurarColumnas();
        configurarFiltros();
        cargarDatosTabla();
    }


    public void recibirIdInventario(int idInventario) {
        this.idInventario = idInventario;
        cargarDatosTabla();
    }


    private void cargarDatosTabla() {
        List<ProductoResumenDTO> resumenProductos = this.ensambladorDTOProducto.ensamblarDetalleProductosResumen(
                this.servicioProductos.obtenerProductosDeInventario(this.idInventario), obtenerFecha()
        );
        listaObservable.clear();
        listaObservable.addAll(resumenProductos);
    }


    private void configurarColumnas() {
        colCodigo.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().codigoProducto()));
        colCodigo.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(String uuid, boolean empty) {
                super.updateItem(uuid, empty);
                if (empty || uuid == null) {
                    setText(null);
                    setTooltip(null);
                } else {
                    setText("#" + uuid.substring(0, Math.min(uuid.length(), 8)));
                    setTooltip(new Tooltip("ID Completo: " + uuid));
                }
            }
        });
        colNombre.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().nombre()));
        colStock.setCellValueFactory(celda -> new SimpleIntegerProperty(celda.getValue().stock()).asObject());
        NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        colValor.setCellValueFactory(celda -> new SimpleObjectProperty<>(celda.getValue().valorVenta()));
        colValor.setCellFactory(col -> new TableCell<>() {
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
        colEstado.setCellValueFactory(celda -> new SimpleBooleanProperty(celda.getValue().disponible()).asObject());
        colEstado.setCellFactory(col -> new TableCell<>() {
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
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros.run());
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource(RutasVista.CREAR_PRODUCTO_VIEW));
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
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo abrir la ventana de creación de producto.");
        }
    }


    @FXML
    void cambiarEstadoProducto(ActionEvent event) {
        ProductoResumenDTO seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención", "Por favor, selecciona un producto para cambiar su estado.");
            return;
        }
        String textoNuevoEstado = seleccionado.disponible() ? "NO DISPONIBLE" : "DISPONIBLE";
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar cambio de estado");
        confirmacion.setHeaderText("Vas a modificar el producto: " + seleccionado.nombre());
        confirmacion.setContentText("¿Estás seguro de que deseas marcar este producto como " + textoNuevoEstado + "?");
        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                try {
                    this.servicioProductos.cambiarEstadoProducto(this.idInventario, seleccionado.codigoProducto());
                    cargarDatosTabla();
                } catch (Exception e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo cambiar el estado: " + e.getMessage());
                }
            }
        });
    }

}

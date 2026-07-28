package ProyectoPropio1.vista.controladores.puntoDeVenta;

import ProyectoPropio1.aplicacion.dto.ItemCarritoDTO;
import ProyectoPropio1.aplicacion.orquestadores.OrquestadorVentas;
import ProyectoPropio1.vista.utilidades.CargadorVistas;
import ProyectoPropio1.vista.utilidades.FormateadorNumeros;
import ProyectoPropio1.vista.utilidades.RutasVista;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.beans.binding.Bindings;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;

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

    //CONSTRUCTOR:

    public MenuDeVentasControlador(OrquestadorVentas orquestadorVentas) {
        this.orquestadorVentas = orquestadorVentas;
    }

    //METODOS:

    private LocalDate obtenerFecha(){
        return LocalDate.now();
    }

    @FXML
    public void initialize() {
        listaCarrito = FXCollections.observableArrayList();
        tablaCarrito.setItems(listaCarrito);
        colCodigo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().codigoArticulo()));
        colDescripcion.setCellValueFactory(cellData -> {
            ItemCarritoDTO item = cellData.getValue();
            String descripcionArmada = item.tipoItem() + ": " + item.nombreArticulo();
            return new SimpleStringProperty(descripcionArmada);
        });        colCantidad.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().cantidad()).asObject());
        colPrecio.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().precioUnitario()));
        colPrecio.setCellFactory(columna -> new TableCell<>() {
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
        colSubtotal.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().subtotal()));
        colSubtotal.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal subtotal, boolean empty) {
                super.updateItem(subtotal, empty);
                if (empty || subtotal == null) {
                    setText(null);
                } else {
                    setText(FormateadorNumeros.formatoMoneda(subtotal));
                }
            }
        });
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
        txtCodigo.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                agregarItem(null);
            }
        });
        Platform.runLater(() -> txtCodigo.requestFocus());
        this.orquestadorVentas.abrirCarritoSesion();
        actualizarTotales();
    }

    private void actualizarTotales() {
        lblSubtotal.setText("$ 0.00");
        lblImpuestos.setText("$ 0.00");
        lblTotalGeneral.setText("$ 0.00");
    }


    @FXML
    public void volverAlMenu(ActionEvent event) {
        try {
            Stage stageActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
            CargadorVistas.cambiarPantalla(stageActual, RutasVista.PANEL_DE_CONTROL_POS_VIEW);
        } catch (Exception e) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error de Navegación");
            alerta.setHeaderText("NO se pudo Cargar la Pantalla");
            alerta.setContentText("Ocurrió un Problema al Intentar Abrir la Vista.\n" +
                    "Ruta Solicitada: " + RutasVista.PANEL_DE_CONTROL_POS_VIEW + "\n" +
                    "Si el problema persiste, contacte al Administrador o al Creador Original 😎 Jose Daniel 😎.");
            DialogPane panelAlerta = alerta.getDialogPane();
            panelAlerta.setPrefSize(500, 280);
            URL urlCss = getClass().getResource(RutasVista.ESTILOS_CSS_PANEL_DE_CONTROL_POS);
            if (urlCss != null) {
                panelAlerta.getStylesheets().add(urlCss.toExternalForm());
            } else {
                panelAlerta.setPrefSize(500, 180);
            }
            alerta.showAndWait();
        }
    }


    @FXML
    public void agregarItem(javafx.event.ActionEvent event) {
        String codigo = txtCodigo.getText().trim();
        if (codigo.isEmpty()) {
            return;
        }
        try {
            this.orquestadorVentas.agregarItemAlCarrito(codigo, obtenerFecha());
            actualizarTablaYTotales();
            txtCodigo.clear();
        } catch (Exception e) {
            mostrarAlertaError("Error al agregar", e.getMessage());
            txtCodigo.selectAll();
        } finally {
            txtCodigo.requestFocus();
        }
    }

    private void actualizarTablaYTotales() {
        List<ItemCarritoDTO> itemsDelDominio = this.orquestadorVentas
                .obtenerVistaPreviaCarrito(obtenerFecha())
                .carritoItems();
        listaCarrito.setAll(itemsDelDominio);
        BigDecimal subtotalVenta = BigDecimal.ZERO;
        BigDecimal impuestos = BigDecimal.ZERO;
        for (ItemCarritoDTO item : itemsDelDominio) {
            subtotalVenta = subtotalVenta.add(item.subtotal());
            impuestos = impuestos.add(item.impuestos());
        }
        BigDecimal totalGeneral = subtotalVenta.add(impuestos);
        lblSubtotal.setText(FormateadorNumeros.formatoMoneda(subtotalVenta));
        lblImpuestos.setText(FormateadorNumeros.formatoMoneda(impuestos));
        lblTotalGeneral.setText(FormateadorNumeros.formatoMoneda(totalGeneral));
    }

    private void mostrarAlertaError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }






    @FXML
    public void eliminarItemSeleccionado(ActionEvent event) {

    }

    @FXML
    public void cancelarVenta(ActionEvent event) {

    }

    @FXML
    public void procesarVenta(ActionEvent event) {

    }

    @FXML
    public void aumentarCantidadSeleccionada(ActionEvent event) {

    }

    @FXML
    public void reducirCantidadSeleccionada(ActionEvent event) {

    }


}//===================================================================================================================//


package RetailManagementSystem.vista.controladores.puntoDeVenta;

import RetailManagementSystem.aplicacion.dto.FacturaDTO;
import RetailManagementSystem.aplicacion.dto.ItemVendidoFacturaDTO;
import RetailManagementSystem.aplicacion.servicios.ServicioConfiguraciones;
import RetailManagementSystem.vista.utilidades.FormateadorNumeros;
import RetailManagementSystem.vista.utilidades.RutasVista;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

public class FacturaGeneradaControlador {

    //ATRIBUTOS:

    @FXML public Label lblNombreTienda;
    @FXML private Label lblNumeroFactura;
    @FXML private Label lblFechaFactura;
    @FXML private Label lblSubtotal;
    @FXML private Label lblImpuestos;
    @FXML private Label lblTotal;
    @FXML private TableView<ItemVendidoFacturaDTO> tablaDetalle;
    @FXML private TableColumn<ItemVendidoFacturaDTO, Integer> colCant;
    @FXML private TableColumn<ItemVendidoFacturaDTO, String> colDesc;
    @FXML private TableColumn<ItemVendidoFacturaDTO, BigDecimal> colPrecio;
    @FXML private TableColumn<ItemVendidoFacturaDTO, BigDecimal> colTotal;

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final ServicioConfiguraciones servicioConfiguraciones;

    //CONSTRUCTOR:

    public FacturaGeneradaControlador(ServicioConfiguraciones servicioConfiguraciones) {
        this.servicioConfiguraciones = servicioConfiguraciones;
    }

    //MÉTODOS:

    @FXML
    public void initialize() {
        colCant.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().cantidad()).asObject());
        colDesc.setCellValueFactory(cellData -> {
            ItemVendidoFacturaDTO item = cellData.getValue();
            String descripcion = item.tipoItem() + ": " + item.nombreItem();
            return new SimpleStringProperty(descripcion);
        });
        colPrecio.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().precioUnitario()));
        colPrecio.setCellFactory(col -> new TableCell<>() {
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
        colTotal.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().totalLinea()));
        colTotal.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal total, boolean empty) {
                super.updateItem(total, empty);
                if (empty || total == null) {
                    setText(null);
                } else {
                    setText(FormateadorNumeros.formatoMoneda(total));
                }
            }
        });
    }

    public void cargarFactura(FacturaDTO factura) {
        if (factura == null) return;
        cargarNombreTienda();
        lblNumeroFactura.setText(factura.numeroFactura());
        if (factura.fechaEmision() != null) {
            lblFechaFactura.setText(factura.fechaEmision().format(FORMATO_FECHA));
        } else {
            lblFechaFactura.setText("-");
        }
        tablaDetalle.setItems(FXCollections.observableArrayList(factura.listaItemsFinales()));
        lblSubtotal.setText(FormateadorNumeros.formatoMoneda(factura.subTotal()));
        lblImpuestos.setText(FormateadorNumeros.formatoMoneda(factura.totalImpuestos()));
        lblTotal.setText(FormateadorNumeros.formatoMoneda(factura.totalGeneral()));
    }

    private void cargarNombreTienda() {
        CompletableFuture.supplyAsync(() -> {
                    return this.servicioConfiguraciones.obtenerValorConfiguracion(RutasVista.NOMBRE_TIENDA_CLAVE);
                }).thenAcceptAsync(nombreTienda -> {
                    if (nombreTienda != null && !nombreTienda.isBlank()) {
                        lblNombreTienda.setText(nombreTienda);
                    } else {
                        lblNombreTienda.setText("Mi Tienda");
                    }
                }, Platform::runLater)
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        lblNombreTienda.setText("Tienda (Modo Offline)");
                        System.err.println("Error al Cargar el Nombre de la Tienda: " + ex.getMessage());
                    });
                    return null;
                });
    }


    @FXML
    public void cerrarFactura(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }


}//===================================================================================================================//


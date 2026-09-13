package RetailManagementSystem.vista.controladores.puntoDeVenta;

import RetailManagementSystem.aplicacion.dto.ventas.FacturaDTO;
import RetailManagementSystem.aplicacion.dto.ventas.ItemVendidoFacturaDTO;
import RetailManagementSystem.aplicacion.servicios.ServicioConfiguraciones;
import RetailManagementSystem.vista.configuracion.ConfiguradorExcepciones;
import RetailManagementSystem.vista.utilidades.FormateadorNumeros;
import RetailManagementSystem.vista.utilidades.GestorAlertas;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Window;

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

    private Window getVentana(){
        return lblFechaFactura.getScene() != null ? lblFechaFactura.getScene().getWindow() : null;
    }


    @FXML
    public void initialize() {
        colCant.setCellValueFactory(cell -> new SimpleObjectProperty<>(
                cell.getValue().cantidad())
        );
        colDesc.setCellValueFactory(cellData -> {
            ItemVendidoFacturaDTO item = cellData.getValue();
            String descripcion = item.tipoItem() + ": " + item.nombreItem();
            return new SimpleStringProperty(descripcion);
        });
        colPrecio.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                cellData.getValue().precioUnitario())
        );
        colPrecio.setCellFactory(col -> crearCeldaMoneda());
        colTotal.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                cellData.getValue().totalLinea())
        );
        colTotal.setCellFactory(col -> crearCeldaMoneda());
    }

    private TableCell<ItemVendidoFacturaDTO, BigDecimal> crearCeldaMoneda() {
        return new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal monto, boolean empty) {
                super.updateItem(monto, empty);
                if (empty || monto == null) {
                    setText(null);
                } else {
                    setText(FormateadorNumeros.formatoMoneda(monto));
                }
            }
        };
    }

    public void cargarFactura(FacturaDTO factura) {
        if (factura == null) return;
        cargarNombreTienda();
        lblNumeroFactura.setText(factura.numeroFactura());
        if (factura.fechaEmision() != null) {
            lblFechaFactura.setText(factura.fechaEmision().format(FORMATO_FECHA));
        } else {
            lblFechaFactura.setText("--");
        }
        tablaDetalle.setItems(FXCollections.observableArrayList(factura.listaItemsFinales()));
        lblSubtotal.setText(FormateadorNumeros.formatoMoneda(factura.subTotal()));
        lblImpuestos.setText(FormateadorNumeros.formatoMoneda(factura.totalImpuestos()));
        lblTotal.setText(FormateadorNumeros.formatoMoneda(factura.totalGeneral()));
    }

    private void cargarNombreTienda() {
        lblNombreTienda.setText("Cargando...");
        CompletableFuture.supplyAsync(
                this.servicioConfiguraciones::obtenerNombreTienda
        ).thenAccept(nombreTienda ->
            Platform.runLater(() -> {
                if (nombreTienda != null && !nombreTienda.isBlank()) {
                    lblNombreTienda.setText(nombreTienda);
                } else {
                    lblNombreTienda.setText("Mi Tienda");
                }
            })
        ).exceptionally(ex -> {
            Platform.runLater(() -> {
                lblNombreTienda.setText("Tienda (Modo Offline)");
                Throwable causa = ConfiguradorExcepciones.obtenerCausaRaiz(ex);
                GestorAlertas.mostrarAlertaError(
                        getVentana(), "Error de Carga",
                        "Error al Obtener el Nombre de la Tienda",
                        "NO se pudo Leer la Configuración Local: " + causa.getMessage() + "\n" +
                                "Vertica tu conexión para seguir utilizando la App."
                );
            });
            return null;
        });
    }


    @FXML
    private void cerrarFactura(ActionEvent event) {
        Window ventana = getVentana();
        if (ventana != null){
            ventana.hide();
        }
    }


}//===================================================================================================================//


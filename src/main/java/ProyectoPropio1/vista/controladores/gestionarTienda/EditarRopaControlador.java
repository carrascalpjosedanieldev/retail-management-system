package ProyectoPropio1.vista.controladores.gestionarTienda;

import ProyectoPropio1.aplicacion.dto.DatosTotalesProductoRopaDTO;
import ProyectoPropio1.aplicacion.dto.DescuentoDTO;
import ProyectoPropio1.aplicacion.dto.ImpuestoDTO;
import ProyectoPropio1.aplicacion.servicios.ServicioDescuentos;
import ProyectoPropio1.aplicacion.servicios.ServicioImpuestos;
import ProyectoPropio1.aplicacion.servicios.ServicioProductos;
import ProyectoPropio1.aplicacion.ensambladores.EnsambladorDTODescuento;
import ProyectoPropio1.aplicacion.ensambladores.EnsambladorDTOImpuesto;
import ProyectoPropio1.vista.utilidades.RutasVista;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;

public class EditarRopaControlador {

    //ATRIBUTOS:

    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtTalla;
    @FXML private TextField txtCompra;
    @FXML private TextField txtGanancia;
    @FXML private TextField txtStock;
    @FXML private ComboBox<ImpuestoDTO> cbImpuesto;
    @FXML private ComboBox<DescuentoDTO> cbDescuento;

    private int idInventario;

    private DatosTotalesProductoRopaDTO productoOriginal;

    private final ServicioImpuestos servicioImpuesto;
    private final ServicioDescuentos servicioDescuento;
    private final ServicioProductos servicioProductos;

    private final EnsambladorDTOImpuesto ensambladorDTOImpuesto;
    private final EnsambladorDTODescuento ensambladorDTODescuento;

    //CONSTRUCTOR:

    public EditarRopaControlador(ServicioImpuestos servicioImpuesto, ServicioDescuentos servicioDescuento,
                                 ServicioProductos servicioProductos, EnsambladorDTOImpuesto ensambladorDTOImpuesto,
                                 EnsambladorDTODescuento ensambladorDTODescuento) {
        this.servicioImpuesto = servicioImpuesto;
        this.servicioDescuento = servicioDescuento;
        this.servicioProductos = servicioProductos;
        this.ensambladorDTOImpuesto = ensambladorDTOImpuesto;
        this.ensambladorDTODescuento = ensambladorDTODescuento;
    }


    //MÉTODOS:

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        DialogPane panelAlerta = alerta.getDialogPane();
        panelAlerta.setMinHeight(Region.USE_PREF_SIZE);
        URL urlCss = getClass().getResource(RutasVista.ESTILOS_CSS_EDITAR_ROPA);
        if (urlCss != null) {
            panelAlerta.getStylesheets().add(urlCss.toExternalForm());
        }
        alerta.showAndWait();
    }


    @FXML
    public void initialize() {
        configurarFiltrosTexto();
        configurarVisualizacionCombos();
        cargarDatosCombos();
    }

    private void configurarFiltrosTexto() {
        String regexDecimal = "\\d*(\\.\\d*)?";
        txtCompra.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().matches(regexDecimal) ? change : null));
        txtGanancia.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().matches(regexDecimal) ? change : null));
    }

    private void cargarDatosCombos() {
        try {
            List<ImpuestoDTO> listaImpuestos = this.ensambladorDTOImpuesto.ensamblarDetalleImpuestos(
                    this.servicioImpuesto.obtenerImpuestosActivos()
            );
            List<DescuentoDTO> listaDescuentos = this.ensambladorDTODescuento.ensamblarDetalleDescuentos(
                    this.servicioDescuento.obtenerDescuentosActivos()
            );
            cbImpuesto.setItems(FXCollections.observableArrayList(listaImpuestos));
            cbDescuento.setItems(FXCollections.observableArrayList(listaDescuentos));
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Carga",
                    "NO se pudieron Cargar las Listas de Impuestos o Descuentos.\n" +
                            "Error:  " + e.getMessage());
        }
    }

    private void configurarVisualizacionCombos() {
        cbImpuesto.setConverter(new StringConverter<>() {
            @Override public String toString(ImpuestoDTO i) {
                return i == null ? "Seleccione..." : i.nombre() + " (" + i.porcentaje() + "%)";
            }
            @Override public ImpuestoDTO fromString(String s) { return null; }
        });
        cbDescuento.setConverter(new StringConverter<>() {
            @Override public String toString(DescuentoDTO d) {
                return d == null ? "Seleccione..." : d.nombre() + " (" + d.porcentaje() + "%)";
            }
            @Override public DescuentoDTO fromString(String s) { return null; }
        });

    }

    public void cargarDatosProducto(DatosTotalesProductoRopaDTO producto, int idInventario) {
        this.productoOriginal = producto;
        this.idInventario = idInventario;
        txtCodigo.setText(producto.codigo());
        txtTalla.setText(producto.talla() != null ? producto.talla().name() : "Sin Talla");
        txtNombre.setText(producto.nombre());
        txtCompra.setText(producto.valorCompra() != null ? producto.valorCompra().toString() : "0");
        txtGanancia.setText(producto.porcentajeGanancia() != null ? producto.porcentajeGanancia().toString() : "0");
        txtStock.setText(String.valueOf(producto.stock()));
        if (producto.datosImpuesto() != null) {
            cbImpuesto.getItems().stream()
                    .filter(i -> i.idImpuesto() == producto.datosImpuesto().idImpuesto())
                    .findFirst()
                    .ifPresent(cbImpuesto.getSelectionModel()::select);
        }
        if (producto.datosDescuento() != null) {
            cbDescuento.getItems().stream()
                    .filter(d -> d.idDescuento() == producto.datosDescuento().idDescuento())
                    .findFirst()
                    .ifPresent(cbDescuento.getSelectionModel()::select);
        }
    }


    @FXML
    void guardarCambios(ActionEvent event) {
        guardarCambios();
    }

    private void guardarCambios(){
        String nombreNuevo = txtNombre.getText().trim();
        if (nombreNuevo.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos Incompletos",
                    "El Nombre de la Prenda NO puede estar Vacío.");
            return;
        }
        BigDecimal valorCompra;
        BigDecimal porcentajeGanancia;
        try {
            valorCompra = new BigDecimal(txtCompra.getText().trim());
            porcentajeGanancia = new BigDecimal(txtGanancia.getText().trim());
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Datos Inválidos",
                    "Por favor, verifique que los campos de Valor de Compra, Ganancia y Stock contengan únicamente números válidos.");
            return;
        }
        ImpuestoDTO impuestoSeleccionado = cbImpuesto.getValue();
        DescuentoDTO descuentoSeleccionado = cbDescuento.getValue();
        if (impuestoSeleccionado == null || descuentoSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección Requerida",
                    "Debe seleccionar un Impuesto, un Descuento para el Producto.");
            return;
        }
        try {
            servicioProductos.actualizarProductoRopaDeInventario(
                    this.idInventario,
                    this.productoOriginal.codigo(),
                    nombreNuevo,
                    valorCompra,
                    porcentajeGanancia,
                    impuestoSeleccionado.idImpuesto(),
                    descuentoSeleccionado.idDescuento()
            );
            mostrarAlerta(Alert.AlertType.INFORMATION, "Actualización Exitosa",
                    "La Prenda se ha Actualizado Correctamente.");
            cerrarVentana();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al Actualizar",
                    "NO se pudo Guardar la Información en la Base de Datos.\nDetalle: " + e.getMessage());
        }
    }


    @FXML
    void cancelar(ActionEvent event) {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }


}//===================================================================================================================//


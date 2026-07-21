package ProyectoPropio1.vista.controladores;

import ProyectoPropio1.dto.DatosTotalesProductoRopaDTO;
import ProyectoPropio1.dto.DescuentoDTO;
import ProyectoPropio1.dto.ImpuestoDTO;
import ProyectoPropio1.servicios.aplicacion.ServicioDescuentos;
import ProyectoPropio1.servicios.aplicacion.ServicioImpuestos;
import ProyectoPropio1.servicios.aplicacion.ServicioProductos;
import ProyectoPropio1.servicios.ensambladores.EnsambladorDTODescuento;
import ProyectoPropio1.servicios.ensambladores.EnsambladorDTOImpuesto;
import ProyectoPropio1.utilidades.FabricaEnsambladores;
import ProyectoPropio1.utilidades.FabricaServicios;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.List;

public class EditarRopaControlador {

    @FXML private TextField txtCodigo;

    @FXML private TextField txtNombre;

    @FXML private TextField txtTalla;

    @FXML private TextField txtCompra;

    @FXML private TextField txtGanancia;

    @FXML private TextField txtStock;

    @FXML private ComboBox<ImpuestoDTO> cbImpuesto;

    @FXML private ComboBox<DescuentoDTO> cbDescuento;

    private DatosTotalesProductoRopaDTO productoOriginal;

    private int idInventario;

    private final ServicioImpuestos servicioImpuesto = FabricaServicios.obtenerServicioImpuestos();

    private final ServicioDescuentos servicioDescuento = FabricaServicios.obtenerServicioDescuentos();

    private final ServicioProductos servicioProductos = FabricaServicios.obtenerServicioProductos();

    private final EnsambladorDTOImpuesto ensambladorDTOImpuesto = FabricaEnsambladores.obtenerEnsambladorDTOImpuesto();

    private final EnsambladorDTODescuento ensambladorDTODescuento = FabricaEnsambladores.obtenerEnsambladorDTODescuento();

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    public void initialize() {
        configurarVisualizacionCombos();
        cargarDatosCombos();
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
                    "No se pudieron cargar las listas de impuestos o descuentos.\nDetalle: " + e.getMessage());
        }
    }

    private void configurarVisualizacionCombos() {
        cbImpuesto.setConverter(new javafx.util.StringConverter<ImpuestoDTO>() {
            @Override
            public String toString(ImpuestoDTO impuesto) {
                if (impuesto == null) return "Sin Impuesto";
                return impuesto.nombre() + " (" + impuesto.porcentaje() + "%)";
            }
            @Override
            public ImpuestoDTO fromString(String string) {
                return null;
            }
        });
        cbDescuento.setConverter(new javafx.util.StringConverter<DescuentoDTO>() {
            @Override
            public String toString(DescuentoDTO descuento) {
                if (descuento == null) return "Sin Descuento";
                return descuento.nombre() + " (" + descuento.porcentaje() + "%)";
            }
            @Override
            public DescuentoDTO fromString(String string) {
                return null;
            }
        });

    }

    public void cargarDatosProducto(DatosTotalesProductoRopaDTO producto, int idInventario) {
        this.productoOriginal = producto;
        this.idInventario = idInventario;
        txtCodigo.setText(producto.codigo());
        txtTalla.setText(producto.talla() != null ? producto.talla().name() : "");
        txtNombre.setText(producto.nombre());
        txtCompra.setText(producto.valorCompra() != null ? producto.valorCompra().toString() : "0");
        txtGanancia.setText(producto.porcentajeGanancia() != null ? producto.porcentajeGanancia().toString() : "0");
        txtStock.setText(String.valueOf(producto.stock()));
        if (producto.datosImpuesto() != null) {
            for (ImpuestoDTO imp : cbImpuesto.getItems()) {
                if (imp.idImpuesto() == producto.datosImpuesto().idImpuesto()) {
                    cbImpuesto.getSelectionModel().select(imp);
                    break;
                }
            }
        }
        if (producto.datosDescuento() != null) {
            for (DescuentoDTO desc : cbDescuento.getItems()) {
                if (desc.idDescuento() == producto.datosDescuento().idDescuento()) {
                    cbDescuento.getSelectionModel().select(desc);
                    break;
                }
            }
        }
    }


    @FXML
    void guardarCambios(ActionEvent event) {
        String nombreNuevo = txtNombre.getText().trim();
        if (nombreNuevo.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos Incompletos", "El nombre de la prenda no puede estar vacío.");
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
        try {
            Integer idImpuesto = (impuestoSeleccionado != null) ? impuestoSeleccionado.idImpuesto() : null;
            Integer idDescuento = (descuentoSeleccionado != null) ? descuentoSeleccionado.idDescuento() : null;
            servicioProductos.actualizarProductoRopaDeInventario(
                    this.idInventario,
                    this.productoOriginal.codigo(),
                    nombreNuevo,
                    valorCompra,
                    porcentajeGanancia,
                    idImpuesto,
                    idDescuento
            );
            mostrarAlerta(Alert.AlertType.INFORMATION, "Actualización Exitosa", "La prenda se ha actualizado correctamente.");
            cerrarVentana();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al Actualizar",
                    "No se pudo guardar la información en la base de datos.\nDetalle: " + e.getMessage());
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

}

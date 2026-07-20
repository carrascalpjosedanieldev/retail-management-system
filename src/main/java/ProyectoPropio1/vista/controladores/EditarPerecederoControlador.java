package ProyectoPropio1.vista.controladores;

import ProyectoPropio1.dto.DatosTotalesProductoPerecederoDTO;
import ProyectoPropio1.dto.DescuentoDTO;
import ProyectoPropio1.dto.ImpuestoDTO;
import ProyectoPropio1.dto.PoliticaVencimientoDTO;
import ProyectoPropio1.servicios.aplicacion.ServicioDescuentos;
import ProyectoPropio1.servicios.aplicacion.ServicioImpuestos;
import ProyectoPropio1.servicios.aplicacion.ServicioPoliticaVencimiento;
import ProyectoPropio1.servicios.aplicacion.ServicioProductos;
import ProyectoPropio1.servicios.ensambladores.EnsambladorDTODescuento;
import ProyectoPropio1.servicios.ensambladores.EnsambladorDTOImpuesto;
import ProyectoPropio1.servicios.ensambladores.EnsambladorDTOPoliticaVencimiento;
import ProyectoPropio1.utilidades.FabricaEnsambladores;
import ProyectoPropio1.utilidades.FabricaServicios;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EditarPerecederoControlador {

    @FXML
    private TextField txtCodigo;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtFechaVencimiento;

    @FXML
    private ComboBox<PoliticaVencimientoDTO> cbPoliticaVencimiento;

    @FXML
    private TextField txtCompra;

    @FXML
    private TextField txtGanancia;

    @FXML
    private TextField txtStock;

    @FXML
    private ComboBox<ImpuestoDTO> cbImpuesto;

    @FXML
    private ComboBox<DescuentoDTO> cbDescuento;

    private DatosTotalesProductoPerecederoDTO productoOriginal;

    private int idInventario;

    private final ServicioImpuestos servicioImpuesto = FabricaServicios.obtenerServicioImpuestos();

    private final ServicioDescuentos servicioDescuento = FabricaServicios.obtenerServicioDescuentos();

    private final ServicioPoliticaVencimiento servicioPoliticaVencimiento = FabricaServicios.obtenerServicioPoliticas();

    private final ServicioProductos servicioProductos = FabricaServicios.obtenerServicioProductos();

    private final EnsambladorDTOImpuesto ensambladorDTOImpuesto = FabricaEnsambladores.obtenerEnsambladorDTOImpuesto();

    private final EnsambladorDTODescuento ensambladorDTODescuento = FabricaEnsambladores.obtenerEnsambladorDTODescuento();

    private final EnsambladorDTOPoliticaVencimiento ensambladorDTOPoliticaVencimiento = FabricaEnsambladores.obtenerEnsambladorDTOPoliticaVencimiento();

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    public void initialize() {
        configurarFormatoComboBoxes();
        cargarListasDesplegables();
    }

    private void configurarFormatoComboBoxes() {
        cbImpuesto.setConverter(new StringConverter<ImpuestoDTO>() {
            @Override
            public String toString(ImpuestoDTO impuesto) {
                if (impuesto == null) return "Seleccione impuesto...";
                return impuesto.nombre() + " (" + impuesto.porcentaje() + "%)";
            }
            @Override
            public ImpuestoDTO fromString(String string) { return null; }
        });
        cbDescuento.setConverter(new StringConverter<DescuentoDTO>() {
            @Override
            public String toString(DescuentoDTO descuento) {
                if (descuento == null) return "Seleccione descuento...";
                return descuento.nombre() + " (" + descuento.porcentaje() + "%)";
            }
            @Override
            public DescuentoDTO fromString(String string) { return null; }
        });
        cbPoliticaVencimiento.setConverter(new StringConverter<PoliticaVencimientoDTO>() {
            @Override
            public String toString(PoliticaVencimientoDTO politica) {
                if (politica == null) return "Seleccione política...";
                return politica.nombrePolitica();
            }
            @Override
            public PoliticaVencimientoDTO fromString(String string) { return null; }
        });
    }


    private void cargarListasDesplegables() {
        try {
            List<ImpuestoDTO> listaImpuestos = this.ensambladorDTOImpuesto.ensamblarDetalleImpuestos(
                    this.servicioImpuesto.obtenerImpuestosActivos()
            );
            if (listaImpuestos != null) {
                cbImpuesto.getItems().setAll(listaImpuestos);
            }
            List<DescuentoDTO> listaDescuentos = this.ensambladorDTODescuento.ensamblarDetalleDescuentos(
                    this.servicioDescuento.obtenerDescuentosActivos()
            );
            if (listaDescuentos != null) {
                cbDescuento.getItems().setAll(listaDescuentos);
            }
            List<PoliticaVencimientoDTO> listaPoliticas = this.ensambladorDTOPoliticaVencimiento.ensamblarDetallePoliticasVencimiento(
                    this.servicioPoliticaVencimiento.obtenerPoliticasVencimientoActivas()
            );
            if (listaPoliticas != null) {
                cbPoliticaVencimiento.getItems().setAll(listaPoliticas);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Carga",
                    "No se pudieron cargar las listas desplegables.\nDetalle: " + e.getMessage());
        }
    }

    public void cargarDatosProducto(DatosTotalesProductoPerecederoDTO producto, int idInventario) {
        this.productoOriginal = producto;
        this.idInventario = idInventario;
        txtCodigo.setText(producto.codigo());
        txtNombre.setText(producto.nombre());
        txtCompra.setText(producto.valorCompra() != null ? producto.valorCompra().toString() : "0");
        txtGanancia.setText(producto.porcentajeGanancia() != null ? producto.porcentajeGanancia().toString() : "0");
        txtStock.setText(String.valueOf(producto.stock()));
        if (producto.fechaVencimiento() != null) {
            DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            txtFechaVencimiento.setText(producto.fechaVencimiento().format(formatoFecha));
        } else {
            txtFechaVencimiento.setText("Sin fecha");
        }
        if (producto.datosPoliticaVencimiento() != null) {
            for (PoliticaVencimientoDTO pol : cbPoliticaVencimiento.getItems()) {
                if (pol.idPoliticaVencimiento() == producto.datosPoliticaVencimiento().idPoliticaVencimiento()) {
                    cbPoliticaVencimiento.getSelectionModel().select(pol);
                    break;
                }
            }
        }
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
            mostrarAlerta(Alert.AlertType.WARNING, "Campos Incompletos", "El nombre del producto no puede estar vacío.");
            return;
        }
        BigDecimal valorCompra;
        BigDecimal porcentajeGanancia;
        try {
            valorCompra = new BigDecimal(txtCompra.getText().trim());
            porcentajeGanancia = new BigDecimal(txtGanancia.getText().trim());
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Datos Inválidos",
                    "Por favor, verifique que los campos de Valor de Compra y Ganancia contengan únicamente números válidos.");
            return;
        }
        ImpuestoDTO impuestoSeleccionado = cbImpuesto.getValue();
        DescuentoDTO descuentoSeleccionado = cbDescuento.getValue();
        PoliticaVencimientoDTO politicaSeleccionada = cbPoliticaVencimiento.getValue();
        try {
            Integer idImpuesto = (impuestoSeleccionado != null) ? impuestoSeleccionado.idImpuesto() : null;
            Integer idDescuento = (descuentoSeleccionado != null) ? descuentoSeleccionado.idDescuento() : null;
            Integer idPolitica = (politicaSeleccionada != null) ? politicaSeleccionada.idPoliticaVencimiento() : null;
            servicioProductos.actualizarProductoPerecederoDeInventario(
                    this.idInventario,
                    this.productoOriginal.codigo(),
                    nombreNuevo,
                    valorCompra,
                    porcentajeGanancia,
                    idImpuesto,
                    idDescuento,
                    idPolitica
            );
            mostrarAlerta(Alert.AlertType.INFORMATION, "Actualización Exitosa", "El producto perecedero se ha actualizado correctamente.");
            cerrarVentana();
        } catch (Exception e) {
            e.printStackTrace();
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

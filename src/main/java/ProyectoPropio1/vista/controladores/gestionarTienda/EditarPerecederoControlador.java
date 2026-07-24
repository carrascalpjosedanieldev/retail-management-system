package ProyectoPropio1.vista.controladores.gestionarTienda;

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
import ProyectoPropio1.utilidades.RutasVista;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EditarPerecederoControlador {

    //ATRIBUTOS:

    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtFechaVencimiento;
    @FXML private ComboBox<PoliticaVencimientoDTO> cbPoliticaVencimiento;
    @FXML private TextField txtCompra;
    @FXML private TextField txtGanancia;
    @FXML private TextField txtStock;
    @FXML private ComboBox<ImpuestoDTO> cbImpuesto;
    @FXML private ComboBox<DescuentoDTO> cbDescuento;

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
        DialogPane panelAlerta = alerta.getDialogPane();
        panelAlerta.setMinHeight(Region.USE_PREF_SIZE);
        URL urlCss = getClass().getResource(RutasVista.ESTILOS_CSS_EDITAR_PERECEDERO);
        if (urlCss != null) {
            panelAlerta.getStylesheets().add(urlCss.toExternalForm());
        }
        alerta.showAndWait();
    }


    @FXML
    public void initialize() {
        configurarFiltrosTexto();
        configurarFormatoComboBoxes();
        cargarListasDesplegables();
    }

    private void configurarFiltrosTexto() {
        String regexDecimal = "\\d*(\\.\\d*)?";
        txtCompra.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().matches(regexDecimal) ? change : null));
        txtGanancia.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().matches(regexDecimal) ? change : null));
    }

    private void configurarFormatoComboBoxes() {
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
        cbPoliticaVencimiento.setConverter(new StringConverter<>() {
            @Override public String toString(PoliticaVencimientoDTO p) {
                return p == null ? "Seleccione..." : p.nombrePolitica();
            }
            @Override public PoliticaVencimientoDTO fromString(String s) { return null; }
        });
    }

    private void cargarListasDesplegables() {
        try {
            List<ImpuestoDTO> listaImpuestos = ensambladorDTOImpuesto.ensamblarDetalleImpuestos(servicioImpuesto.obtenerImpuestosActivos());
            if (listaImpuestos != null) cbImpuesto.getItems().setAll(listaImpuestos);
            List<DescuentoDTO> listaDescuentos = ensambladorDTODescuento.ensamblarDetalleDescuentos(servicioDescuento.obtenerDescuentosActivos());
            if (listaDescuentos != null) cbDescuento.getItems().setAll(listaDescuentos);
            List<PoliticaVencimientoDTO> listaPoliticas = ensambladorDTOPoliticaVencimiento.ensamblarDetallePoliticasVencimiento(servicioPoliticaVencimiento.obtenerPoliticasVencimientoActivas());
            if (listaPoliticas != null) cbPoliticaVencimiento.getItems().setAll(listaPoliticas);
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Carga",
                    "NO se Pudieron Cargar las Listas.\nError:  " + e.getMessage());
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
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        txtFechaVencimiento.setText(producto.fechaVencimiento() != null ? producto.fechaVencimiento().format(formatoFecha) : "Sin fecha");
        if (producto.datosPoliticaVencimiento() != null) {
            cbPoliticaVencimiento.getItems().stream()
                    .filter(p -> p.idPoliticaVencimiento() == producto.datosPoliticaVencimiento().idPoliticaVencimiento())
                    .findFirst()
                    .ifPresent(cbPoliticaVencimiento.getSelectionModel()::select);
        }
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
                    "El Nombre del Producto NO puede estar Vacío.");
            return;
        }
        BigDecimal valorCompra;
        BigDecimal porcentajeGanancia;
        try {
            valorCompra = new BigDecimal(txtCompra.getText().trim());
            porcentajeGanancia = new BigDecimal(txtGanancia.getText().trim());
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Datos Inválidos",
                    "Por favor, Verifique que los campos de Valor de Compra y Ganancia contengan únicamente Números Válidos.");
            return;
        }
        ImpuestoDTO impuestoSeleccionado = cbImpuesto.getValue();
        DescuentoDTO descuentoSeleccionado = cbDescuento.getValue();
        PoliticaVencimientoDTO politicaSeleccionada = cbPoliticaVencimiento.getValue();
        if (impuestoSeleccionado == null || descuentoSeleccionado == null || politicaSeleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección Requerida",
                    "Debe seleccionar un Impuesto, un Descuento y una Política de Vencimiento para el producto.");
            return;
        }
        try {
            servicioProductos.actualizarProductoPerecederoDeInventario(
                    this.idInventario,
                    this.productoOriginal.codigo(),
                    nombreNuevo,
                    valorCompra,
                    porcentajeGanancia,
                    impuestoSeleccionado.idImpuesto(),
                    descuentoSeleccionado.idDescuento(),
                    politicaSeleccionada.idPoliticaVencimiento()
            );
            mostrarAlerta(Alert.AlertType.INFORMATION, "Actualización Exitosa",
                    "El Producto Perecedero se ha Actualizado Correctamente.");
            cerrarVentana();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al Actualizar",
                    "NO se pudo Guardar la Información en la Base de Datos.\nError:  " + e.getMessage());
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


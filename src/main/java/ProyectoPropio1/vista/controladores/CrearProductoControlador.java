package ProyectoPropio1.vista.controladores;

import ProyectoPropio1.dominio.Producto;
import ProyectoPropio1.dominio.enums.Talla;
import ProyectoPropio1.dominio.enums.TipoProducto;
import ProyectoPropio1.dto.DescuentoDTO;
import ProyectoPropio1.dto.ImpuestoDTO;
import ProyectoPropio1.dto.PoliticaVencimientoDTO;
import ProyectoPropio1.servicios.aplicacion.*;
import ProyectoPropio1.utilidades.FabricaServicios;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class CrearProductoControlador {

    @FXML private RadioButton rbRopa;

    @FXML private RadioButton rbPerecedero;

    @FXML private TextField txtNombre;

    @FXML private TextField txtValorCompra;

    @FXML private TextField txtGanancia;

    @FXML private TextField txtStock;

    @FXML private ComboBox<ImpuestoDTO> cbImpuesto;

    @FXML private ComboBox<DescuentoDTO> cbDescuento;

    @FXML private VBox boxRopa;

    @FXML private ComboBox<String> cbTalla;

    @FXML private VBox boxPerecedero;

    @FXML private DatePicker dpFechaVencimiento;

    @FXML private ComboBox<PoliticaVencimientoDTO> cbPolitica;

    private ToggleGroup grupoTipo;

    private int idInventario;

    private final ServicioImpuestos servicioImpuestos = FabricaServicios.obtenerServicioImpuestos();

    private final ServicioDescuentos servicioDescuentos = FabricaServicios.obtenerServicioDescuentos();

    private final ServicioPoliticaVencimiento servicioPolitica = FabricaServicios.obtenerServicioPoliticas();

    private final FabricaProductos fabricaProductos = new FabricaProductos(servicioImpuestos, servicioDescuentos, servicioPolitica);

    private final ServicioProductos servicioProductos = FabricaServicios.obtenerServicioProductos(); // Para guardar

    @FXML
    public void initialize() {
        configurarRadioButtons();
        configurarComboBoxes();
        cargarDatosComboBoxes();
    }

    public void recibirIdInventario(int idInventario) {
        this.idInventario = idInventario;
    }

    private void configurarRadioButtons() {
        grupoTipo = new ToggleGroup();
        rbRopa.setToggleGroup(grupoTipo);
        rbPerecedero.setToggleGroup(grupoTipo);
        rbRopa.setUserData(TipoProducto.ROPA);
        rbPerecedero.setUserData(TipoProducto.PERECEDERO);
        grupoTipo.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                TipoProducto tipoSeleccionado = (TipoProducto) newVal.getUserData();
                if (tipoSeleccionado == TipoProducto.ROPA) {
                    boxRopa.setVisible(true);
                    boxRopa.setManaged(true);
                    boxPerecedero.setVisible(false);
                    boxPerecedero.setManaged(false);
                } else if (tipoSeleccionado == TipoProducto.PERECEDERO) {
                    boxRopa.setVisible(false);
                    boxRopa.setManaged(false);
                    boxPerecedero.setVisible(true);
                    boxPerecedero.setManaged(true);
                }
            }
        });
    }

    private void configurarComboBoxes() {
        List<String> listaTallas = Arrays.stream(Talla.values())
                .map(Enum::name)
                .toList();
        cbTalla.setItems(FXCollections.observableArrayList(listaTallas));
        cbImpuesto.setConverter(new StringConverter<>() {
            @Override public String toString(ImpuestoDTO dto) { return dto != null ? dto.nombre() + " (" + dto.porcentaje() + "%)" : ""; }
            @Override public ImpuestoDTO fromString(String string) { return null; }
        });
        cbDescuento.setConverter(new StringConverter<>() {
            @Override public String toString(DescuentoDTO dto) { return dto != null ? dto.nombre() + " (" + dto.porcentaje() + "%)" : ""; }
            @Override public DescuentoDTO fromString(String string) { return null; }
        });
        cbPolitica.setConverter(new StringConverter<>() {
            @Override public String toString(PoliticaVencimientoDTO dto) { return dto != null ? dto.nombrePolitica() : ""; }
            @Override public PoliticaVencimientoDTO fromString(String string) { return null; }
        });
    }

    private void cargarDatosComboBoxes() {
        List<ImpuestoDTO> impuestos = servicioImpuestos.obtenerImpuestosActivos().stream()
            .map(i -> new ImpuestoDTO(i.getId(), i.getNombre(), i.getPorcentaje(), "ACTIVO"))
            .toList();
        cbImpuesto.setItems(FXCollections.observableArrayList(impuestos));
        List<DescuentoDTO> descuentos = servicioDescuentos.obtenerDescuentosActivos().stream()
                .map(d -> new DescuentoDTO(d.getId(), d.getNombre(), d.getPorcentaje(), "ACTIVO"))
                .toList();
        cbDescuento.setItems(FXCollections.observableArrayList(descuentos));
        List<PoliticaVencimientoDTO> politicas = servicioPolitica.obtenerPoliticasVencimientoActivas().stream()
                .map(p -> new PoliticaVencimientoDTO(
                        p.getIdPolitica(),
                        p.getNombre(),
                        p.getDiasUmbral(),
                        p.getPorcentajeDescuento(),
                        "ACTIVO"
                ))
                .toList();
        cbPolitica.setItems(FXCollections.observableArrayList(politicas));
    }

    @FXML
    void guardarProducto(ActionEvent event) {
        try {
            if (txtNombre.getText().isBlank() || txtValorCompra.getText().isBlank() ||
                    txtGanancia.getText().isBlank() || txtStock.getText().isBlank()) {
                mostrarAlerta("Error", "Todos los campos de texto son obligatorios.");
                return;
            }
            String nombre = txtNombre.getText();
            BigDecimal valorCompra = new BigDecimal(txtValorCompra.getText());
            BigDecimal ganancia = new BigDecimal(txtGanancia.getText());
            int stock = Integer.parseInt(txtStock.getText());
            ImpuestoDTO impuestoSel = cbImpuesto.getValue();
            DescuentoDTO descuentoSel = cbDescuento.getValue();
            if (impuestoSel == null || descuentoSel == null) {
                mostrarAlerta("Error", "Debes seleccionar un Impuesto y un Descuento.");
                return;
            }
            TipoProducto tipoSeleccionado = (TipoProducto) grupoTipo.getSelectedToggle().getUserData();
            if (tipoSeleccionado == TipoProducto.ROPA) {
                String tallaSel = cbTalla.getValue();
                if (tallaSel == null) {
                    mostrarAlerta("Error", "Debes seleccionar una talla.");
                    return;
                }
                Producto nuevoRopa = fabricaProductos.fabricarProductoRopa(
                        nombre, valorCompra, ganancia, stock,
                        impuestoSel.idImpuesto(), descuentoSel.idDescuento(), tallaSel
                );
                servicioProductos.registrarProducto(this.idInventario, nuevoRopa);
            } else if (tipoSeleccionado == TipoProducto.PERECEDERO) {
                LocalDate fechaVenc = dpFechaVencimiento.getValue();
                PoliticaVencimientoDTO politicaSel = cbPolitica.getValue();
                if (fechaVenc == null || politicaSel == null) {
                    mostrarAlerta("Error", "Debes seleccionar Fecha y Política de vencimiento.");
                    return;
                }
                Producto nuevoPerecedero = fabricaProductos.fabricarProductoPerecedero(
                        nombre, valorCompra, ganancia, stock,
                        impuestoSel.idImpuesto(), descuentoSel.idDescuento(),
                        fechaVenc, politicaSel.idPoliticaVencimiento(), LocalDate.now()
                );
                servicioProductos.registrarProducto(this.idInventario, nuevoPerecedero);
            }
            mostrarAlerta("Éxito", "Producto creado correctamente.");
            cerrarVentana();
        } catch (NumberFormatException e) {
            mostrarAlerta("Error de formato", "Verifica que los campos numéricos (Valor, Ganancia, Stock) contengan solo números válidos y sin espacios.");
        } catch (IllegalArgumentException e) {
            mostrarAlerta("Regla de Negocio", e.getMessage());
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

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
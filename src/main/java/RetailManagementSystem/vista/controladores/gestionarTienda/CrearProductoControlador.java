package RetailManagementSystem.vista.controladores.gestionarTienda;

import RetailManagementSystem.aplicacion.servicios.ServicioDescuentos;
import RetailManagementSystem.aplicacion.servicios.ServicioImpuestos;
import RetailManagementSystem.aplicacion.servicios.ServicioPoliticaVencimiento;
import RetailManagementSystem.dominio.entidades.Producto;
import RetailManagementSystem.dominio.enums.Talla;
import RetailManagementSystem.dominio.enums.TipoProducto;
import RetailManagementSystem.aplicacion.dto.DescuentoDTO;
import RetailManagementSystem.aplicacion.dto.ImpuestoDTO;
import RetailManagementSystem.aplicacion.dto.PoliticaVencimientoDTO;
import RetailManagementSystem.dominio.excepciones.CapacidadInventarioExcedidaException;
import RetailManagementSystem.aplicacion.fabricas.FabricaProductos;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorProductoInventario;
import RetailManagementSystem.vista.utilidades.RutasVista;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.net.URL;
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

    private final ServicioImpuestos servicioImpuestos;
    private final ServicioDescuentos servicioDescuentos;
    private final ServicioPoliticaVencimiento servicioPolitica;

    private final FabricaProductos fabricaProductos;

    private final OrquestadorProductoInventario orquestadorProductoInventario;

    //CONSTRUCTOR:

    public CrearProductoControlador(
            ServicioImpuestos servicioImpuestos, ServicioDescuentos servicioDescuentos,
            ServicioPoliticaVencimiento servicioPolitica, OrquestadorProductoInventario orquestadorProductoInventario
    ) {
        this.servicioImpuestos = servicioImpuestos;
        this.servicioDescuentos = servicioDescuentos;
        this.servicioPolitica = servicioPolitica;
        this.fabricaProductos = new FabricaProductos(servicioImpuestos, servicioDescuentos, servicioPolitica);
        this.orquestadorProductoInventario = orquestadorProductoInventario;
    }

    //MÉTODOS:

    public void recibirIdInventario(int idInventario) {
        this.idInventario = idInventario;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        DialogPane panelAlerta = alerta.getDialogPane();
        panelAlerta.setMinHeight(Region.USE_PREF_SIZE);
        URL urlCss = getClass().getResource(RutasVista.ESTILOS_CSS_CREAR_PRODUCTOS);
        if (urlCss != null) {
            panelAlerta.getStylesheets().add(urlCss.toExternalForm());
        }
        alerta.showAndWait();
    }


    @FXML
    public void initialize() {
        configurarFiltrosTexto();
        configurarRadioButtons();
        configurarComboBoxes();
        cargarDatosComboBoxes();
    }

    private void configurarFiltrosTexto() {
        txtStock.setTextFormatter(new TextFormatter<>(change ->
                change.getText().matches("\\d*") ? change : null));
        String regexDecimal = "\\d*(\\.\\d*)?";
        txtValorCompra.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().matches(regexDecimal) ? change : null));
        txtGanancia.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().matches(regexDecimal) ? change : null));
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
                boolean esRopa = (tipoSeleccionado == TipoProducto.ROPA);
                boxRopa.setVisible(esRopa);
                boxRopa.setManaged(esRopa);
                boxPerecedero.setVisible(!esRopa);
                boxPerecedero.setManaged(!esRopa);
            }
        });
        rbRopa.setSelected(true);
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
        guardarProducto();
    }

    private void guardarProducto(){
        try {
            if (txtNombre.getText().isBlank() || txtValorCompra.getText().isBlank() ||
                    txtGanancia.getText().isBlank() || txtStock.getText().isBlank()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Campos de Texto Vacíos",
                        "Todos los Campos de Texto son Obligatorios.");
                return;
            }
            String nombre = txtNombre.getText();
            BigDecimal valorCompra = new BigDecimal(txtValorCompra.getText());
            BigDecimal ganancia = new BigDecimal(txtGanancia.getText());
            int stock = Integer.parseInt(txtStock.getText());
            ImpuestoDTO impuestoSel = cbImpuesto.getValue();
            DescuentoDTO descuentoSel = cbDescuento.getValue();
            if (impuestoSel == null || descuentoSel == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Impuesto NO Seleccionado",
                        "Debes seleccionar un Impuesto y un Descuento.");
                return;
            }
            TipoProducto tipoSeleccionado = (TipoProducto) grupoTipo.getSelectedToggle().getUserData();
            Producto producto = null;
            if (tipoSeleccionado == TipoProducto.ROPA) {
                String tallaSel = cbTalla.getValue();
                if (tallaSel == null) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Talla NO Seleccionada",
                            "Debes Seleccionar una Talla.");
                    return;
                }
                producto = fabricaProductos.fabricarProductoRopa(
                        nombre, valorCompra, ganancia, stock,
                        impuestoSel.idImpuesto(), descuentoSel.idDescuento(), tallaSel
                );
            } else if (tipoSeleccionado == TipoProducto.PERECEDERO) {
                LocalDate fechaVenc = dpFechaVencimiento.getValue();
                PoliticaVencimientoDTO politicaSel = cbPolitica.getValue();
                if (fechaVenc == null || politicaSel == null) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Campos NO Seleccionados",
                            "Debes seleccionar Fecha y Política de Vencimiento.");
                    return;
                }
                producto = fabricaProductos.fabricarProductoPerecedero(
                        nombre, valorCompra, ganancia, stock,
                        impuestoSel.idImpuesto(), descuentoSel.idDescuento(),
                        fechaVenc, politicaSel.idPoliticaVencimiento(), LocalDate.now()
                );
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Tipo de Producto NO Identificado",
                        "Tipo de Producto Invalido");
                return;
            }
            this.orquestadorProductoInventario.validarEspacioInventarioYGuardarProducto(this.idInventario, producto);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                    "Producto Creado Correctamente.");
            cerrarVentana();
        } catch (CapacidadInventarioExcedidaException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Capacidad Excedida", e.getMessage());
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Error de Formato en los Números",
                    "Verifica que los Campos Numéricos (Valor, Ganancia, Stock) Contengan Solo números Válidos y sin Espacios.");
        } catch (IllegalArgumentException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Error al Registrar el Producto",
                    "Hay un Error en los Datos Ingresados:\n" + e.getMessage());
        } catch (Exception e){
            mostrarAlerta(Alert.AlertType.ERROR, "Error Critico",
                    "NO se pudo Guardar el Producto en la Base de Datos\n" +
                            "Error:  " + e.getMessage());
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


package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarInventarios.gestionarProductos.tabGeneral;

import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.dto.ventas.ProductoResumenDTO;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorDescuentos;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorImpuestos;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorPoliticaVencimiento;
import RetailManagementSystem.dominio.entidades.comercial.Producto;
import RetailManagementSystem.dominio.enums.Talla;
import RetailManagementSystem.dominio.enums.TipoProducto;
import RetailManagementSystem.aplicacion.dto.gestion.DescuentoDTO;
import RetailManagementSystem.aplicacion.dto.gestion.ImpuestoDTO;
import RetailManagementSystem.aplicacion.dto.gestion.PoliticaVencimientoDTO;
import RetailManagementSystem.dominio.excepciones.reglasDeNegocio.CapacidadInventarioExcedidaException;
import RetailManagementSystem.aplicacion.fabricas.FabricaProductos;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorGestionStock;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.infraestructura.seguridad.ValidadorSeguridad;
import RetailManagementSystem.vista.configuracion.ConfiguradorExcepciones;
import RetailManagementSystem.vista.utilidades.GestorAlertas;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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

    private ObservableList<ProductoResumenDTO> listaObservable;

    private final OrquestadorImpuestos orquestadorImpuestos;

    private final OrquestadorDescuentos orquestadorDescuentos;

    private final OrquestadorPoliticaVencimiento orquestadorPoliticaVencimiento;

    private final FabricaProductos fabricaProductos;

    private final OrquestadorGestionStock orquestadorGestionStock;

    private UsuarioDTOCompleto usuarioActual;

    //CONSTRUCTOR:

    public CrearProductoControlador(
            OrquestadorImpuestos orquestadorImpuestos, OrquestadorDescuentos orquestadorDescuentos,
            OrquestadorPoliticaVencimiento orquestadorPoliticaVencimiento, FabricaProductos fabricaProductos,
            OrquestadorGestionStock orquestadorGestionStock
    ) {
        this.orquestadorImpuestos = orquestadorImpuestos;
        this.orquestadorDescuentos = orquestadorDescuentos;
        this.orquestadorPoliticaVencimiento = orquestadorPoliticaVencimiento;
        this.fabricaProductos = fabricaProductos;
        this.orquestadorGestionStock = orquestadorGestionStock;
    }

    //MÉTODOS:

    private Window getVentana(){
        return boxRopa.getScene() != null ? boxRopa.getScene().getWindow() : null;
    }


    public void cargarDatos(
            UsuarioDTOCompleto usuarioActual, int idInventario, ObservableList<ProductoResumenDTO> listaObservable
    ) {
        ValidadorSeguridad.exigirPermiso(usuarioActual, PermisosApp.REGISTRAR_PRODUCTOS);
        this.usuarioActual = usuarioActual;
        if (idInventario <=0 ){
            throw new IllegalArgumentException("El ID recibido NO es Valido.");
        }
        this.idInventario = idInventario;
        this.listaObservable = listaObservable;
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
        String regexDecimal = "^[0-9]*\\.?[0-9]*$";
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
            @Override public String toString(ImpuestoDTO dto) {
                return dto != null ? dto.nombre() + " (" + dto.porcentaje() + "%)" : "";
            }
            @Override public ImpuestoDTO fromString(String string) { return null; }
        });
        cbDescuento.setConverter(new StringConverter<>() {
            @Override public String toString(DescuentoDTO dto) {
                return dto != null ? dto.nombre() + " (" + dto.porcentaje() + "%)" : "";
            }
            @Override public DescuentoDTO fromString(String string) { return null; }
        });
        cbPolitica.setConverter(new StringConverter<>() {
            @Override public String toString(PoliticaVencimientoDTO dto) {
                return dto != null ? dto.nombrePolitica() : "";
            }
            @Override public PoliticaVencimientoDTO fromString(String string) { return null; }
        });
    }

    private void cargarDatosComboBoxes() {
        CompletableFuture<List<ImpuestoDTO>> futureImpuestos =
                CompletableFuture.supplyAsync(this.orquestadorImpuestos::obtenerImpuestosActivos);
        CompletableFuture<List<DescuentoDTO>> futureDescuentos =
                CompletableFuture.supplyAsync(this.orquestadorDescuentos::obtenerDescuentosActivos);
        CompletableFuture<List<PoliticaVencimientoDTO>> futurePoliticasV =
                CompletableFuture.supplyAsync(this.orquestadorPoliticaVencimiento::obtenerPoliticasVActivas);
        CompletableFuture.allOf(
                futureImpuestos, futureDescuentos, futurePoliticasV
        ).thenAccept(v -> {
            List<ImpuestoDTO> impuestos = futureImpuestos.join();
            List<DescuentoDTO> descuentos = futureDescuentos.join();
            List<PoliticaVencimientoDTO> politicas = futurePoliticasV.join();
            Platform.runLater(() -> {
                if (!impuestos.isEmpty()) cbImpuesto.getItems().setAll(impuestos);
                if (!descuentos.isEmpty()) cbDescuento.getItems().setAll(descuentos);
                if (!politicas.isEmpty()) cbPolitica.getItems().setAll(politicas);
            });
        }).exceptionally(ex -> {
            Platform.runLater(() -> {
                Throwable causa = ConfiguradorExcepciones.obtenerCausaRaiz(ex);
                GestorAlertas.mostrarAlertaError(
                        getVentana(),
                        "Error de Conexión",
                        "Faltan Datos Obligatorios para Operar.",
                        "No se pudieron cargar las listas desplegables desde la base de datos.\n" +
                                "Se Cerrará la Ventana por Seguridad.\n" +
                                "Verifica tu Conexión y Notifícale este Error al Administrador:\n" +
                                causa.getMessage()
                );
                cerrarVentana();
            });
            return null;
        });
    }


    @FXML
    private void guardarProducto(ActionEvent event) {
        guardarProducto();
    }

    private void guardarProducto(){
        String nombre = txtNombre.getText().trim();
        String valorCompraTexto = txtValorCompra.getText().trim();
        String gananciaTexto = txtGanancia.getText().trim();
        String stockTexto = txtStock.getText().trim();
        if (nombre.isBlank() || valorCompraTexto.isBlank() || gananciaTexto.isBlank() || stockTexto.isBlank()) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Campos de Texto Vacíos", null,
                    "Todos los Campos de Texto son Obligatorios."
            );
            return;
        }
        BigDecimal valorCompra;
        BigDecimal ganancia;
        int stock;
        try {
            valorCompra = new BigDecimal(valorCompraTexto);
            ganancia = new BigDecimal(gananciaTexto);
            stock = Integer.parseInt(stockTexto);
        } catch (NumberFormatException e) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Error de Formato en los Números", null,
                    "Verifica que los Campos Numéricos (Valor, Ganancia, Stock) Contengan Solo números Válidos y sin Espacios."
            );
            return;
        } catch (IllegalArgumentException e) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Error al Registrar el Producto", null,
                    "Hay un Error en los Datos Ingresados:\n" + e.getMessage()
            );
            return;
        }
        ImpuestoDTO impuestoSel = cbImpuesto.getValue();
        DescuentoDTO descuentoSel = cbDescuento.getValue();
        if (impuestoSel == null || descuentoSel == null) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Impuesto NO Seleccionado", null,
                    "Debes seleccionar un Impuesto y un Descuento."
            );
            return;
        }
        TipoProducto tipoSeleccionado = (TipoProducto) grupoTipo.getSelectedToggle().getUserData();
        Producto producto = crearProductoDesdeFormulario(
                tipoSeleccionado, nombre, valorCompra, ganancia, stock, impuestoSel.idImpuesto(),
                descuentoSel.idDescuento()
        );
        if (producto == null){
            return;
        }
        CompletableFuture.supplyAsync(()->
                this.orquestadorGestionStock.validarEspacioInventarioYGuardarProducto(
                        this.usuarioActual, this.idInventario, producto, LocalDate.now()
                )
        ).thenAccept(productoRegistrado->
            Platform.runLater(()->{
                listaObservable.add(productoRegistrado);
                GestorAlertas.mostrarAlertaInformacion(
                        getVentana(), "Éxito", null,
                        "Producto Creado Correctamente."
                );
                cerrarVentana();
            })
        ).exceptionally(ex->{
            Platform.runLater(()->{
                Throwable causa = ConfiguradorExcepciones.obtenerCausaRaiz(ex);
                if (causa instanceof IllegalArgumentException ||
                    causa instanceof IllegalStateException){
                    GestorAlertas.mostrarAlertaError(
                            getVentana(), "Error en los Datos Ingresados", null,
                            "Verifica los Datos que Ingresaste, Detalle del Error:\n" +
                                    causa.getMessage()
                    );
                } else if (causa instanceof CapacidadInventarioExcedidaException){
                    GestorAlertas.mostrarAlertaError(
                            getVentana(),"Capacidad del Inventario Excedida", null,
                            causa.getMessage()
                    );
                } else {
                    GestorAlertas.mostrarAlertaError(
                            getVentana(), "Error Critico", null,
                            "Verifica tu Conexión y Notificale este Error al Administrador:\n"
                                    + causa.getMessage()
                    );
                }
            });
            return null;
        });
    }

    private Producto crearProductoDesdeFormulario(
            TipoProducto tipoSeleccionado, String nombre, BigDecimal valorCompra, BigDecimal ganancia,
            int stock, int idImpuesto, int idDescuento
    ) {
        if (tipoSeleccionado == TipoProducto.ROPA) {
            String tallaSel = cbTalla.getValue();
            if (tallaSel == null) {
                GestorAlertas.mostrarAlertaWarning(
                        getVentana(), "Talla NO Seleccionada", null,
                        "Debes Seleccionar una Talla."
                );
                return null;
            }
            return fabricaProductos.fabricarProductoRopa(
                    nombre, valorCompra, ganancia, stock,
                    idImpuesto, idDescuento, tallaSel
            );
        } else if (tipoSeleccionado == TipoProducto.PERECEDERO) {
            LocalDate fechaVenc = dpFechaVencimiento.getValue();
            PoliticaVencimientoDTO politicaSel = cbPolitica.getValue();
            if (fechaVenc == null || politicaSel == null) {
                GestorAlertas.mostrarAlertaWarning(
                        getVentana(), "Campos NO Seleccionados", null,
                        "Debes seleccionar Fecha y Política de Vencimiento."
                );
                return null;
            }
            return fabricaProductos.fabricarProductoPerecedero(
                    nombre, valorCompra, ganancia, stock,
                    idImpuesto, idDescuento, fechaVenc,
                    politicaSel.idPoliticaVencimiento(), LocalDate.now()
            );
        } else {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Error", null,
                    "Tipo Invalido");
            return null;
        }
    }


    @FXML
    private void cancelar(ActionEvent event) {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Window ventana = getVentana();
        if (ventana != null){
            ventana.hide();
        }
    }


}//===================================================================================================================//


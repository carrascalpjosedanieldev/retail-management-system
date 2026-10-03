package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarProductos.tabGeneral.crearProducto;

import RetailManagementSystem.aplicacion.dto.creacion.DatosGeneralesCreacionProductoDTO;
import RetailManagementSystem.aplicacion.dto.creacion.DetalleCreacionProductoDTO;
import RetailManagementSystem.aplicacion.dto.creacion.FormularioProductoDTO;
import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.dto.ventas.ProductoResumenDTO;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorDescuentos;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorImpuestos;
import RetailManagementSystem.dominio.enums.TipoProducto;
import RetailManagementSystem.aplicacion.dto.gestion.DescuentoDTO;
import RetailManagementSystem.aplicacion.dto.gestion.ImpuestoDTO;
import RetailManagementSystem.aplicacion.dto.gestion.PoliticaVencimientoDTO;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorGestionStock;
import RetailManagementSystem.dominio.excepciones.reglasDeNegocio.CapacidadInventarioExcedidaException;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.infraestructura.seguridad.ValidadorSeguridad;
import RetailManagementSystem.vista.configuracion.ConfiguradorExcepciones;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarProductos.tabGeneral.crearProducto.estrategias.EstrategiaCreacionDetalleProducto;
import RetailManagementSystem.vista.utilidades.CargadorVistas;
import RetailManagementSystem.vista.utilidades.GestorAlertas;
import RetailManagementSystem.vista.utilidades.RutasVista;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Window;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CrearProductoControlador {

    @FXML private ComboBox<TipoProducto> cbTipoProducto;
    @FXML private TextField txtNombre;
    @FXML private TextField txtValorCompra;
    @FXML private TextField txtGanancia;
    @FXML private TextField txtStock;
    @FXML private ComboBox<ImpuestoDTO> cbImpuesto;
    @FXML private ComboBox<DescuentoDTO> cbDescuento;
    @FXML private StackPane panelAtributosEspecificos;

    private FormularioEspecificoControlador controladorHijoActual;

    private List<PoliticaVencimientoDTO> politicasCache;

    private final Map<TipoProducto, EstrategiaCreacionDetalleProducto<?, ?>> estrategiasCreacion;

    private int idInventario;

    private ObservableList<ProductoResumenDTO> listaObservable;

    private UsuarioDTOCompleto usuarioActual;

    private final OrquestadorImpuestos orquestadorImpuestos;

    private final OrquestadorDescuentos orquestadorDescuentos;

    private final OrquestadorGestionStock orquestadorGestionStock;

    //CONSTRUCTOR:

    public CrearProductoControlador(
            Map<TipoProducto, EstrategiaCreacionDetalleProducto<?, ?>> estrategiasCreacion,
            OrquestadorImpuestos orquestadorImpuestos, OrquestadorDescuentos orquestadorDescuentos,
            OrquestadorGestionStock orquestadorGestionStock
    ) {
        this.estrategiasCreacion = estrategiasCreacion;
        this.orquestadorImpuestos = orquestadorImpuestos;
        this.orquestadorDescuentos = orquestadorDescuentos;
        this.orquestadorGestionStock = orquestadorGestionStock;
    }

    //MÉTODOS:

    private Window getVentana(){
        return panelAtributosEspecificos.getScene() != null ? panelAtributosEspecificos.getScene().getWindow() : null;
    }

    public void cargarDatos(
            UsuarioDTOCompleto usuarioActual, int idInventario, ObservableList<ProductoResumenDTO> listaObservable
    ) {
        ValidadorSeguridad.exigirPermiso(usuarioActual, PermisosApp.REGISTRAR_PRODUCTOS);
        this.usuarioActual = usuarioActual;
        if (idInventario <= 0 ){
            throw new IllegalArgumentException("El ID recibido NO es Valido.");
        }
        this.idInventario = idInventario;
        this.listaObservable = listaObservable;
    }


    @FXML
    public void initialize() {
        configurarFiltrosTexto();
        configurarComboBoxesGenerales();
        configurarComboBoxTipoProducto();
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

    private void configurarComboBoxTipoProducto() {
        cbTipoProducto.getItems().addAll(TipoProducto.values());
        cbTipoProducto.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                cargarFragmentoEspecifico(newVal);
            }
        });
        cbTipoProducto.getSelectionModel().select(TipoProducto.ROPA);
    }

    private void cargarFragmentoEspecifico(TipoProducto tipo) {
        String rutaFxml;
        switch (tipo){
            case ROPA -> rutaFxml = RutasVista.FORMULARIO_ROPA_VIEW;
            case PERECEDERO -> rutaFxml = RutasVista.FORMULARIO_PERECEDERO_VIEW;
            default -> throw new IllegalStateException(
                    "NO hay un Formulario Especifico para el Tipo de Producto: " + tipo.name()
            );
        }
        CargadorVistas.VistaCargada<FormularioEspecificoControlador> vistaCargada =
                CargadorVistas.cargarFragmentoConInyeccion(rutaFxml, controlador -> {
                    if (controlador instanceof FormularioPerecederoControlador ctrlPerecedero) {
                        if (politicasCache != null) {
                            ctrlPerecedero.cargarPoliticas(politicasCache);
                        } else {
                            ctrlPerecedero.cargarPoliticas().thenAccept(lista -> {
                                this.politicasCache = lista;
                            }).exceptionally(ex -> {
                                Platform.runLater(() -> {
                                    Throwable causa = ConfiguradorExcepciones.obtenerCausaRaiz(ex);
                                    GestorAlertas.mostrarAlertaError(
                                            getVentana(),
                                            "Error al Cargar las Políticas de Vencimiento", null,
                                            "NO se pudieron Cargar las Políticas de Vencimiento.\n" +
                                                    causa.getMessage()
                                    );
                                    cbTipoProducto.getSelectionModel().select(TipoProducto.ROPA);
                                });
                                return null;
                            });
                        }
                    }
                });
        this.controladorHijoActual = vistaCargada.controlador();
        panelAtributosEspecificos.getChildren().setAll(vistaCargada.nodo());
    }

    private void configurarComboBoxesGenerales() {
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
    }

    private void cargarDatosComboBoxes() {
        CompletableFuture<List<ImpuestoDTO>> futureImpuestos =
                CompletableFuture.supplyAsync(this.orquestadorImpuestos::obtenerImpuestosActivos);
        CompletableFuture<List<DescuentoDTO>> futureDescuentos =
                CompletableFuture.supplyAsync(this.orquestadorDescuentos::obtenerDescuentosActivos);
        CompletableFuture.allOf(
                futureImpuestos, futureDescuentos
        ).thenAccept(v -> {
            List<ImpuestoDTO> impuestos = futureImpuestos.join();
            List<DescuentoDTO> descuentos = futureDescuentos.join();
            Platform.runLater(() -> {
                if (!impuestos.isEmpty()) {
                    cbImpuesto.getItems().setAll(impuestos);
                }
                if (!descuentos.isEmpty()) {
                    cbDescuento.getItems().setAll(descuentos);
                }
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
                    "Verifica que los Campos Numéricos (Valor, Ganancia, Stock) " +
                            "Contengan Solo números Válidos y sin Espacios."
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
        TipoProducto tipoProducto = cbTipoProducto.getValue();
        DatosGeneralesCreacionProductoDTO datosGenerales = empaquetarDatosGenerales(
                tipoProducto, nombre, valorCompra, ganancia, stock, impuestoSel.idImpuesto(),
                descuentoSel.idDescuento()
        );
        DetalleCreacionProductoDTO detalle;
        try {
            EstrategiaCreacionDetalleProducto<DetalleCreacionProductoDTO, FormularioEspecificoControlador>
                    estrategiaCreacion = obtenerEstrategia(tipoProducto);
            detalle = estrategiaCreacion.crearDetalle(controladorHijoActual);
        } catch (IllegalStateException e) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Faltan Atributos Específicos", null,
                    e.getMessage()
            );
            return;
        }
        FormularioProductoDTO formularioProducto = new FormularioProductoDTO(datosGenerales, detalle);
        LocalDate fecha = LocalDate.now();
        CompletableFuture.supplyAsync(()->
                this.orquestadorGestionStock.validarEspacioInventarioYGuardarProducto(
                        this.usuarioActual, this.idInventario, formularioProducto, fecha
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

    private DatosGeneralesCreacionProductoDTO empaquetarDatosGenerales(
            TipoProducto tipoProducto, String nombre, BigDecimal valorCompra, BigDecimal ganancia, int stock,
            int idImpuesto, int idDescuento
    ){
        return new DatosGeneralesCreacionProductoDTO(
                tipoProducto, nombre, valorCompra, ganancia, stock, idImpuesto, idDescuento
        );
    }

    @SuppressWarnings("unchecked")
    private <T extends DetalleCreacionProductoDTO, C extends FormularioEspecificoControlador>
    EstrategiaCreacionDetalleProducto<T, C> obtenerEstrategia(TipoProducto tipoProducto) {
        EstrategiaCreacionDetalleProducto<T, C> estrategia =
                (EstrategiaCreacionDetalleProducto<T, C>) estrategiasCreacion.get(tipoProducto);
        if (estrategia == null){
            throw new IllegalStateException(
                    "NO Existe una Estrategia de Creación de Detalle para el Producto: " + tipoProducto
            );
        }
        return estrategia;
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


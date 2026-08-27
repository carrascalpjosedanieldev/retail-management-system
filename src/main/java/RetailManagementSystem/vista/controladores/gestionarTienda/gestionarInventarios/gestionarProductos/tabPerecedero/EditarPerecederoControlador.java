package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarInventarios.gestionarProductos.tabPerecedero;

import RetailManagementSystem.aplicacion.dto.comercial.DatosTotalesProductoPerecederoDTO;
import RetailManagementSystem.aplicacion.dto.gestion.DescuentoDTO;
import RetailManagementSystem.aplicacion.dto.gestion.ImpuestoDTO;
import RetailManagementSystem.aplicacion.dto.gestion.PoliticaVencimientoDTO;
import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorDescuentos;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorImpuestos;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorPoliticaVencimiento;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorProductos;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.vista.utilidades.GestorAlertas;
import RetailManagementSystem.vista.utilidades.UtilidadesLista;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Window;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EditarPerecederoControlador {

    //ATRIBUTOS:

    @FXML private Button btnCancelar;
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

    private ObservableList<DatosTotalesProductoPerecederoDTO> listaPerecederos;

    private final OrquestadorImpuestos orquestadorImpuestos;

    private final OrquestadorDescuentos orquestadorDescuentos;

    private final OrquestadorPoliticaVencimiento orquestadorPoliticaVencimiento;

    private final OrquestadorProductos orquestadorProductos;

    private UsuarioDTOCompleto usuarioActual;

    //CONSTRUCTOR:

    public EditarPerecederoControlador(
            OrquestadorImpuestos orquestadorImpuestos, OrquestadorDescuentos orquestadorDescuentos,
            OrquestadorPoliticaVencimiento orquestadorPoliticaVencimiento, OrquestadorProductos orquestadorProductos
    ) {
        this.orquestadorImpuestos = orquestadorImpuestos;
        this.orquestadorDescuentos = orquestadorDescuentos;
        this.orquestadorPoliticaVencimiento = orquestadorPoliticaVencimiento;
        this.orquestadorProductos = orquestadorProductos;
    }

    //MÉTODOS:

    public void cargarDatos(
            UsuarioDTOCompleto usuarioActual, DatosTotalesProductoPerecederoDTO producto, int idInventario,
            ObservableList<DatosTotalesProductoPerecederoDTO> listaPerecederos
    ) {
        if (usuarioActual == null){
            throw new IllegalArgumentException("Usuario Nulo, Error al Recibir el Usuario");
        }
        if (!usuarioActual.tienePermiso(PermisosApp.ADMINISTRAR_PRODUCTOS)){
            GestorAlertas.mostrarAlertaError(
                    getVentana(),
                    "Acceso Denegado", "Privilegios Insuficientes",
                    "NO tienes los Permisos Necesarios para Editar Productos."
            );
            return;
        }
        this.usuarioActual = usuarioActual;
        this.productoOriginal = producto;
        this.idInventario = idInventario;
        this.listaPerecederos = listaPerecederos;
        txtCodigo.setText(producto.codigo());
        txtNombre.setText(producto.nombre());
        txtCompra.setText(String.valueOf(producto.valorCompra()));
        txtGanancia.setText(String.valueOf(producto.porcentajeGanancia()));
        txtStock.setText(String.valueOf(producto.stock()));
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        txtFechaVencimiento.setText(producto.fechaVencimiento().format(formatoFecha));
        Platform.runLater(()-> btnCancelar.requestFocus());
    }

    private Window getVentana(){
        return btnCancelar.getScene() != null ? btnCancelar.getScene().getWindow() : null;
    }


    @FXML
    public void initialize() {
        configurarFiltrosTexto();
        configurarFormatoComboBoxes();
        cargarListasDesplegables();
    }

    private void configurarFiltrosTexto() {
        String regexDecimal = "^[0-9]*\\.?[0-9]*$";
        txtCompra.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().matches(regexDecimal) ? change : null));
        txtGanancia.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().matches(regexDecimal) ? change : null));
    }

    private void configurarFormatoComboBoxes() {
        cbImpuesto.setPromptText("Seleccione un Impuesto...");
        cbDescuento.setPromptText("Seleccione un Descuento...");
        cbPoliticaVencimiento.setPromptText("Seleccione una Política...");
        cbImpuesto.setConverter(new StringConverter<>() {
            @Override
            public String toString(ImpuestoDTO i) {
                return i == null ? "" : i.nombre() + " (" + i.porcentaje() + "%)";
            }
            @Override
            public ImpuestoDTO fromString(String s) { return null; }
        });
        cbDescuento.setConverter(new StringConverter<>() {
            @Override
            public String toString(DescuentoDTO d) {
                return d == null ? "" : d.nombre() + " (" + d.porcentaje() + "%)";
            }
            @Override
            public DescuentoDTO fromString(String s) { return null; }
        });
        cbPoliticaVencimiento.setConverter(new StringConverter<>() {
            @Override
            public String toString(PoliticaVencimientoDTO p) {
                return p == null ? "" : p.nombrePolitica();
            }
            @Override
            public PoliticaVencimientoDTO fromString(String s) { return null; }
        });
    }

    private void preseleccionarComboBoxes() {
        if (this.productoOriginal == null) return;
        cbPoliticaVencimiento.getItems().stream()
                .filter(p -> p.idPoliticaVencimiento() == productoOriginal.datosPoliticaVencimiento().idPoliticaVencimiento())
                .findFirst()
                .ifPresent(cbPoliticaVencimiento.getSelectionModel()::select);
        cbImpuesto.getItems().stream()
                .filter(i -> i.idImpuesto() == productoOriginal.datosImpuesto().idImpuesto())
                .findFirst()
                .ifPresent(cbImpuesto.getSelectionModel()::select);
        cbDescuento.getItems().stream()
                .filter(d -> d.idDescuento() == productoOriginal.datosDescuento().idDescuento())
                .findFirst()
                .ifPresent(cbDescuento.getSelectionModel()::select);
    }

    private void cargarListasDesplegables() {
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
                if (!politicas.isEmpty()) cbPoliticaVencimiento.getItems().setAll(politicas);
                preseleccionarComboBoxes();
            });
        }).exceptionally(ex -> {
            Platform.runLater(() -> {
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
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
    private void guardarCambios(ActionEvent event) {
        guardarCambios();
    }

    private void guardarCambios(){
        String nombreNuevo = txtNombre.getText().trim();
        String valorCompraTexto = txtCompra.getText().trim();
        String porcentajeGananciaTexto = txtGanancia.getText().trim();
        if (nombreNuevo.isEmpty() || valorCompraTexto.isEmpty() || porcentajeGananciaTexto.isEmpty()) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Campos Incompletos", null,
                    "El Nombre, Valor Compra o Porcentaje Ganancia del Producto NO pueden estar Vacíos."
            );
            return;
        }
        BigDecimal valorCompra;
        BigDecimal porcentajeGanancia;
        try {
            valorCompra = new BigDecimal(valorCompraTexto);
            porcentajeGanancia = new BigDecimal(porcentajeGananciaTexto);
        } catch (NumberFormatException e) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Datos Inválidos", null,
                    "Por favor, Verifique que los campos de Valor de Compra y Ganancia contengan únicamente Números Válidos."
            );
            return;
        }
        ImpuestoDTO impuestoSeleccionado = cbImpuesto.getValue();
        DescuentoDTO descuentoSeleccionado = cbDescuento.getValue();
        PoliticaVencimientoDTO politicaSeleccionada = cbPoliticaVencimiento.getValue();
        if (impuestoSeleccionado == null || descuentoSeleccionado == null || politicaSeleccionada == null) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Selección Requerida", null,
                    "Debe seleccionar un Impuesto, un Descuento y una Política de Vencimiento para el producto."
            );
            return;
        }
        CompletableFuture.supplyAsync(()->
            this.orquestadorProductos.actualizarProductoPerecederoDeInventario(
                    this.usuarioActual,
                    this.idInventario,
                    this.productoOriginal.codigo(),
                    nombreNuevo,
                    valorCompra,
                    porcentajeGanancia,
                    impuestoSeleccionado.idImpuesto(),
                    descuentoSeleccionado.idDescuento(),
                    politicaSeleccionada.idPoliticaVencimiento(),
                    LocalDate.now()
            )
        ).thenAccept(perecederoActualizado->
            Platform.runLater(()-> {
                UtilidadesLista.reemplazarPorIdentidad(
                        this.listaPerecederos,
                        perecederoActualizado,
                        item -> item.codigo().equals(perecederoActualizado.codigo())
                );
                GestorAlertas.mostrarAlertaInformacion(
                        getVentana(), "Actualización Exitosa", null,
                        "El Producto Perecedero se ha Actualizado Correctamente."
                );
                cerrarVentana();
            })
        ).exceptionally(ex->{
            Platform.runLater(()->{
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                if (causa instanceof IllegalArgumentException ||
                    causa instanceof IllegalStateException){
                    GestorAlertas.mostrarAlertaError(
                            getVentana(), "Error en los Datos", null,
                            "Error:  " + causa.getMessage()
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


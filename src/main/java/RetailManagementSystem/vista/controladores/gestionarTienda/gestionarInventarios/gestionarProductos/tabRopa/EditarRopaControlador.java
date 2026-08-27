package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarInventarios.gestionarProductos.tabRopa;

import RetailManagementSystem.aplicacion.dto.comercial.DatosTotalesProductoRopaDTO;
import RetailManagementSystem.aplicacion.dto.gestion.DescuentoDTO;
import RetailManagementSystem.aplicacion.dto.gestion.ImpuestoDTO;
import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorDescuentos;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorImpuestos;
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
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EditarRopaControlador {

    //ATRIBUTOS:

    @FXML private Button btnCancelar;
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

    private ObservableList<DatosTotalesProductoRopaDTO> listaRopa;

    private final OrquestadorImpuestos orquestadorImpuestos;

    private final OrquestadorDescuentos orquestadorDescuentos;

    private final OrquestadorProductos orquestadorProductos;

    private UsuarioDTOCompleto usuarioActual;

    //CONSTRUCTOR:

    public EditarRopaControlador(
            OrquestadorImpuestos orquestadorImpuestos, OrquestadorDescuentos orquestadorDescuentos,
            OrquestadorProductos orquestadorProductos
    ) {
        this.orquestadorImpuestos = orquestadorImpuestos;
        this.orquestadorDescuentos = orquestadorDescuentos;
        this.orquestadorProductos = orquestadorProductos;
    }


    //MÉTODOS:

    public void cargarDatosProducto(
            UsuarioDTOCompleto usuarioActual, DatosTotalesProductoRopaDTO producto, int idInventario,
            ObservableList<DatosTotalesProductoRopaDTO> listaRopa
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
        this.listaRopa = listaRopa;
        txtCodigo.setText(producto.codigo());
        txtTalla.setText(producto.talla().name());
        txtNombre.setText(producto.nombre());
        txtCompra.setText(String.valueOf(producto.valorCompra()));
        txtGanancia.setText(String.valueOf(producto.porcentajeGanancia()));
        txtStock.setText(String.valueOf(producto.stock()));
        Platform.runLater(()-> btnCancelar.requestFocus());
    }

    private Window getVentana(){
        return btnCancelar.getScene() != null ? btnCancelar.getScene().getWindow() : null;
    }


    @FXML
    public void initialize() {
        configurarFiltrosTexto();
        configurarVisualizacionCombos();
        cargarDatosCombos();
    }

    private void configurarFiltrosTexto() {
        String regexDecimal = "^[0-9]*\\.?[0-9]*$";
        txtCompra.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().matches(regexDecimal) ? change : null));
        txtGanancia.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().matches(regexDecimal) ? change : null));
    }

    private void preseleccionarComboBoxes() {
        if (this.productoOriginal == null) return;
        cbImpuesto.getItems().stream()
                .filter(i -> i.idImpuesto() == productoOriginal.datosImpuesto().idImpuesto())
                .findFirst()
                .ifPresent(cbImpuesto.getSelectionModel()::select);
        cbDescuento.getItems().stream()
                .filter(d -> d.idDescuento() == productoOriginal.datosDescuento().idDescuento())
                .findFirst()
                .ifPresent(cbDescuento.getSelectionModel()::select);
    }

    private void cargarDatosCombos() {
        CompletableFuture<List<ImpuestoDTO>> futureImpuestos =
                CompletableFuture.supplyAsync(this.orquestadorImpuestos::obtenerImpuestosActivos);
        CompletableFuture<List<DescuentoDTO>> futureDescuentos =
                CompletableFuture.supplyAsync(this.orquestadorDescuentos::obtenerDescuentosActivos);
        CompletableFuture.allOf(
                futureImpuestos, futureDescuentos
        ).thenAccept(v->{
            List<ImpuestoDTO> impuestos = futureImpuestos.join();
            List<DescuentoDTO> descuentos = futureDescuentos.join();
            Platform.runLater(()->{
                if (!impuestos.isEmpty()) cbImpuesto.getItems().setAll(impuestos);
                if (!descuentos.isEmpty()) cbDescuento.getItems().setAll(descuentos);
                preseleccionarComboBoxes();
            });
        }).exceptionally(ex->{
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

    private void configurarVisualizacionCombos() {
        cbImpuesto.setPromptText("Seleccione un Impuesto...");
        cbDescuento.setPromptText("Seleccione un Descuento...");
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
                    "El Nombre de la Prenda NO puede estar Vacío."
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
                    "Por favor, verifique que los campos de Valor de Compra, Ganancia y Stock contengan números válidos."
            );
            return;
        }
        ImpuestoDTO impuestoSeleccionado = cbImpuesto.getValue();
        DescuentoDTO descuentoSeleccionado = cbDescuento.getValue();
        if (impuestoSeleccionado == null || descuentoSeleccionado == null) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Selección Requerida", null,
                    "Debe seleccionar un Impuesto, un Descuento para el Producto."
            );
            return;
        }
        CompletableFuture.supplyAsync(()->
                this.orquestadorProductos.actualizarProductoRopaDeInventario(
                this.usuarioActual,
                this.idInventario,
                this.productoOriginal.codigo(),
                nombreNuevo,
                valorCompra,
                porcentajeGanancia,
                impuestoSeleccionado.idImpuesto(),
                descuentoSeleccionado.idDescuento()
                )
        ).thenAccept(ropaActualizada->
            Platform.runLater(()->{
                UtilidadesLista.reemplazarPorIdentidad(
                        listaRopa,
                        ropaActualizada,
                        item -> item.codigo().equals(ropaActualizada.codigo())
                );
                GestorAlertas.mostrarAlertaInformacion(
                        getVentana(), "Actualización Exitosa", null,
                    "La Prenda se ha Actualizado Correctamente."
                );
                cerrarVentana();
            })
        ).exceptionally(ex->{
            Platform.runLater(()->{
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                if (causa instanceof  IllegalArgumentException ||
                    causa instanceof  IllegalStateException){
                    GestorAlertas.mostrarAlertaError(
                            getVentana(), "Error en los Datos Ingresados", null,
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

    private void cerrarVentana() {
        Window ventana = getVentana();
        if (ventana != null){
            ventana.hide();
        }
    }


    @FXML
    private void cancelar(ActionEvent event) {
        cerrarVentana();
    }


}//===================================================================================================================//


package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarInventarios.gestionarProductos.tabRopa;

import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorProductos;
import RetailManagementSystem.dominio.enums.Talla;
import RetailManagementSystem.aplicacion.dto.comercial.DatosTotalesProductoRopaDTO;
import RetailManagementSystem.aplicacion.dto.gestion.DescuentoDTO;
import RetailManagementSystem.aplicacion.dto.gestion.ImpuestoDTO;
import RetailManagementSystem.dominio.excepciones.autenticacionYSeguridad.AccesoDenegadoException;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.infraestructura.seguridad.ValidadorSeguridad;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarInventarios.GestionInventariosControlador;
import RetailManagementSystem.vista.utilidades.*;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Window;
import javafx.util.Duration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TabRopaControlador {

    //ATRIBUTOS:

    @FXML private TextField txtBuscar;
    @FXML private TableView<DatosTotalesProductoRopaDTO> tablaRopa;
    @FXML private TableColumn<DatosTotalesProductoRopaDTO, String> colCodigo;
    @FXML private TableColumn<DatosTotalesProductoRopaDTO, String> colNombre;
    @FXML private TableColumn<DatosTotalesProductoRopaDTO, Talla> colTalla;
    @FXML private TableColumn<DatosTotalesProductoRopaDTO, BigDecimal> colCompra;
    @FXML private TableColumn<DatosTotalesProductoRopaDTO, BigDecimal> colGanancia;
    @FXML private TableColumn<DatosTotalesProductoRopaDTO, ImpuestoDTO> colImpuesto;
    @FXML private TableColumn<DatosTotalesProductoRopaDTO, DescuentoDTO> colDescuento;
    @FXML private TableColumn<DatosTotalesProductoRopaDTO, BigDecimal> colVentaFinal;
    @FXML private TableColumn<DatosTotalesProductoRopaDTO, Integer> colStock;
    @FXML private TableColumn<DatosTotalesProductoRopaDTO, String> colEstado;

    private int idInventario;

    private final OrquestadorProductos orquestadorProductos;

    private final ObservableList<DatosTotalesProductoRopaDTO> listaObservable = FXCollections.observableArrayList();

    private FilteredList<DatosTotalesProductoRopaDTO> listaFiltrada;

    private UsuarioDTOCompleto usuarioActual;

    //CONSTRUCTOR:

    public TabRopaControlador(OrquestadorProductos orquestadorProductos) {
        this.orquestadorProductos = orquestadorProductos;
    }

    //MÉTODOS:

    public void recibirIdInventarioYUsuario(UsuarioDTOCompleto usuarioActual, int idInventario) {
        ValidadorSeguridad.exigirAlgunPermiso(usuarioActual, List.of(
                PermisosApp.VER_PRODUCTOS,
                PermisosApp.EDITAR_PRODUCTO
        ));
        this.usuarioActual = usuarioActual;
        this.idInventario = idInventario;
        cargarDatosTabla();
    }

    private Window getVentana(){
        return tablaRopa.getScene() != null ? tablaRopa.getScene().getWindow() : null;
    }


    private void cargarDatosTabla() {
        CompletableFuture.supplyAsync(()->
                this.orquestadorProductos.obtenerProductosRopaDeInventario(this.idInventario, LocalDate.now())
        ).thenAccept(listaRopa->
            Platform.runLater(()->{
                listaObservable.clear();
                listaObservable.setAll(listaRopa);
            })
        ).exceptionally(ex->{
            Platform.runLater(()->{
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                GestorAlertas.mostrarAlertaError(
                        getVentana(), "Error Crítico de Carga",
                        "NO se pudieron Cargar los Datos del Inventario.",
                        "Ocurrió un Error al Cargar los Productos Ropa. La Ventana se Cerrará por Seguridad.\n" +
                                "Verifica tu Conexión y Notificale este Error al Administrador:\n" +
                                causa.getMessage()
                );
                try {
                    CargadorVistas.cambiarPantallaInyectada(
                            getVentana(),
                            RutasVista.GESTIONAR_INVENTARIOS_VIEW,
                            (GestionInventariosControlador c) -> {
                                c.cargarDatos(this.usuarioActual);
                            }
                    );
                } catch (AccesoDenegadoException exc){
                    GestorAlertas.mostrarAlertaAccesoDenegado(getVentana(), exc);
                }
            });
            return null;
        });
    }


    @FXML
    public void initialize() {
        configurarColumnas();
        configurarFiltro();
    }

    private void configurarColumnas() {
        colCodigo.setCellValueFactory(celda -> new SimpleStringProperty(
                celda.getValue().codigo())
        );
        colCodigo.setCellFactory(columna -> new TableCell<>() {
            private final Tooltip tooltipFlotante = new Tooltip();
            {
                tooltipFlotante.getStyleClass().add("tooltip-codigo");
                getStyleClass().add("codigo-copiable");
                tooltipFlotante.setShowDelay(Duration.millis(100));
                setAlignment(Pos.CENTER);
            }
            @Override
            protected void updateItem(String codigo, boolean empty) {
                super.updateItem(codigo, empty);
                if (empty || codigo == null) {
                    setText(null);
                    setTooltip(null);
                    setOnMouseClicked(null);
                    setStyle("");
                } else {
                    String codigoCorto = codigo.length() > 10 ? codigo.substring(0, 10) + "..." : codigo;
                    setText(codigoCorto);
                    tooltipFlotante.setText(codigo + "\n(Clic para copiar)");
                    setTooltip(tooltipFlotante);
                    setOnMouseClicked(evt -> {
                        ClipboardContent contenido = new ClipboardContent();
                        contenido.putString(codigo);
                        Clipboard.getSystemClipboard().setContent(contenido);
                    });
                }
            }
        });
        colNombre.setCellValueFactory(celda -> new SimpleStringProperty(
                celda.getValue().nombre())
        );
        colStock.setCellValueFactory(celda -> new SimpleIntegerProperty(
                celda.getValue().stock()).asObject()
        );
        colTalla.setCellValueFactory(celda -> new SimpleObjectProperty<>(
                celda.getValue().talla())
        );
        colTalla.setCellFactory(col -> new TableCell<>() {
            {
                setAlignment(Pos.CENTER);
            }
            @Override
            protected void updateItem(Talla talla, boolean empty) {
                super.updateItem(talla, empty);
                setText((empty || talla == null) ? null : talla.name());
            }
        });
        colCompra.setCellValueFactory(celda -> new SimpleObjectProperty<>(
                celda.getValue().valorCompra())
        );
        colCompra.setCellFactory(col -> crearCeldaMoneda());
        colVentaFinal.setCellValueFactory(celda -> new SimpleObjectProperty<>(
                celda.getValue().valorVentaFinal())
        );
        colVentaFinal.setCellFactory(col -> crearCeldaMoneda());
        colGanancia.setCellValueFactory(celda -> new SimpleObjectProperty<>(
                celda.getValue().porcentajeGanancia())
        );
        colGanancia.setCellFactory(col -> new TableCell<>() {
            {
                setAlignment(Pos.CENTER);
            }
            @Override
            protected void updateItem(BigDecimal ganancia, boolean empty) {
                super.updateItem(ganancia, empty);
                setText((empty || ganancia == null) ? null : ganancia.toPlainString() + "%");
            }
        });
        colImpuesto.setCellValueFactory(celda -> new SimpleObjectProperty<>(celda.getValue().datosImpuesto()));
        colImpuesto.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(ImpuestoDTO imp, boolean empty) {
                super.updateItem(imp, empty);
                setText((empty || imp == null) ? null : imp.nombre() + " (" + imp.porcentaje() + "%)");
            }
        });
        colDescuento.setCellValueFactory(celda -> new SimpleObjectProperty<>(celda.getValue().datosDescuento()));
        colDescuento.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(DescuentoDTO desc, boolean empty) {
                super.updateItem(desc, empty);
                setText((empty || desc == null) ? null : desc.nombre() + " (" + desc.porcentaje() + "%)");
            }
        });
        colStock.setCellValueFactory(celda -> new SimpleIntegerProperty(celda.getValue().stock()).asObject());
        colStock.setCellFactory(columna -> new TableCell<>() {
            {
                setAlignment(Pos.CENTER);
            }
            @Override
            protected void updateItem(Integer stock, boolean empty) {
                super.updateItem(stock, empty);
                getStyleClass().removeAll("stock-sin-existencias", "stock-bajo", "stock-normal");
                if (empty || stock == null) {
                    setText(null);
                    return;
                }
                setText(String.valueOf(stock));
                if (stock <= 0) {
                    getStyleClass().add("stock-sin-existencias");
                } else if (stock <= 5) {
                    getStyleClass().add("stock-bajo");
                } else {
                    getStyleClass().add("stock-normal");
                }
            }
        });
        colEstado.setCellValueFactory(celda -> {
            boolean esActivo = celda.getValue().activo();
            String estado = esActivo ? "Disponible" : "NO Disponible";
            return new SimpleStringProperty(estado);
        });
        colEstado.setCellFactory(columna -> new TableCell<>() {
            {
                setAlignment(Pos.CENTER);
            }
            @Override
            protected void updateItem(String estado, boolean empty) {
                super.updateItem(estado, empty);
                getStyleClass().removeAll("estado-disponible", "estado-no-disponible");
                if (empty || estado == null) {
                    setText(null);
                    return;
                }
                setText(estado);
                if (estado.equalsIgnoreCase("Disponible")) {
                    getStyleClass().add("estado-disponible");
                } else {
                    getStyleClass().add("estado-no-disponible");
                }
            }
        });
    }

    private TableCell<DatosTotalesProductoRopaDTO, BigDecimal> crearCeldaMoneda() {
        return new TableCell<>() {
            {
                setAlignment(Pos.CENTER);
            }
            @Override
            protected void updateItem(BigDecimal precio, boolean empty) {
                super.updateItem(precio, empty);
                setText((empty || precio == null) ? null : FormateadorNumeros.formatoMoneda(precio));
            }
        };
    }

    private void configurarFiltro() {
        listaFiltrada = new FilteredList<>(listaObservable, b -> true);
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> {
            listaFiltrada.setPredicate(ropa -> {
                if (newVal == null || newVal.isBlank()) return true;
                String filtro = newVal.toLowerCase().trim();
                return ropa.nombre().toLowerCase().contains(filtro) ||
                        ropa.codigo().toLowerCase().contains(filtro) ||
                        ropa.talla().name().toLowerCase().contains(filtro);
            });
        });
        SortedList<DatosTotalesProductoRopaDTO> listaOrdenada = new SortedList<>(listaFiltrada);
        listaOrdenada.comparatorProperty().bind(tablaRopa.comparatorProperty());
        tablaRopa.setItems(listaOrdenada);
    }


    @FXML
    private void abrirEditorRopa(ActionEvent event) {
        DatosTotalesProductoRopaDTO productoSeleccionado = tablaRopa.getSelectionModel().getSelectedItem();
        if (productoSeleccionado == null) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Selección requerida", null,
                    "Por favor, Seleccione una Prenda de Ropa en la Tabla para Editarla."
            );
            return;
        }
        try {
            CargadorVistas.abrirModalConInyeccion(
                    RutasVista.EDITAR_ROPA_VIEW,
                    "Editar Prenda de Ropa", getVentana(),
                    (EditarRopaControlador c)->{
                        c.cargarDatosProducto(this.usuarioActual, productoSeleccionado, this.idInventario, listaObservable);
                    }
            );
        } catch (AccesoDenegadoException ex){
            GestorAlertas.mostrarAlertaAccesoDenegado(getVentana(), ex);
        }
    }


    @FXML
    private void cambiarEstadoProducto(ActionEvent event) {
        cambiarEstadoProducto();
    }

    private void cambiarEstadoProducto(){
        DatosTotalesProductoRopaDTO seleccionado = tablaRopa.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Selección Requerida", null,
                    "Por favor, Seleccione una Prenda de Ropa en la Tabla para Cambiar su Estado."
            );
            return;
        }
        if (!GestorAlertas.mostrarConfirmacion(
                getVentana(), "Confirmar Cambio de Estado", null,
                "¿Está Seguro que desea Cambiar el Estado del Producto:\n"
                        + seleccionado.codigo() + " - " + seleccionado.nombre() + "?")
        ){
            return;
        }
        CompletableFuture.runAsync(()->
                this.orquestadorProductos.cambiarEstadoProducto(
                        this.usuarioActual, this.idInventario, seleccionado.codigo()
                )
        ).thenRun(()->
            Platform.runLater(()->{
                DatosTotalesProductoRopaDTO actualizado = new DatosTotalesProductoRopaDTO(
                        seleccionado.codigo(),
                        seleccionado.nombre(),
                        seleccionado.valorCompra(),
                        seleccionado.porcentajeGanancia(),
                        seleccionado.valorVentaFinal(),
                        seleccionado.stock(),
                        seleccionado.datosImpuesto(),
                        seleccionado.datosDescuento(),
                        seleccionado.talla(),
                        !seleccionado.activo()
                );
                UtilidadesLista.reemplazarPorIdentidad(
                        listaObservable,
                        actualizado,
                        item-> item.codigo().equals(actualizado.codigo())
                );
                GestorAlertas.mostrarAlertaInformacion(
                        getVentana(), "Estado Actualizado", null,
                        "El Estado del Producto se Actualizó Correctamente."
                );
            })
        ).exceptionally(ex->{
            Platform.runLater(()->{
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                GestorAlertas.mostrarAlertaError(
                        getVentana(), "NO se pudo Completar la Acción", null,
                        "Error:  " + causa.getMessage()
                );
            });
            return null;
        });
    }


}//===================================================================================================================//


package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarInventarios.gestionarProductos.tabPerecedero;

import RetailManagementSystem.aplicacion.dto.comercial.DatosTotalesProductoPerecederoDTO;
import RetailManagementSystem.aplicacion.dto.gestion.DescuentoDTO;
import RetailManagementSystem.aplicacion.dto.gestion.ImpuestoDTO;
import RetailManagementSystem.aplicacion.dto.gestion.PoliticaVencimientoDTO;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorProductos;
import RetailManagementSystem.vista.utilidades.CargadorVistas;
import RetailManagementSystem.vista.utilidades.FormateadorNumeros;
import RetailManagementSystem.vista.utilidades.GestorAlertas;
import RetailManagementSystem.vista.utilidades.RutasVista;

import javafx.application.Platform;
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
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

public class TabPerecederosControlador {

    //ATRIBUTOS:

    @FXML private TextField txtBuscar;
    @FXML private TableView<DatosTotalesProductoPerecederoDTO> tablaPerecederos;
    @FXML private TableColumn<DatosTotalesProductoPerecederoDTO, String> colCodigo;
    @FXML private TableColumn<DatosTotalesProductoPerecederoDTO, String> colNombre;
    @FXML private TableColumn<DatosTotalesProductoPerecederoDTO, LocalDate> colFechaVencimiento;
    @FXML private TableColumn<DatosTotalesProductoPerecederoDTO, String> colPoliticaVencimiento;
    @FXML private TableColumn<DatosTotalesProductoPerecederoDTO, String> colEstaVencido;
    @FXML private TableColumn<DatosTotalesProductoPerecederoDTO, BigDecimal> colCompra;
    @FXML private TableColumn<DatosTotalesProductoPerecederoDTO, BigDecimal> colGanancia;
    @FXML private TableColumn<DatosTotalesProductoPerecederoDTO, String> colImpuesto;
    @FXML private TableColumn<DatosTotalesProductoPerecederoDTO, String> colDescuento;
    @FXML private TableColumn<DatosTotalesProductoPerecederoDTO, BigDecimal> colVentaFinal;
    @FXML private TableColumn<DatosTotalesProductoPerecederoDTO, Integer> colStock;
    @FXML private TableColumn<DatosTotalesProductoPerecederoDTO, String> colDisponible;

    private int idInventario;

    private final OrquestadorProductos orquestadorProductos;

    private final ObservableList<DatosTotalesProductoPerecederoDTO> listaMaestraPerecederos = FXCollections.observableArrayList();

    private FilteredList<DatosTotalesProductoPerecederoDTO> listaFiltrada;

    //CONSTRUCTOR:

    public TabPerecederosControlador(OrquestadorProductos orquestadorProductos) {
        this.orquestadorProductos = orquestadorProductos;
    }

    //MÉTODOS:

    private Window getVentana(){
        return tablaPerecederos.getScene().getWindow();
    }


    public void recibirIdInventario(int idInventario) {
        this.idInventario = idInventario;
        cargarDatosTabla();
    }

    private void cargarDatosTabla() {
        CompletableFuture.supplyAsync(()->
                this.orquestadorProductos.obtenerProductosPerecederosDeInventario(this.idInventario)
        ).thenAccept(listaPerecederos->
            Platform.runLater(()->{
                listaMaestraPerecederos.clear();
                if (listaPerecederos != null && !listaPerecederos.isEmpty()) {
                    listaMaestraPerecederos.addAll(listaPerecederos);
                }
            })
        ).exceptionally(ex->{
            Platform.runLater(()->{
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                GestorAlertas.mostrarAlertaError(
                        getVentana(), "Error Crítico de Carga",
                        "No se pudieron cargar los datos del inventario.",
                        "Ocurrió un Error al cargar los Productos Perecederos. La Ventana se Cerrará por Seguridad.\n" +
                                "Verifica tu Conexión y Notificale este Error al Administrador:\n" +
                                causa.getMessage()
                );
                Stage stageActual = (Stage) getVentana();
                CargadorVistas.cambiarPantalla(stageActual, RutasVista.GESTIONAR_INVENTARIOS_VIEW);
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
        colCodigo.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().codigo())
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
        colNombre.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().nombre())
        );
        colEstaVencido.setCellValueFactory(cellData -> {
            boolean estaVencido = cellData.getValue().estaVencido();
            String estado = estaVencido ? "Vencido" : "Vigente";
            return new SimpleStringProperty(estado);
        });
        colEstaVencido.setCellFactory(col -> new TableCell<>() {
            {
                setAlignment(Pos.CENTER);
            }
            @Override
            protected void updateItem(String estadoVencido, boolean empty) {
                super.updateItem(estadoVencido, empty);
                getStyleClass().removeAll("estado-vencido", "estado-al-dia");
                if (empty || estadoVencido == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(estadoVencido);
                    boolean esVencido = estadoVencido.equalsIgnoreCase("Vencido");
                    if (esVencido) {
                        getStyleClass().add("estado-vencido");
                    } else {
                        getStyleClass().add("estado-al-dia");
                    }
                }
            }
        });
        colStock.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                cellData.getValue().stock())
        );
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
        colDisponible.setCellValueFactory(cellData -> {
            boolean esActivo = cellData.getValue().activo();
            String disponible = esActivo ? "Disponible" : "NO Disponible";
            return new SimpleStringProperty(disponible);
        });
        colDisponible.setCellFactory(columna -> new TableCell<>() {
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
        colFechaVencimiento.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                cellData.getValue().fechaVencimiento())
        );
        colPoliticaVencimiento.setCellValueFactory(cellData -> {
            PoliticaVencimientoDTO politica = cellData.getValue().datosPoliticaVencimiento();
            return new SimpleStringProperty(politica.nombrePolitica());
        });
        colImpuesto.setCellValueFactory(cellData -> {
            ImpuestoDTO impuesto = cellData.getValue().datosImpuesto();
            return new SimpleStringProperty(impuesto.nombre() + " (" + impuesto.porcentaje() + "%)");
        });
        colDescuento.setCellValueFactory(cellData -> {
            DescuentoDTO descuento = cellData.getValue().datosDescuento();
            return new SimpleStringProperty(descuento.nombre() + " (" + descuento.porcentaje() + "%)");
        });
        colCompra.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                cellData.getValue().valorCompra())
        );
        colGanancia.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                cellData.getValue().porcentajeGanancia())
        );
        colVentaFinal.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                cellData.getValue().valorVentaFinal())
        );
        formatearColumnaMoneda(colCompra);
        formatearColumnaMoneda(colVentaFinal);
        colGanancia.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                cellData.getValue().porcentajeGanancia())
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
    }

    private void formatearColumnaMoneda(TableColumn<DatosTotalesProductoPerecederoDTO, BigDecimal> columna) {
        columna.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal precio, boolean empty) {
                super.updateItem(precio, empty);
                if (empty || precio == null) {
                    setText(null);
                } else {
                    setText(FormateadorNumeros.formatoMoneda(precio));
                }
            }
        });
    }

    private void configurarFiltro() {
        listaFiltrada = new FilteredList<>(listaMaestraPerecederos, p -> true);
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            listaFiltrada.setPredicate(producto -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }
                String textoBusqueda = newValue.toLowerCase().trim();
                boolean coincideCodigo = producto.codigo() != null && producto.codigo().toLowerCase().contains(textoBusqueda);
                boolean coincideNombre = producto.nombre() != null && producto.nombre().toLowerCase().contains(textoBusqueda);
                return coincideCodigo || coincideNombre;
            });
        });
        SortedList<DatosTotalesProductoPerecederoDTO> listaOrdenada = new SortedList<>(listaFiltrada);
        listaOrdenada.comparatorProperty().bind(tablaPerecederos.comparatorProperty());
        tablaPerecederos.setItems(listaOrdenada);
    }


    @FXML
    void abrirEditorPerecedero(ActionEvent event) {
        DatosTotalesProductoPerecederoDTO productoSeleccionado = tablaPerecederos.getSelectionModel().getSelectedItem();
        if (productoSeleccionado == null) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Selección Requerida", null,
                    "Por favor, Seleccione un Producto Perecedero en la Tabla para Editarlo."
            );
            return;
        }
        CargadorVistas.abrirModalConInyeccion(
                RutasVista.EDITAR_PERECEDERO_VIEW,
                "Editar Producto Perecedero", getVentana(),
                (EditarPerecederoControlador c)->{
                    c.cargarDatos(productoSeleccionado, this.idInventario, listaMaestraPerecederos);
                }
        );
    }


    @FXML
    void cambiarEstadoProducto(ActionEvent event) {
        cambiarEstadoProducto();
    }

    private void cambiarEstadoProducto(){
        DatosTotalesProductoPerecederoDTO seleccionado = this.tablaPerecederos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Selección Requerida", null,
                    "Por favor, Seleccione un Producto Perecedero de la Tabla para Cambiar su Estado."
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
                this.orquestadorProductos.cambiarEstadoProducto(this.idInventario, seleccionado.codigo())
        ).thenRun(()->
            Platform.runLater(()->{
                DatosTotalesProductoPerecederoDTO actualizado = new DatosTotalesProductoPerecederoDTO(
                        seleccionado.codigo(),
                        seleccionado.nombre(),
                        seleccionado.valorCompra(),
                        seleccionado.porcentajeGanancia(),
                        seleccionado.valorVentaFinal(),
                        seleccionado.stock(),
                        seleccionado.datosImpuesto(),
                        seleccionado.datosDescuento(),
                        seleccionado.fechaVencimiento(),
                        seleccionado.datosPoliticaVencimiento(),
                        seleccionado.estaVencido(),
                        !seleccionado.activo()
                );
                int indice = listaMaestraPerecederos.indexOf(seleccionado);
                listaMaestraPerecederos.set(indice, actualizado);
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


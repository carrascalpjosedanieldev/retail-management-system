package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarInventarios.gestionarProductos;

import RetailManagementSystem.aplicacion.dto.comercial.DatosTotalesProductoPerecederoDTO;
import RetailManagementSystem.aplicacion.dto.gestion.DescuentoDTO;
import RetailManagementSystem.aplicacion.dto.gestion.ImpuestoDTO;
import RetailManagementSystem.aplicacion.dto.gestion.PoliticaVencimientoDTO;
import RetailManagementSystem.aplicacion.servicios.ServicioProductos;
import RetailManagementSystem.aplicacion.ensambladores.EnsambladorDTOProducto;
import RetailManagementSystem.vista.excepciones.CargarVistaException;
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
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
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

    private final ServicioProductos servicioProductos;

    private final EnsambladorDTOProducto ensambladorDTOProducto;

    private final ObservableList<DatosTotalesProductoPerecederoDTO> listaMaestraPerecederos = FXCollections.observableArrayList();

    private FilteredList<DatosTotalesProductoPerecederoDTO> listaFiltrada;

    //CONSTRUCTOR:

    public TabPerecederosControlador(ServicioProductos servicioProductos, EnsambladorDTOProducto ensambladorDTOProducto) {
        this.servicioProductos = servicioProductos;
        this.ensambladorDTOProducto = ensambladorDTOProducto;
    }

    //MÉTODOS:

    public void recibirIdInventario(int idInventario) {
        this.idInventario = idInventario;
        cargarDatosTabla();
    }


    @FXML
    public void initialize() {
        configurarColumnas();
        configurarFiltro();
    }

    private void configurarColumnas() {
        colCodigo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().codigo()));
        colCodigo.setCellFactory(columna -> new TableCell<>() {
            private final Tooltip tooltipFlotante = new Tooltip();
            {
                tooltipFlotante.setStyle("-fx-background-color: #1e293b; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 5px 10px;");
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
                    setStyle("-fx-cursor: hand; -fx-text-fill: #3b82f6;");
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
            boolean esActivo = cellData.getValue().estaVencido();
            String estado = esActivo ? "Activo" : "Inactivo";
            return new SimpleStringProperty(estado);
        });
        colEstaVencido.setCellFactory(col -> new TableCell<>() {
            {
                setAlignment(Pos.CENTER);
            }
            @Override
            protected void updateItem(String estadoVencido, boolean empty) {
                super.updateItem(estadoVencido, empty);
                if (empty || estadoVencido == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(estadoVencido);
                    boolean esVencido = estadoVencido.equalsIgnoreCase("Sí") || estadoVencido.equalsIgnoreCase("Vencido");
                    setStyle(esVencido
                            ? "-fx-text-fill: #ef4444; -fx-font-weight: bold;"
                            : "-fx-text-fill: #10b981; -fx-font-weight: bold;");
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
                if (empty || stock == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.valueOf(stock));
                    if (stock <= 0) {
                        setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                    } else if (stock <= 5) {
                        setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
                    }
                }
            }
        });
        colDisponible.setCellValueFactory(cellData -> {
            boolean estaDisponible = cellData.getValue().estaVencido();
            String disponible = estaDisponible ? "Disponible" : "NO Disponible";
            return new SimpleStringProperty(disponible);
        });
        colDisponible.setCellFactory(columna -> new TableCell<>() {
            {
                setAlignment(Pos.CENTER);
            }
            @Override
            protected void updateItem(String estado, boolean empty) {
                super.updateItem(estado, empty);
                if (empty || estado == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(estado);
                    setStyle(estado.equalsIgnoreCase("Activo") || estado.equalsIgnoreCase("Disponible")
                            ? "-fx-text-fill: #10b981; -fx-font-weight: bold;"
                            : "-fx-text-fill: #ef4444; -fx-font-weight: bold;");
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

    private void cargarDatosTabla() {
        try {
            List<DatosTotalesProductoPerecederoDTO> datosBD = this.ensambladorDTOProducto.ensamblarDetalleProductosPerecedero(
                    this.servicioProductos.obtenerProductosPerecederoDeInventario(this.idInventario)
            );
            listaMaestraPerecederos.clear();
            if (datosBD != null && !datosBD.isEmpty()) {
                listaMaestraPerecederos.addAll(datosBD);
            }
        } catch (RuntimeException e) {
            GestorAlertas.mostrarAlertaError(
                    "Error Crítico de Carga",
                    "No se pudieron cargar los datos del inventario.",
                    "Ocurrió un error al cargar los productos perecederos. La ventana se cerrará por seguridad.\nDetalle: " + e.getMessage()
            );
            if (tablaPerecederos != null && tablaPerecederos.getScene() != null) {
                Stage stageActual = (Stage) tablaPerecederos.getScene().getWindow();
                stageActual.close();
            }
        }
    }


    @FXML
    void abrirEditorPerecedero(ActionEvent event) {
        DatosTotalesProductoPerecederoDTO productoSeleccionado = tablaPerecederos.getSelectionModel().getSelectedItem();
        if (productoSeleccionado == null) {
            GestorAlertas.mostrarAlertaWarning(
                    "Selección Requerida", null,
                    "Por favor, Seleccione un Producto Perecedero en la Tabla para Editarlo."
            );
            return;
        }
        String rutaFxml = RutasVista.EDITAR_PERECEDERO_VIEW;
        try {
            FXMLLoader loader = CargadorVistas.obtenerLoaderConfigurado(rutaFxml);
            Parent root = loader.load();
            EditarPerecederoControlador controladorEditor = loader.getController();
            controladorEditor.cargarDatosProducto(productoSeleccionado, this.idInventario);
            Stage stageEditor = new Stage();
            stageEditor.setScene(new Scene(root));
            stageEditor.setTitle("Editar Producto Perecedero");
            stageEditor.initModality(Modality.WINDOW_MODAL);
            Stage ventanaPadre = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stageEditor.initOwner(ventanaPadre);
            stageEditor.setResizable(false);
            stageEditor.showAndWait();
            cargarDatosTabla();
        } catch (IOException | IllegalStateException e) {
            throw new CargarVistaException(rutaFxml, "No se pudo cargar el archivo FXML.", e);
        }
    }


    @FXML
    void cambiarEstadoProducto(ActionEvent event) {
        cambiarEstadoProducto();
    }

    private void cambiarEstadoProducto(){
        DatosTotalesProductoPerecederoDTO seleccionado = this.tablaPerecederos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            GestorAlertas.mostrarAlertaWarning(
                    "Selección Requerida", null,
                    "Por favor, Seleccione un Producto Perecedero de la Tabla para Cambiar su Estado."
            );
            return;
        }
        if (!GestorAlertas.mostrarConfirmacion(
                "Confirmar Cambio de Estado", null,
                "¿Está Seguro que desea Cambiar el Estado del Producto:\n"
                        + seleccionado.codigo() + " - " + seleccionado.nombre() + "?")
        ){
            return;
        }
        CompletableFuture.runAsync(()->
                this.servicioProductos.cambiarEstadoProducto(this.idInventario, seleccionado.codigo())
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
                        "Estado Actualizado", null,
                        "El Estado del Producto se Actualizó Correctamente."
                );
            })
        ).exceptionally(ex->{
            Platform.runLater(()->{
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                GestorAlertas.mostrarAlertaError(
                        "NO se pudo Completar la Acción", null,
                        "Error:  " + causa.getMessage()
                );
            });
            return null;
        });
    }


}//===================================================================================================================//


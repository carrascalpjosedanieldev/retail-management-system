package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarServicios;

import RetailManagementSystem.aplicacion.dto.gestion.DescuentoDTO;
import RetailManagementSystem.aplicacion.dto.gestion.ImpuestoDTO;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorDescuentos;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorImpuestos;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorServicios;
import RetailManagementSystem.aplicacion.dto.comercial.ServicioDTO;
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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class GestionServiciosControlador {

    //ATRIBUTOS:

    @FXML private TableColumn<ServicioDTO, String> colCodigo;
    @FXML private TableColumn<ServicioDTO, String> colDescuento;
    @FXML private TableColumn<ServicioDTO, String> colEstado;
    @FXML private TableColumn<ServicioDTO, String> colImpuesto;
    @FXML private TableColumn<ServicioDTO, String> colNombre;
    @FXML private TableColumn<ServicioDTO, BigDecimal> colPrecioBase;
    @FXML private TableColumn<ServicioDTO, BigDecimal> colPrecioFinal;
    @FXML private TableView<ServicioDTO> tablaServicios;
    @FXML private TextField txtBuscar;

    private final OrquestadorServicios orquestadorServicios;

    private final OrquestadorDescuentos orquestadorDescuentos;

    private final OrquestadorImpuestos orquestadorImpuestos;

    private final ObservableList<ServicioDTO> listaObservableServicios = FXCollections.observableArrayList();

    //CONSTRUCTOR:

    public GestionServiciosControlador(
            OrquestadorServicios orquestadorServicios, OrquestadorDescuentos orquestadorDescuentos,
            OrquestadorImpuestos orquestadorImpuestos
    ) {
        this.orquestadorServicios = orquestadorServicios;
        this.orquestadorDescuentos = orquestadorDescuentos;
        this.orquestadorImpuestos = orquestadorImpuestos;
    }

    //MÉTODOS:

    @FXML
    public void initialize() {
        configurarColumnas();
        configurarFiltroBusqueda();
        cargarDatosTabla();
    }

    private void configurarColumnas(){
        colCodigo.setCellValueFactory(celda -> new SimpleStringProperty(
                celda.getValue().codigo())
        );
        colNombre.setCellValueFactory(celda -> new SimpleStringProperty(
                celda.getValue().nombre())
        );
        colPrecioBase.setCellValueFactory(celda -> new SimpleObjectProperty<>(
                celda.getValue().precioBase())
        );
        colImpuesto.setCellValueFactory(celda -> new SimpleStringProperty(
                celda.getValue().datosImpuesto().nombre())
        );
        colDescuento.setCellValueFactory(celda -> new SimpleStringProperty(
                celda.getValue().datosDescuento().nombre())
        );
        colPrecioFinal.setCellValueFactory(celda -> new SimpleObjectProperty<>(
                celda.getValue().precioFinal())
        );
        colEstado.setCellValueFactory(celda -> {
            boolean esActivo = celda.getValue().activo();
            String textoEstado = esActivo ? "Activo" : "Inactivo";
            return new SimpleStringProperty(textoEstado);
        });
        configurarColumnaMoneda(colPrecioBase);
        configurarColumnaMoneda(colPrecioFinal);
        colEstado.setCellFactory(columna -> new TableCell<ServicioDTO, String>() {
            @Override
            protected void updateItem(String estado, boolean empty) {
                super.updateItem(estado, empty);
                getStyleClass().removeAll("estado-activo", "estado-inactivo");
                if (empty || estado == null) {
                    setText(null);
                } else {
                    setText(estado);
                    String estiloCss = estado.equalsIgnoreCase("Activo") ? "estado-activo" : "estado-inactivo";
                    getStyleClass().add(estiloCss);
                }
            }
        });
    }

    private void configurarColumnaMoneda(TableColumn<ServicioDTO, BigDecimal> columna) {
        columna.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal precio, boolean empty) {
                super.updateItem(precio, empty);
                getStyleClass().removeAll("columna-moneda");
                if (empty || precio == null) {
                    setText(null);
                } else {
                    setText(FormateadorNumeros.formatoMoneda(precio));
                    getStyleClass().add("columna-moneda");
                }
            }
        });
    }

    private void configurarFiltroBusqueda(){
        FilteredList<ServicioDTO> listaFiltrada = new FilteredList<>(listaObservableServicios, b -> true);
        txtBuscar.textProperty().addListener((observable, valorViejo, valorNuevo) -> {
            listaFiltrada.setPredicate(servicio -> {
                if (valorNuevo == null || valorNuevo.isBlank()) {
                    return true;
                }
                String filtro = valorNuevo.toLowerCase();
                return servicio.codigo().toLowerCase().contains(filtro) ||
                        servicio.nombre().toLowerCase().contains(filtro);
            });
        });
        SortedList<ServicioDTO> listaOrdenada = new SortedList<>(listaFiltrada);
        listaOrdenada.comparatorProperty().bind(tablaServicios.comparatorProperty());
        tablaServicios.setItems(listaOrdenada);
    }

    private void cargarDatosTabla() {
        LocalDate fechaActual = LocalDate.now();
        CompletableFuture.supplyAsync(()->
                this.orquestadorServicios.obtenerTodosLosServicios(fechaActual)
        ).thenAccept(listaServicios->
            Platform.runLater(()->
                listaObservableServicios.setAll(listaServicios)
            )
        ).exceptionally(ex->{
            Platform.runLater(() -> {
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                GestorAlertas.mostrarAlertaError(
                        "Error Critico",
                        "NO se pudo Completar la Acción.",
                        "Notificale al Administrador este Error:\n" + causa.getMessage()
                );
                Stage stageActual = (Stage) tablaServicios.getScene().getWindow();
                CargadorVistas.cambiarPantalla(stageActual, RutasVista.GESTIONAR_TIENDA_VIEW);
            });
            return null;
        });
    }


    @FXML
    void abrirFormularioEdicion(ActionEvent event) {
        ServicioDTO seleccionado = tablaServicios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            GestorAlertas.mostrarAlertaWarning(
                    "Atención", null,
                    "Por favor, Selecciona un Servicio de la Tabla para Modificarlo."
            );
            return;
        }
        ejecutarConCatalogosListos((listaImpuestos, listaDescuentos)->{
            abrirModalEdicion(seleccionado, listaImpuestos, listaDescuentos);
        });
    }

    private void abrirModalEdicion(ServicioDTO seleccionado, List<ImpuestoDTO> listaImpuestos, List<DescuentoDTO> listaDescuentos){
        String rutaFxml = RutasVista.EDITAR_SERVICIO_VIEW;
        try {
            FXMLLoader loader = CargadorVistas.obtenerLoaderConfigurado(rutaFxml);
            Parent root = loader.load();
            EditarServicioControlador controlador = loader.getController();
            controlador.cargarDatos(seleccionado, listaObservableServicios, listaImpuestos, listaDescuentos);
            Stage stageEditar = new Stage();
            stageEditar.setTitle("Editando Servicio");
            stageEditar.initModality(Modality.APPLICATION_MODAL);
            stageEditar.setResizable(false);
            Scene escenaEditar = new Scene(root);
            stageEditar.setScene(escenaEditar);
            stageEditar.showAndWait();
        } catch (IOException e) {
            throw new CargarVistaException(rutaFxml, "NO se pudo Cargar el Archivo FXML.", e);
        }
    }

    private void ejecutarConCatalogosListos(BiConsumer<List<ImpuestoDTO>, List<DescuentoDTO>> accionVisual) {
        CompletableFuture<List<ImpuestoDTO>> futuroImpuestos =
                CompletableFuture.supplyAsync(this.orquestadorImpuestos::obtenerImpuestosActivos);
        CompletableFuture<List<DescuentoDTO>> futuroDescuentos =
                CompletableFuture.supplyAsync(this.orquestadorDescuentos::obtenerDescuentosActivos);
        futuroImpuestos.thenCombine(futuroDescuentos, (impuestos, descuentos) -> {
            if (impuestos.isEmpty()) {
                throw new IllegalStateException("NO puedes realizar esta Acción sin al menos un Impuesto Activo.");
            }
            if (descuentos.isEmpty()) {
                throw new IllegalStateException("NO puedes realizar esta Acción sin al menos un Descuento Activo.");
            }
            Platform.runLater(() -> accionVisual.accept(impuestos, descuentos));
            return null;
        }).exceptionally(ex -> {
            Platform.runLater(() -> {
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                GestorAlertas.mostrarAlertaWarning("Configuración Requerida", null, causa.getMessage());
            });
            return null;
        });
    }


    @FXML
    void abrirFormularioNuevo(ActionEvent event) {
        ejecutarConCatalogosListos(this::abrirModalCrear);
    }

    private void abrirModalCrear(List<ImpuestoDTO> listaImpuestos, List<DescuentoDTO> listaDescuentos){
        String rutaFxml = RutasVista.CREAR_SERVICIO_VIEW;
        try {
            FXMLLoader loader = CargadorVistas.obtenerLoaderConfigurado(rutaFxml);
            Parent root = loader.load();
            CrearServicioControlador controlador = loader.getController();
            controlador.cargarDatos(listaObservableServicios, listaImpuestos, listaDescuentos);
            Stage stageCrear = new Stage();
            stageCrear.setTitle("Creando Servicio");
            stageCrear.initModality(Modality.APPLICATION_MODAL);
            stageCrear.setResizable(false);
            Scene escenaCrear = new Scene(root);
            stageCrear.setScene(escenaCrear);
            stageCrear.showAndWait();
        } catch (IOException e) {
            throw new CargarVistaException(rutaFxml, "NO se pudo Cargar el Archivo FXML.", e);
        }
    }


    @FXML
    void cambiarEstadoServicio(ActionEvent event) {
        cambiarEstadoServicio();
    }

    private void cambiarEstadoServicio(){
        ServicioDTO seleccionado = tablaServicios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            GestorAlertas.mostrarAlertaWarning(
                    "Atención", null,
                    "Por favor, Selecciona un Servicio de la Tabla para Cambiar su Estado."
            );
            return;
        }
        String accion = seleccionado.activo() ? "Desactivar" : "Activar";
        if (!GestorAlertas.mostrarConfirmacion(
                "Confirmar Cambio de Estado", null,
                "¿Estás Seguro de que Deseas " + accion + " el Servicio:\n" +
                        seleccionado.codigo() + " - " + seleccionado.nombre() + "?"
        )){
            return;
        }
        CompletableFuture.runAsync(()->
                this.orquestadorServicios.cambiarEstadoServicio(seleccionado.codigo())
        ).thenRun(()->
            Platform.runLater(()->{
                ServicioDTO actualizado = new ServicioDTO(
                        seleccionado.codigo(),
                        seleccionado.nombre(),
                        seleccionado.precioBase(),
                        seleccionado.precioFinal(),
                        !seleccionado.activo(),
                        seleccionado.datosImpuesto(),
                        seleccionado.datosDescuento()
                );
                int indice = listaObservableServicios.indexOf(seleccionado);
                listaObservableServicios.set(indice, actualizado);
                GestorAlertas.mostrarAlertaInformacion(
                        "Éxito", null,
                        "El Estado del Servicio ha sido Actualizado Correctamente."
                );
            })
        ).exceptionally(ex->{
            Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
            GestorAlertas.mostrarAlertaError(
                    "NO se pudo Completar la Acción", null,
                    "Error:  " + causa.getMessage()
            );
            return null;
        });
    }


    @FXML
    void volverAlPanel(ActionEvent event) {
        Stage stageActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
        CargadorVistas.cambiarPantalla(stageActual, RutasVista.GESTIONAR_TIENDA_VIEW);
    }


}//===================================================================================================================//


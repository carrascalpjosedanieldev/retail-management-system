package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarServicios;

import RetailManagementSystem.aplicacion.dto.gestion.DescuentoDTO;
import RetailManagementSystem.aplicacion.dto.gestion.ImpuestoDTO;
import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorDescuentos;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorImpuestos;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorServicios;
import RetailManagementSystem.aplicacion.dto.comercial.ServicioDTO;
import RetailManagementSystem.dominio.excepciones.autenticacionYSeguridad.AccesoDenegadoException;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.infraestructura.seguridad.ValidadorSeguridad;
import RetailManagementSystem.vista.configuracion.ConfiguradorExcepciones;
import RetailManagementSystem.vista.controladores.gestionarTienda.GestionarTiendaControlador;
import RetailManagementSystem.vista.utilidades.*;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Window;

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
    @FXML private Button btnCambiarEstado;
    @FXML private Button btnModificar;
    @FXML private Button btnNuevo;

    private final OrquestadorServicios orquestadorServicios;

    private final OrquestadorDescuentos orquestadorDescuentos;

    private final OrquestadorImpuestos orquestadorImpuestos;

    private final ObservableList<ServicioDTO> listaObservableServicios = FXCollections.observableArrayList();

    private UsuarioDTOCompleto usuarioActual;

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

    public void cargarDatos(UsuarioDTOCompleto usuarioActual){
        ValidadorSeguridad.exigirAlgunPermiso(usuarioActual, List.of(
                PermisosApp.VER_SERVICIOS,
                PermisosApp.REGISTRAR_SERVICIOS,
                PermisosApp.MODIFICAR_SERVICIOS,
                PermisosApp.CAMBIAR_ESTADO_SERVICIOS
        ));
        this.usuarioActual = usuarioActual;
        protegerBoton(btnNuevo, PermisosApp.REGISTRAR_SERVICIOS);
        protegerBoton(btnModificar, PermisosApp.MODIFICAR_SERVICIOS);
        protegerBoton(btnCambiarEstado, PermisosApp.CAMBIAR_ESTADO_SERVICIOS);
    }

    private void protegerBoton(Button boton, String permisoRequerido) {
        boolean tieneAcceso = this.usuarioActual.tienePermiso(permisoRequerido);
        boton.setVisible(tieneAcceso);
        boton.setManaged(tieneAcceso);
    }

    private Window getVentana(){
        return tablaServicios.getScene() != null ? tablaServicios.getScene().getWindow() : null;
    }


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
                Throwable causa = ConfiguradorExcepciones.obtenerCausaRaiz(ex);
                GestorAlertas.mostrarAlertaError(
                        getVentana(), "Error Critico",
                        "NO se pudo Completar la Acción.",
                        "Notificale al Administrador este Error:\n" + causa.getMessage()
                );
                volverAGestionTienda();
            });
            return null;
        });
    }


    @FXML
    private void abrirFormularioEdicion(ActionEvent event) {
        ServicioDTO seleccionado = tablaServicios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Atención", null,
                    "Por favor, Selecciona un Servicio de la Tabla para Modificarlo."
            );
            return;
        }
        ejecutarConCatalogosListos((listaImpuestos, listaDescuentos)->{
            abrirModalEdicion(seleccionado, listaImpuestos, listaDescuentos);
        });
    }

    private void abrirModalEdicion(
            ServicioDTO seleccionado, List<ImpuestoDTO> listaImpuestos, List<DescuentoDTO> listaDescuentos
    ) {
        try {
            CargadorVistas.abrirModalConInyeccion(
                    RutasVista.EDITAR_SERVICIO_VIEW,
                    "Editando Servicio", getVentana(),
                    (EditarServicioControlador c)->{
                        c.cargarDatos(
                                this.usuarioActual, seleccionado, listaObservableServicios, listaImpuestos,
                                listaDescuentos
                        );
                    }
            );
        } catch (AccesoDenegadoException ex){
            GestorAlertas.mostrarAlertaAccesoDenegado(getVentana(), ex);
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
                Throwable causa = ConfiguradorExcepciones.obtenerCausaRaiz(ex);
                GestorAlertas.mostrarAlertaWarning(
                        getVentana(), "Configuración Requerida", null,
                        causa.getMessage()
                );
            });
            return null;
        });
    }


    @FXML
    private void abrirFormularioNuevo(ActionEvent event) {
        ejecutarConCatalogosListos(this::abrirModalCrear);
    }

    private void abrirModalCrear(List<ImpuestoDTO> listaImpuestos, List<DescuentoDTO> listaDescuentos){
        try {
            CargadorVistas.abrirModalConInyeccion(
                    RutasVista.CREAR_SERVICIO_VIEW,
                    "Creando Servicio", getVentana(),
                    (CrearServicioControlador c)->{
                        c.cargarDatos(this.usuarioActual, listaObservableServicios, listaImpuestos, listaDescuentos);
                    }
            );
        } catch (AccesoDenegadoException ex){
            GestorAlertas.mostrarAlertaAccesoDenegado(getVentana(), ex);
        }
    }


    @FXML
    private void cambiarEstadoServicio(ActionEvent event) {
        cambiarEstadoServicio();
    }

    private void cambiarEstadoServicio(){
        ServicioDTO seleccionado = tablaServicios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Atención", null,
                    "Por favor, Selecciona un Servicio de la Tabla para Cambiar su Estado."
            );
            return;
        }
        String accion = seleccionado.activo() ? "Desactivar" : "Activar";
        if (!GestorAlertas.mostrarConfirmacion(
                getVentana(), "Confirmar Cambio de Estado", null,
                "¿Estás Seguro de que Deseas " + accion + " el Servicio:\n" +
                        seleccionado.codigo() + " - " + seleccionado.nombre() + "?"
        )){
            return;
        }
        CompletableFuture.runAsync(()->
                this.orquestadorServicios.cambiarEstadoServicio(this.usuarioActual, seleccionado.codigo())
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
                UtilidadesLista.reemplazarPorIdentidad(
                        listaObservableServicios,
                        actualizado,
                        item-> item.codigo().equals(actualizado.codigo())
                );
                GestorAlertas.mostrarAlertaInformacion(
                        getVentana(), "Éxito", null,
                        "El Estado del Servicio ha sido Actualizado Correctamente."
                );
            })
        ).exceptionally(ex->{
            Throwable causa = ConfiguradorExcepciones.obtenerCausaRaiz(ex);
            GestorAlertas.mostrarAlertaError(
                    getVentana(), "NO se pudo Completar la Acción", null,
                    "Error:  " + causa.getMessage()
            );
            return null;
        });
    }


    @FXML
    private void volverAlPanel(ActionEvent event) {
        volverAGestionTienda();
    }

    private void volverAGestionTienda(){
        try {
            CargadorVistas.cambiarPantallaInyectada(
                    getVentana(),
                    RutasVista.GESTIONAR_TIENDA_VIEW,
                    (GestionarTiendaControlador c) -> {
                        c.cargarUsuario(this.usuarioActual);
                    }
            );
        } catch (AccesoDenegadoException ex){
            GestorAlertas.mostrarAlertaAccesoDenegado(getVentana(), ex);
        }
    }


}//===================================================================================================================//


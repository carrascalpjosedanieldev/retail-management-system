package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarInventarios;

import RetailManagementSystem.aplicacion.dto.gestion.InventarioDTO;
import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.servicios.ServicioInventario;
import RetailManagementSystem.aplicacion.ensambladores.EnsambladorDTOInventario;
import RetailManagementSystem.dominio.excepciones.autenticacionYSeguridad.AccesoDenegadoException;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.infraestructura.seguridad.ValidadorSeguridad;
import RetailManagementSystem.vista.configuracion.ConfiguradorExcepciones;
import RetailManagementSystem.vista.controladores.gestionarTienda.GestionarTiendaControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarInventarios.gestionarProductos.GestionProductosControlador;
import RetailManagementSystem.vista.utilidades.CargadorVistas;
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
import javafx.scene.control.*;
import javafx.stage.Window;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GestionInventariosControlador {

    @FXML private TableColumn<InventarioDTO, Integer> colCapacidadLibre;
    @FXML private TableColumn<InventarioDTO, Integer> colCapacidadMax;
    @FXML private TableColumn<InventarioDTO, Integer> colCapacidadOcupada;
    @FXML private TableColumn<InventarioDTO, Integer> colId;
    @FXML private TableColumn<InventarioDTO, String> colNombre;
    @FXML private TableView<InventarioDTO> tablaInventarios;
    @FXML private TextField txtBuscar;
    @FXML private Button btnModificarInv;
    @FXML private Button btnNuevoInv;
    @FXML private Button btnAumentarCapacidadMax;
    @FXML private Button btnVerOEditarProductos;


    private final ServicioInventario servicioInventario;

    private final EnsambladorDTOInventario ensambladorDTOInventario;

    private final ObservableList<InventarioDTO> listaObservable = FXCollections.observableArrayList();

    private UsuarioDTOCompleto usuarioActual;

    //CONSTRUCTOR:

    public GestionInventariosControlador(
            ServicioInventario servicioInventario, EnsambladorDTOInventario ensambladorDTOInventario
    ) {
        this.servicioInventario = servicioInventario;
        this.ensambladorDTOInventario = ensambladorDTOInventario;
    }

    //MÉTODOS:

    public void cargarDatos(UsuarioDTOCompleto usuarioActual){
        ValidadorSeguridad.exigirAlgunPermiso(usuarioActual, List.of(
                PermisosApp.VER_SERVICIOS,
                PermisosApp.REGISTRAR_SERVICIOS,
                PermisosApp.AUMENTAR_CAPACIDAD_MAXIMA_INVENTARIO
        ));
        this.usuarioActual = usuarioActual;
        configurarVisibilidadModulos();
    }

    private boolean tieneAccesoAlModulo(List<String> permisosDelModulo) {
        return permisosDelModulo.stream()
                .anyMatch(permiso -> this.usuarioActual.tienePermiso(permiso));
    }

    private void configurarVisibilidadModulos() {
        boolean nuevo = tieneAccesoAlModulo(List.of(
                PermisosApp.REGISTRAR_INVENTARIOS
        ));
        btnNuevoInv.setVisible(nuevo);
        btnNuevoInv.setManaged(nuevo);
        boolean editar = tieneAccesoAlModulo(List.of(
                PermisosApp.EDITAR_INVENTARIOS
        ));
        btnModificarInv.setVisible(editar);
        btnModificarInv.setManaged(editar);
        boolean aumentarCapacidad = tieneAccesoAlModulo(List.of(
                PermisosApp.AUMENTAR_CAPACIDAD_MAXIMA_INVENTARIO
        ));
        btnAumentarCapacidadMax.setVisible(aumentarCapacidad);
        btnAumentarCapacidadMax.setManaged(aumentarCapacidad);
        boolean visualizar = tieneAccesoAlModulo(List.of(
                PermisosApp.VER_PRODUCTOS

        ));
        boolean productos = tieneAccesoAlModulo(List.of(
                PermisosApp.REGISTRAR_PRODUCTOS,
                PermisosApp.EDITAR_PRODUCTO,
                PermisosApp.CAMBIAR_ESTADO_PRODUCTO,
                PermisosApp.TRASLADAR_PRODUCTOS
        ));
        if (!productos){
            btnVerOEditarProductos.setText("Ver Productos del Inventario");
        }
        btnVerOEditarProductos.setVisible(visualizar);
        btnVerOEditarProductos.setManaged(visualizar);
    }

    private Window getVentana(){
        return tablaInventarios.getScene() != null ? tablaInventarios.getScene().getWindow() : null;
    }


    @FXML
    public void initialize() {
        configurarColumnasTabla();
        configurarFiltroBusqueda();
        cargarDatosTabla();
    }

    private void configurarColumnasTabla(){
        colId.setCellValueFactory(celda -> new SimpleObjectProperty<>(
                celda.getValue().idInventario())
        );
        colNombre.setCellValueFactory(celda -> new SimpleStringProperty(
                celda.getValue().nombre())
        );
        colCapacidadMax.setCellValueFactory(celda -> new SimpleObjectProperty<>(
                celda.getValue().capacidadMaxima())
        );
        colCapacidadOcupada.setCellValueFactory(celda -> new SimpleObjectProperty<>(
                celda.getValue().capacidadOcupada())
        );
        colCapacidadLibre.setCellValueFactory(celda -> new SimpleObjectProperty<>(
                celda.getValue().capacidadLibre())
        );
        colCapacidadLibre.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(Integer libre, boolean empty) {
                super.updateItem(libre, empty);
                getStyleClass().removeAll("capacidad-agotada", "capacidad-baja", "capacidad-ok");
                if (empty || libre == null) {
                    setText(null);
                } else {
                    setText(String.valueOf(libre));
                    if (libre <= 0) {
                        getStyleClass().add("capacidad-agotada");
                    } else if (libre <= 10) {
                        getStyleClass().add("capacidad-baja");
                    } else {
                        getStyleClass().add("capacidad-ok");
                    }
                }
            }
        });
    }

    private void configurarFiltroBusqueda(){
        FilteredList<InventarioDTO> listaFiltrada = new FilteredList<>(listaObservable, inv -> true);
        txtBuscar.textProperty().addListener((observable, valorViejo, valorNuevo) -> {
            if (valorNuevo == null || valorNuevo.isBlank()){
                listaFiltrada.setPredicate(inv -> true);
                return;
            }
            String filtro = valorNuevo.toLowerCase().trim();
            listaFiltrada.setPredicate(inv -> {
                String idComoTexto = String.valueOf(inv.idInventario());
                String nombre = inv.nombre() != null ? inv.nombre().toLowerCase() : "";
                return nombre.contains(filtro) || idComoTexto.contains(filtro);
            });
        });
        SortedList<InventarioDTO> listaOrdenada = new SortedList<>(listaFiltrada);
        listaOrdenada.comparatorProperty().bind(tablaInventarios.comparatorProperty());
        tablaInventarios.setItems(listaOrdenada);
    }

    private void cargarDatosTabla() {
        CompletableFuture.supplyAsync(() ->
            this.ensambladorDTOInventario.ensamblarDetalleInventarioGeneral(
                    this.servicioInventario.obtenerTodosLosInventarios()
            )
        ).thenAcceptAsync(
                listaObservable::setAll, Platform::runLater
        ).exceptionally(ex -> {
            Platform.runLater(() -> {
                Throwable causa = ConfiguradorExcepciones.obtenerCausaRaiz(ex);
                GestorAlertas.mostrarAlertaError(
                        getVentana(), "Error", null,
                        "Se Cerrara la Ventana por Seguridad.\n" +
                                "No se pudieron cargar los inventarios: " + causa.getMessage()
                );
                volverAGestionTienda();
            });
            return null;
        });
    }


    @FXML
    private void abrirFormularioEdicion(ActionEvent event) {
        abrirFormularioEdicion();
    }

    private void abrirFormularioEdicion(){
        InventarioDTO seleccionado = tablaInventarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Atención", null,
                    "Por favor, Selecciona un Inventario de la Tabla para Modificarlo."
            );
            return;
        }
        try {
            CargadorVistas.abrirModalConInyeccion(
                    RutasVista.EDITAR_INVENTARIO_VIEW,
                    "Editando Inventario", getVentana(),
                    (EditarInventarioControlador c)->{
                        c.cargarDatos(this.usuarioActual, seleccionado, listaObservable);
                    }
            );
        } catch (AccesoDenegadoException ex){
            GestorAlertas.mostrarAlertaAccesoDenegado(getVentana(), ex);
        }
    }


    @FXML
    private void abrirFormularioNuevo(ActionEvent event) {
        try {
            CargadorVistas.abrirModalConInyeccion(
                    RutasVista.CREAR_INVENTARIO_VIEW,
                    "Creando Inventario", getVentana(),
                    (CrearInventarioControlador c)->{
                        c.cargarDatos(this.usuarioActual, listaObservable);
                    }
            );
        } catch (AccesoDenegadoException ex){
            GestorAlertas.mostrarAlertaAccesoDenegado(getVentana(), ex);
        }
    }


    @FXML
    private void editarProductosInventario(ActionEvent event) {
        InventarioDTO seleccionado = tablaInventarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Atención", null,
                    "Selecciona un Inventario para ver sus Productos."
            );
            return;
        }
        try {
            CargadorVistas.cambiarPantallaInyectada(
                    getVentana(),
                    RutasVista.GESTIONAR_PRODUCTOS_VIEW,
                    (GestionProductosControlador c)->{
                        c.inicializarConInventarioYUsuario(this.usuarioActual, seleccionado.idInventario());
                    }
            );
        } catch (AccesoDenegadoException ex){
            GestorAlertas.mostrarAlertaAccesoDenegado(getVentana(), ex);
        }
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


    public void aumentarCapacidadMax(ActionEvent event) {
        InventarioDTO seleccionado = tablaInventarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Atención", null,
                    "Selecciona un Inventario para Aumentar su Capacidad Maxima."
            );
            return;
        }
        try {
            CargadorVistas.abrirModalConInyeccion(
                    RutasVista.AUMENTAR_CAPACIDAD_VIEW,
                    "Aumentar Capacidad Inventario",
                    getVentana(),
                    (AumentarCapacidadControlador c) -> {
                        c.cargarDatos(this.usuarioActual, seleccionado, listaObservable);
                    }
            );
        } catch (AccesoDenegadoException ex){
            GestorAlertas.mostrarAlertaAccesoDenegado(getVentana(), ex);
        }
    }
}//===================================================================================================================//


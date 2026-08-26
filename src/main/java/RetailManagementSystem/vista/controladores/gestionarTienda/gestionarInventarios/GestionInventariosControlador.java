package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarInventarios;

import RetailManagementSystem.aplicacion.dto.gestion.InventarioDTO;
import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.servicios.ServicioInventario;
import RetailManagementSystem.aplicacion.ensambladores.EnsambladorDTOInventario;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
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
        if (usuarioActual == null){
            throw new IllegalArgumentException("Usuario Nulo, Error al Recibir el Usuario");
        }
        if (!usuarioActual.tienePermiso(PermisosApp.VER_SERVICIOS) ||
            !usuarioActual.tienePermiso(PermisosApp.ADMINISTRAR_SERVICIOS)){
            GestorAlertas.mostrarAlertaError(
                    getVentana(),
                    "Acceso Denegado", "Privilegios Insuficientes",
                    "NO tienes los Permisos Necesarios para Ver o Gestionar los Servicios."
            );
            volverAGestionTienda();
            return;
        }
        this.usuarioActual = usuarioActual;
        configurarVisibilidadModulos();
    }

    private boolean tieneAccesoAlModulo(List<String> permisosDelModulo) {
        return permisosDelModulo.stream()
                .anyMatch(permiso -> this.usuarioActual.tienePermiso(permiso));
    }

    private void configurarVisibilidadModulos() {
        boolean administrar = tieneAccesoAlModulo(List.of(
                PermisosApp.ADMINISTRAR_INVENTARIOS
        ));
        btnNuevoInv.setVisible(administrar);
        btnNuevoInv.setManaged(administrar);
        btnModificarInv.setVisible(administrar);
        btnModificarInv.setManaged(administrar);
        if (!administrar){
            btnVerOEditarProductos.setText("Ver Productos del Inventario");
        }
        boolean visualizar = tieneAccesoAlModulo(List.of(
                PermisosApp.VER_PRODUCTOS,
                PermisosApp.ADMINISTRAR_PRODUCTOS,
                PermisosApp.TRASLADAR_PRODUCTOS
        ));
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
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                GestorAlertas.mostrarAlertaError(
                        getVentana(), "Error", null,
                        "No se pudieron cargar los inventarios: " + causa.getMessage()
                );
            });
            return null;
        });
    }


    @FXML
    void abrirFormularioEdicion(ActionEvent event) {
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
        CargadorVistas.abrirModalConInyeccion(
                RutasVista.EDITAR_INVENTARIO_VIEW,
                "Editando Inventario", getVentana(),
                (EditarInventarioControlador c)->{
                    c.cargarDatos(this.usuarioActual, seleccionado, listaObservable);
                }
        );
    }


    @FXML
    void abrirFormularioNuevo(ActionEvent event) {
        CargadorVistas.abrirModalConInyeccion(
                RutasVista.CREAR_INVENTARIO_VIEW,
                "Creando Inventario", getVentana(),
                (CrearInventarioControlador c)->{
                    c.cargarDatos(this.usuarioActual, listaObservable);
                }
        );
    }


    @FXML
    void editarProductosInventario(ActionEvent event) {
        InventarioDTO seleccionado = tablaInventarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Atención", null,
                    "Selecciona un Inventario para ver sus Productos."
            );
            return;
        }
        CargadorVistas.cambiarPantallaInyectada(
                getVentana(),
                RutasVista.GESTIONAR_PRODUCTOS_VIEW,
                (GestionProductosControlador c)->{
                    c.inicializarConInventarioYUsuario(this.usuarioActual, seleccionado.idInventario());
                }
        );
    }


    @FXML
    void volverAlPanel(ActionEvent event) {
        volverAGestionTienda();
    }

    private void volverAGestionTienda(){
        CargadorVistas.cambiarPantallaInyectada(
                getVentana(),
                RutasVista.GESTIONAR_TIENDA_VIEW,
                (GestionarTiendaControlador c) -> {
                    c.cargarUsuario(this.usuarioActual);
                }
        );
    }


}//===================================================================================================================//


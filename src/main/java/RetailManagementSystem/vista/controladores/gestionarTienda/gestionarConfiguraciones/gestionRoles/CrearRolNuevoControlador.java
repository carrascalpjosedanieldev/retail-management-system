package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarConfiguraciones.gestionRoles;

import RetailManagementSystem.aplicacion.dto.seguridad.PermisoDTO;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorPermisos;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorRoles;
import RetailManagementSystem.infraestructura.persistencia.excepciones.PersistenciaException;
import RetailManagementSystem.vista.utilidades.CargadorVistas;
import RetailManagementSystem.vista.utilidades.GestorAlertas;
import RetailManagementSystem.vista.utilidades.RutasVista;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CrearRolNuevoControlador {

    //ATRIBUTOS:

    @FXML private ComboBox<String> cbModulos;
    @FXML private CheckBox chkActivo;
    @FXML private TableColumn<PermisoDTO, Integer> colIdAgreg;
    @FXML private TableColumn<PermisoDTO, String> colModuloAgreg;
    @FXML private TableColumn<PermisoDTO, String> colNombreAgreg;
    @FXML private TableColumn<PermisoDTO, Integer> colIdDisp;
    @FXML private TableColumn<PermisoDTO, String> colNombreDisp;
    @FXML private Label lblContadorPermisos;
    @FXML private TableView<PermisoDTO> tablaPermisosAgregados;
    @FXML private TableView<PermisoDTO> tablaPermisosDisponibles;
    @FXML private TextField txtNombreRol;

    private final OrquestadorPermisos orquestadorPermisos;

    private final OrquestadorRoles orquestadorRoles;

    private final ObservableList<PermisoDTO> listaMaestraPermisos = FXCollections.observableArrayList();

    private final ObservableList<PermisoDTO> listaPermisosAgregados = FXCollections.observableArrayList();

    private FilteredList<PermisoDTO> listaFiltrada;

    //CONSTRUCTOR:

    public CrearRolNuevoControlador(OrquestadorPermisos orquestadorPermisos, OrquestadorRoles orquestadorRoles) {
        this.orquestadorPermisos = orquestadorPermisos;
        this.orquestadorRoles = orquestadorRoles;
    }

    //MÉTODOS:

    private Window getVentana(){
        return tablaPermisosAgregados.getScene() != null ? tablaPermisosAgregados.getScene().getWindow() : null;
    }


    @FXML
    public void initialize(){
        configurarColumnasDisp();
        configurarColumnasAgreg();
        configurarFiltroModulos();
        configurarEstructuraFiltros();
        cargarDatosDesdeBD();
        tablaPermisosAgregados.setItems(listaPermisosAgregados);
        lblContadorPermisos.textProperty().bind(
                Bindings.concat(Bindings.size(listaPermisosAgregados), " asignados")
        );

    }

    private void configurarColumnasDisp() {
        colNombreDisp.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().nombre())
        );
        colIdDisp.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                cellData.getValue().idPermiso())
        );
    }

    private void configurarColumnasAgreg() {
        colIdAgreg.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                cellData.getValue().idPermiso())
        );
        colNombreAgreg.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().nombre())
        );
        colModuloAgreg.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().modulo())
        );
    }

    private void configurarEstructuraFiltros() {
        listaFiltrada = new FilteredList<>(listaMaestraPermisos, p -> true);
        tablaPermisosDisponibles.setItems(listaFiltrada);
        cbModulos.valueProperty().addListener((observable, oldValue, newValue) -> {
            listaFiltrada.setPredicate(permiso -> {
                if (newValue == null || newValue.equals("Todos los Módulos")) {
                    return true;
                }
                return newValue.equals(permiso.modulo());
            });
        });
    }

    private void cargarDatosDesdeBD() {
        CompletableFuture.supplyAsync(
                this.orquestadorPermisos::obtenerPermisosActivos
        ).thenAccept(listaPermisos -> {
            Platform.runLater(() -> {
                listaMaestraPermisos.clear();
                if (listaPermisos != null && !listaPermisos.isEmpty()) {
                    listaMaestraPermisos.addAll(listaPermisos);
                }
                actualizarOpcionesComboBox();
            });
        }).exceptionally(ex -> {
            Platform.runLater(() -> {
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                GestorAlertas.mostrarAlertaError(
                        getVentana(), "Error Crítico",
                        "NO se pudieron Cargar los Permisos",
                        "Hubo un fallo al conectar con la base de datos: " + causa.getMessage() + "\n" +
                                "Notificale el error al Administrador y Verifica tu Conexión."
                );
                CargadorVistas.cambiarPantalla(getVentana(), RutasVista.GESTION_ROLES_VIEW);
            });
            return null;
        });
    }

    private void actualizarOpcionesComboBox() {
        List<String> modulosUnicos = listaMaestraPermisos.stream()
                .map(PermisoDTO::modulo)
                .filter(modulo -> modulo != null && !modulo.isBlank())
                .distinct()
                .sorted()
                .toList();
        cbModulos.getItems().clear();
        cbModulos.getItems().add("Todos los Módulos");
        cbModulos.getItems().addAll(modulosUnicos);
        cbModulos.getSelectionModel().selectFirst();
    }

    private void configurarFiltroModulos() {
        List<String> modulosUnicos = listaMaestraPermisos.stream()
                .map(PermisoDTO::modulo)
                .distinct()
                .sorted()
                .toList();
        cbModulos.getItems().clear();
        cbModulos.getItems().add("Todos los Módulos");
        cbModulos.getItems().addAll(modulosUnicos);
        cbModulos.getSelectionModel().selectFirst();
        listaFiltrada = new FilteredList<>(listaMaestraPermisos, p -> true);
        tablaPermisosDisponibles.setItems(listaFiltrada);
        cbModulos.valueProperty().addListener((observable, oldValue, newValue) -> {
            listaFiltrada.setPredicate(permiso -> {
                if (newValue == null || newValue.equals("Todos los Módulos")) {
                    return true;
                }
                return newValue.equals(permiso.modulo());
            });
        });
    }


    @FXML
    private void agregarPermisoAlRol(ActionEvent event) {
        agregarPermisoAlRol();
    }

    private void agregarPermisoAlRol(){
        PermisoDTO permisoSeleccionado = tablaPermisosDisponibles.getSelectionModel().getSelectedItem();
        if (permisoSeleccionado == null) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Atención", null,
                    "Por favor, Selecciona un Permiso de la Tabla para Agregarlo."
            );
            return;
        }
        boolean yaExiste = listaPermisosAgregados.stream()
                .anyMatch(p -> p.idPermiso() == permisoSeleccionado.idPermiso());
        if (yaExiste) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Permiso Duplicado",
                    "El Permiso ya fue Agregado",
                    "El Permiso -" + permisoSeleccionado.nombre() + "- Ya se Encuentra en la Lista de este Rol."
            );
            return;
        }
        listaPermisosAgregados.add(permisoSeleccionado);
        tablaPermisosDisponibles.getSelectionModel().clearSelection();
    }


    @FXML
    private void quitarPermisoDelRol(ActionEvent event) {
        quitarPermisoDelRol();
    }

    private void quitarPermisoDelRol(){
        PermisoDTO permisoSeleccionado = tablaPermisosAgregados.getSelectionModel().getSelectedItem();
        if (permisoSeleccionado == null) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Atención", null,
                    "Por favor, Selecciona un Permiso de la Tabla para Quitarlo."
            );
            return;
        }
        listaPermisosAgregados.remove(permisoSeleccionado);
        tablaPermisosAgregados.getSelectionModel().clearSelection();
    }


    @FXML
    private void guardarCambios(ActionEvent event) {
        guardarCambios();
    }

    private void guardarCambios(){
        String nombre = txtNombreRol.getText();
        if (nombre == null || nombre.trim().isEmpty()) {
            GestorAlertas.mostrarAlertaError(
                    getVentana(), "Error de Validación",
                    "Nombre de Rol Inválido",
                    "El nombre del rol no puede estar vacío. Por favor, ingrese un nombre."
            );
            return;
        }
        String nombreProcesado = nombre.trim();
        boolean estaActivo = chkActivo.isSelected();
        List<PermisoDTO> permisosSeleccionados = new ArrayList<>(listaPermisosAgregados);
        CompletableFuture.runAsync(()->
                this.orquestadorRoles.registrarRolNuevo(nombreProcesado, estaActivo, permisosSeleccionados)
        ).thenRun(()->
            Platform.runLater(()->{
                GestorAlertas.mostrarAlertaInformacion(
                        getVentana(), "Operación Exitosa",
                        "Rol Guardado",
                        "Rol creado correctamente."
                );
                CargadorVistas.cambiarPantalla(getVentana(), RutasVista.GESTION_ROLES_VIEW);
            })
        ).exceptionally(ex->{
            Platform.runLater(()->{
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                if (causa instanceof IllegalArgumentException){
                    GestorAlertas.mostrarAlertaError(
                            getVentana(), "Error al Guardar",
                            "NO se pudo Registrar el Rol",
                            "Error:  " + causa.getMessage()
                    );
                } else if (causa instanceof PersistenciaException){
                    GestorAlertas.mostrarAlertaError(
                            getVentana(), "Error del Sistema",
                            "Fallo de Comunicación",
                            "Hubo un problema guardando en la base de datos: " + causa.getMessage() + "\n" +
                                    "Notificale el error al Administrador y Verifica tu Conexión.");
                }
            });
            return null;
        });
    }


    @FXML
    private void cancelar(ActionEvent event) {
        CargadorVistas.cambiarPantalla(getVentana(), RutasVista.GESTION_ROLES_VIEW);
    }

}//===================================================================================================================//


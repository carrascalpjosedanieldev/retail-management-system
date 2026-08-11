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
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

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

    @FXML
    public void initialize(){
        configurarColumnasDisp();
        configurarColumnasAgreg();
        boolean cargaExitosa = cargarDatosDesdeBD();
        if (!cargaExitosa) {
            cerrarVentanaSeguro();
            return;
        }
        tablaPermisosAgregados.setItems(listaPermisosAgregados);
        lblContadorPermisos.textProperty().bind(
                Bindings.concat(Bindings.size(listaPermisosAgregados), " asignados")
        );
        configurarFiltroModulos();
    }

    private void configurarColumnasDisp() {
        colNombreDisp.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().nombre()));
        colIdDisp.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().idPermiso()));
    }

    private void configurarColumnasAgreg() {
        colIdAgreg.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().idPermiso()));
        colNombreAgreg.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().nombre()));
        colModuloAgreg.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().modulo()));
    }

    private boolean cargarDatosDesdeBD() {
        try {
            List<PermisoDTO> datosBD = this.orquestadorPermisos.obtenerPermisosActivos();
            listaMaestraPermisos.clear();
            if (datosBD != null && !datosBD.isEmpty()) {
                listaMaestraPermisos.addAll(datosBD);
            }
            return true;
        } catch (RuntimeException e) {
            GestorAlertas.mostrarAlertaError(
                    "Error Crítico",
                    "NO se pudieron Cargar los Permisos",
                    "Hubo un fallo al conectar con la base de datos: " + e.getMessage()
            );
            return false;
        }
    }

    private void cerrarVentanaSeguro() {
        Platform.runLater(() -> {
            Stage stageActual = (Stage) cbModulos.getScene().getWindow();
            CargadorVistas.cambiarPantalla(stageActual, RutasVista.GESTION_ROLES_VIEW);
        });
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
            return;
        }
        boolean yaExiste = listaPermisosAgregados.stream()
                .anyMatch(p -> p.idPermiso() == permisoSeleccionado.idPermiso());
        if (yaExiste) {
            GestorAlertas.mostrarAlertaWarning(
                    "Permiso Duplicado",
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
                    "Error de Validación",
                    "Nombre de Rol Inválido",
                    "El nombre del rol no puede estar vacío. Por favor, ingrese un nombre."
            );
            return;
        }
        String nombreProcesado = nombre.trim();
        boolean estaActivo = chkActivo.isSelected();
        List<PermisoDTO> permisosSeleccionados = new ArrayList<>(listaPermisosAgregados);
        try {
            this.orquestadorRoles.registrarRolNuevo(nombreProcesado, estaActivo, permisosSeleccionados);
            GestorAlertas.mostrarAlertaInformacion(
                    "Operación Exitosa",
                    "Rol Guardado",
                    "Rol creado correctamente."
            );
            cerrarVentanaSeguro();
        } catch (IllegalArgumentException e) {
            GestorAlertas.mostrarAlertaError(
                    "Error al Guardar",
                    "NO se pudo Registrar el Rol",
                    "Error:  " + e.getMessage()
            );
        } catch (PersistenciaException e) {
            GestorAlertas.mostrarAlertaError("Error del Sistema",
                    "Fallo de Comunicación",
                    "Hubo un problema guardando en la base de datos. Intente más tarde.");
        }
    }


    @FXML
    private void cancelar(ActionEvent event) {
        cerrarVentanaSeguro();
    }

}//===================================================================================================================//


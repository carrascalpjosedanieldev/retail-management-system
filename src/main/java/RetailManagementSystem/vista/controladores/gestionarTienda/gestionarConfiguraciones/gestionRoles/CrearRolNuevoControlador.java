package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarConfiguraciones.gestionRoles;

import RetailManagementSystem.aplicacion.dto.seguridad.PermisoDTO;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorPermisos;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorRoles;
import RetailManagementSystem.vista.utilidades.GestorAlertas;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

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
            GestorAlertas.mostrarError(
                    "Error Crítico",
                    "NO se pudieron Cargar los Permisos",
                    "Hubo un fallo al conectar con la base de datos: " + e.getMessage()
            );
            return false;
        }
    }

    private void cerrarVentanaSeguro() {
        Platform.runLater(() -> {
            Stage stage = (Stage) cbModulos.getScene().getWindow();
            if (stage != null) {
                stage.close();
            }
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

    }

    @FXML
    private void cancelar(ActionEvent event) {

    }

    @FXML
    private void guardarCambios(ActionEvent event) {

    }

    @FXML
    private void quitarPermisoDelRol(ActionEvent event) {

    }

}//===================================================================================================================//


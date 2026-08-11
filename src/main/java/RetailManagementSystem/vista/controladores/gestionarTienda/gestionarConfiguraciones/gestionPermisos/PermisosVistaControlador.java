package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarConfiguraciones.gestionPermisos;

import RetailManagementSystem.aplicacion.dto.seguridad.PermisoDTO;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorPermisos;
import RetailManagementSystem.vista.utilidades.CargadorVistas;
import RetailManagementSystem.vista.utilidades.GestorAlertas;
import RetailManagementSystem.vista.utilidades.RutasVista;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

public class PermisosVistaControlador {

    //ATRIBUTO:

    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbFiltroModulo;
    @FXML private ToggleGroup tgEstado;
    @FXML private ToggleButton btnFiltroTodos;
    @FXML private ToggleButton btnFiltroActivos;
    @FXML private ToggleButton btnFiltroInactivos;
    @FXML private Button btnSalir;
    @FXML private TableView<PermisoDTO> tablaPermisos;
    @FXML private TableColumn<PermisoDTO, Integer> colId;
    @FXML private TableColumn<PermisoDTO, String> colNombre;
    @FXML private TableColumn<PermisoDTO, String> colModulo;
    @FXML private TableColumn<PermisoDTO, String> colDescripcion;
    @FXML private TableColumn<PermisoDTO, String> colEstado;

    private final OrquestadorPermisos orquestadorPermisos;

    private final ObservableList<PermisoDTO> listaMaestraPermisos = FXCollections.observableArrayList();

    private FilteredList<PermisoDTO> listaFiltrada;

    //CONSTRUCTOR:

    public PermisosVistaControlador(OrquestadorPermisos orquestadorPermisos) {
        this.orquestadorPermisos = orquestadorPermisos;
    }

    //MÉTODOS:

    @FXML
    public void initialize() {
        configurarColumnas();
        cargarDatosDesdeBD();
        configurarFiltrosReactivos();
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(cellData -> {
            return new ReadOnlyObjectWrapper<>(cellData.getValue().idPermiso());
        });
        colNombre.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(cellData.getValue().nombre());
        });
        colModulo.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(cellData.getValue().modulo());
        });
        colDescripcion.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(cellData.getValue().descripcion());
        });
        colEstado.setCellValueFactory(cellData -> {
            boolean estaActivo = cellData.getValue().activo();
            return new SimpleStringProperty(estaActivo ? "Activo" : "Inactivo");
        });
    }

    private void cargarDatosDesdeBD() {
        try {
            List<PermisoDTO> datosBD = this.orquestadorPermisos.obtenerTodosLosPermisos();
            listaMaestraPermisos.clear();
            if (datosBD != null && !datosBD.isEmpty()) {
                listaMaestraPermisos.addAll(datosBD);
            }
            List<String> modulosUnicos = listaMaestraPermisos.stream()
                    .map(PermisoDTO::modulo)
                    .filter(modulo -> modulo != null && !modulo.isBlank())
                    .distinct()
                    .sorted()
                    .toList();
            cbFiltroModulo.getItems().clear();
            cbFiltroModulo.getItems().add("Todos los Módulos");
            cbFiltroModulo.getItems().addAll(modulosUnicos);
            cbFiltroModulo.getSelectionModel().selectFirst();
        } catch (RuntimeException e) {
            GestorAlertas.mostrarError(
                    "Error Crítico",
                    "No se pudieron cargar los permisos",
                    "Hubo un fallo al conectar con la base de datos: " + e.getMessage()
            );
            if (btnSalir != null && btnSalir.getScene() != null) {
                ((Stage) btnSalir.getScene().getWindow()).close();
            }
        }
    }

    private void configurarFiltrosReactivos() {
        listaFiltrada = new FilteredList<>(listaMaestraPermisos, b -> true);
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> aplicarFiltros());
        cbFiltroModulo.valueProperty().addListener((observable, oldValue, newValue) -> aplicarFiltros());
        tgEstado.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
            if (newToggle == null) {
                tgEstado.selectToggle(btnFiltroTodos);
            } else {
                aplicarFiltros();
            }
        });
        tablaPermisos.setItems(listaFiltrada);
    }

    private void aplicarFiltros() {
        listaFiltrada.setPredicate(permiso -> {
            String textoBusqueda = txtBuscar.getText();
            boolean coincideTexto = true;
            if (textoBusqueda != null && !textoBusqueda.isBlank()) {
                String filtro = textoBusqueda.toLowerCase();
                coincideTexto = permiso.nombre().toLowerCase().contains(filtro) ||
                        permiso.descripcion().toLowerCase().contains(filtro) ||
                        String.valueOf(permiso.idPermiso()).contains(filtro);
            }
            String moduloSeleccionado = cbFiltroModulo.getValue();
            boolean coincideModulo = true;

            if (moduloSeleccionado != null && !moduloSeleccionado.equals("Todos los Módulos")) {
                coincideModulo = permiso.modulo().equalsIgnoreCase(moduloSeleccionado);
            }
            boolean coincideEstado = true;
            ToggleButton toggleActivo = (ToggleButton) tgEstado.getSelectedToggle();
            if (toggleActivo == btnFiltroActivos) {
                coincideEstado = permiso.activo();
            } else if (toggleActivo == btnFiltroInactivos) {
                coincideEstado = !permiso.activo();
            }
            return coincideTexto && coincideModulo && coincideEstado;
        });
    }


    @FXML
    public void accionSalir(ActionEvent event) {
        Stage stageActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
        CargadorVistas.cambiarPantalla(stageActual, RutasVista.GESTIONAR_CONFIGURACIONES_VIEW);
    }


}//===================================================================================================================//


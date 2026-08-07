package RetailManagementSystem.vista.controladores.login;

import RetailManagementSystem.aplicacion.dto.seguridad.PermisoDTO;
import RetailManagementSystem.aplicacion.servicios.ServicioPermiso;
import RetailManagementSystem.vista.utilidades.GestorAlertas;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;

public class PermisosVistaControlador {

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

    // =================================================================================
    // 2. DEPENDENCIAS Y ESTADO
    // =================================================================================
    private final ServicioPermiso servicioPermiso;

    // Las listas observables que conectan los datos con la tabla
    private ObservableList<PermisoDTO> listaMaestraPermisos;

    private FilteredList<PermisoDTO> listaFiltrada;

    /**
     * Constructor para la Inyección de Dependencias.
     * Este controlador debe ser instanciado por tu ContenedorDependencias.
     */
    public PermisosVistaControlador(ServicioPermiso servicioPermiso) {
        this.servicioPermiso = servicioPermiso;
        this.listaMaestraPermisos = FXCollections.observableArrayList();
    }


    @FXML
    public void initialize() {
        configurarColumnas();
        cargarDatosDesdeBD();
        configurarFiltrosReactivos();
        configurarBotonSalir();
    }

    // =================================================================================
    // 4. CONFIGURACIÓN DE LA INTERFAZ
    // =================================================================================
    private void configurarColumnas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colModulo.setCellValueFactory(new PropertyValueFactory<>("modulo"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        // Columna personalizada: Convertimos el booleano 'activo' a texto legible
        colEstado.setCellValueFactory(cellData -> {
            boolean estaActivo = cellData.getValue().activo();
            return new SimpleStringProperty(estaActivo ? "Activo" : "Inactivo");
        });
    }

    private void cargarDatosDesdeBD() {
        try {
            List<PermisoDTO> datosBD = servicioPermiso.obtenerTodosLosPermisos();
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
            // Evitar pantalla zombie cerrándola
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
                        permiso.descripcion().toLowerCase().contains(filtro);
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

    private void configurarBotonSalir() {
        btnSalir.setOnAction((ActionEvent event) -> {
            Stage stage = (Stage) btnSalir.getScene().getWindow();
            stage.close();
        });
    }

}//===================================================================================================================//


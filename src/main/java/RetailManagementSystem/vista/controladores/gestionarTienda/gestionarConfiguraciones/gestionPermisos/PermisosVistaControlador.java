package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarConfiguraciones.gestionPermisos;

import RetailManagementSystem.aplicacion.dto.seguridad.PermisoDTO;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorPermisos;
import RetailManagementSystem.vista.utilidades.CargadorVistas;
import RetailManagementSystem.vista.utilidades.GestorAlertas;
import RetailManagementSystem.vista.utilidades.RutasVista;

import RetailManagementSystem.vista.utilidades.UtilidadesLista;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Window;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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

    private Window getVentana(){
        return tablaPermisos.getScene() != null ? tablaPermisos.getScene().getWindow() : null;
    }


    @FXML
    public void initialize() {
        configurarColumnas();
        cargarDatosDesdeBD();
        configurarFiltrosReactivos();
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().idPermiso())
        );
        colNombre.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().nombre())
        );
        colModulo.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().modulo())
        );
        colDescripcion.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().descripcion())
        );
        colEstado.setCellValueFactory(cellData -> {
            boolean estaActivo = cellData.getValue().activo();
            return new SimpleStringProperty(estaActivo ? "Activo" : "Inactivo");
        });
        colEstado.setCellFactory(columna -> new TableCell<PermisoDTO, String>() {
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

    private void cargarDatosDesdeBD() {
        CompletableFuture.supplyAsync(
                this.orquestadorPermisos::obtenerTodosLosPermisos
        ).thenAccept(listaPermisos -> {
            Platform.runLater(()->{
                listaMaestraPermisos.clear();
                listaMaestraPermisos.addAll(listaPermisos);
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
            });
        }).exceptionally(ex->{
            Platform.runLater(()->{
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                GestorAlertas.mostrarAlertaError(
                        getVentana(), "Error Crítico",
                        "No se pudieron cargar los permisos",
                        "Hubo un fallo al conectar con la Base de Datos: " + causa.getMessage() + "\n" +
                                "Comunicate con el Administrador y Revisa tu conexión."
                );
                CargadorVistas.cambiarPantalla(getVentana(), RutasVista.GESTIONAR_CONFIGURACIONES_VIEW);
            });
            return null;
        });
    }

    private void configurarFiltrosReactivos() {
        listaFiltrada = new FilteredList<>(listaMaestraPermisos, b -> true);
        txtBuscar.textProperty().addListener(
                (observable, oldValue, newValue) -> aplicarFiltros()
        );
        cbFiltroModulo.valueProperty().addListener(
                (observable, oldValue, newValue) -> aplicarFiltros()
        );
        tgEstado.selectedToggleProperty().addListener(
                (observable, oldToggle, newToggle) -> {
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
    public void accionCambiarEstado(ActionEvent event) {
        PermisoDTO permisoSeleccionado = tablaPermisos.getSelectionModel().getSelectedItem();
        if (permisoSeleccionado == null){
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Atención", null,
                    "Por favor, selecciona un Permiso de la Tabla para cambiar su Estado."
            );
            return;
        }
        if (!GestorAlertas.mostrarConfirmacion(getVentana(), "Confirmar", null,
                "¿Estás Seguro de Cambiar el Estado del Permiso?")) {
            return;
        }
        CompletableFuture.runAsync(()->
                this.orquestadorPermisos.cambiarEstadoPermiso(permisoSeleccionado.idPermiso(), permisoSeleccionado.activo())
        ).thenRun(()->{
            Platform.runLater(()->{
                GestorAlertas.mostrarAlertaInformacion(
                        getVentana(), "Éxito", null,
                        "El Estado se ha Actualizado Correctamente."
                );
                PermisoDTO actualizado = new PermisoDTO(
                        permisoSeleccionado.idPermiso(),
                        permisoSeleccionado.nombre(),
                        permisoSeleccionado.descripcion(),
                        permisoSeleccionado.modulo(),
                        !permisoSeleccionado.activo()
                );
                UtilidadesLista.reemplazarPorIdentidad(
                        listaMaestraPermisos,
                        actualizado,
                        item -> item.idPermiso() == actualizado.idPermiso()
                );
            });
        }).exceptionally(ex->{
            Platform.runLater(()->{
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                GestorAlertas.mostrarAlertaError(
                        getVentana(), "Error Critico",
                        "NO se pudo Completar la Acción.",
                        "Notificale al Administrador este Error:\n" + causa.getMessage()
                );
            });
            return null;
        });
    }


    @FXML
    public void accionSalir(ActionEvent event) {
        CargadorVistas.cambiarPantalla(getVentana(), RutasVista.GESTIONAR_CONFIGURACIONES_VIEW);
    }


}//===================================================================================================================//


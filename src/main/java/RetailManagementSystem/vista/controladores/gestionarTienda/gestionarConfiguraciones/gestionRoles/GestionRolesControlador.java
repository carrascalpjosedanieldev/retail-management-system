package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarConfiguraciones.gestionRoles;

import RetailManagementSystem.aplicacion.dto.seguridad.RolDTO;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorRoles;
import RetailManagementSystem.vista.utilidades.CargadorVistas;
import RetailManagementSystem.vista.utilidades.GestorAlertas;
import RetailManagementSystem.vista.utilidades.RutasVista;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Window;

import java.util.concurrent.CompletableFuture;

public class GestionRolesControlador {

    //ATRIBUTOS:

    @FXML private TextField txtBuscar;
    @FXML private TableView<RolDTO> tablaRoles;
    @FXML private TableColumn<RolDTO, Integer> colId;
    @FXML private TableColumn<RolDTO, String> colNombre;
    @FXML private TableColumn<RolDTO, String> colEstado;

    private final OrquestadorRoles orquestadorRoles;

    private final ObservableList<RolDTO> listaMaestraRoles = FXCollections.observableArrayList();

    private FilteredList<RolDTO> listaFiltrada;

    //CONSTRUCTOR:

    public GestionRolesControlador(OrquestadorRoles orquestadorRoles) {
        this.orquestadorRoles = orquestadorRoles;
    }

    //MÉTODOS:

    private Window getVentana(){
        return tablaRoles.getScene() != null ? tablaRoles.getScene().getWindow() : null;
    }


    @FXML
    public void initialize() {
        configurarColumnas();
        cargarDatosDesdeBD();
        configurarFiltroBusqueda();
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                cellData.getValue().idRol())
        );
        colNombre.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().nombre()
        ));
        colEstado.setCellValueFactory(cellData -> {
            boolean estaActivo = cellData.getValue().activo();
            return new SimpleStringProperty(estaActivo ? "Activo" : "Inactivo");
        });
        colEstado.setCellFactory(columna -> new TableCell<RolDTO, String>(){
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
                this.orquestadorRoles::obtenerTodosLosRoles
        ).thenAccept(listaRoles ->
            Platform.runLater(()->{
                listaMaestraRoles.clear();
                if (listaRoles != null && !listaRoles.isEmpty()) {
                    listaMaestraRoles.addAll(listaRoles);
                }
            })
        ).exceptionally(ex->{
            Platform.runLater(()-> {
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                GestorAlertas.mostrarAlertaError(
                        getVentana(), "Error de Carga",
                        "No se pudieron cargar los roles.",
                        "Detalle: " + causa.getMessage() + "\n" +
                                "Notificale el error al Administrador y Verifica tu conexión,"
                );
                CargadorVistas.cambiarPantalla(getVentana(), RutasVista.GESTIONAR_CONFIGURACIONES_VIEW);
            });
            return null;
        });
    }

    private void configurarFiltroBusqueda() {
        listaFiltrada = new FilteredList<>(listaMaestraRoles, b -> true);
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            listaFiltrada.setPredicate(rol -> {
                if (newValue == null || newValue.isBlank()) {
                    return true;
                }
                String filtro = newValue.toLowerCase();
                boolean coincideId = String.valueOf(rol.idRol()).contains(filtro);
                boolean coincideNombre = rol.nombre().toLowerCase().contains(filtro);
                return coincideId || coincideNombre;
            });
        });
        tablaRoles.setItems(listaFiltrada);
    }


    @FXML
    private void abrirFormularioNuevo(ActionEvent event) {
        CargadorVistas.cambiarPantalla(getVentana(), RutasVista.CREAR_ROL_NUEVO_VIEW);
    }


    @FXML
    private void abrirFormularioEdicion(ActionEvent event) {
        RolDTO rolSeleccionado = tablaRoles.getSelectionModel().getSelectedItem();
        if (rolSeleccionado == null) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Atención", null,
                    "Por favor, Selecciona un Rol de la Tabla para Modificarlo."
            );
            return;
        }
        CargadorVistas.abrirModalConInyeccion(
                RutasVista.MODIFICAR_DATOS_ROL_VIEW,
                "Editando Rol -" + rolSeleccionado.nombre() + "-", getVentana(),
                (ModificarDatosRolControlador c)->{
                    c.cargarDatos(rolSeleccionado, listaMaestraRoles);
                }
        );
    }


    @FXML
    public void administrarPermisosRol(ActionEvent event) {
        RolDTO rolSeleccionado = tablaRoles.getSelectionModel().getSelectedItem();
        if (rolSeleccionado == null) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Atención", null,
                    "Por favor, Selecciona un Rol de la Tabla para Administrar sus Permisos."
            );
            return;
        }
        CargadorVistas.cambiarPantallaInyectada(
                RutasVista.ADMINISTRAR_PERMISOS_DE_ROL_VIEW,
                getVentana(),
                (AdministrarPermisosDeRolControlador c)->{
                    c.cargarDatos(rolSeleccionado);
                }
        );
    }


    @FXML
    private void volverAlPanel(ActionEvent event) {
        CargadorVistas.cambiarPantalla(getVentana(), RutasVista.GESTIONAR_CONFIGURACIONES_VIEW);
    }


}//===================================================================================================================//


package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarConfiguraciones.gestionRoles;

import RetailManagementSystem.aplicacion.dto.seguridad.RolDTO;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorRoles;
import RetailManagementSystem.vista.excepciones.CargarVistaException;
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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
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
        return tablaRoles.getScene().getWindow();
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
                Stage stageActual = (Stage) getVentana();
                CargadorVistas.cambiarPantalla(stageActual, RutasVista.GESTIONAR_CONFIGURACIONES_VIEW);
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
        Stage stageActual = (Stage) getVentana();
        CargadorVistas.cambiarPantalla(stageActual, RutasVista.CREAR_ROL_NUEVO_VIEW);
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
        String rutaFxml = RutasVista.MODIFICAR_DATOS_ROL_VIEW;
        try {
            FXMLLoader loader = CargadorVistas.obtenerLoaderConfigurado(rutaFxml);
            Parent root = loader.load();
            ModificarDatosRolControlador controlador = loader.getController();
            controlador.cargarDatos(rolSeleccionado, listaMaestraRoles);
            Stage stageEdicion = new Stage();
            stageEdicion.setTitle("Editando Rol -" + rolSeleccionado.nombre() + "-");
            stageEdicion.initModality(Modality.APPLICATION_MODAL);
            stageEdicion.setResizable(false);
            Scene escenaEdicion = new Scene(root);
            stageEdicion.setScene(escenaEdicion);
            stageEdicion.showAndWait();
        } catch (IOException e) {
            throw new CargarVistaException(rutaFxml, "NO se pudo Cargar el Archivo FXML.", e);
        }
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
        String rutaFxml = RutasVista.ADMINISTRAR_PERMISOS_DE_ROL_VIEW;
        try {
            FXMLLoader loader = CargadorVistas.obtenerLoaderConfigurado(rutaFxml);
            Parent root = loader.load();
            AdministrarPermisosDeRolControlador controlador = loader.getController();
            controlador.cargarDatos(rolSeleccionado);
            Stage stageActual = (Stage) getVentana();
            stageActual.getScene().setRoot(root);
        } catch (IOException e) {
            throw new CargarVistaException(rutaFxml, "NO se pudo Cargar el Archivo FXML.", e);
        }
    }


    @FXML
    private void volverAlPanel(ActionEvent event) {
        Stage stageActual = (Stage) getVentana();
        CargadorVistas.cambiarPantalla(stageActual, RutasVista.GESTIONAR_CONFIGURACIONES_VIEW);
    }


}//===================================================================================================================//


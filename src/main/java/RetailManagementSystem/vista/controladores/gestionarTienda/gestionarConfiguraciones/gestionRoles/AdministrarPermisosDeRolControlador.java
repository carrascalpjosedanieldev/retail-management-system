package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarConfiguraciones.gestionRoles;

import RetailManagementSystem.aplicacion.dto.seguridad.PermisoDTO;
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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AdministrarPermisosDeRolControlador {

    //ATRIBUTOS:

    @FXML private TableView<PermisoDTO> tablaPermisosRol;
    @FXML private TableColumn<PermisoDTO, Integer> colId;
    @FXML private TableColumn<PermisoDTO, String> colNombre;
    @FXML private TableColumn<PermisoDTO, String> colModulo;
    @FXML private TableColumn<PermisoDTO, String> colEstado;
    @FXML private Label lblNombreRol;

    private final OrquestadorRoles orquestadorRoles;

    private final ObservableList<PermisoDTO> listaObservablePermisos = FXCollections.observableArrayList();

    private RolDTO datosRol;

    //CONSTRUCTOR:

    public AdministrarPermisosDeRolControlador(OrquestadorRoles orquestadorRoles) {
        this.orquestadorRoles = orquestadorRoles;
    }

    //MÉTODOS:

    public void cargarDatos(RolDTO datosRol){
        if (datosRol == null){
            GestorAlertas.mostrarAlertaError(
                    "Error", null,
                    "NO puedes Administrar los Permisos de un Rol Vacío."
            );
            return;
        }
        this.datosRol = datosRol;
        lblNombreRol.setText(this.datosRol.nombre());
        listaObservablePermisos.setAll(this.datosRol.permisos());
    }


    @FXML
    public void initialize(){
        configurarColumnas();
        tablaPermisosRol.setItems(listaObservablePermisos);
    }

    private void configurarColumnas(){
        colId.setCellValueFactory(cellData-> new SimpleObjectProperty<>(
                cellData.getValue().idPermiso()
        ));
        colNombre.setCellValueFactory(cellData-> new SimpleStringProperty(
                cellData.getValue().nombre()
        ));
        colModulo.setCellValueFactory(cellData-> new SimpleStringProperty(
                cellData.getValue().modulo()
        ));
        colEstado.setCellValueFactory(cellData-> {
            boolean esActivo = cellData.getValue().activo();
            String estado = esActivo ? "Activo" : "Inactivo";
            return new SimpleStringProperty(estado);
        });
        colEstado.setCellFactory(columna-> new TableCell<PermisoDTO, String>(){
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


    @FXML
    void anadirPermiso(ActionEvent event) {
        String rutaFxml = RutasVista.ANADIR_PERMISO_AL_ROL_VIEW;
        try {
            FXMLLoader loader = CargadorVistas.obtenerLoaderConfigurado(rutaFxml);
            Parent root = loader.load();
            AnadirPermisoAlRolControlador controlador = loader.getController();
            controlador.cargarDatos(listaObservablePermisos);
            Stage stageAdministrar = new Stage();
            stageAdministrar.setTitle("Administrar Permisos.");
            stageAdministrar.initModality(Modality.APPLICATION_MODAL);
            stageAdministrar.setResizable(false);
            Scene escenaEdicion = new Scene(root);
            stageAdministrar.setScene(escenaEdicion);
            stageAdministrar.showAndWait();
        } catch (IOException e) {
            throw new CargarVistaException(rutaFxml, "NO se pudo Cargar el Archivo FXML.", e);
        }
    }


    @FXML
    void eliminarPermiso(ActionEvent event) {
        PermisoDTO seleccionado = tablaPermisosRol.getSelectionModel().getSelectedItem();
        if (seleccionado == null){
            GestorAlertas.mostrarAlertaWarning(
                    "Atención", null,
                    "Por favor, selecciona un Permiso de la Tabla para Eliminarlo."
            );
            return;
        }
        if (!GestorAlertas.mostrarConfirmacion("Confirmar", null,
                "¿Estás Seguro de Eliminar este Permiso?")) {
            return;
        }
        listaObservablePermisos.remove(seleccionado);
        tablaPermisosRol.getSelectionModel().clearSelection();
    }


    @FXML
    void guardarCambios(ActionEvent event) {
        if (!GestorAlertas.mostrarConfirmacion("Confirmar", null,
                "¿Quieres guardar los cambios en el Rol?")){
            return;
        }
        CompletableFuture.runAsync(()->{
            List<PermisoDTO> listaPermisos = new ArrayList<>(listaObservablePermisos);
            this.orquestadorRoles.actualizarPermisosRol(this.datosRol.idRol(), listaPermisos);
        }).thenRun(()->{
            Platform.runLater(()->{
                GestorAlertas.mostrarAlertaInformacion(
                        "Éxito", null,
                        "Se han guardado los cambios con Éxito."
                );
                volverAlPanel();
            });
        }).exceptionally(ex->{
            Platform.runLater(()->{
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                GestorAlertas.mostrarAlertaError(
                        "Error Critico",
                        "NO se pudo Completar la Acción.",
                        "Verifica tu conexión y Notificale al Administrador este Error:\n"
                                + causa.getMessage()
                );
            });
            return null;
        });
    }

    private void volverAlPanel(){
        Stage stageActual = (Stage) lblNombreRol.getScene().getWindow();
        CargadorVistas.cambiarPantalla(stageActual, RutasVista.GESTION_ROLES_VIEW);
    }


    @FXML
    void cancelar(ActionEvent event) {
        if (!GestorAlertas.mostrarConfirmacion("Salir?", "Estas seguro de salir",
                "Si sales ahora se descartaran los cambios realizados.")){
            return;
        }
        volverAlPanel();
    }


}//===================================================================================================================//


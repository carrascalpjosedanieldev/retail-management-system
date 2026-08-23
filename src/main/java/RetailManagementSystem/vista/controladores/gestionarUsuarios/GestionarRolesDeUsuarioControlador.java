package RetailManagementSystem.vista.controladores.gestionarUsuarios;

import RetailManagementSystem.aplicacion.dto.seguridad.RolDTO;
import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorRoles;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorUsuarios;
import RetailManagementSystem.vista.utilidades.GestorAlertas;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GestionarRolesDeUsuarioControlador {

    //ATRIBUTOS:

    @FXML private Button btnCancelar;
    @FXML private Button btnGuardar;
    @FXML private TableColumn<RolDTO, Integer> colIdActual;
    @FXML private TableColumn<RolDTO, Integer> colIdDisponible;
    @FXML private TableColumn<RolDTO, String> colNombreActual;
    @FXML private TableColumn<RolDTO, String> colNombreDisponible;
    @FXML private TableColumn<RolDTO, String> colActivoActual;
    @FXML private TableColumn<RolDTO, String> colActivoDisponible;
    @FXML private Label lblNombreUsuario;
    @FXML private TableView<RolDTO> tablaRolesActuales;
    @FXML private TableView<RolDTO> tablaRolesDisponibles;

    private UsuarioDTOCompleto datosUsuario;

    private final OrquestadorUsuarios orquestadorUsuarios;

    private final OrquestadorRoles orquestadorRoles;

    private final ObservableList<RolDTO> listaRolesActuales = FXCollections.observableArrayList();

    private final ObservableList<RolDTO> listaRolesDisponibles = FXCollections.observableArrayList();

    //CONSTRUCTOR:

    public GestionarRolesDeUsuarioControlador(OrquestadorUsuarios orquestadorUsuarios, OrquestadorRoles orquestadorRoles) {
        this.orquestadorUsuarios = orquestadorUsuarios;
        this.orquestadorRoles = orquestadorRoles;
    }

    //MÉTODOS:

    public void cargarDatos(Long idUsuario){
        CompletableFuture.supplyAsync(()->
                this.orquestadorUsuarios.obtenerDatosTotalesUsuario(idUsuario)
        ).thenAccept(datosUsuario->
                Platform.runLater(()->{
                    this.datosUsuario = datosUsuario;
                    lblNombreUsuario.setText(datosUsuario.getNombreCompleto());
                    listaRolesActuales.setAll(datosUsuario.roles());
                    cargarRolesDisponibles();
                })
        ).exceptionally(ex->{
            Platform.runLater(()->{
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                GestorAlertas.mostrarAlertaError(
                        getVentana(), "Error Critico",
                        "NO se Recibieron los Roles del Usuario.",
                        "Se Cerrara la Ventana por Seguridad\n" +
                                "Verifica tu Conexión y Notificale al Administrador este Error:\n" +
                                causa.getMessage()
                );
                cerrarModal();
            });
            return null;
        });
    }

    private Window getVentana(){
        return btnCancelar.getScene() != null ? btnCancelar.getScene().getWindow() : null;
    }


    private void cargarRolesDisponibles(){
        CompletableFuture.supplyAsync(
                this.orquestadorRoles::obtenerTodosLosRoles
        ).thenApply(todosLosRoles->{
            List<RolDTO> listaModificable = new ArrayList<>(todosLosRoles);
            List<Integer> idsQueYaTiene = this.datosUsuario.roles().stream()
                    .map(RolDTO::idRol)
                    .toList();
            return listaModificable.stream()
                    .filter(rol -> !idsQueYaTiene.contains(rol.idRol()))
                    .toList();
        }).thenAccept(listaRolesFiltrados->
            Platform.runLater(()->
                listaRolesDisponibles.addAll(listaRolesFiltrados)
            )
        ).exceptionally(ex->{
            Platform.runLater(()->{
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                GestorAlertas.mostrarAlertaError(
                        getVentana(), "Error Critico",
                        "NO se Recibieron los Roles Disponibles.",
                        "Se Cerrara la Ventana por Seguridad\n" +
                                "Verifica tu Conexión y Notificale al Administrador este Error:\n" +
                                causa.getMessage()
                );
                cerrarModal();
            });
            return null;
        });
    }


    @FXML
    void initialize(){
        configurarColumnas();
        tablaRolesActuales.setItems(listaRolesActuales);
        tablaRolesDisponibles.setItems(listaRolesDisponibles);
    }

    private void configurarColumnas(){
        colIdActual.setCellValueFactory(cellData-> new SimpleObjectProperty<>(
                cellData.getValue().idRol()
        ));
        colIdDisponible.setCellValueFactory(cellData-> new SimpleObjectProperty<>(
                cellData.getValue().idRol()
        ));
        colNombreActual.setCellValueFactory(cellData-> new SimpleStringProperty(
                cellData.getValue().nombre()
        ));
        colNombreDisponible.setCellValueFactory(cellData-> new SimpleStringProperty(
                cellData.getValue().nombre()
        ));
        colActivoActual.setCellValueFactory(cellData-> {
            boolean esActivo = cellData.getValue().activo();
            String activo = esActivo ? "Activo" : "Inactivo";
            return new SimpleStringProperty(activo);
        });
        colActivoActual.setCellFactory(columna -> new TableCell<RolDTO, String>(){
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
        colActivoDisponible.setCellValueFactory(cellData-> {
            boolean esActivo = cellData.getValue().activo();
            String activo = esActivo ? "Activo" : "Inactivo";
            return new SimpleStringProperty(activo);
        });
        colActivoDisponible.setCellFactory(columna -> new TableCell<RolDTO, String>(){
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
    void anadirRol(ActionEvent event) {
        RolDTO seleccionado = tablaRolesDisponibles.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Atención", null,
                    "Por favor, Selecciona un Rol de la Tabla de Roles Disponibles para Agregarlo."
            );
            return;
        }
        listaRolesActuales.add(seleccionado);
        listaRolesDisponibles.remove(seleccionado);
        tablaRolesDisponibles.getSelectionModel().clearSelection();
    }


    @FXML
    void quitarRol(ActionEvent event) {
        RolDTO seleccionado = tablaRolesActuales.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Atención", null,
                    "Por favor, Selecciona un Rol de la Tabla de Roles Actuales para Agregarlo."
            );
            return;
        }
        listaRolesDisponibles.add(seleccionado);
        listaRolesActuales.remove(seleccionado);
        tablaRolesActuales.getSelectionModel().clearSelection();
    }


    @FXML
    void guardarCambios(ActionEvent event) {



    }


    @FXML
    void cerrarVentana(ActionEvent event) {
        if (!GestorAlertas.mostrarConfirmacion(
                getVentana(), "¿Estas Seguro de Salir?", null,
                "Si Sales Ahora, se Descartaran los Cambios Realizados."
        )){
            return;
        }
        cerrarModal();
    }

    private void cerrarModal(){
        Window ventana = getVentana();
        if (ventana != null){
            ventana.hide();
        }
    }

}//===================================================================================================================//


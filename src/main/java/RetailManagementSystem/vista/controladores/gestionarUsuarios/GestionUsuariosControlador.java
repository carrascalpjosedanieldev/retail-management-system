package RetailManagementSystem.vista.controladores.gestionarUsuarios;

import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorUsuarios;
import RetailManagementSystem.vista.utilidades.CargadorVistas;
import RetailManagementSystem.vista.utilidades.GestorAlertas;
import RetailManagementSystem.vista.utilidades.RutasVista;

import RetailManagementSystem.vista.utilidades.UtilidadesLista;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Window;

import java.util.concurrent.CompletableFuture;

public class GestionUsuariosControlador {

    //ATRIBUTOS:

    @FXML private TableColumn<UsuarioDTOCompleto, Long> colId;
    @FXML private TableColumn<UsuarioDTOCompleto, String> colNombre;
    @FXML private TableColumn<UsuarioDTOCompleto, String> colApellido;
    @FXML private TableColumn<UsuarioDTOCompleto, String> colEmail;
    @FXML private TableColumn<UsuarioDTOCompleto, String> colEstado;
    @FXML private TableView<UsuarioDTOCompleto> tablaUsuarios;
    @FXML private TextField txtBuscar;
    @FXML private Button btnEditar;
    @FXML private Button btnCambiarEstado;
    @FXML private Button btnGestionarRoles;
    @FXML private Button btnRestablecerClave;

    private final OrquestadorUsuarios orquestadorUsuarios;

    private final ObservableList<UsuarioDTOCompleto> listaObservableUsuarios = FXCollections.observableArrayList();

    //CONSTRUCTOR:

    public GestionUsuariosControlador(OrquestadorUsuarios orquestadorUsuarios) {
        this.orquestadorUsuarios = orquestadorUsuarios;
    }

    //MÉTODOS:

    private Window getVentana(){
        return tablaUsuarios.getScene() != null ? tablaUsuarios.getScene().getWindow() : null;
    }


    @FXML
    public void initialize() {
        configurarBotones();
        configurarColumnas();
        configurarFiltroBusqueda();
        cargarDatosTabla();
    }

    private void configurarBotones(){
        BooleanBinding sinSeleccion = tablaUsuarios.getSelectionModel().selectedItemProperty().isNull();
        btnEditar.disableProperty().bind(sinSeleccion);
        btnCambiarEstado.disableProperty().bind(sinSeleccion);
        btnGestionarRoles.disableProperty().bind(sinSeleccion);
        btnRestablecerClave.disableProperty().bind(sinSeleccion);
    }

    private void configurarColumnas(){
        colId.setCellValueFactory(cellData-> new SimpleObjectProperty<>(
                cellData.getValue().idUsuario()
        ));
        colNombre.setCellValueFactory(cellData-> new SimpleStringProperty(
                cellData.getValue().nombre()
        ));
        colApellido.setCellValueFactory(cellData-> new SimpleStringProperty(
                cellData.getValue().apellido()
        ));
        colEmail.setCellValueFactory(cellData-> new SimpleStringProperty(
                cellData.getValue().email()
        ));
        colEstado.setCellValueFactory(cellData-> {
            boolean esActivo = cellData.getValue().activo();
            String estado = esActivo ? "Activo" : "Inactivo";
            return new SimpleStringProperty(estado);
        });
        colEstado.setCellFactory(columna-> new TableCell<UsuarioDTOCompleto, String>(){
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

    private void configurarFiltroBusqueda(){
        FilteredList<UsuarioDTOCompleto> listaFiltrada = new FilteredList<>(
                listaObservableUsuarios, b-> true
        );
        txtBuscar.textProperty().addListener((observable, valorViejo, valorNuevo)->{
            listaFiltrada.setPredicate(usuario->{
                if (valorNuevo == null || valorNuevo.isBlank()) {
                    return true;
                }
                String filtro = valorNuevo.toLowerCase().trim();
                return String.valueOf(usuario.idUsuario()).contains(filtro) ||
                        usuario.getNombreCompleto().toLowerCase().contains(filtro) ||
                        usuario.email().toLowerCase().contains(filtro);
            });
        });
        SortedList<UsuarioDTOCompleto> listaOrdenada = new SortedList<>(listaFiltrada);
        listaOrdenada.comparatorProperty().bind(tablaUsuarios.comparatorProperty());
        tablaUsuarios.setItems(listaOrdenada);
    }

    private void cargarDatosTabla() {
        CompletableFuture.supplyAsync(
                this.orquestadorUsuarios::obtenerTodosLosUsuarios
        ).thenAccept(listaUsuarios ->
                Platform.runLater(() ->
                        listaObservableUsuarios.setAll(listaUsuarios)
                )
        ).exceptionally(ex -> {
            Platform.runLater(() -> {
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                GestorAlertas.mostrarAlertaError(
                        getVentana(), "Error Critico",
                        "NO se pudo Completar la Acción.",
                        "Se Cerrara la Ventana por Seguridad.\n" +
                                "Notificale al Administrador este Error:\n" + causa.getMessage()
                );
                CargadorVistas.cambiarPantalla(getVentana(), RutasVista.MENU_PRINCIPAL_VIEW);
            });
            return null;
        });
    }


    @FXML
    void abrirFormularioNuevo(ActionEvent event) {
        CargadorVistas.abrirModalConInyeccion(
                RutasVista.REGISTRAR_USUARIO_VIEW,
                "Registrando Usuario", getVentana(),
                (RegistrarUsuarioControlador c)-> {
                    c.cargarDatos(listaObservableUsuarios);
                }
        );
    }


    @FXML
    void abrirFormularioEdicion(ActionEvent event) {
        UsuarioDTOCompleto seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null){
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Selecciona un Usuario", null,
                    "Debes seleccionar un Usuario para poder Editarlo."
            );
            return;
        }
        CargadorVistas.abrirModalConInyeccion(
                RutasVista.EDITAR_USUARIO_VIEW,
                "Editando Usuario", getVentana(),
                (EditarUsuarioControlador c)-> {
                    c.cargarDatos(seleccionado, listaObservableUsuarios);
                }
        );
    }


    @FXML
    void cambiarEstadoUsuario(ActionEvent event) {
        UsuarioDTOCompleto seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null){
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Selecciona un Usuario", null,
                    "Debes seleccionar us Usuario para poder Cambiar su Estado."
            );
            return;
        }
        if (!GestorAlertas.mostrarConfirmacion(getVentana(), "Confirmar", null,
                "¿Estás Seguro de Cambiar el Estado del Usuario -" + seleccionado.getNombreCompleto() + "-?")) {
            return;
        }
        CompletableFuture.runAsync(()->
                this.orquestadorUsuarios.cambiarEstadoUsuario(seleccionado.idUsuario())
        ).thenRun(()->
                Platform.runLater(()->{
                    UsuarioDTOCompleto actualizado = new UsuarioDTOCompleto(
                            seleccionado.idUsuario(),
                            seleccionado.nombre(),
                            seleccionado.apellido(),
                            seleccionado.email(),
                            !seleccionado.activo(),
                            seleccionado.debeCambiarContrasena(),
                            seleccionado.roles(),
                            seleccionado.permisos()
                    );
                    UtilidadesLista.reemplazarPorIdentidad(
                            listaObservableUsuarios,
                            actualizado,
                            item -> item.idUsuario().equals(actualizado.idUsuario())
                    );
                    GestorAlertas.mostrarAlertaInformacion(
                            getVentana(), "Éxito", null,
                            "El Estado ha sido cambiado con Éxito."
                    );
                })
        ).exceptionally(ex->{
            Platform.runLater(()->{
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                GestorAlertas.mostrarAlertaError(
                        getVentana(), "Error Critico",
                        "NO se pudo Completar la Acción.",
                        "Verifica tu Conexión y Notificale al Administrador este Error:\n" +
                                causa.getMessage()
                );
            });
            return null;
        });
    }


    @FXML
    void gestionarRolesUsuario(ActionEvent event) {

    }


    @FXML
    void restablecerContrasena(ActionEvent event) {

    }


    @FXML
    void volverAlPanel(ActionEvent event) {
        CargadorVistas.cambiarPantalla(getVentana(), RutasVista.MENU_PRINCIPAL_VIEW);
    }

}//===================================================================================================================//


package RetailManagementSystem.vista.controladores.gestionarUsuarios;

import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOBasico;
import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorUsuarios;
import RetailManagementSystem.dominio.excepciones.autenticacionYSeguridad.AccesoDenegadoException;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.infraestructura.seguridad.ValidadorSeguridad;
import RetailManagementSystem.vista.controladores.menuPrincipal.MenuPrincipalControlador;
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

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GestionUsuariosControlador {

    //ATRIBUTOS:

    @FXML private TableColumn<UsuarioDTOBasico, Long> colId;
    @FXML private TableColumn<UsuarioDTOBasico, String> colNombre;
    @FXML private TableColumn<UsuarioDTOBasico, String> colApellido;
    @FXML private TableColumn<UsuarioDTOBasico, String> colEmail;
    @FXML private TableColumn<UsuarioDTOBasico, String> colEstado;
    @FXML private TableView<UsuarioDTOBasico> tablaUsuarios;
    @FXML private TextField txtBuscar;
    @FXML private Button btnNuevo;
    @FXML private Button btnEditar;
    @FXML private Button btnCambiarEstado;
    @FXML private Button btnGestionarRoles;
    @FXML private Button btnRestablecerClave;

    private UsuarioDTOCompleto usuarioActual;

    private final OrquestadorUsuarios orquestadorUsuarios;

    private final ObservableList<UsuarioDTOBasico> listaObservableUsuarios = FXCollections.observableArrayList();

    //CONSTRUCTOR:

    public GestionUsuariosControlador(OrquestadorUsuarios orquestadorUsuarios) {
        this.orquestadorUsuarios = orquestadorUsuarios;
    }

    //MÉTODOS:

    public void cargarUsuario(UsuarioDTOCompleto usuarioActual){
        ValidadorSeguridad.exigirAlgunPermiso(usuarioActual, List.of(
                PermisosApp.VER_USUARIOS,
                PermisosApp.REGISTRAR_USUARIOS,
                PermisosApp.EDITAR_USUARIOS,
                PermisosApp.CAMBIAR_ESTADO_USUARIOS,
                PermisosApp.GESTIONAR_ROLES_USUARIO,
                PermisosApp.RESTABLECER_CONTRASENA_USUARIO
        ));
        this.usuarioActual = usuarioActual;
        protegerBoton(btnNuevo, PermisosApp.REGISTRAR_USUARIOS);
        protegerBoton(btnEditar, PermisosApp.EDITAR_USUARIOS);
        protegerBoton(btnCambiarEstado, PermisosApp.CAMBIAR_ESTADO_SERVICIOS);
        protegerBoton(btnGestionarRoles, PermisosApp.GESTIONAR_ROLES_USUARIO);
        protegerBoton(btnRestablecerClave, PermisosApp.RESTABLECER_CONTRASENA_USUARIO);
    }

    private void protegerBoton(Button boton, String permisoRequerido) {
        boolean tieneAcceso = this.usuarioActual.tienePermiso(permisoRequerido);
        boton.setVisible(tieneAcceso);
        boton.setManaged(tieneAcceso);
    }

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
        colEstado.setCellFactory(columna-> new TableCell<UsuarioDTOBasico, String>(){
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
        FilteredList<UsuarioDTOBasico> listaFiltrada = new FilteredList<>(
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
        SortedList<UsuarioDTOBasico> listaOrdenada = new SortedList<>(listaFiltrada);
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
                volverAGestionarTienda();
            });
            return null;
        });
    }


    @FXML
    private void abrirFormularioNuevo(ActionEvent event) {
        try {
            CargadorVistas.abrirModalConInyeccion(
                    RutasVista.REGISTRAR_USUARIO_VIEW,
                    "Registrando Usuario", getVentana(),
                    (RegistrarUsuarioControlador c)-> {
                        c.cargarDatos(this.usuarioActual, listaObservableUsuarios);
                    }
            );
        } catch (AccesoDenegadoException ex){
            GestorAlertas.mostrarAlertaAccesoDenegado(getVentana(), ex);
        }

    }


    @FXML
    private void abrirFormularioEdicion(ActionEvent event) {
        UsuarioDTOBasico seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null){
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Selecciona un Usuario", null,
                    "Debes seleccionar un Usuario para poder Editarlo."
            );
            return;
        }
        try {
            CargadorVistas.abrirModalConInyeccion(
                    RutasVista.EDITAR_USUARIO_VIEW,
                    "Editando Usuario", getVentana(),
                    (EditarUsuarioControlador c)-> {
                        c.cargarDatos(this.usuarioActual, seleccionado, listaObservableUsuarios);
                    }
            );
        } catch (AccesoDenegadoException ex){
            GestorAlertas.mostrarAlertaAccesoDenegado(getVentana(), ex);
        }
    }


    @FXML
    private void cambiarEstadoUsuario(ActionEvent event) {
        UsuarioDTOBasico seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
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
                this.orquestadorUsuarios.cambiarEstadoUsuario(this.usuarioActual, seleccionado.idUsuario())
        ).thenRun(()->
                Platform.runLater(()->{
                    UsuarioDTOBasico actualizado = new UsuarioDTOBasico(
                            seleccionado.idUsuario(),
                            seleccionado.nombre(),
                            seleccionado.apellido(),
                            seleccionado.email(),
                            !seleccionado.activo(),
                            seleccionado.debeCambiarContrasena()
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
    private void gestionarRolesUsuario(ActionEvent event) {
        UsuarioDTOBasico seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null){
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Selecciona un Usuario", null,
                    "Debes seleccionar us Usuario para poder Gestionar sus Roles."
            );
            return;
        }
        try {
            CargadorVistas.abrirModalConInyeccion(
                    RutasVista.GESTIONAR_ROLES_USUARIO_VIEW,
                    "Gestionando Roles", getVentana(),
                    (GestionarRolesDeUsuarioControlador c)->{
                        c.cargarDatos(this.usuarioActual, seleccionado.idUsuario());
                    }
            );
        } catch (AccesoDenegadoException ex){
            GestorAlertas.mostrarAlertaAccesoDenegado(getVentana(), ex);
        }
    }


    @FXML
    private void restablecerContrasena(ActionEvent event) {
        UsuarioDTOBasico seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null){
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Selecciona un Usuario", null,
                    "Debes seleccionar us Usuario para poder Restablecer su Contraseña."
            );
            return;
        }
        if (!GestorAlertas.mostrarConfirmacion(getVentana(), "Confirmar", null,
                "¿Estás Seguro de Restablecer la Contraseña del Usuario -" + seleccionado.getNombreCompleto() + "-?")) {
            return;
        }
        try {
            CargadorVistas.abrirModalConInyeccion(
                    RutasVista.RESTABLECER_CONTRASENA_VIEW,
                    "Restableciendo Contraseña", getVentana(),
                    (RestablecerContrasenaControlador c)-> {
                        c.cargarDatos(this.usuarioActual, seleccionado.idUsuario(), seleccionado.getNombreCompleto());
                    }
            );
        } catch (AccesoDenegadoException ex){
            GestorAlertas.mostrarAlertaAccesoDenegado(getVentana(), ex);
        }
    }


    @FXML
    private void volverAlPanel(ActionEvent event) {
        volverAGestionarTienda();
    }

    private void volverAGestionarTienda(){
        try {
            CargadorVistas.cambiarPantallaInyectada(
                    getVentana(),
                    RutasVista.MENU_PRINCIPAL_VIEW,
                    (MenuPrincipalControlador c) -> {
                        c.recibirUsuarioActual(this.usuarioActual);
                    }
            );
        } catch (AccesoDenegadoException ex){
            GestorAlertas.mostrarAlertaAccesoDenegado(getVentana(), ex);
        }
    }

}//===================================================================================================================//


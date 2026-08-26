package RetailManagementSystem.vista.controladores.gestionarUsuarios;

import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOBasico;
import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorUsuarios;
import RetailManagementSystem.dominio.excepciones.conflictos.EmailDuplicadoException;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.vista.utilidades.GestorAlertas;
import RetailManagementSystem.vista.utilidades.UtilidadesLista;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Window;

import java.util.concurrent.CompletableFuture;

public class EditarUsuarioControlador {

    //ATRIBUTOS:

    @FXML private Button btnCancelar;
    @FXML private Label lblIdUsuario;
    @FXML private TextField txtApellido;
    @FXML private TextField txtEmail;
    @FXML private TextField txtNombre;

    private UsuarioDTOBasico datosUsuario;

    private ObservableList<UsuarioDTOBasico> listaObservable;

    private final OrquestadorUsuarios orquestadorUsuarios;

    private UsuarioDTOCompleto usuarioActual;

    //CONSTRUCTOR:

    public EditarUsuarioControlador(OrquestadorUsuarios orquestadorUsuarios) {
        this.orquestadorUsuarios = orquestadorUsuarios;
    }

    //MÉTODOS:

    public void cargarDatos(
            UsuarioDTOCompleto usuarioActual, UsuarioDTOBasico datosUsuario,
            ObservableList<UsuarioDTOBasico> listaObservable
    ) {
        if (usuarioActual == null){
            throw new IllegalArgumentException("Usuario Nulo, Error al Recibir el Usuario");
        }
        if (!usuarioActual.tienePermiso(PermisosApp.EDITAR_USUARIOS)){
            GestorAlertas.mostrarAlertaError(
                    getVentana(),
                    "Acceso Denegado", "Privilegios Insuficientes",
                    "NO tienes los Permisos Necesarios para Editar los Usuarios."
            );
            cerrarModal();
            return;
        }
        this.usuarioActual = usuarioActual;
        this.datosUsuario = datosUsuario;
        this.listaObservable = listaObservable;
        lblIdUsuario.setText(String.valueOf(datosUsuario.idUsuario()));
        txtNombre.setText(datosUsuario.nombre());
        txtApellido.setText(datosUsuario.apellido());
        txtEmail.setText(datosUsuario.email());
        Platform.runLater(()-> btnCancelar.requestFocus());
    }

    private Window getVentana(){
        return btnCancelar.getScene() != null ? btnCancelar.getScene().getWindow() : null;
    }


    @FXML
    void guardarCambios(ActionEvent event) {
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String email = txtEmail.getText().trim();
        if (nombre.isBlank() || apellido.isBlank() || email.isBlank()){
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Datos Incompletos",
                    "Por Favor, Ingresa todos los Datos.",
                    "Los Campos Nombre, Apellido y Email son Obligatorios."
            );
            return;
        }
        if (!esEmailValido(email)){
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Email Invalido",
                    "El Email Ingresado NO Cumple las Características mínimas de un Email Valido",
                    "Por favor, Ingresa un Email Real"
            );
            return;
        }
        CompletableFuture.supplyAsync(()->
                this.orquestadorUsuarios.actualizarDatosUsuario(
                        this.usuarioActual, datosUsuario.idUsuario(), nombre, apellido, email
                )
        ).thenAccept(actualizado->
            Platform.runLater(()->{
                UtilidadesLista.reemplazarPorIdentidad(
                        listaObservable,
                        actualizado,
                        item -> item.idUsuario().equals(actualizado.idUsuario())
                );
                GestorAlertas.mostrarAlertaInformacion(
                        getVentana(), "Éxito", null,
                        "El Usuario ha sido Actualizado con Exito"
                );
                cerrarModal();
            })
        ).exceptionally(ex->{
            Platform.runLater(()->{
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                if (causa instanceof EmailDuplicadoException){
                    GestorAlertas.mostrarAlertaError(
                            getVentana(), "Email Duplicado", null,
                            "Lo Sentimos, debes Ingresar otro Email.\n" +
                                    causa.getMessage()
                    );
                } else if (causa instanceof IllegalArgumentException){
                    GestorAlertas.mostrarAlertaError(
                            getVentana(), "Datos Inválidos", null,
                            "Por favor, Revisa los Datos Ingresados y Corrígelos"
                    );
                } else {
                    GestorAlertas.mostrarAlertaError(
                            getVentana(), "Error Critico",
                            "NO se pudo Completar la Acción.",
                            "Verifica tu Conexión y Notificale al Administrador este Error:\n" +
                                    causa.getMessage()
                    );
                }
            });
            return null;
        });
    }

    private boolean esEmailValido(String email) {
        String regexEmail = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(regexEmail);
    }

    private void cerrarModal(){
        Window ventana = getVentana();
        if (ventana != null){
            ventana.hide();
        }
    }


    @FXML
    void cerrarVentana(ActionEvent event) {
        cerrarModal();
    }

}//===================================================================================================================//


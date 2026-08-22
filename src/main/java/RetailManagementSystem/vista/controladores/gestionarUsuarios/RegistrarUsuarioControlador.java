package RetailManagementSystem.vista.controladores.gestionarUsuarios;

import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOBasico;
import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorUsuarios;

import RetailManagementSystem.dominio.excepciones.conflictos.EmailDuplicadoException;
import RetailManagementSystem.vista.utilidades.GestorAlertas;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Window;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class RegistrarUsuarioControlador {

    //ATRIBUTOS:

    @FXML private Button btnCancelar;
    @FXML private CheckBox chkActivo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtEmail;

    private final OrquestadorUsuarios orquestadorUsuarios;

    private ObservableList<UsuarioDTOBasico> listaObservable;

    //CONSTRUCTOR:

    public RegistrarUsuarioControlador(OrquestadorUsuarios orquestadorUsuarios) {
        this.orquestadorUsuarios = orquestadorUsuarios;
    }

    //MÉTODOS:

    public void cargarDatos(ObservableList<UsuarioDTOBasico> listaObservable){
        this.listaObservable = listaObservable;
        Platform.runLater(()-> btnCancelar.requestFocus());
    }

    private Window getVentana(){
        return btnCancelar.getScene() != null ? btnCancelar.getScene().getWindow() : null;
    }


    @FXML
    public void initialize() {
        configurarFiltrosTexto();
    }

    private void configurarFiltrosTexto() {
        String regexNombres = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*$";
        txtNombre.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().matches(regexNombres) ? change : null));
        txtApellido.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().matches(regexNombres) ? change : null));
    }

    private boolean esEmailValido(String email) {
        String regexEmail = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(regexEmail);
    }


    @FXML
    void guardarUsuario(ActionEvent event) {
        guardarUsuario();
    }

    private void guardarUsuario(){
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String email = txtEmail.getText().trim();
        boolean activo = chkActivo.isSelected();
        if (nombre.isBlank() || apellido.isBlank() || email.isBlank()){
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Datos Incompletos",
                    "El Nombre, el Apellido y el Email son Datos Obligatorios.",
                    "Por favor, Ingrese un Nombre, Apellido y Email Validos."
            );
            return;
        }
        if (!esEmailValido(email)){
            GestorAlertas.mostrarAlertaError(
                    getVentana(), "Email Invalido",
                    "El Email Ingresado NO Cumple las Características mínimas de un Email Valido",
                    "Por favor, Ingresa un Email Real"
            );
            return;
        }
        CompletableFuture.supplyAsync(()->
                this.orquestadorUsuarios.registrarUsuarioYObtenerContrasenaTemporal(
                        nombre, apellido, email, activo
                )
        ).thenAccept(registro->{
            Platform.runLater(()->{
                listaObservable.add(registro.usuario());
                String claveVisible = new String(registro.claveTemporal());
                GestorAlertas.mostrarAlertaInformacion(
                        getVentana(), "Éxito", "Usuario Registrado",
                        "La Clave Temporal del Usuario -" + registro.usuario().getNombreCompleto() + "- es:\n" +
                                claveVisible
                );
                Arrays.fill(registro.claveTemporal(), '\0');
                cerrarPantalla();
            });
        }).exceptionally(ex->{
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

    private void cerrarPantalla(){
        Window ventana = getVentana();
        if (ventana != null){
            ventana.hide();
        }
    }


    @FXML
    void cerrarVentana(ActionEvent event) {
        cerrarPantalla();
    }

}//===================================================================================================================//


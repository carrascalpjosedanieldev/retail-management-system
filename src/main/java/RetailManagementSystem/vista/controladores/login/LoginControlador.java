package RetailManagementSystem.vista.controladores.login;

import RetailManagementSystem.aplicacion.orquestadores.OrquestadorLogin;
import RetailManagementSystem.dominio.excepciones.autenticacionYSeguridad.CredencialesInvalidasException;
import RetailManagementSystem.dominio.excepciones.autenticacionYSeguridad.UsuarioBloqueadoException;
import RetailManagementSystem.dominio.excepciones.autenticacionYSeguridad.UsuarioInactivoException;
import RetailManagementSystem.vista.controladores.menuPrincipal.MenuPrincipalControlador;
import RetailManagementSystem.vista.utilidades.CargadorVistas;
import RetailManagementSystem.vista.utilidades.GestorAlertas;
import RetailManagementSystem.vista.utilidades.RutasVista;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class LoginControlador {

    //ATRIBUTOS:

    @FXML private Button btnVerContrasena;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtPasswordVisible;
    @FXML private VBox vboxBaseBlanca;

    private final OrquestadorLogin orquestadorLogin;

    //CONSTRUCTOR:

    public LoginControlador(OrquestadorLogin orquestadorLogin) {
        this.orquestadorLogin = orquestadorLogin;
    }

    //MÉTODOS:

    @FXML
    void initialize() {
        txtPasswordVisible.textProperty().bindBidirectional(txtPassword.textProperty());
    }


    @FXML
    void alternarVisibilidadContrasena(ActionEvent event) {
        boolean estaOculto = txtPassword.isVisible();
        txtPassword.setVisible(!estaOculto);
        txtPasswordVisible.setVisible(estaOculto);
        btnVerContrasena.setText(estaOculto ? "🙈" : "👁");
        if (estaOculto) {
            txtPasswordVisible.requestFocus();
            txtPasswordVisible.positionCaret(txtPasswordVisible.getText().length());
        } else {
            txtPassword.requestFocus();
            txtPassword.positionCaret(txtPassword.getText().length());
        }
    }


    @FXML
    void ingresarAlSistema(ActionEvent event) {
        String email = txtEmail.getText().trim();
        String contrasena = txtPassword.getText().trim();
        if (email.isBlank() || contrasena.isBlank()){
            GestorAlertas.mostrarAlertaError(
                    getVentana(), "Datos Incompletos", "Ingresa los Datos",
                    "Por favor, Ingresa el Email y la Contraseña para Continuar."
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
        char[] contrasenaPlana = contrasena.toCharArray();
        CompletableFuture.supplyAsync(()->{
            try {
                return this.orquestadorLogin.autenticar(email, contrasenaPlana);
            } finally {
                Arrays.fill(contrasenaPlana, '\0');
                Platform.runLater(() -> {
                    txtPassword.clear();
                    txtPasswordVisible.clear();
                });
            }
        }).thenAccept(usuarioAutenticado->
            Platform.runLater(()->{
                CargadorVistas.cambiarPantallaConInyeccion(
                        getVentana(),
                        RutasVista.MENU_PRINCIPAL_VIEW,
                        (MenuPrincipalControlador c) -> c.recibirUsuarioActual(usuarioAutenticado)
                );
            })
        ).exceptionally(ex->{
            Platform.runLater(()->{
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                switch (causa) {
                    case IllegalArgumentException illegalArgumentException -> GestorAlertas.mostrarAlertaError(
                            getVentana(), "Datos Inválidos", null,
                            "Verifica los Datos Ingresados:  " + illegalArgumentException.getMessage()
                    );
                    case CredencialesInvalidasException credencialesInvalidasException ->
                            GestorAlertas.mostrarAlertaError(
                                    getVentana(), "Credenciales Invalidas", null,
                                    credencialesInvalidasException.getMessage()
                            );
                    case UsuarioInactivoException usuarioInactivoException -> GestorAlertas.mostrarAlertaError(
                            getVentana(), "Usuario Inactivo", "NO puedes Continuar",
                            "Habla con el Administrador y Notificale tu Problema.\n" +
                                    usuarioInactivoException.getMessage()
                    );
                    case UsuarioBloqueadoException usuarioBloqueadoException -> GestorAlertas.mostrarAlertaError(
                            getVentana(), "Usuario Bloqueado", "NO puedes Continuar",
                            "Por favor, Espera a que pase tu Tiempo de Bloqueo para volverlo a Intentar o habla con el Administrador.\n" +
                                    usuarioBloqueadoException.getMessage()
                    );
                    default -> GestorAlertas.mostrarAlertaError(
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

    private Window getVentana(){
        return vboxBaseBlanca.getScene() != null ? vboxBaseBlanca.getScene().getWindow() : null;
    }


    @FXML
    void salirDeLaApp(ActionEvent event) {
        Window ventana = getVentana();
        GestorAlertas.mostrarAlertaSalirDelSistema(ventana);
    }


}//===================================================================================================================//


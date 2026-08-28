package RetailManagementSystem.vista.controladores.login;

import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorLogin;
import RetailManagementSystem.dominio.excepciones.autenticacionYSeguridad.AccesoDenegadoException;
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
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Window;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class CambioContrasenaControlador {

    //ATRIBUTOS:

    @FXML private Button btnRestablecer;
    @FXML private Button btnVerConfirmar;
    @FXML private Button btnVerNueva;
    @FXML private Label lblNombreUsuario;
    @FXML private PasswordField txtConfirmarOculta;
    @FXML private TextField txtConfirmarVis;
    @FXML private PasswordField txtNuevaOculta;
    @FXML private TextField txtNuevaVis;

    private UsuarioDTOCompleto usuarioActual;

    private final OrquestadorLogin orquestadorLogin;

    //CONSTRUCTOR:

    public CambioContrasenaControlador(OrquestadorLogin orquestadorLogin) {
        this.orquestadorLogin = orquestadorLogin;
    }

    //MÉTODOS:

    public void cargarDatos(UsuarioDTOCompleto usuarioActual){
        if (usuarioActual == null){
            throw new AccesoDenegadoException("NO hay una Sesión Activa.");
        }
        this.usuarioActual = usuarioActual;
        lblNombreUsuario.setText(usuarioActual.getNombreCompleto());
    }

    private Window getVentana(){
        return btnRestablecer.getScene() != null ? btnRestablecer.getScene().getWindow() : null;
    }


    @FXML
    public void initialize() {
        txtNuevaOculta.textProperty().bindBidirectional(txtNuevaVis.textProperty());
        txtConfirmarOculta.textProperty().bindBidirectional(txtConfirmarVis.textProperty());
    }


    @FXML
    private void alternarVisibilidadNueva(ActionEvent event) {
        boolean estaOculto = txtNuevaVis.isVisible();
        txtNuevaVis.setVisible(!estaOculto);
        txtNuevaOculta.setVisible(estaOculto);
        btnVerNueva.setText(estaOculto ? "🙈" : "👁");
        if (estaOculto) {
            txtNuevaOculta.requestFocus();
            txtNuevaOculta.positionCaret(txtNuevaOculta.getText().length());
        } else {
            txtNuevaVis.requestFocus();
            txtNuevaVis.positionCaret(txtNuevaVis.getText().length());
        }
    }


    @FXML
    private void alternarVisibilidadConfirmar(ActionEvent event) {
        boolean estaOculto = txtConfirmarVis.isVisible();
        txtConfirmarVis.setVisible(!estaOculto);
        txtConfirmarOculta.setVisible(estaOculto);
        btnVerConfirmar.setText(estaOculto ? "🙈" : "👁");
        if (estaOculto) {
            txtConfirmarOculta.requestFocus();
            txtConfirmarOculta.positionCaret(txtConfirmarOculta.getText().length());
        } else {
            txtConfirmarVis.requestFocus();
            txtConfirmarVis.positionCaret(txtNuevaVis.getText().length());
        }
    }


    @FXML
    private void restablecerContrasena(ActionEvent event) {
        restablecerContrasena();
    }

    private void restablecerContrasena(){
        String contrasena = txtNuevaOculta.getText().trim();
        String contrasenaConfirmar = txtConfirmarOculta.getText().trim();
        if (contrasena.isBlank() || contrasenaConfirmar.isBlank()){
            GestorAlertas.mostrarAlertaError(
                    getVentana(), "Datos Incompletos", "Ingresa los Datos",
                    "Por favor, Ingresa la Contraseña Y Confirmala para Continuar."
            );
            return;
        }
        if (!contrasena.equals(contrasenaConfirmar)){
            GestorAlertas.mostrarAlertaError(
                    getVentana(), "Datos Invalidos", "Las Contraseñas deben ser Iguales",
                    "Por favor, Ingresa la Contraseña Y Confirmala para Restablecerla."
            );
            return;
        }
        char[] contrasenaPlana = contrasena.toCharArray();
        CompletableFuture.runAsync(()->{
            try {
                this.orquestadorLogin.cambiarContrasenaDefinitiva(this.usuarioActual.idUsuario(), contrasenaPlana);
            } finally {
                Arrays.fill(contrasenaPlana, '\0');
                Platform.runLater(() -> {
                    txtNuevaOculta.clear();
                    txtNuevaVis.clear();
                    txtConfirmarOculta.clear();
                    txtConfirmarVis.clear();
                });
            }
        }).thenRun(()->
                Platform.runLater(()-> {
                    try {
                        CargadorVistas.cambiarPantallaConInyeccionYTamanoNormal(
                                getVentana(),
                                RutasVista.MENU_PRINCIPAL_VIEW,
                                (MenuPrincipalControlador c) -> c.recibirUsuarioActual(this.usuarioActual)
                        );
                    } catch (AccesoDenegadoException ex){
                        GestorAlertas.mostrarAlertaAccesoDenegado(getVentana(), ex);
                    }
                })
        ).exceptionally(ex->{
            Platform.runLater(()->{
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                switch (causa) {
                    case IllegalArgumentException illegalArgumentException -> GestorAlertas.mostrarAlertaError(
                            getVentana(), "Datos Inválidos", null,
                            "Verifica los Datos Ingresados:  " + illegalArgumentException.getMessage()
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

}//===================================================================================================================//


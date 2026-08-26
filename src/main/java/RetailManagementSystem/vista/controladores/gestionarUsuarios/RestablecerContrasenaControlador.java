package RetailManagementSystem.vista.controladores.gestionarUsuarios;

import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorUsuarios;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.vista.utilidades.GestorAlertas;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Window;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class RestablecerContrasenaControlador {

    //ATRIBUTOS:

    @FXML private Button btnCerrar;
    @FXML private Button btnCopiar;
    @FXML private Label lblContrasenaTemporal;
    @FXML private Label lblNombreUsuario;

    private final OrquestadorUsuarios orquestadorUsuarios;

    private UsuarioDTOCompleto usuarioActual;

    //CONSTRUCTOR:

    public RestablecerContrasenaControlador(OrquestadorUsuarios orquestadorUsuarios) {
        this.orquestadorUsuarios = orquestadorUsuarios;
    }

    //MÉTODOS:

    public void cargarDatos(UsuarioDTOCompleto usuarioActual, Long idUsuario, String nombreUsuario){
        if (usuarioActual == null){
            throw new IllegalArgumentException("Usuario Nulo, Error al Recibir el Usuario");
        }
        if (!usuarioActual.tienePermiso(PermisosApp.GESTIONAR_USUARIOS)){
            GestorAlertas.mostrarAlertaError(
                    getVentana(),
                    "Acceso Denegado", "Privilegios Insuficientes",
                    "NO tienes los Permisos Necesarios para Restablecer Contraseñas."
            );
            cerrarModal();
            return;
        }
        this.usuarioActual = usuarioActual;
        lblNombreUsuario.setText(nombreUsuario);
        CompletableFuture.supplyAsync(()->
                this.orquestadorUsuarios.restablecerContrasenaPorAdmin(this.usuarioActual, idUsuario)
        ).thenAccept(contrasenaArray->
            Platform.runLater(()->{
                String contrasenaVisible = new String(contrasenaArray);
                lblContrasenaTemporal.setText(contrasenaVisible);
                Arrays.fill(contrasenaArray, '\0');
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

    private Window getVentana(){
        return btnCerrar.getScene() != null ? btnCerrar.getScene().getWindow() : null;
    }

    @FXML
    void copiarContrasena(ActionEvent event) {
        String contrasena = lblContrasenaTemporal.getText();
        if (contrasena == null || contrasena.isBlank()) {
            return;
        }
        Clipboard portapapeles = Clipboard.getSystemClipboard();
        ClipboardContent contenido = new ClipboardContent();
        contenido.putString(contrasena);
        portapapeles.setContent(contenido);
        String textoOriginal = btnCopiar.getText();
        btnCopiar.setText("¡Copiado!");
        btnCopiar.setDisable(true);
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).thenRun(() -> Platform.runLater(() -> {
            btnCopiar.setText(textoOriginal);
            btnCopiar.setDisable(false);
            ClipboardContent contenidoVacio = new ClipboardContent();
            contenidoVacio.putString("");
            portapapeles.setContent(contenidoVacio);
        }));
    }

    @FXML
    private void cerrarVentana(ActionEvent event) {
        cerrarModal();
    }

    private void cerrarModal(){
        Window ventana = getVentana();
        if (ventana != null){
            ventana.hide();
        }
    }

}//===================================================================================================================//


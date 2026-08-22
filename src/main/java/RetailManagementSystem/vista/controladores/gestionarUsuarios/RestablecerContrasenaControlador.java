package RetailManagementSystem.vista.controladores.gestionarUsuarios;

import RetailManagementSystem.aplicacion.orquestadores.OrquestadorUsuarios;
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

    //CONSTRUCTOR:

    public RestablecerContrasenaControlador(OrquestadorUsuarios orquestadorUsuarios) {
        this.orquestadorUsuarios = orquestadorUsuarios;
    }

    //MÉTODOS:

    public void cargarDatos(Long idUsuario, String nombreUsuario){
        lblNombreUsuario.setText(nombreUsuario);
        CompletableFuture.supplyAsync(()->
                this.orquestadorUsuarios.restablecerContrasenaPorAdmin(idUsuario)
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
    void cerrarVentana(ActionEvent event) {
        Window ventana = getVentana();
        if (ventana != null){
            ventana.hide();
        }
    }

}//===================================================================================================================//


package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarConfiguraciones.gestionRoles;

import RetailManagementSystem.aplicacion.dto.seguridad.RolDTO;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorRoles;
import RetailManagementSystem.vista.utilidades.GestorAlertas;

import RetailManagementSystem.vista.utilidades.UtilidadesLista;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.concurrent.CompletableFuture;

public class ModificarDatosRolControlador {

    //ATRIBUTOS:

    @FXML private Button btnCancelar;
    @FXML private Label lblIdRol;
    @FXML private TextField txtNombreRol;
    @FXML private CheckBox chkActivo;

    private final OrquestadorRoles orquestadorRoles;

    private RolDTO rol;

    private ObservableList<RolDTO> listaObservable;

    //CONSTRUCTOR:

    public ModificarDatosRolControlador(OrquestadorRoles orquestadorRoles) {
        this.orquestadorRoles = orquestadorRoles;
    }

    //MÉTODOS:

    private Window getVentana(){
        return btnCancelar.getScene() != null ? btnCancelar.getScene().getWindow() : null;
    }


    @FXML
    private void initialize(){
        txtNombreRol.textProperty().addListener((obs, viejo, nuevo) -> {
            if (nuevo != null && nuevo.length() > 50) {
                txtNombreRol.setText(viejo);
            }
        });
    }

    public void cargarDatos(RolDTO rol, ObservableList<RolDTO> listaObservable) {
        if (rol == null) {
            GestorAlertas.mostrarAlertaError(
                    getVentana(), "Error",
                    "Datos Inválidos",
                    "NO se recibió un Rol para Editar.");
            cerrarVentanaSeguro();
            return;
        }
        this.rol = rol;
        this.lblIdRol.setText("ID: #" + rol.idRol());
        this.txtNombreRol.setText(rol.nombre());
        this.chkActivo.setSelected(rol.activo());
        this.listaObservable = listaObservable;
        Platform.runLater(()->btnCancelar.requestFocus());
    }

    private void cerrarVentanaSeguro(){
        Window ventana = getVentana();
        if (ventana != null){
            ventana.hide();
        }
    }


    @FXML
    public void guardarCambios(ActionEvent event) {
        String nombreActualizado = txtNombreRol.getText().trim();
        boolean activo = chkActivo.isSelected();
        if (nombreActualizado.isEmpty()){
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Datos Incompletos",
                    "El Nombre es Obligatorio.",
                    "Por favor escribe un Nombre Valido."
            );
            return;
        }
        CompletableFuture.supplyAsync(()->
                this.orquestadorRoles.actualizarDatosRol(this.rol.idRol(), nombreActualizado, activo)
        ).thenAccept(rolActualizado->
            Platform.runLater(()->{
                UtilidadesLista.reemplazarPorIdentidad(
                        listaObservable,
                        rolActualizado,
                        item -> item.idRol() == rolActualizado.idRol()
                );
                cerrarVentanaSeguro();
            })
        ).exceptionally(ex->{
            Platform.runLater(()->{
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                if (causa instanceof IllegalArgumentException){
                    GestorAlertas.mostrarAlertaError(
                            getVentana(), "Error",
                            "NO se Actualizo.",
                            "NO se pudo Completar la Accion.\n" +
                                    "Error:  " + causa.getMessage()
                    );
                } else {
                    GestorAlertas.mostrarAlertaError(
                            getVentana(), "Error Critico",
                            "NO se pudo Completar la Acción.",
                            "Notificale al Administrador este Error:\n" + causa.getMessage()
                    );
                }
            });
            return null;
        });
    }


    @FXML
    public void cancelar(ActionEvent event) {
        cerrarVentanaSeguro();
    }


}//===================================================================================================================//


package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarConfiguraciones.gestionRoles;

import RetailManagementSystem.aplicacion.dto.seguridad.RolDTO;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorRoles;
import RetailManagementSystem.vista.utilidades.GestorAlertas;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ModificarDatosRolControlador {

    //ATRIBUTOS:

    @FXML private Label lblIdRol;
    @FXML private TextField txtNombreRol;
    @FXML private CheckBox chkActivo;

    private final OrquestadorRoles orquestadorRoles;

    private int idRolActual;

    private String nombreOriginal;

    //CONSTRUCTOR:

    public ModificarDatosRolControlador(OrquestadorRoles orquestadorRoles) {
        this.orquestadorRoles = orquestadorRoles;
    }

    //MÉTODOS:

    @FXML
    private void initialize(){
        txtNombreRol.textProperty().addListener((obs, viejo, nuevo) -> {
            if (nuevo != null && nuevo.length() > 50) {
                txtNombreRol.setText(viejo);
            }
        });
    }

    public void cargarDatosRol(RolDTO rol) {
        if (rol == null) {
            GestorAlertas.mostrarAlertaError("Error",
                    "Datos Inválidos",
                    "No se recibió un rol para editar.");
            cerrarVentanaSeguro();
            return;
        }
        this.idRolActual = rol.idRol();
        this.nombreOriginal = rol.nombre();
        this.lblIdRol.setText("ID: #" + this.idRolActual);
        this.txtNombreRol.setText(rol.nombre());
        this.chkActivo.setSelected(rol.activo());
    }

    private void cerrarVentanaSeguro(){

    }


    @FXML
    public void cancelar(ActionEvent event) {
    }


    @FXML
    public void guardarCambios(ActionEvent event) {

    }

}//===================================================================================================================//


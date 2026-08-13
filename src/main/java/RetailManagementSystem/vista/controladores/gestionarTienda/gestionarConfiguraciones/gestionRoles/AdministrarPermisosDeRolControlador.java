package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarConfiguraciones.gestionRoles;

import RetailManagementSystem.aplicacion.dto.seguridad.PermisoDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class AdministrarPermisosDeRolControlador {

    //ATRIBUTOS:

    @FXML private TableView<PermisoDTO> tablaPermisosRol;
    @FXML private TableColumn<PermisoDTO, String> colEstado;
    @FXML private TableColumn<PermisoDTO, Integer> colId;
    @FXML private TableColumn<PermisoDTO, String> colModulo;
    @FXML private TableColumn<PermisoDTO, String> colNombre;
    @FXML private Label lblNombreRol;

    //CONSTRUCTOR:

    //MÉTODOS:

    @FXML
    public void initialize(){

    }


    @FXML
    void anadirPermiso(ActionEvent event) {

    }


    @FXML
    void eliminarPermiso(ActionEvent event) {

    }


    @FXML
    void volverARoles(ActionEvent event) {

    }


}//===================================================================================================================//


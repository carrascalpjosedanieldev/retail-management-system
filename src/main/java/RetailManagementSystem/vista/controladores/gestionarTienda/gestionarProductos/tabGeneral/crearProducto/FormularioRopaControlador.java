package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarProductos.tabGeneral.crearProducto;

import RetailManagementSystem.dominio.enums.Talla;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import java.util.List;

public class FormularioRopaControlador implements FormularioEspecificoControlador {

    //ATRIBUTOS:

    @FXML private ComboBox<Talla> cbTalla;

    //MÉTODOS:

    @FXML
    public void initialize() {
        List<Talla> listaTallas = List.of(Talla.values());
        cbTalla.setItems(FXCollections.observableArrayList(listaTallas));
    }

    public Talla getTallaSeleccionada() {
        return cbTalla.getValue();
    }

}//===================================================================================================================//


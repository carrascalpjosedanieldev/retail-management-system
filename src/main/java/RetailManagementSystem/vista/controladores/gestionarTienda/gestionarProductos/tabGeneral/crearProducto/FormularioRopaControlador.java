package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarProductos.tabGeneral.crearProducto;

import RetailManagementSystem.dominio.enums.Talla;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import java.util.Arrays;
import java.util.List;

public class FormularioRopaControlador implements FormularioEspecificoControlador {

    //ATRIBUTOS:

    @FXML private ComboBox<String> cbTalla;

    //MÉTODOS:

    @FXML
    public void initialize() {
        List<String> listaTallas = Arrays.stream(Talla.values())
                .map(Enum::name)
                .toList();
        cbTalla.setItems(FXCollections.observableArrayList(listaTallas));
    }

    public String getTallaSeleccionada() {
        return cbTalla.getValue();
    }

}//===================================================================================================================//


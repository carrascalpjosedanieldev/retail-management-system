package RetailManagementSystem.vista.formularios.crearProducto;

import RetailManagementSystem.dominio.enums.Talla;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import java.util.Arrays;
import java.util.List;

public class FormularioRopaControlador implements FormularioEspecificoControlador{

    @FXML
    private ComboBox<String> cbTalla;

    @FXML
    public void initialize() {
        List<String> listaTallas = Arrays.stream(Talla.values())
                .map(Enum::name)
                .toList();
        cbTalla.setItems(FXCollections.observableArrayList(listaTallas));
    }

    @Override
    public boolean esValido() {
        return cbTalla.getValue() != null;
    }

    @Override
    public void mostrarErrores() {
        if (cbTalla.getValue() == null) {
            if (!cbTalla.getStyleClass().contains("campo-error")) {
                cbTalla.getStyleClass().add("campo-error");
            }
        } else {
            cbTalla.getStyleClass().remove("campo-error");
        }
    }

    public String getTallaSeleccionada() {
        return cbTalla.getValue();
    }

}//===================================================================================================================//


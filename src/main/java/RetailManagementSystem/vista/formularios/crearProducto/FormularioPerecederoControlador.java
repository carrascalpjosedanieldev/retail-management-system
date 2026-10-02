package RetailManagementSystem.vista.formularios.crearProducto;

import RetailManagementSystem.aplicacion.dto.gestion.PoliticaVencimientoDTO;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.List;

public class FormularioPerecederoControlador implements FormularioEspecificoControlador{

    @FXML
    private DatePicker dpFechaVencimiento;

    @FXML
    private ComboBox<PoliticaVencimientoDTO> cbPolitica;

    @FXML
    public void initialize() {
        dpFechaVencimiento.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });

        cbPolitica.setConverter(new StringConverter<>() {
            @Override
            public String toString(PoliticaVencimientoDTO dto) {
                return dto != null ? dto.nombrePolitica() : "";
            }
            @Override
            public PoliticaVencimientoDTO fromString(String string) { return null; }
        });
    }

    public void cargarPoliticas(List<PoliticaVencimientoDTO> politicas) {
        if (politicas != null && !politicas.isEmpty()) {
            cbPolitica.getItems().setAll(politicas);
        }
    }

    @Override
    public boolean esValido() {
        boolean fechaValida = dpFechaVencimiento.getValue() != null && !dpFechaVencimiento.getValue().isBefore(LocalDate.now());
        boolean politicaValida = cbPolitica.getValue() != null;
        return fechaValida && politicaValida;
    }

    @Override
    public void mostrarErrores() {
        if (dpFechaVencimiento.getValue() == null || dpFechaVencimiento.getValue().isBefore(LocalDate.now())) {
            if (!dpFechaVencimiento.getStyleClass().contains("campo-error")) {
                dpFechaVencimiento.getStyleClass().add("campo-error");
            }
        } else {
            dpFechaVencimiento.getStyleClass().remove("campo-error");
        }

        if (cbPolitica.getValue() == null) {
            if (!cbPolitica.getStyleClass().contains("campo-error")) {
                cbPolitica.getStyleClass().add("campo-error");
            }
        } else {
            cbPolitica.getStyleClass().remove("campo-error");
        }
    }

    public LocalDate getFechaSeleccionada() {
        return dpFechaVencimiento.getValue();
    }

    public PoliticaVencimientoDTO getPoliticaSeleccionada() {
        return cbPolitica.getValue();
    }

}//===================================================================================================================//


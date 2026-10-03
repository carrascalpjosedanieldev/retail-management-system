package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarProductos.tabGeneral.crearProducto;

import RetailManagementSystem.aplicacion.dto.gestion.PoliticaVencimientoDTO;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorPoliticaVencimiento;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FormularioPerecederoControlador implements FormularioEspecificoControlador{

    //ATRIBUTOS:

    @FXML private DatePicker dpFechaVencimiento;
    @FXML private ComboBox<PoliticaVencimientoDTO> cbPolitica;

    private final OrquestadorPoliticaVencimiento orquestadorPoliticaVencimiento;

    //CONSTRUCTOR:

    public FormularioPerecederoControlador(OrquestadorPoliticaVencimiento orquestadorPoliticaVencimiento) {
        this.orquestadorPoliticaVencimiento = orquestadorPoliticaVencimiento;
    }

    //MÉTODOS:

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

    public CompletableFuture<List<PoliticaVencimientoDTO>> cargarPoliticas(){
        return CompletableFuture.supplyAsync(
                this.orquestadorPoliticaVencimiento::obtenerPoliticasVActivas
        ).thenApply(listaPoliticas -> {
            Platform.runLater(() -> cbPolitica.getItems().setAll(listaPoliticas));
            return listaPoliticas;
        });
    }

    public LocalDate getFechaSeleccionada() {
        return dpFechaVencimiento.getValue();
    }

    public PoliticaVencimientoDTO getPoliticaSeleccionada() {
        return cbPolitica.getValue();
    }

}//===================================================================================================================//


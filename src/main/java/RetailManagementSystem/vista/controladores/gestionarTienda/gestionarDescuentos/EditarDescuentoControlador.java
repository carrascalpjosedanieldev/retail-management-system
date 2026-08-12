package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarDescuentos;

import RetailManagementSystem.aplicacion.dto.gestion.DescuentoDTO;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorDescuentos;
import RetailManagementSystem.vista.utilidades.FormateadorNumeros;
import RetailManagementSystem.vista.utilidades.GestorAlertas;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

public class EditarDescuentoControlador {

    //ATRIBUTOS:

    @FXML private Button btnCancelar;
    @FXML private Label lblNombreImpuesto;
    @FXML private TextField txtNombre;
    @FXML private TextField txtPorcentaje;

    private DescuentoDTO datosDescuento;

    private ObservableList<DescuentoDTO> listaObservable;

    private final OrquestadorDescuentos orquestadorDescuentos;

    //CONSTRUCTOR:

    public EditarDescuentoControlador(OrquestadorDescuentos orquestadorDescuentos) {
        this.orquestadorDescuentos = orquestadorDescuentos;
    }

    //MÉTODOS:

    @FXML
    public void initialize(){

    }


    public void cargarDatos(DescuentoDTO datosDescuento, ObservableList<DescuentoDTO> listaObservable) {
        if (datosDescuento == null) {
            throw new IllegalArgumentException("No puedes editar un Descuento Vacío.");
        }
        this.datosDescuento = datosDescuento;
        this.listaObservable = listaObservable;
        lblNombreImpuesto.setText(this.datosDescuento.nombre());
        txtNombre.setText(this.datosDescuento.nombre());
        txtPorcentaje.setText(this.datosDescuento.porcentaje().toString());
        Platform.runLater(() -> {
            btnCancelar.requestFocus();
        });
    }


    @FXML
    void actualizarDescuento(ActionEvent event) {
        String nombre = txtNombre.getText();
        String nuevoPorcentajeTexto = txtPorcentaje.getText().trim();
        if (nombre.isEmpty() || nuevoPorcentajeTexto.isEmpty()){
            GestorAlertas.mostrarAlertaWarning(
                    "Datos Incompletos",
                    "El Nombre y el Porcentaje son Obligatorios.",
                    "Por favor escribe un Nombre y un Porcentaje Validos."
            );
            return;
        }
        BigDecimal porcentaje;
        try {
            porcentaje = FormateadorNumeros.stringAPorcentaje(nuevoPorcentajeTexto);
        } catch (NumberFormatException e) {
            GestorAlertas.mostrarAlertaWarning(
                    "Número Inválido", null,
                    "Error al Ingresar el Porcentaje:\n" + e.getMessage()
            );
            return;
        }
        CompletableFuture.supplyAsync(()-> {
            return this.orquestadorDescuentos.actualizarDescuento(
                    this.datosDescuento.idDescuento(), nombre, porcentaje
            );
        }).thenAccept(descuentoActualizado -> {
            Platform.runLater(() -> {
                int indice = listaObservable.indexOf(this.datosDescuento);
                listaObservable.set(indice, descuentoActualizado);
                cerrarPantalla();
            });
        }).exceptionally(ex -> {
            Platform.runLater(() -> {
                GestorAlertas.mostrarAlertaError("Error Critico",
                        "NO se pudo Completar la Acción.",
                        "Notificale al Administrador este Error:\n" +
                                ex.getMessage());
                cerrarPantalla();
            });
            return null;
        });
    }

    private void cerrarPantalla(){
        Stage stageActual = (Stage) lblNombreImpuesto.getScene().getWindow();
        stageActual.close();
    }


    @FXML
    void cancelar(ActionEvent event) {
        cerrarPantalla();
    }

}//===================================================================================================================//


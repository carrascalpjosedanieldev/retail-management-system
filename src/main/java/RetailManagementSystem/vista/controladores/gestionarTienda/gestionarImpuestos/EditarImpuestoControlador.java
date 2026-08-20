package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarImpuestos;

import RetailManagementSystem.aplicacion.dto.gestion.ImpuestoDTO;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorImpuestos;
import RetailManagementSystem.vista.utilidades.FormateadorNumeros;
import RetailManagementSystem.vista.utilidades.GestorAlertas;

import RetailManagementSystem.vista.utilidades.UtilidadesLista;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

public class EditarImpuestoControlador {

    //ATRIBUTOS:

    @FXML private Button btnCancelar;
    @FXML private Label lblNombreImpuesto;
    @FXML private TextField txtNombre;
    @FXML private TextField txtPorcentaje;

    private ImpuestoDTO datosImpuesto;

    private ObservableList<ImpuestoDTO> listaObservable;

    private final OrquestadorImpuestos orquestadorImpuestos;

    //CONSTRUCTOR:

    public EditarImpuestoControlador(OrquestadorImpuestos orquestadorImpuestos) {
        this.orquestadorImpuestos = orquestadorImpuestos;
    }

    //MÉTODOS:

    public void cargarDatos(ImpuestoDTO datosImpuesto, ObservableList<ImpuestoDTO> listaObservable) {
        if (datosImpuesto == null) {
            throw new IllegalArgumentException("No puedes editar un Impuesto Vacío.");
        }
        this.datosImpuesto = datosImpuesto;
        this.listaObservable = listaObservable;
        lblNombreImpuesto.setText(this.datosImpuesto.nombre());
        txtNombre.setText(this.datosImpuesto.nombre());
        txtPorcentaje.setText(this.datosImpuesto.porcentaje().toString());
        Platform.runLater(() -> btnCancelar.requestFocus());
    }

    private Window getVentana(){
        return btnCancelar.getScene().getWindow();
    }


    @FXML
    void actualizarImpuesto(ActionEvent event) {
        String nombre = txtNombre.getText().trim();
        String nuevoPorcentajeTexto = txtPorcentaje.getText().trim();
        if (nombre.isEmpty() || nuevoPorcentajeTexto.isEmpty()){
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Datos Incompletos",
                    "El Nombre y el Porcentaje son Obligatorios.",
                    "Por favor escribe un Nombre y un Porcentaje Validos."
            );
            return;
        }
        BigDecimal porcentaje;
        try {
            porcentaje = FormateadorNumeros.stringAPorcentaje(nuevoPorcentajeTexto);
        } catch (IllegalArgumentException e) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Número Inválido", null,
                    "Error al Ingresar el Porcentaje:\n" + e.getMessage()
            );
            return;
        }
        CompletableFuture.supplyAsync(()->
            this.orquestadorImpuestos.actualizarImpuesto(
                    this.datosImpuesto.idImpuesto(), nombre, porcentaje
            )
        ).thenAccept(impuestoActualizado ->
            Platform.runLater(()->{
                UtilidadesLista.reemplazarPorIdentidad(
                        listaObservable,
                        impuestoActualizado,
                        item -> item.idImpuesto() == impuestoActualizado.idImpuesto()
                );
                GestorAlertas.mostrarAlertaInformacion(
                        getVentana(), "Éxito", null,
                        "El Impuesto se ha Actualizado con Éxito."
                );
                cerrarPantalla();
            })
        ).exceptionally(ex->{
            Platform.runLater(() -> {
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                GestorAlertas.mostrarAlertaError(
                        getVentana(), "Error Critico",
                        "NO se pudo Completar la Acción.",
                        "Notificale al Administrador este Error:\n" + causa.getMessage()
                );
                cerrarPantalla();
            });
            return null;
        });
    }

    private void cerrarPantalla(){
        Stage stageActual = (Stage) getVentana();
        stageActual.close();
    }


    @FXML
    void cancelar(ActionEvent event) {
        cerrarPantalla();
    }

}//===================================================================================================================//


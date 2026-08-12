package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarPoliticasV;

import RetailManagementSystem.aplicacion.dto.gestion.PoliticaVencimientoDTO;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorPoliticaVencimiento;
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

public class EditarPoliticaVencimientoControlador {

    //ATRIBUTOS:

    @FXML private Button btnCancelar;
    @FXML private Label lblNombrePoliticaV;
    @FXML private TextField txtDiasUmbral;
    @FXML private TextField txtNombre;
    @FXML private TextField txtPorcentaje;

    private PoliticaVencimientoDTO datosPoliticaV;

    private ObservableList<PoliticaVencimientoDTO> listaObservable;

    private final OrquestadorPoliticaVencimiento orquestadorPoliticaVencimiento;

    //CONSTRUCTOR:

    public EditarPoliticaVencimientoControlador(OrquestadorPoliticaVencimiento orquestadorPoliticaVencimiento) {
        this.orquestadorPoliticaVencimiento = orquestadorPoliticaVencimiento;
    }

    //MÉTODOS:

    public void cargarDatos(PoliticaVencimientoDTO datosPoliticaV, ObservableList<PoliticaVencimientoDTO> listaObservable) {
        if (datosPoliticaV == null) {
            throw new IllegalArgumentException("No puedes editar una Política de Vencimiento Vacía.");
        }
        this.datosPoliticaV = datosPoliticaV;
        this.listaObservable = listaObservable;
        lblNombrePoliticaV.setText(this.datosPoliticaV.nombrePolitica());
        txtNombre.setText(this.datosPoliticaV.nombrePolitica());
        txtDiasUmbral.setText(String.valueOf(this.datosPoliticaV.diasUmbral()));
        txtPorcentaje.setText(this.datosPoliticaV.porcentajeDescuento().toString());
        Platform.runLater(() -> {
            btnCancelar.requestFocus();
        });
    }


    @FXML
    void actualizarPoliticaV(ActionEvent event) {
        String nuevoNombre = txtNombre.getText().trim();
        String nuevoPorcentajeTexto = txtPorcentaje.getText().trim();
        String nuevoDiasUmbralTexto = txtDiasUmbral.getText().trim();
        if (nuevoNombre.isEmpty()) {
            GestorAlertas.mostrarAlertaWarning(
                    "Error de Validación", null,
                    "El Nombre de la Política NO puede estar Vacío."
            );
            return;
        }
        if (nuevoPorcentajeTexto.isEmpty() || nuevoDiasUmbralTexto.isEmpty()) {
            GestorAlertas.mostrarAlertaWarning(
                    "Error de Validación", null,
                    "El Porcentaje y los Dias Umbral NO pueden estar Vacíos."
            );
            return;
        }
        BigDecimal porcentaje;
        try {
            porcentaje = FormateadorNumeros.stringAPorcentaje(nuevoPorcentajeTexto);
        } catch (IllegalArgumentException e) {
            GestorAlertas.mostrarAlertaWarning(
                    "Número Inválido", null,
                    "Error al Ingresar el Porcentaje:\n" + e.getMessage()
            );
            return;
        }
        int diasUmbral;
        try {
            diasUmbral = Integer.parseInt(nuevoDiasUmbralTexto);
        } catch (NumberFormatException e) {
            GestorAlertas.mostrarAlertaWarning(
                    "Número Inválido", null,
                    "Los Días Umbral deben ser un Número Entero válido."
            );
            return;
        }
        CompletableFuture.supplyAsync(()->{
            return this.orquestadorPoliticaVencimiento.actualizarPoliticaVencimiento(
                    this.datosPoliticaV.idPoliticaVencimiento(), nuevoNombre, diasUmbral, porcentaje
            );
        }).thenAccept( politicaVActualizada ->{
            Platform.runLater(()->{
                int indice = listaObservable.indexOf(this.datosPoliticaV);
                listaObservable.set(indice, politicaVActualizada);
                GestorAlertas.mostrarAlertaInformacion(
                        "Éxito", null,
                        "La Política de Vencimiento se ha Actualizado con Éxito."
                );
                cerrarPantalla();
            });
        }).exceptionally(ex->{
            Platform.runLater(() -> {
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                GestorAlertas.mostrarAlertaError(
                        "Error Critico",
                        "NO se pudo Completar la Acción.",
                        "Notificale al Administrador este Error:\n" + causa.getMessage()
                );
                cerrarPantalla();
            });
            return null;
        });
    }

    private void cerrarPantalla(){
        Stage stageActual = (Stage) lblNombrePoliticaV.getScene().getWindow();
        stageActual.close();
    }


    @FXML
    void cancelar(ActionEvent event) {
        cerrarPantalla();
    }


}//===================================================================================================================//


package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarImpuestos;

import RetailManagementSystem.aplicacion.dto.gestion.ImpuestoDTO;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorImpuestos;
import RetailManagementSystem.vista.utilidades.FormateadorNumeros;
import RetailManagementSystem.vista.utilidades.GestorAlertas;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

public class CrearImpuestoControlador {

    //ATRIBUTOS:

    @FXML private Button btnCancelar;
    @FXML private CheckBox chkActivo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtPorcentaje;

    private final OrquestadorImpuestos orquestadorImpuestos;

    private ObservableList<ImpuestoDTO> listaObservable;

    //CONSTRUCTOR:

    public CrearImpuestoControlador(OrquestadorImpuestos orquestadorImpuestos) {
        this.orquestadorImpuestos = orquestadorImpuestos;
    }

    //MÉTODOS:

    public void cargarDatos(ObservableList<ImpuestoDTO> listaObservable){
        this.listaObservable = listaObservable;
    }


    @FXML
    void guardarImpuesto(ActionEvent event) {
        String nombre = txtNombre.getText().trim();
        String porcentajeTexto = txtPorcentaje.getText().trim();
        boolean activo = chkActivo.isSelected();
        if (nombre.isEmpty() || porcentajeTexto.isEmpty()){
            GestorAlertas.mostrarAlertaWarning(
                    "Datos Incompletos",
                    "El Nombre y el Porcentaje son Obligatorios.",
                    "Por favor escribe un Nombre y un Porcentaje Validos."
            );
            return;
        }
        BigDecimal porcentaje;
        try {
            porcentaje = FormateadorNumeros.stringAPorcentaje(porcentajeTexto);
        } catch (IllegalArgumentException e) {
            GestorAlertas.mostrarAlertaWarning(
                    "Número Inválido", null,
                    "Error al Ingresar el Porcentaje:\n" + e.getMessage()
            );
            return;
        }
        CompletableFuture.supplyAsync(()->{
            return this.orquestadorImpuestos.registrarImpuesto(nombre, porcentaje, activo);
        }).thenAccept(impuestoRegistrado ->{
            Platform.runLater(()-> {
                listaObservable.add(impuestoRegistrado);
                cerrarPantalla();
            });
        }).exceptionally(ex->{
            Platform.runLater(()->{
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                GestorAlertas.mostrarAlertaError(
                        "Error Critico",
                        "NO se pudo Completar la Acción.",
                        "Notificale al Administrador este Error:\n" + causa.getMessage()
                );
            });
            return null;
        });
    }

    private void cerrarPantalla(){
        Stage stageActual = (Stage) btnCancelar.getScene().getWindow();
        stageActual.close();
    }


    @FXML
    void cancelar(ActionEvent event) {
        cerrarPantalla();
    }


}//===================================================================================================================//


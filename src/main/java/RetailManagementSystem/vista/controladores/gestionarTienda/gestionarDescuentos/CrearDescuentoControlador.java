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
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

public class CrearDescuentoControlador {

    //ATRIBUTOS:

    @FXML private Button btnCancelar;
    @FXML private CheckBox chkActivo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtPorcentaje;

    private final OrquestadorDescuentos orquestadorDescuentos;

    private ObservableList<DescuentoDTO> listaObservable;

    //CONSTRUCTOR:

    public CrearDescuentoControlador(OrquestadorDescuentos orquestadorDescuentos) {
        this.orquestadorDescuentos = orquestadorDescuentos;
    }

    //MÉTODOS:

    public void cargarDatos(ObservableList<DescuentoDTO> listaObservable){
        this.listaObservable = listaObservable;
    }


    @FXML
    void guardarDescuento(ActionEvent event) {
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
        CompletableFuture.supplyAsync(()-> {
            return this.orquestadorDescuentos.registrarDescuento(
                    nombre, porcentaje, activo
            );
        }).thenAccept(descuentoRegistrado -> {
            Platform.runLater(() -> {
                listaObservable.add(descuentoRegistrado);
                cerrarPantalla();
            });
        }).exceptionally(ex -> {
            Platform.runLater(() -> {
                GestorAlertas.mostrarAlertaError("Error Critico",
                        "NO se pudo Completar la Acción.",
                        "Notificale al Administrador este Error:\n" +
                                ex.getMessage());
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


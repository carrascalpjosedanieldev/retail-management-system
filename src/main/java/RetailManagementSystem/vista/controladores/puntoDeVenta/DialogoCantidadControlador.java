package RetailManagementSystem.vista.controladores.puntoDeVenta;

import RetailManagementSystem.vista.utilidades.GestorAlertas;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Window;

public class DialogoCantidadControlador {

    //ATRIBUTOS:

    @FXML private Button btnCancelar;
    @FXML private Label lblAccion;
    @FXML private Label lblAccionCabecera;
    @FXML private Label lblNombreItem;
    @FXML private TextField txtCantidad;

    private int cantidadFinal;

    private boolean confirmado = false;

    //GETTERS:

    public boolean isConfirmado() {
        return confirmado;
    }

    public int getCantidadFinal() {
        return cantidadFinal;
    }

    //MÉTODOS:

    private Window getVentana(){
        return btnCancelar.getScene() != null ? btnCancelar.getScene().getWindow() : null;
    }


    public void configurarDialogo(String accion, int cantidadInicial, String nombreItem) {
        lblAccionCabecera.setText(accion);
        lblAccion.setText(accion);
        lblNombreItem.setText(nombreItem);
        txtCantidad.setText(String.valueOf(cantidadInicial));
        cantidadFinal = cantidadInicial;
    }


    @FXML
    public void initialize(){
        txtCantidad.setTextFormatter(new TextFormatter<>(cambio -> {
            String nuevoTexto = cambio.getControlNewText();
            if (nuevoTexto.isEmpty()) {
                return cambio;
            }
            if (nuevoTexto.matches("^[1-9][0-9]{0,4}$")) {
                return cambio;
            }
            return null;
        }));
    }


    @FXML
    void aceptar(ActionEvent event) {
        String texto = txtCantidad.getText().trim();
        if (texto.isEmpty()) {
            GestorAlertas.mostrarAlertaWarning(
                    btnCancelar.getScene().getWindow(),"Cantidad Vacía", null,
                    "Debe ingresar una cantidad mayor a cero."
            );
            return;
        }
        try {
            this.cantidadFinal = Integer.parseInt(texto);
            this.confirmado = true;
            cerrarVentana();
        } catch (NumberFormatException e) {
            GestorAlertas.mostrarAlertaError(
                    btnCancelar.getScene().getWindow(), "Número Inválido", null,
                    "La cantidad ingresada es demasiado grande."

            );
        }
    }

    private void cerrarVentana() {
        Window ventana = getVentana();
        if (ventana != null){
            ventana.hide();
        }
    }


    @FXML
    void cancelar(ActionEvent event) {
        cerrarVentana();
    }

}//===================================================================================================================//


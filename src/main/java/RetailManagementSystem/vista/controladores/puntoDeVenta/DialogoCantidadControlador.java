package RetailManagementSystem.vista.controladores.puntoDeVenta;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;

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
            if (cambio.getText().matches("[0-9]*")) {
                return cambio;
            }
            return null;
        }));
    }


    @FXML
    void aceptar(ActionEvent event) {
        String texto = txtCantidad.getText().trim();
        if (texto.isEmpty()) return;
        this.cantidadFinal = Integer.parseInt(texto);
        this.confirmado = true;
        cerrarVentana(event);
    }

    private void cerrarVentana(ActionEvent event) {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }


    @FXML
    void cancelar(ActionEvent event) {
        cerrarVentana(event);
    }

}//===================================================================================================================//


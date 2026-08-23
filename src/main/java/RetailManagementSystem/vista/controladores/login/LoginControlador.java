package RetailManagementSystem.vista.controladores.login;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class LoginControlador {

    //ATRIBUTOS:

    @FXML private Button btnVerContrasena;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtPasswordVisible;
    @FXML private VBox vboxBaseBlanca;

    //CONSTRUCTOR:


    //MÉTODOS:

    @FXML
    void initialize() {
        txtPasswordVisible.textProperty().bindBidirectional(txtPassword.textProperty());
    }


    @FXML
    void alternarVisibilidadContrasena(ActionEvent event) {
        boolean estaOculto = txtPassword.isVisible();
        txtPassword.setVisible(!estaOculto);
        txtPasswordVisible.setVisible(estaOculto);
        btnVerContrasena.setText(estaOculto ? "🙈" : "👁");
        if (estaOculto) {
            txtPasswordVisible.requestFocus();
            txtPasswordVisible.positionCaret(txtPasswordVisible.getText().length());
        } else {
            txtPassword.requestFocus();
            txtPassword.positionCaret(txtPassword.getText().length());
        }
    }


    @FXML
    void ingresarAlSistema(ActionEvent event) {

    }


    @FXML
    void salirDeLaApp(ActionEvent event) {

    }


}//===================================================================================================================//


package RetailManagementSystem.vista.utilidades;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;

import java.net.URL;

public class GestorAlertas {

    public static void mostrarError(String titulo, String encabezado, String contenido) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(encabezado);
        alerta.setContentText(contenido);
        DialogPane panelAlerta = alerta.getDialogPane();
        URL urlCss = GestorAlertas.class.getResource(RutasVista.ESTILOS_CSS_ALERTA_DE_NAVEGACION);
        if (urlCss != null) {
            panelAlerta.getStylesheets().add(urlCss.toExternalForm());
            panelAlerta.setPrefSize(500, 280);
        } else {
            panelAlerta.setPrefSize(500, 180);
        }
        alerta.showAndWait();
    }

    public static void mostrarAlerta(String titulo, String encabezado, String contenido) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle(titulo);
        alerta.setHeaderText(encabezado);
        alerta.setContentText(contenido);
        DialogPane panelAlerta = alerta.getDialogPane();
        URL urlCss = GestorAlertas.class.getResource(RutasVista.ESTILOS_CSS_ALERTA_DE_NAVEGACION);
        if (urlCss != null) {
            panelAlerta.getStylesheets().add(urlCss.toExternalForm());
            panelAlerta.setPrefSize(500, 280);
        } else {
            panelAlerta.setPrefSize(500, 180);
        }
        alerta.showAndWait();
    }

    public static void mostrarInformacion(String titulo, String encabezado, String contenido) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(encabezado);
        alerta.setContentText(contenido);
        DialogPane panelAlerta = alerta.getDialogPane();
        URL urlCss = GestorAlertas.class.getResource(RutasVista.ESTILOS_CSS_ALERTA_DE_NAVEGACION);
        if (urlCss != null) {
            panelAlerta.getStylesheets().add(urlCss.toExternalForm());
            panelAlerta.setPrefSize(500, 280);
        } else {
            panelAlerta.setPrefSize(500, 180);
        }
        alerta.showAndWait();
    }

}//===================================================================================================================//


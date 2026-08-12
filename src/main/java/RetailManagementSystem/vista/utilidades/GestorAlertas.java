package RetailManagementSystem.vista.utilidades;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.Region;

import java.net.URL;
import java.util.Optional;

public class GestorAlertas {

    private static void mostrarAlerta(Alert.AlertType tipo, String titulo, String encabezado, String contenido){
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(encabezado);
        alerta.setContentText(contenido);
        DialogPane panelAlerta = alerta.getDialogPane();
        URL urlCss = GestorAlertas.class.getResource(RutasVista.ESTILOS_CSS_ALERTAS);
        if (urlCss != null) {
            panelAlerta.getStylesheets().add(urlCss.toExternalForm());
        }
        panelAlerta.setMinHeight(Region.USE_PREF_SIZE);
        alerta.showAndWait();
    }

    public static void mostrarAlertaError(String titulo, String encabezado, String contenido) {
        mostrarAlerta(Alert.AlertType.ERROR, titulo, encabezado, contenido);
    }

    public static void mostrarAlertaWarning(String titulo, String encabezado, String contenido) {
        mostrarAlerta(Alert.AlertType.WARNING, titulo, encabezado, contenido);
    }

    public static void mostrarAlertaInformacion(String titulo, String encabezado, String contenido) {
        mostrarAlerta(Alert.AlertType.INFORMATION, titulo, encabezado, contenido);
    }


    public static boolean mostrarConfirmacion(String titulo, String encabezado, String contenido) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle(titulo);
        confirmacion.setHeaderText(encabezado);
        confirmacion.setContentText(contenido);
        DialogPane panelAlerta = confirmacion.getDialogPane();
        URL urlCss = GestorAlertas.class.getResource(RutasVista.ESTILOS_CSS_ALERTAS);
        if (urlCss != null) {
            panelAlerta.getStylesheets().add(urlCss.toExternalForm());
        }
        panelAlerta.setMinHeight(Region.USE_PREF_SIZE);
        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        return respuesta.isPresent() && respuesta.get() == ButtonType.OK;
    }

}//===================================================================================================================//


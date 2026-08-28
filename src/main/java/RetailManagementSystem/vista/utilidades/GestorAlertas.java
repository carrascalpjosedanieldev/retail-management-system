package RetailManagementSystem.vista.utilidades;

import RetailManagementSystem.dominio.excepciones.autenticacionYSeguridad.AccesoDenegadoException;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.Window;

import java.net.URL;
import java.util.Optional;

public class GestorAlertas {

    private static void mostrarAlerta(Alert.AlertType tipo, Window ventana, String titulo, String encabezado, String contenido){
        Alert alerta = new Alert(tipo);
        if (ventana != null) {
            alerta.initOwner(ventana);
        }
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

    public static void mostrarAlertaError(Window ventana, String titulo, String encabezado, String contenido) {
        mostrarAlerta(Alert.AlertType.ERROR, ventana, titulo, encabezado, contenido);
    }

    public static void mostrarAlertaWarning(Window ventana, String titulo, String encabezado, String contenido) {
        mostrarAlerta(Alert.AlertType.WARNING, ventana, titulo, encabezado, contenido);
    }

    public static void mostrarAlertaInformacion(Window ventana, String titulo, String encabezado, String contenido) {
        mostrarAlerta(Alert.AlertType.INFORMATION, ventana,titulo, encabezado, contenido);
    }


    public static boolean mostrarConfirmacion(Window ventana, String titulo, String encabezado, String contenido) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        if (ventana != null){
            confirmacion.initOwner(ventana);
        }
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


    public static void mostrarAlertaSalirDelSistema(Window ventanaPadre, boolean haySesionActiva) {
        Label iconoAmigable = new Label("👋");
        iconoAmigable.setId("iconoAlerta");
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        if (ventanaPadre != null) {
            alerta.initOwner(ventanaPadre);
        }
        alerta.setTitle("Confirmar Salida");
        alerta.setHeaderText(null);
        String textoAlerta = haySesionActiva
                ? "¿Estás Seguro de que deseas Cerrar Sesión y Salir del Sistema?"
                : "¿Estás Seguro de que deseas Salir del Sistema?";
        alerta.setContentText(textoAlerta);
        alerta.setGraphic(iconoAmigable);
        DialogPane panelAlerta = alerta.getDialogPane();
        URL urlCss = GestorAlertas.class.getResource(RutasVista.ESTILOS_CSS_ALERTA_SALIR_DEL_SISTEMA);
        if (urlCss != null) {
            panelAlerta.getStylesheets().add(urlCss.toExternalForm());
        }
        Button botonAceptar = (Button) panelAlerta.lookupButton(ButtonType.OK);
        if (botonAceptar != null) {
            botonAceptar.setId("btnSalir");
        }
        Optional<ButtonType> respuesta = alerta.showAndWait();
        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            Platform.exit();
            System.exit(0);
        }
    }

    public static void mostrarAlertaAccesoDenegado(Window ventana, AccesoDenegadoException exception){
        mostrarAlertaError(
                ventana,
                "Acceso Denegado", "Privilegios Insuficientes",
                exception.getMessage()
        );
    }

}//===================================================================================================================//


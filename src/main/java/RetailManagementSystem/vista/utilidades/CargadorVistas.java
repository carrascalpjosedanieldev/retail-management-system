package RetailManagementSystem.vista.utilidades;

import RetailManagementSystem.infraestructura.inyeccion.FabricaControladores;
import RetailManagementSystem.vista.excepciones.CargarVistaException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.function.Consumer;

public class CargadorVistas {

    private static final FabricaControladores FABRICA_C = new FabricaControladores();

    public static void cambiarPantalla(Window ventanaActual, String rutaFxml){
        if (ventanaActual == null) {
            throw new IllegalArgumentException("La Ventana Actual NO Puede ser Nula para Cambiar de Pantalla.");
        }
        Parent nuevaVista = cargarVista(rutaFxml);
        if (ventanaActual.getScene() != null) {
            ventanaActual.getScene().setRoot(nuevaVista);
        } else if (ventanaActual instanceof Stage stage) {
            stage.setScene(new Scene(nuevaVista));
        } else {
            throw new IllegalStateException(
                    "Imposible cambiar la pantalla: La Ventana NO es un Stage y NO tiene Escena."
            );
        }

    }

    public static void cambiarPantallaConTamanoPequeno(Window ventanaActual, String rutaFxml){
        if (ventanaActual == null) {
            throw new IllegalArgumentException("La Ventana Actual NO Puede ser Nula para Cambiar de Pantalla.");
        }
        Parent nuevaVista = cargarVista(rutaFxml);
        if (ventanaActual instanceof Stage stage) {
            Scene nuevaEscena = new Scene(nuevaVista, 600, 600);
            stage.setTitle("Sistema de Gestión de Tienda - JavaFX");
            stage.setMaximized(false);
            stage.setScene(nuevaEscena);
            stage.setOnCloseRequest(e -> {
                e.consume();
                GestorAlertas.mostrarAlertaSalirDelSistema(stage, false);
            });
            stage.setWidth(600);
            stage.setHeight(600);
            stage.setMinWidth(600);
            stage.setMinHeight(600);
            stage.setResizable(false);
            stage.centerOnScreen();
        } else {
            throw new IllegalStateException(
                    "Imposible cambiar la pantalla: La Ventana NO es un Stage y NO tiene Escena."
            );
        }

    }

    public static <T> void cambiarPantallaConInyeccionYTamanoNormal(Window ventanaActual, String rutaFxml, Consumer<T> inyector) {
        if (ventanaActual == null) {
            throw new IllegalArgumentException("La Ventana NO puede ser Nula.");
        }
        try {
            FXMLLoader loader = obtenerLoaderConfigurado(rutaFxml);
            Parent nuevaVista = loader.load();
            T controlador = loader.getController();
            if (inyector != null) {
                inyector.accept(controlador);
            }
            if (ventanaActual instanceof Stage stage) {
                Scene nuevaEscena = new Scene(nuevaVista, 1280, 720);
                stage.setScene(nuevaEscena);
                stage.setOnCloseRequest(e -> {
                    e.consume();
                    GestorAlertas.mostrarAlertaSalirDelSistema(stage, true);
                });
                stage.setResizable(true);
                stage.setWidth(1280);
                stage.setHeight(720);
                stage.setMinWidth(1024);
                stage.setMinHeight(600);
                stage.centerOnScreen();
            }
        } catch (IOException e) {
            throw new CargarVistaException(rutaFxml, "Error al Inyectar y Cargar la vista.", e);
        }
    }

    public static Parent cargarVista(String rutaFxml) {
        try {
            FXMLLoader loader = new FXMLLoader(CargadorVistas.class.getResource(rutaFxml));
            loader.setControllerFactory(FABRICA_C);
            return loader.load();
        } catch (IOException e){
            throw new CargarVistaException(rutaFxml, "No se pudo cargar el archivo FXML.", e);
        }
    }


    public static FXMLLoader obtenerLoaderConfigurado(String rutaFxml) {
        FXMLLoader loader = new FXMLLoader(CargadorVistas.class.getResource(rutaFxml));
        loader.setControllerFactory(FABRICA_C);
        return loader;
    }


    public static <T> T abrirModalConInyeccion(
            String rutaFxml, String tituloModal, Window ventanaPadre, Consumer<T> inicializadorControlador
    ) {
        try {
            FXMLLoader loader = CargadorVistas.obtenerLoaderConfigurado(rutaFxml);
            Parent root = loader.load();
            T controlador = loader.getController();
            if (inicializadorControlador != null) {
                inicializadorControlador.accept(controlador);
            }
            Stage stageModal = new Stage();
            stageModal.setTitle(tituloModal);
            stageModal.setScene(new Scene(root));
            stageModal.initModality(Modality.APPLICATION_MODAL);
            stageModal.setResizable(false);
            if (ventanaPadre != null){
                stageModal.initOwner(ventanaPadre);
            }
            stageModal.showAndWait();
            return controlador;
        } catch (IOException e) {
            throw new CargarVistaException(rutaFxml, "NO se pudo Cargar el Archivo FXML.", e);
        } catch (IllegalArgumentException | IllegalStateException e) {
            GestorAlertas.mostrarAlertaError(
                    ventanaPadre, "Error de Inicialización", null,
                    "No se pudo abrir la ventana por un conflicto de datos:\n" + e.getMessage()
            );
            return null;
        }
    }


    public static <T> T abrirModalSinInyeccion(String rutaFxml, String tituloModal, Window ventanaPadre) {
        return abrirModalConInyeccion(rutaFxml, tituloModal, ventanaPadre, null);
    }


    public static <T> void cambiarPantallaInyectada(Window ventana, String rutaFxml, Consumer<T> inicializadorControlador){
        if (ventana == null || ventana.getScene() == null) {
            throw new IllegalStateException("Imposible Navegar: La Ventana Origen o su Escena son Nulas.");
        }
        try {
            FXMLLoader loader = CargadorVistas.obtenerLoaderConfigurado(rutaFxml);
            Parent root = loader.load();
            T controlador = loader.getController();
            if (inicializadorControlador != null) {
                inicializadorControlador.accept(controlador);
            }
            ventana.getScene().setRoot(root);
        } catch (IOException e) {
            throw new CargarVistaException(rutaFxml, "NO se pudo Cargar el Archivo FXML.", e);
        } catch (IllegalArgumentException | IllegalStateException e) {
            GestorAlertas.mostrarAlertaError(
                    ventana, "Error de Navegación", null,
                    "No se pudo cargar la pantalla por un conflicto de datos:\n" + e.getMessage()

            );
        }
    }


}//===================================================================================================================//


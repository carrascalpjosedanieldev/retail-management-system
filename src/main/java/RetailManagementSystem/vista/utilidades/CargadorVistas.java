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

    public static Parent cargarVista(String rutaFxml) {
        try {
            FXMLLoader loader = new FXMLLoader(CargadorVistas.class.getResource(rutaFxml));
            loader.setControllerFactory(FABRICA_C);
            return loader.load();
        } catch (IOException e){
            throw new CargarVistaException(rutaFxml, "No se pudo cargar el archivo FXML.", e);
        }
    }

    public static void cambiarPantalla(Stage stageActual, String rutaFxml){
        Parent nuevaVista = cargarVista(rutaFxml);
        if (stageActual.getScene() != null) {
            stageActual.getScene().setRoot(nuevaVista);
        } else {
            Scene escena = new Scene(nuevaVista);
            stageActual.setScene(escena);
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
            stageModal.initOwner(ventanaPadre);
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

    public static <T> void cambiarPantallaInyectada(String rutaFxml, Window ventana, Consumer<T> inicializadorControlador){
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


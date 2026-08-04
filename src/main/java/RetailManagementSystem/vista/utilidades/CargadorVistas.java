package RetailManagementSystem.vista.utilidades;

import RetailManagementSystem.infraestructura.inyeccion.FabricaControladores;
import RetailManagementSystem.vista.excepciones.CargarVistaException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

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

}//===================================================================================================================//


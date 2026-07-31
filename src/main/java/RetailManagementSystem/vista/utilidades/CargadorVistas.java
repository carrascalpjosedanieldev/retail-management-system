package RetailManagementSystem.vista.utilidades;

import RetailManagementSystem.infraestructura.inyeccion.FabricaControladores;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CargadorVistas {

    private static final FabricaControladores FABRICA_C = new FabricaControladores();

    public static Parent cargarVista(String rutaFxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(CargadorVistas.class.getResource(rutaFxml));
        loader.setControllerFactory(FABRICA_C);
        return loader.load();
    }

    public static void cambiarPantalla(Stage stageActual, String rutaFxml) throws IOException {
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


package ProyectoPropio1.aplicacion;

// PARA EXPORTAR EL PROYECTO FACILMENTE:
// Get-ChildItem -Recurse -Filter *.java | Get-Content | Out-File proyecto_completo.txt

import ProyectoPropio1.utilidades.RutasVista;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

// mvn javafx:run -e

public class App extends Application {

    public static void main(String[] args) {

        launch(args);

    }

    @Override
    public void start(Stage stagePrincipal) throws Exception {
        try {

            FXMLLoader cargador = new FXMLLoader(getClass().getResource(RutasVista.MENU_PRINCIPAL_VIEW));
            Parent raiz = cargador.load();

            Scene escena = new Scene(raiz, 1280, 720);

            stagePrincipal.setTitle("Sistema de Gestión de Tienda - JavaFX");
            stagePrincipal.setScene(escena);

            stagePrincipal.setResizable(true);

            stagePrincipal.setMinWidth(1024);
            stagePrincipal.setMinHeight(600);

            stagePrincipal.show();

        } catch (IOException e) {
            System.err.println("¡Error crítico! No se pudo cargar el archivo FXML inicial.");
            System.err.println("Verifica que el archivo exista en src/main/resources/vistas/MenuPrincipal.fxml");
            e.printStackTrace();
        }
    }

}

// mvn javafx:run -e


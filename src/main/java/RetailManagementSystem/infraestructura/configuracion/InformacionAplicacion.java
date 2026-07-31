package RetailManagementSystem.infraestructura.configuracion;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class InformacionAplicacion {

    //VERSION DEL PROYECTO:

    public static final String VERSION = cargarVersion();

    private static String cargarVersion() {
        Properties propiedades = new Properties();
        try (InputStream input = InformacionAplicacion.class.getClassLoader().getResourceAsStream("version.properties")) {
            if (input == null) {
                throw new IllegalStateException("NO se encontró el archivo version.properties en el classpath.");
            }
            propiedades.load(input);
            String version = propiedades.getProperty("version");
            if (version == null || version.trim().isEmpty()) {
                throw new IllegalStateException("El archivo version.properties no contiene la propiedad 'version'.");
            }
            return version;
        } catch (IOException e) {
            throw new RuntimeException("Error inesperado al intentar leer la versión del sistema.", e);
        }
    }

    public static String obtenerVersion() {
        return VERSION;
    }


}//===================================================================================================================//


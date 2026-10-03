package RetailManagementSystem.infraestructura.configuracion;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class InformacionAplicacion {

    //VERSION DE LA APLICACIÓN:

    private final String version;

    public InformacionAplicacion(String rutaProperties) {
        this.version = cargarVersion(rutaProperties);
    }

    private String cargarVersion(String rutaProperties) {
        Properties propiedades = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(rutaProperties)) {
            if (input == null) {
                throw new IllegalStateException("NO se encontró el archivo " + rutaProperties + " en el classpath.");
            }
            propiedades.load(input);
            String version = propiedades.getProperty("version");
            if (version == null || version.trim().isEmpty()) {
                throw new IllegalStateException("El archivo " + rutaProperties + " no contiene la propiedad 'version'.");
            }
            return version;
        } catch (IOException e) {
            throw new RuntimeException("Error inesperado al intentar leer la versión del sistema.", e);
        }
    }

    public String obtenerVersion() {
        return version;
    }

}//===================================================================================================================//


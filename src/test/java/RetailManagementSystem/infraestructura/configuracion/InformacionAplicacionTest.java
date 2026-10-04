package RetailManagementSystem.infraestructura.configuracion;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InformacionAplicacionTest {

    @Test
    void deberiaCargarLaVersionCorrectamenteCuandoElArchivoYLaPropiedadSonValidos() {
        //ARRANGE
        InformacionAplicacion infoApp = new InformacionAplicacion("version/valid-version.properties");
        //ACT
        String version = infoApp.obtenerVersion();
        //ASSERT
        assertEquals("1.5.0", version);
    }

    @Test
    void deberiaLanzarExcepcionCuandoElArchivoNoExisteEnElClasspath() {
        //ACT AND ASSERT
        IllegalStateException excepcion = assertThrows(
                IllegalStateException.class,
                () -> new InformacionAplicacion("archivo-fantasma.properties")
        );
        assertTrue(excepcion.getMessage().contains("NO se Encontró el Archivo"));
    }

    @Test
    void deberiaLanzarExcepcionCuandoElArchivoExistePeroNoTieneLaClaveVersion() {
        //ACT AND ASSERT
        IllegalStateException excepcion = assertThrows(
                IllegalStateException.class,
                () -> new InformacionAplicacion("version/sin-version.properties")
        );
        assertTrue(excepcion.getMessage().contains("NO Contiene la Propiedad 'version'"));
    }

    @Test
    void deberiaLanzarExcepcionCuandoLaClaveVersionEstaVaciaOEnBlanco() {
        //ACT AND ASSERT
        IllegalStateException excepcion = assertThrows(
                IllegalStateException.class,
                () -> new InformacionAplicacion("version/version-vacia.properties")
        );
        assertTrue(excepcion.getMessage().contains("NO Contiene la Propiedad 'version'"));
    }

}//===================================================================================================================//


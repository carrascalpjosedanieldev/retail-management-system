package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.aplicacion.dto.consultas.ConfiguracionSistemaDTO;
import RetailManagementSystem.aplicacion.dto.seguridad.PoliticaDeBloqueoDTO;
import RetailManagementSystem.aplicacion.puertos.ProveedorConfiguracion;
import RetailManagementSystem.dominio.excepciones.recursosNoEncontrados.ConfiguracionDelsistemaNoEncontradaException;
import RetailManagementSystem.dominio.excepciones.recursosNoEncontrados.ValorConfiguracionNoEncontradaException;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioConfiguracion;
import RetailManagementSystem.dominio.puertos.transacciones.GestorTransaccional;
import RetailManagementSystem.dominio.puertos.transacciones.OperacionTransaccional;
import RetailManagementSystem.dominio.puertos.transacciones.OperacionTransaccionalConRetorno;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServicioConfiguracionesTest {

    private static final String CONF_DATOS_TIENDA = "NOMBRE_PROYECTO_PROPIO_ORIGINAL";

    private static final String CONF_MAX_INTENTOS = "SEGURIDAD_MAX_INTENTOS";

    private static final String CONF_MINUTOS_BLOQUEO = "SEGURIDAD_MINUTOS_BLOQUEO";

    @Mock
    private RepositorioConfiguracion repositorioConfiguracionFalso;

    @Mock
    private ProveedorConfiguracion proveedorConfiguracionFalso;

    @Mock
    private GestorTransaccional gestorTransaccionalFalso;

    private static final String VALOR_CONFIGURACION_POR_DEFECTO = "VALOR POR DEFECTO";

    private static final ConfiguracionSistemaDTO CONFIGURACION_SISTEMA_POR_DEFECTO =
            new ConfiguracionSistemaDTO(VALOR_CONFIGURACION_POR_DEFECTO, "DESCRIPCIÓN POR DEFECTO");

    @InjectMocks
    private ServicioConfiguraciones servicioConfiguraciones;

    @BeforeEach
    void setUp(){
        lenient().when(gestorTransaccionalFalso.ejecutarEnTransaccionConRetorno(any()))
                .thenAnswer(invocation -> {
                    OperacionTransaccionalConRetorno<?> operacion = invocation.getArgument(0);
                    return operacion.ejecutar();
                });
        lenient().when(gestorTransaccionalFalso.ejecutarEnTransaccionDeLectura(any()))
                .thenAnswer(invocation -> {
                    OperacionTransaccionalConRetorno<?> operacion = invocation.getArgument(0);
                    return operacion.ejecutar();
                });
        lenient().doAnswer(invocation -> {
            OperacionTransaccional operacion = invocation.getArgument(0);
            operacion.ejecutar();
            return null;
        }).when(gestorTransaccionalFalso).ejecutarEnTransaccion(any());
    }

    //TESTS

    @Test
    void deberiaObtenerElValorConfiguracionCorrectamente(){
        //ARRANGE
        String clave = "CLAVE_PRUEBA";
        when(repositorioConfiguracionFalso.obtenerValorConfiguracion(clave))
                .thenReturn(VALOR_CONFIGURACION_POR_DEFECTO);
        //ACT
        String resultado = servicioConfiguraciones.obtenerValorConfiguracion(clave);
        //ASSERT
        assertEquals(VALOR_CONFIGURACION_POR_DEFECTO, resultado);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionDeLectura(any());
    }

    @Test
    void deberiaLanzarExcepcionSiElValorConfiguracionNoExisteAlObtener(){
        //ARRANGE
        String claveInexistente = "CLAVE_FALSA";
        String mensajeEsperado = "El Valor para la Configuración de Clave -" + claveInexistente + "- NO Existe.";
        when(repositorioConfiguracionFalso.obtenerValorConfiguracion(claveInexistente))
                .thenThrow(new ValorConfiguracionNoEncontradaException(mensajeEsperado));
        //ACT AND ASSERT
        ValorConfiguracionNoEncontradaException exception = assertThrows(
                ValorConfiguracionNoEncontradaException.class,
                ()-> servicioConfiguraciones.obtenerValorConfiguracion(claveInexistente)
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionDeLectura(any());
    }

    @Test
    void deberiaObtenerLaConfiguracionDelSistemaCorrectamente(){
        //ARRANGE
        String clave = "CLAVE_PRUEBA";
        when(repositorioConfiguracionFalso.obtenerConfiguracionSistema(clave))
                .thenReturn(CONFIGURACION_SISTEMA_POR_DEFECTO);
        //ACT
        ConfiguracionSistemaDTO resultado = servicioConfiguraciones.obtenerConfiguracionSistema(clave);
        //ASSERT
        assertEquals(CONFIGURACION_SISTEMA_POR_DEFECTO.valor(), resultado.valor());
        assertEquals(CONFIGURACION_SISTEMA_POR_DEFECTO.descripcion(), resultado.descripcion());
    }

    @Test
    void deberiaLanzarExcepcionSiLaConfiguracionDelSistemaNoExisteAlObtener(){
        //ARRANGE
        String claveInexistente = "CLAVE_FALSA";
        String mensajeEsperado = "La Configuración del Sistema de Clave -" + claveInexistente + "- NO Existe.";
        when(repositorioConfiguracionFalso.obtenerConfiguracionSistema(claveInexistente))
                .thenThrow(new ConfiguracionDelsistemaNoEncontradaException(mensajeEsperado));
        //ACT AND ASSERT
        ConfiguracionDelsistemaNoEncontradaException exception = assertThrows(
                ConfiguracionDelsistemaNoEncontradaException.class,
                ()-> servicioConfiguraciones.obtenerConfiguracionSistema(claveInexistente)
        );
        assertEquals(mensajeEsperado, exception.getMessage());
    }

    @Test
    void deberiaCambiarNombreYDescripcionTiendaCorrectamente(){
        //ARRANGE
        String nombreNuevo = "  Tienda  ";
        String descripcionNueva = "   Descripción  ";
        //ACT
        servicioConfiguraciones.cambiarNombreYDescripcionTienda(nombreNuevo, descripcionNueva);
        //ASSERT
        verify(repositorioConfiguracionFalso)
                .actualizarConfiguracionSistemaConfiguracion(CONF_DATOS_TIENDA, "Tienda", "Descripción");
        verify(proveedorConfiguracionFalso).invalidarCache(CONF_DATOS_TIENDA);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccion(any());
    }

    @ParameterizedTest
    @CsvSource(value = {"null", "''", "'   '"} , nullValues = "null")
    void deberiaLanzarExcepcionSiElNombreNuevoDeLaTiendaEsInvalido(String nombreNuevoInvalido){
        //ARRANGE
        String descripcionNueva = "   Descripción  ";
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> servicioConfiguraciones.cambiarNombreYDescripcionTienda(nombreNuevoInvalido, descripcionNueva)
        );
        assertEquals("El Nombre de la Tienda NO puede estar Vacío.", exception.getMessage());
        verifyNoInteractions(repositorioConfiguracionFalso, proveedorConfiguracionFalso, gestorTransaccionalFalso);
    }

    @ParameterizedTest
    @CsvSource(value = {"null", "''", "'   '"} , nullValues = "null")
    void deberiaLanzarExcepcionSiLaDescripcionNuevaDeLaTiendaEsInvalido(String descripcionNuevaInvalida){
        //ARRANGE
        String nombreNuevo = "  Tienda  ";
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> servicioConfiguraciones.cambiarNombreYDescripcionTienda(nombreNuevo, descripcionNuevaInvalida)
        );
        assertEquals("La Description NO puede estar Vacía.", exception.getMessage());
        verifyNoInteractions(repositorioConfiguracionFalso, proveedorConfiguracionFalso, gestorTransaccionalFalso);
    }

    @Test
    void deberiaObtenerLaPoliticaDeBloqueoCorrectamente(){
        //ARRANGE
        ConfiguracionSistemaDTO dtoMaxIntentos =
                new ConfiguracionSistemaDTO("3", "Intentos máximos antes de bloqueo");
        ConfiguracionSistemaDTO dtoMinutosBloqueo =
                new ConfiguracionSistemaDTO("15", "Minutos de bloqueo tras exceder intentos");
        when(repositorioConfiguracionFalso.obtenerConfiguracionSistema(CONF_MAX_INTENTOS))
                .thenReturn(dtoMaxIntentos);
        when(repositorioConfiguracionFalso.obtenerConfiguracionSistema(CONF_MINUTOS_BLOQUEO))
                .thenReturn(dtoMinutosBloqueo);
        //ACT
        PoliticaDeBloqueoDTO resultado = servicioConfiguraciones.obtenerPoliticaDeBloqueo();
        //ASSERT
        assertNotNull(resultado);
        assertEquals(dtoMaxIntentos, resultado.maxIntentos());
        assertEquals(dtoMinutosBloqueo, resultado.minutosBloqueo());
        verify(repositorioConfiguracionFalso).obtenerConfiguracionSistema(CONF_MAX_INTENTOS);
        verify(repositorioConfiguracionFalso).obtenerConfiguracionSistema(CONF_MINUTOS_BLOQUEO);
        verify(gestorTransaccionalFalso, times(2)).ejecutarEnTransaccionConRetorno(any());
    }

    @Test
    void deberiaActualizarMaxIntentosCorrectamente() {
        // ARRANGE
        int nuevoMaximo = 5;
        // ACT
        servicioConfiguraciones.actualizarMaxIntentos(nuevoMaximo);
        // ASSERT
        verify(repositorioConfiguracionFalso).actualizarValorConfiguracion(CONF_MAX_INTENTOS, "5");
        verify(proveedorConfiguracionFalso).invalidarCache(CONF_MAX_INTENTOS);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccion(any());
    }

    @ParameterizedTest
    @CsvSource({"0", "-1", "-10"})
    void deberiaLanzarExcepcionAlActualizarMaxIntentosConValoresInvalidos(int valorInvalido) {
        // ARRANGE
        String mensajeEsperado = "Los Intentos Máximos son Inválidos";
        // ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioConfiguraciones.actualizarMaxIntentos(valorInvalido)
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verifyNoInteractions(repositorioConfiguracionFalso, proveedorConfiguracionFalso, gestorTransaccionalFalso);
    }

    @Test
    void deberiaActualizarMaxMinutosBloqueosCorrectamente() {
        // ARRANGE
        int nuevosMinutos = 15;
        // ACT
        servicioConfiguraciones.actualizarMaxMinutosBloqueos(nuevosMinutos);
        // ASSERT
        verify(repositorioConfiguracionFalso).actualizarValorConfiguracion(CONF_MINUTOS_BLOQUEO, "15");
        verify(proveedorConfiguracionFalso).invalidarCache(CONF_MINUTOS_BLOQUEO);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccion(any());
    }

    @ParameterizedTest
    @CsvSource({"0", "-1", "-30"})
    void deberiaLanzarExcepcionAlActualizarMaxMinutosBloqueosConValoresInvalidos(int valorInvalido) {
        // ARRANGE
        String mensajeEsperado = "Los Minutos de Bloqueo son Inválidos";
        // ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioConfiguraciones.actualizarMaxMinutosBloqueos(valorInvalido)
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verifyNoInteractions(repositorioConfiguracionFalso, proveedorConfiguracionFalso, gestorTransaccionalFalso);
    }

    @Test
    void deberiaLanzarExcepcionYNoInvalidarCacheSiLaConfiguracionNoExisteAlActualizar() {
        // ARRANGE
        int nuevoMaximo = 5;
        String mensajeEsperado = "NO se pudo Actualizar: La Clave NO le Pertenece a ninguna Configuración.";
        doThrow(new ValorConfiguracionNoEncontradaException(mensajeEsperado))
                .when(repositorioConfiguracionFalso)
                .actualizarValorConfiguracion(CONF_MAX_INTENTOS, "5");
        // ACT AND ASSERT
        ValorConfiguracionNoEncontradaException exception = assertThrows(
                ValorConfiguracionNoEncontradaException.class,
                () -> servicioConfiguraciones.actualizarMaxIntentos(nuevoMaximo)
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verifyNoInteractions(proveedorConfiguracionFalso);
    }

}//===================================================================================================================//


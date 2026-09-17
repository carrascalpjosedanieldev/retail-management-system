package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.dominio.excepciones.recursosNoEncontrados.ImpuestoNoEncontradoException;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioImpuestos;

import RetailManagementSystem.dominio.puertos.transacciones.GestorTransaccional;
import RetailManagementSystem.dominio.puertos.transacciones.OperacionTransaccional;
import RetailManagementSystem.dominio.puertos.transacciones.OperacionTransaccionalConRetorno;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServicioImpuestosTest {

    @Mock
    private RepositorioImpuestos repoImpuestosFalso;

    @Mock
    private GestorTransaccional gestorTransaccionalFalso;

    @InjectMocks
    private ServicioImpuestos servicioImpuestos;

    private Impuesto impuestoPrueba;

    private static final String NOMBRE_POR_DEFECTO = "IVA 2026";

    private static final BigDecimal PORCENTAJE_POR_DEFECTO = new BigDecimal("19");

    @BeforeEach
    void setUp() {
        impuestoPrueba = Impuesto.reconstruirDesdeBD(
                1,
                NOMBRE_POR_DEFECTO,
                PORCENTAJE_POR_DEFECTO,
                true
        );
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

    //TEST'S

    @Test
    void deberiaRegistrarUnImpuestoCorrectamente(){
        // ARRANGE
        when(repoImpuestosFalso.insertarImpuesto(any(Impuesto.class))).thenReturn(impuestoPrueba);
        // ACT
        Impuesto resultado = servicioImpuestos.registrarImpuesto(NOMBRE_POR_DEFECTO, PORCENTAJE_POR_DEFECTO, true);
        // ASSERT
        assertNotNull(resultado.getId());
        ArgumentCaptor<Impuesto> captor = ArgumentCaptor.forClass(Impuesto.class);
        verify(repoImpuestosFalso).insertarImpuesto(captor.capture());
        Impuesto impuestoCapturado = captor.getValue();
        assertNull(impuestoCapturado.getId());
        assertEquals(NOMBRE_POR_DEFECTO, impuestoCapturado.getNombre());
        assertEquals(0, impuestoCapturado.getPorcentaje().compareTo(PORCENTAJE_POR_DEFECTO));
        assertTrue(impuestoCapturado.isActivo());
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
    }

    @Test
    void deberiaLanzarExcepcionCuandoObtenerImpuestoNoExiste(){
        // ARRANGE
        int idInexistente = 99;
        String mensajeEsperado = "NO existe un Impuesto con el ID: " + idInexistente;
        when(repoImpuestosFalso.obtenerImpuesto(idInexistente))
                .thenThrow(new ImpuestoNoEncontradoException(mensajeEsperado));
        // ACT AND ASSERT
        ImpuestoNoEncontradoException exception = assertThrows(
                ImpuestoNoEncontradoException.class,
                () -> servicioImpuestos.obtenerImpuesto(99)
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(repoImpuestosFalso).obtenerImpuesto(idInexistente);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionDeLectura(any());
    }

    @Test
    void deberiaActualizarUnImpuestoCorrectamente(){
        // ARRANGE
        String nombreNuevo = "Nombre Nuevo";
        BigDecimal porcentajeNuevo = new BigDecimal("25");
        when(repoImpuestosFalso.obtenerImpuesto(1)).thenReturn(impuestoPrueba);
        // ACT
        servicioImpuestos.actualizarImpuesto(1, nombreNuevo, porcentajeNuevo);
        // ASSERT
        ArgumentCaptor<Impuesto> captor = ArgumentCaptor.forClass(Impuesto.class);
        verify(repoImpuestosFalso).actualizarImpuesto(captor.capture());
        Impuesto impuestoCapturado = captor.getValue();
        assertEquals(1, impuestoCapturado.getId());
        assertEquals(nombreNuevo, impuestoCapturado.getNombre());
        assertEquals(0, impuestoCapturado.getPorcentaje().compareTo(porcentajeNuevo));
        assertTrue(impuestoCapturado.isActivo());
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
    }

    @Test
    void deberiaCambiarEstadoCorrectamente(){
        // ARRANGE
        when(repoImpuestosFalso.obtenerImpuesto(1)).thenReturn(impuestoPrueba);
        // ACT
        servicioImpuestos.cambiarEstadoImpuesto(1);
        // ASSERT
        assertFalse(impuestoPrueba.isActivo());
        verify(repoImpuestosFalso).actualizarImpuesto(impuestoPrueba);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccion(any());
    }

    @Test
    void deberiaLanzarExcepcionSiAlCambiarEstadoElImpuestoNoExiste(){
        // ARRANGE
        int idInexistente = 99;
        String mensajeEsperado = "NO existe un Impuesto con el ID: " + idInexistente;
        when(repoImpuestosFalso.obtenerImpuesto(idInexistente))
                .thenThrow(new ImpuestoNoEncontradoException(mensajeEsperado));
        // ACT AND ASSERT
        ImpuestoNoEncontradoException exception = assertThrows(
                ImpuestoNoEncontradoException.class,
                () ->
                    servicioImpuestos.cambiarEstadoImpuesto(idInexistente)
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(repoImpuestosFalso).obtenerImpuesto(idInexistente);
        verifyNoMoreInteractions(repoImpuestosFalso);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccion(any());
    }

    @Test
    void deberiaDevolverLaListaDeImpuestosActivosCorrectamente(){
        // ARRANGE
        List<Impuesto> listaEsperada = List.of(impuestoPrueba);
        when(repoImpuestosFalso.obtenerImpuestosActivos()).thenReturn(listaEsperada);
        // ACT
        List<Impuesto> listaRecibida = servicioImpuestos.obtenerImpuestosActivos();
        // ASSERT
        assertEquals(listaEsperada.size(), listaRecibida.size());
        assertEquals(listaEsperada, listaRecibida);
        verify(repoImpuestosFalso).obtenerImpuestosActivos();
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionDeLectura(any());

    }

    @Test
    void deberiaDevolverLaListaDeTodosLosImpuestosCorrectamente(){
        // ARRANGE
        List<Impuesto> listaEsperada = List.of(impuestoPrueba);
        when(repoImpuestosFalso.obtenerTodosLosImpuestos()).thenReturn(listaEsperada);
        // ACT
        List<Impuesto> listaRecibida = servicioImpuestos.obtenerTodosLosImpuestos();
        // ASSERT
        assertEquals(listaEsperada.size(), listaRecibida.size());
        assertEquals(listaEsperada, listaRecibida);
        verify(repoImpuestosFalso).obtenerTodosLosImpuestos();
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionDeLectura(any());
    }

}//===================================================================================================================//


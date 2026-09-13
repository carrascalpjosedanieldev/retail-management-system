package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.excepciones.recursosNoEncontrados.DescuentoNoEncontradoException;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioDescuentos;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ServicioDescuentosTest {

    @Mock
    private RepositorioDescuentos repoDescuentosFalso;

    @Mock
    private GestorTransaccional gestorTransaccionalFalso;

    @InjectMocks
    private ServicioDescuentos servicioDescuentos;

    private Descuento descuentoPrueba;

    private static final String NOMBRE_POR_DEFECTO = "Navidad";

    private static final BigDecimal PORCENTAJE_POR_DEFECTO = new BigDecimal("20");

    @BeforeEach
    void setUp() {
        descuentoPrueba = Descuento.reconstruirDesdeBD(1, NOMBRE_POR_DEFECTO, PORCENTAJE_POR_DEFECTO, true);
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
    void deberiaRegistrarUnDescuentoCorrectamente(){
        // ARRANGE
        when(repoDescuentosFalso.insertarDescuento(any(Descuento.class))).thenReturn(descuentoPrueba);
        // ACT
        Descuento resultado =
                servicioDescuentos.registrarDescuento(NOMBRE_POR_DEFECTO, PORCENTAJE_POR_DEFECTO, true);
        // ASSERT
        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        ArgumentCaptor<Descuento> captor = ArgumentCaptor.forClass(Descuento.class);
        verify(repoDescuentosFalso).insertarDescuento(captor.capture());
        Descuento descuentoCapturado = captor.getValue();
        assertEquals(NOMBRE_POR_DEFECTO, descuentoCapturado.getNombre());
        assertEquals(0, descuentoCapturado.getPorcentaje().compareTo(PORCENTAJE_POR_DEFECTO));
        assertTrue(descuentoCapturado.isActivo());
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
    }


    @Test
    void deberiaLanzarExcepcionCuandoObtenerDescuentoNoExiste(){
        // ARRANGE
        int idInexistente = 99;
        String mensajeEsperado = "NO existe un Descuento con el ID: " + idInexistente;
        when(repoDescuentosFalso.obtenerDescuento(idInexistente))
                .thenThrow(new DescuentoNoEncontradoException(mensajeEsperado));
        // ACT AND ASSERT
        DescuentoNoEncontradoException exception = assertThrows(
                DescuentoNoEncontradoException.class,
                () -> servicioDescuentos.obtenerDescuento(idInexistente)
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(repoDescuentosFalso).obtenerDescuento(idInexistente);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionDeLectura(any());
    }


    @Test
    void deberiaActualizarUnDescuentoCorrectamente(){
        // ARRANGE
        String nombreNuevo = "Nombre Nuevo";
        BigDecimal porcentajeNuevo = new BigDecimal("25");
        when(repoDescuentosFalso.obtenerDescuento(1)).thenReturn(descuentoPrueba);
        // ACT
        servicioDescuentos.actualizarDescuento(1, nombreNuevo, porcentajeNuevo);
        // ASSERT
        ArgumentCaptor<Descuento> captor = ArgumentCaptor.forClass(Descuento.class);
        verify(repoDescuentosFalso).actualizarDescuento(captor.capture());
        Descuento descuentoCapturado = captor.getValue();
        assertEquals(1, descuentoCapturado.getId());
        assertEquals(nombreNuevo, descuentoCapturado.getNombre());
        assertEquals(0, descuentoCapturado.getPorcentaje().compareTo(porcentajeNuevo));
        assertTrue(descuentoCapturado.isActivo());
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
    }


    @Test
    void deberiaCambiarEstadoCorrectamente(){
        // ARRANGE
        when(repoDescuentosFalso.obtenerDescuento(1)).thenReturn(descuentoPrueba);
        // ACT
        servicioDescuentos.cambiarEstadoDescuento(1);
        // ASSERT
        assertFalse(descuentoPrueba.isActivo());
        verify(repoDescuentosFalso).actualizarDescuento(descuentoPrueba);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccion(any());
    }


    @Test
    void deberiaLanzarExcepcionSiAlCambiarEstadoElDescuentoNoExiste(){
        // ARRANGE
        int idInexistente = 99;
        String mensajeEsperado = "NO existe un Descuento con el ID: " + idInexistente;
        when(repoDescuentosFalso.obtenerDescuento(idInexistente))
                .thenThrow(new DescuentoNoEncontradoException(mensajeEsperado));
        // ACT AND ASSERT
        assertThrows(
                DescuentoNoEncontradoException.class,
                () -> servicioDescuentos.cambiarEstadoDescuento(idInexistente)
        );
        verify(repoDescuentosFalso).obtenerDescuento(idInexistente);
        verifyNoMoreInteractions(repoDescuentosFalso);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccion(any());
    }


    @Test
    void deberiaDevolverLaListaDeDescuentosActivosCorrectamente(){
        // ARRANGE
        List<Descuento> listaEsperada = List.of(descuentoPrueba);
        when(repoDescuentosFalso.obtenerDescuentosActivos()).thenReturn(listaEsperada);
        // ACT
        List<Descuento> listaRecibida = servicioDescuentos.obtenerDescuentosActivos();
        // ASSERT
        assertEquals(listaEsperada.size(), listaRecibida.size());
        assertEquals(listaEsperada, listaRecibida);
        verify(repoDescuentosFalso).obtenerDescuentosActivos();
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionDeLectura(any());
    }


    @Test
    void deberiaDevolverLaListaDeTodosLosDescuentosCorrectamente(){
        // ARRANGE
        List<Descuento> listaEsperada = List.of(descuentoPrueba);
        when(repoDescuentosFalso.obtenerTodosLosDescuentos()).thenReturn(listaEsperada);
        // ACT
        List<Descuento> listaRecibida = servicioDescuentos.obtenerTodosLosDescuentos();
        // ASSERT
        assertEquals(listaEsperada.size(), listaRecibida.size());
        assertEquals(listaEsperada, listaRecibida);
        verify(repoDescuentosFalso).obtenerTodosLosDescuentos();
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionDeLectura(any());
    }


}//===================================================================================================================//


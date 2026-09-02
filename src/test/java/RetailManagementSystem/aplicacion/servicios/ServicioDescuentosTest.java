package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.excepciones.recursosNoEncontrados.DescuentoNoEncontradoException;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioDescuentos;

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

    @InjectMocks
    private ServicioDescuentos servicioDescuentos;

    private Descuento descuentoPrueba;

    private static final String NOMBRE_POR_DEFECTO = "Navidad";

    private static final BigDecimal PORCENTAJE_POR_DEFECTO = new BigDecimal("20");

    @BeforeEach
    void setUp() {
        descuentoPrueba = Descuento.reconstruirDesdeBD(1, NOMBRE_POR_DEFECTO, PORCENTAJE_POR_DEFECTO, true);
    }

    @Test
    void deberiaRegistrarUnDescuentoCorrectamente(){
        // ARRANGE
        when(repoDescuentosFalso.insertarDescuento(any(Descuento.class))).thenReturn(descuentoPrueba);
        // ACT
        Descuento resultado = servicioDescuentos.registrarDescuento(NOMBRE_POR_DEFECTO, PORCENTAJE_POR_DEFECTO, true);
        // ASSERT
        assertNotNull(resultado.getId());
        ArgumentCaptor<Descuento> captor = ArgumentCaptor.forClass(Descuento.class);
        verify(repoDescuentosFalso).insertarDescuento(captor.capture());
        Descuento descuentoCapturado = captor.getValue();
        assertNull(descuentoCapturado.getId());
        assertEquals(NOMBRE_POR_DEFECTO, descuentoCapturado.getNombre());
        assertEquals(0, descuentoCapturado.getPorcentaje().compareTo(PORCENTAJE_POR_DEFECTO));
        assertTrue(descuentoCapturado.isActivo());
    }

    @Test
    void deberiaLanzarExcepcionCuandoObtenerDescuentoNoExiste(){
        // ARRANGE
        when(repoDescuentosFalso.obtenerDescuento(99))
                .thenThrow(new DescuentoNoEncontradoException("NO existe un Descuento con el ID: 99"));
        // ACT AND ASSERT
        assertThrows(DescuentoNoEncontradoException.class, () -> {
            servicioDescuentos.obtenerDescuento(99);
        });
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
    }

    @Test
    void deberiaLanzarExcepcionSiAlCambiarEstadoElDescuentoNoExiste(){
        // ARRANGE
        when(repoDescuentosFalso.obtenerDescuento(99))
                .thenThrow(new DescuentoNoEncontradoException("NO existe un Descuento con el ID: 99"));
        // ACT AND ASSERT
        assertThrows(DescuentoNoEncontradoException.class, () -> {
            servicioDescuentos.cambiarEstadoDescuento(99);
        });
        verify(repoDescuentosFalso).obtenerDescuento(99);
        verifyNoMoreInteractions(repoDescuentosFalso);
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
    }

}


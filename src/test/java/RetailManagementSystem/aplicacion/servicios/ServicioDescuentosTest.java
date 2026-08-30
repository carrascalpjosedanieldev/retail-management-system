package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.excepciones.recursosNoEncontrados.DescuentoNoEncontradoException;
import RetailManagementSystem.dominio.puertos.RepositorioDescuentos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
        when(repoDescuentosFalso.insertarDescuento(any(Descuento.class))).thenReturn(descuentoPrueba);
        Descuento resultado = servicioDescuentos.registrarDescuento(NOMBRE_POR_DEFECTO, PORCENTAJE_POR_DEFECTO, true);
        assertNotNull(resultado.getId());
        assertEquals(NOMBRE_POR_DEFECTO, resultado.getNombre());
        assertEquals(0, resultado.getPorcentaje().compareTo(PORCENTAJE_POR_DEFECTO));
        assertTrue(resultado.isActivo());
        verify(repoDescuentosFalso).insertarDescuento(any(Descuento.class));
    }

    @Test
    void deberiaLanzarExcepcionCuandoObtenerDescuentoNoExiste(){
        when(repoDescuentosFalso.obtenerDescuento(99))
                .thenThrow(new DescuentoNoEncontradoException("NO existe un Descuento con el ID: 99"));
        assertThrows(DescuentoNoEncontradoException.class, () -> {
            servicioDescuentos.obtenerDescuento(99);
        });
    }

    @Test
    void deberiaActualizarUnDescuentoCorrectamente(){
        String nombreNuevo = "Nombre Nuevo";
        BigDecimal porcentajeNuevo = new BigDecimal("25");
        when(repoDescuentosFalso.obtenerDescuento(1)).thenReturn(descuentoPrueba);
        Descuento resultado = servicioDescuentos.actualizarDescuento(1, nombreNuevo, porcentajeNuevo);
        assertEquals(1, resultado.getId());
        assertEquals(nombreNuevo, resultado.getNombre());
        assertEquals(0, resultado.getPorcentaje().compareTo(porcentajeNuevo));
        verify(repoDescuentosFalso).actualizarDescuento(resultado);
    }

    @Test
    void deberiaCambiarEstadoCorrectamente(){
        when(repoDescuentosFalso.obtenerDescuento(1)).thenReturn(descuentoPrueba);
        servicioDescuentos.cambiarEstadoDescuento(1);
        assertFalse(descuentoPrueba.isActivo());
        verify(repoDescuentosFalso).actualizarDescuento(descuentoPrueba);
    }

    @Test
    void deberiaLanzarExcepcionSiAlCambiarEstadoElDescuentoNoExiste(){
        when(repoDescuentosFalso.obtenerDescuento(99))
                .thenThrow(new DescuentoNoEncontradoException("NO existe un Descuento con el ID: 99"));
        assertThrows(DescuentoNoEncontradoException.class, () -> {
            servicioDescuentos.cambiarEstadoDescuento(99);
        });
        verify(repoDescuentosFalso).obtenerDescuento(99);
        verifyNoMoreInteractions(repoDescuentosFalso);
    }

    @Test
    void deberiaDevolverLaListaDeDescuentosActivosCorrectamente(){
        List<Descuento> listaEsperada = List.of(descuentoPrueba);
        when(repoDescuentosFalso.obtenerDescuentosActivos()).thenReturn(listaEsperada);
        List<Descuento> listaRecibida = servicioDescuentos.obtenerDescuentosActivos();
        assertEquals(listaEsperada.size(), listaRecibida.size());
        assertEquals(listaEsperada, listaRecibida);
        verify(repoDescuentosFalso).obtenerDescuentosActivos();
    }

    @Test
    void deberiaDevolverLaListaDeTodosLosDescuentosCorrectamente(){
        List<Descuento> listaEsperada = List.of(descuentoPrueba);
        when(repoDescuentosFalso.obtenerTodosLosDescuentos()).thenReturn(listaEsperada);
        List<Descuento> listaRecibida = servicioDescuentos.obtenerTodosLosDescuentos();
        assertEquals(listaEsperada.size(), listaRecibida.size());
        assertEquals(listaEsperada, listaRecibida);
        verify(repoDescuentosFalso).obtenerTodosLosDescuentos();
    }

}


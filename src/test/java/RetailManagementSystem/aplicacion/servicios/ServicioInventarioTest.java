package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.dominio.entidades.gestion.Inventario;
import RetailManagementSystem.dominio.excepciones.recursosNoEncontrados.InventarioNoEncontradoException;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioInventario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServicioInventarioTest {

    @Mock
    private RepositorioInventario repoInventarioFalso;

    @InjectMocks
    private ServicioInventario servicioInventario;

    private Inventario inventarioPrueba;

    private static final String NOMBRE_POR_DEFECTO = "Inventario Estándar";
    private static final int CAPACIDAD_MAXIMA_POR_DEFECTO = 500;

    @BeforeEach
    void setUp(){
        inventarioPrueba = Inventario.reconstruirDesdeBD(
                1,
                NOMBRE_POR_DEFECTO,
                CAPACIDAD_MAXIMA_POR_DEFECTO,
                250
        );
    }

    @Test
    void deberiaRegistrarInventarioCorrectamente(){
        //ARRANGE
        when(repoInventarioFalso.insertarInventario(any(Inventario.class))).thenReturn(inventarioPrueba);
        //ACT
        Inventario resultado = servicioInventario.registrarInventario(NOMBRE_POR_DEFECTO, CAPACIDAD_MAXIMA_POR_DEFECTO);
        //ASSERT
        assertNotNull(resultado.getIdInventario());
        ArgumentCaptor<Inventario> captor = ArgumentCaptor.forClass(Inventario.class);
        verify(repoInventarioFalso).insertarInventario(captor.capture());
        Inventario inventarioCapturado = captor.getValue();
        assertNull(inventarioCapturado.getIdInventario());
        assertEquals(NOMBRE_POR_DEFECTO, inventarioCapturado.getNombre());
        assertEquals(CAPACIDAD_MAXIMA_POR_DEFECTO, inventarioCapturado.getCapacidadMaxima());
        assertEquals(0, inventarioCapturado.getCapacidadOcupada());
    }

    @Test
    void deberiaObtenerInventarioExistenteCorrectamente() {
        // ARRANGE
        when(repoInventarioFalso.obtenerInventario(1)).thenReturn(inventarioPrueba);
        // ACT
        Inventario resultado = servicioInventario.obtenerInventario(1);
        // ASSERT
        assertNotNull(resultado);
        assertEquals(1, resultado.getIdInventario());
        verify(repoInventarioFalso).obtenerInventario(1);
    }

    @Test
    void deberiaLanzarExcepcionCuandoObtenerInventarioNoExiste(){
        //ARRANGE
        when(repoInventarioFalso.obtenerInventario(99))
                .thenThrow(new InventarioNoEncontradoException("No existe un Inventario con el ID: 99"));
        //ACT AND ASSERT
        InventarioNoEncontradoException exception = assertThrows(
                InventarioNoEncontradoException.class,
                ()-> servicioInventario.obtenerInventario(99)
        );
        assertEquals("No existe un Inventario con el ID: 99", exception.getMessage());
    }

    @Test
    void deberiaActualizarInventarioCorrectamente(){
        //ARRANGE
        String nombreNuevo = "Modificado";
        when(repoInventarioFalso.obtenerInventario(1)).thenReturn(inventarioPrueba);
        //ACT
        servicioInventario.actualizarInventario(1, nombreNuevo);
        //ASSERT
        ArgumentCaptor<Inventario> captor = ArgumentCaptor.forClass(Inventario.class);
        verify(repoInventarioFalso).actualizarInventario(captor.capture());
        Inventario inventarioCapturado = captor.getValue();
        assertEquals(1, inventarioCapturado.getIdInventario());
        assertEquals(nombreNuevo, inventarioCapturado.getNombre());
    }

    @ParameterizedTest
    @CsvSource( value = {"null", "''", "'   '"} , nullValues = "null")
    void noDeberiaActualizarNiPersistirSiElNombreNuevoEsInvalido(String nombreInvalido) {
        // ARRANGE
        when(repoInventarioFalso.obtenerInventario(1)).thenReturn(inventarioPrueba);
        // ACT & ASSERT
        assertThrows(
                IllegalArgumentException.class,
                () -> servicioInventario.actualizarInventario(1, nombreInvalido)
        );
        verify(repoInventarioFalso, never()).actualizarInventario(any());
    }

    @Test
    void deberiaDevolverLaListaConTodosLosInventariosCorrectamente(){
        //ARRANGE
        List<Inventario> listaEsperada = List.of(inventarioPrueba);
        when(repoInventarioFalso.obtenerTodosInventariosConCapacidadOcupada()).thenReturn(listaEsperada);
        //ACT
        List<Inventario> listaRecibida = servicioInventario.obtenerTodosLosInventarios();
        //ASSERT
        assertEquals(listaEsperada, listaRecibida);
        assertEquals(listaEsperada.size(), listaRecibida.size());
        verify(repoInventarioFalso).obtenerTodosInventariosConCapacidadOcupada();
    }

    @Test
    void deberiaAumentarLaCapacidadMaximaDelInventarioCorrectamente(){
        //ARRANGE
        when(repoInventarioFalso.obtenerInventario(1)).thenReturn(inventarioPrueba);
        //ACT
        servicioInventario.aumentarCapacidadMaximaInventario(1, 50);
        //ASSERT
        ArgumentCaptor<Inventario> captor = ArgumentCaptor.forClass(Inventario.class);
        verify(repoInventarioFalso).actualizarInventario(captor.capture());
        Inventario inventarioCapturado = captor.getValue();
        assertEquals(1, inventarioCapturado.getIdInventario());
        assertEquals(550, inventarioCapturado.getCapacidadMaxima());
    }

    @Test
    void deberiaLanzarExcepcionYNoPersistirAlAumentarCapacidadDeInventarioInexistente() {
        // ARRANGE
        when(repoInventarioFalso.obtenerInventario(99))
                .thenThrow(new InventarioNoEncontradoException("No existe un Inventario con el ID: 99"));
        // ACT & ASSERT
        assertThrows(
                InventarioNoEncontradoException.class,
                () -> servicioInventario.aumentarCapacidadMaximaInventario(99, 50)
        );
        verify(repoInventarioFalso, never()).actualizarInventario(any());
    }

}//===================================================================================================================//


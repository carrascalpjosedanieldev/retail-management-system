package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.dominio.entidades.gestion.Inventario;
import RetailManagementSystem.dominio.excepciones.recursosNoEncontrados.InventarioNoEncontradoException;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioInventario;
import RetailManagementSystem.dominio.puertos.transacciones.GestorTransaccional;
import RetailManagementSystem.dominio.puertos.transacciones.OperacionTransaccional;
import RetailManagementSystem.dominio.puertos.transacciones.OperacionTransaccionalConRetorno;
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

    @Mock
    private GestorTransaccional gestorTransaccionalFalso;

    @InjectMocks
    private ServicioInventario servicioInventario;

    private Inventario inventarioPrueba;

    private static final int ID_POR_DEFECTO = 1;

    private static final String NOMBRE_POR_DEFECTO = "Inventario Estándar";

    private static final int CAPACIDAD_MAXIMA_POR_DEFECTO = 500;

    @BeforeEach
    void setUp(){
        inventarioPrueba = Inventario.reconstruirDesdeBD(
                ID_POR_DEFECTO,
                NOMBRE_POR_DEFECTO,
                CAPACIDAD_MAXIMA_POR_DEFECTO,
                250
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
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
        verifyNoMoreInteractions(repoInventarioFalso, gestorTransaccionalFalso);
    }

    @Test
    void deberiaActualizarInventarioCorrectamente(){
        //ARRANGE
        String nombreNuevo = "Modificado";
        when(repoInventarioFalso.obtenerInventario(ID_POR_DEFECTO)).thenReturn(inventarioPrueba);
        //ACT
        servicioInventario.actualizarInventario(ID_POR_DEFECTO, nombreNuevo);
        //ASSERT
        ArgumentCaptor<Inventario> captor = ArgumentCaptor.forClass(Inventario.class);
        verify(repoInventarioFalso).actualizarInventario(captor.capture());
        Inventario inventarioCapturado = captor.getValue();
        assertEquals(ID_POR_DEFECTO, inventarioCapturado.getIdInventario());
        assertEquals(nombreNuevo, inventarioCapturado.getNombre());
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
        verifyNoMoreInteractions(repoInventarioFalso, gestorTransaccionalFalso);
    }

    @Test
    void deberiaLanzarExcepcionSiAlActualizarInventarioNoExiste(){
        //ARRANGE
        int idInexistente = 99;
        String mensajeEsperado = "No existe un Inventario con el ID: " + idInexistente;
        when(repoInventarioFalso.obtenerInventario(idInexistente))
                .thenThrow(new InventarioNoEncontradoException(mensajeEsperado));
        //ACT AND ASSERT
        InventarioNoEncontradoException exception = assertThrows(
                InventarioNoEncontradoException.class,
                ()-> servicioInventario.actualizarInventario(idInexistente, "Modificado")
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(repoInventarioFalso, never()).actualizarInventario(any());
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
        verifyNoMoreInteractions(repoInventarioFalso, gestorTransaccionalFalso);
    }

    @ParameterizedTest
    @CsvSource( value = {"null", "''", "'   '"} , nullValues = "null")
    void noDeberiaActualizarNiPersistirSiElNombreNuevoEsInvalido(String nombreInvalido) {
        // ARRANGE
        when(repoInventarioFalso.obtenerInventario(ID_POR_DEFECTO)).thenReturn(inventarioPrueba);
        // ACT & ASSERT
        assertThrows(
                IllegalArgumentException.class,
                () -> servicioInventario.actualizarInventario(ID_POR_DEFECTO, nombreInvalido)
        );
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
        verifyNoMoreInteractions(repoInventarioFalso, gestorTransaccionalFalso);
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
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionDeLectura(any());
        verifyNoMoreInteractions(repoInventarioFalso, gestorTransaccionalFalso);
    }

    @Test
    void deberiaDevolverListaVaciaCuandoNoExistenInventarios() {
        //ARRANGE
        when(repoInventarioFalso.obtenerTodosInventariosConCapacidadOcupada()).thenReturn(List.of());
        //ACT
        List<Inventario> resultado = servicioInventario.obtenerTodosLosInventarios();
        //ASSERT
        assertTrue(resultado.isEmpty());
        verify(repoInventarioFalso).obtenerTodosInventariosConCapacidadOcupada();
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionDeLectura(any());
        verifyNoMoreInteractions(repoInventarioFalso, gestorTransaccionalFalso);
    }

    @Test
    void deberiaAumentarLaCapacidadMaximaDelInventarioCorrectamente(){
        //ARRANGE
        when(repoInventarioFalso.obtenerInventario(ID_POR_DEFECTO)).thenReturn(inventarioPrueba);
        //ACT
        servicioInventario.aumentarCapacidadMaximaInventario(ID_POR_DEFECTO, 50);
        //ASSERT
        ArgumentCaptor<Inventario> captor = ArgumentCaptor.forClass(Inventario.class);
        verify(repoInventarioFalso).actualizarInventario(captor.capture());
        Inventario inventarioCapturado = captor.getValue();
        assertEquals(ID_POR_DEFECTO, inventarioCapturado.getIdInventario());
        assertEquals(550, inventarioCapturado.getCapacidadMaxima());
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
        verifyNoMoreInteractions(repoInventarioFalso, gestorTransaccionalFalso);
    }

    @ParameterizedTest
    @CsvSource(value = {"0", "-5", "-100", "null"} , nullValues = "null")
    void noDeberiaPersistirSiLaCantidadAAumentarEsInvalida(Integer cantidadInvalida) {
        //ARRANGE
        when(repoInventarioFalso.obtenerInventario(ID_POR_DEFECTO)).thenReturn(inventarioPrueba);
        //ACT AND ASSERT
        assertThrows(
                IllegalArgumentException.class,
                () -> servicioInventario.aumentarCapacidadMaximaInventario(ID_POR_DEFECTO, cantidadInvalida)
        );
        verify(repoInventarioFalso, never()).actualizarInventario(any());
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
        verifyNoMoreInteractions(repoInventarioFalso, gestorTransaccionalFalso);
    }

    @Test
    void deberiaLanzarExcepcionYNoPersistirAlAumentarCapacidadDeInventarioInexistente() {
        // ARRANGE
        int idInexistente = 99;
        String mensajeEsperado = "No existe un Inventario con el ID: " + idInexistente;
        when(repoInventarioFalso.obtenerInventario(idInexistente))
                .thenThrow(new InventarioNoEncontradoException(mensajeEsperado));
        // ACT & ASSERT
        InventarioNoEncontradoException exception = assertThrows(
                InventarioNoEncontradoException.class,
                () -> servicioInventario.aumentarCapacidadMaximaInventario(99, 50)
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(repoInventarioFalso, never()).actualizarInventario(any());
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
        verifyNoMoreInteractions(repoInventarioFalso, gestorTransaccionalFalso);
    }

}//===================================================================================================================//


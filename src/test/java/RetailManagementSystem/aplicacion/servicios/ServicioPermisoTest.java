package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.aplicacion.servicios.seguridad.ServicioPermiso;
import RetailManagementSystem.dominio.entidades.seguridad.Permiso;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioPermiso;
import RetailManagementSystem.dominio.puertos.transacciones.GestorTransaccional;
import RetailManagementSystem.dominio.puertos.transacciones.OperacionTransaccional;
import RetailManagementSystem.dominio.puertos.transacciones.OperacionTransaccionalConRetorno;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServicioPermisoTest {

    private Permiso permisoPruebas;

    @Mock
    private RepositorioPermiso repositorioPermisoFalso;

    @Mock
    private GestorTransaccional gestorTransaccionalFalso;

    @InjectMocks
    private ServicioPermiso servicioPermiso;

    @BeforeEach
    void setUp(){
        permisoPruebas = Permiso.reconstruirDesdeBD(
                1, "Permiso", "Descripción", "Modulo", true
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

    //TESTS

    @Test
    void deberiaObtenerPermisosActivosCorrectamente() {
        // ARRANGE
        List<Permiso> permisosEsperados = List.of(permisoPruebas);
        when(repositorioPermisoFalso.obtenerPermisosActivos()).thenReturn(permisosEsperados);
        // ACT
        List<Permiso> resultado = servicioPermiso.obtenerPermisosActivos();
        // ASSERT
        assertEquals(permisosEsperados, resultado);
        assertEquals(1, resultado.size());
        verify(repositorioPermisoFalso).obtenerPermisosActivos();
        verifyNoMoreInteractions(repositorioPermisoFalso);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionDeLectura(any());
        verifyNoMoreInteractions(gestorTransaccionalFalso);
    }

    @Test
    void deberiaObtenerTodosLosPermisosCorrectamente() {
        // ARRANGE
        List<Permiso> permisosEsperados = List.of(permisoPruebas);
        when(repositorioPermisoFalso.obtenerTodosLosPermisos()).thenReturn(permisosEsperados);
        // ACT
        List<Permiso> resultado = servicioPermiso.obtenerTodosLosPermisos();
        // ASSERT
        assertEquals(permisosEsperados, resultado);
        assertEquals(1, resultado.size());
        verify(repositorioPermisoFalso).obtenerTodosLosPermisos();
        verifyNoMoreInteractions(repositorioPermisoFalso);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionDeLectura(any());
        verifyNoMoreInteractions(gestorTransaccionalFalso);
    }

    @Test
    void deberiaObtenerTodosLosNombresPermisosCorrectamente() {
        // ARRANGE
        List<String> nombresEsperados = List.of("Permiso 1", "Permiso 2");
        when(repositorioPermisoFalso.obtenerNombresTodosLosPermisos()).thenReturn(nombresEsperados);
        // ACT
        List<String> resultado = servicioPermiso.obtenerTodosLosNombresPermisos();
        // ASSERT
        assertEquals(nombresEsperados, resultado);
        assertEquals(2, resultado.size());
        verify(repositorioPermisoFalso).obtenerNombresTodosLosPermisos();
        verifyNoMoreInteractions(repositorioPermisoFalso);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionDeLectura(any());
        verifyNoMoreInteractions(gestorTransaccionalFalso);
    }

    @Test
    void deberiaActualizarPermisoCorrectamente() {
        // ARRANGE
        int idPermiso = 1;
        String nuevaDescripcion = "Esta es la nueva descripción";
        when(repositorioPermisoFalso.obtenerPermisoPorId(idPermiso)).thenReturn(permisoPruebas);
        // ACT
        Permiso resultado = servicioPermiso.actualizarPermiso(idPermiso, nuevaDescripcion);
        // ASSERT
        assertEquals(nuevaDescripcion, resultado.getDescripcion());
        verify(repositorioPermisoFalso).obtenerPermisoPorId(idPermiso);
        verify(repositorioPermisoFalso).actualizarPermiso(permisoPruebas);
        verifyNoMoreInteractions(repositorioPermisoFalso);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
        verifyNoMoreInteractions(gestorTransaccionalFalso);
    }

    @Test
    void deberiaCambiarEstadoPermisoCorrectamente() {
        // ARRANGE
        int idPermiso = 1;
        when(repositorioPermisoFalso.obtenerPermisoPorId(idPermiso)).thenReturn(permisoPruebas);
        boolean estadoOriginal = permisoPruebas.isActivo();
        // ACT
        servicioPermiso.cambiarEstadoPermiso(idPermiso);
        // ASSERT
        assertNotEquals(estadoOriginal, permisoPruebas.isActivo());
        assertFalse(permisoPruebas.isActivo());
        verify(repositorioPermisoFalso).obtenerPermisoPorId(idPermiso);
        verify(repositorioPermisoFalso).actualizarPermiso(permisoPruebas);
        verifyNoMoreInteractions(repositorioPermisoFalso);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccion(any());
        verifyNoMoreInteractions(gestorTransaccionalFalso);
    }

}//===================================================================================================================//


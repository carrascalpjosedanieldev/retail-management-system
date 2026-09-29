package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.dominio.entidades.seguridad.Permiso;
import RetailManagementSystem.dominio.entidades.seguridad.Rol;
import RetailManagementSystem.dominio.excepciones.recursosNoEncontrados.RolNoEncontradoException;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioRol;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public class ServicioRolTest {

    private final Permiso permisoPruebas = Permiso.reconstruirDesdeBD(
            1, "Permiso", "Descripción", "Modulo", true
    );

    private Rol rolPruebas;

    @Mock
    private RepositorioRol repositorioRolFalso;

    @Mock
    private GestorTransaccional gestorTransaccionalFalso;

    @InjectMocks
    private ServicioRol servicioRol;

    @BeforeEach
    void setUp(){
        rolPruebas = Rol.reconstruirDesdeBD(1, "Rol", true);
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
    void deberiaRegistrarRolCorrectamente(){
        //ARRANGE
        String nombreRol = "   Rol Nuevo   ";
        boolean activo = true;
        List<Permiso> permisos = List.of(permisoPruebas);
        //ACT
        servicioRol.registrarRol(nombreRol, activo, permisos);
        //ASSERT
        ArgumentCaptor<Rol> rolCapturador = ArgumentCaptor.forClass(Rol.class);
        verify(repositorioRolFalso).insertarRol(rolCapturador.capture());
        Rol rolGuardado = rolCapturador.getValue();
        assertNull(rolGuardado.getIdRol());
        assertEquals("Rol Nuevo", rolGuardado.getNombre());
        assertEquals(activo, rolGuardado.isActivo());
        assertEquals(permisos, rolGuardado.getPermisos().stream().toList());
        verify(gestorTransaccionalFalso).ejecutarEnTransaccion(any());
        verifyNoMoreInteractions(repositorioRolFalso, gestorTransaccionalFalso);
    }

    @Test
    void deberiaRegistrarRolCorrectamenteAunqueLaListaPermisosEsteVacia(){
        //ARRANGE
        String nombreRol = "   Rol Nuevo   ";
        boolean activo = true;
        List<Permiso> permisos = List.of();
        //ACT
        servicioRol.registrarRol(nombreRol, activo, permisos);
        //ASSERT
        ArgumentCaptor<Rol> rolCapturador = ArgumentCaptor.forClass(Rol.class);
        verify(repositorioRolFalso).insertarRol(rolCapturador.capture());
        Rol rolGuardado = rolCapturador.getValue();
        assertNull(rolGuardado.getIdRol());
        assertEquals("Rol Nuevo", rolGuardado.getNombre());
        assertEquals(activo, rolGuardado.isActivo());
        assertEquals(permisos, rolGuardado.getPermisos().stream().toList());
        verify(gestorTransaccionalFalso).ejecutarEnTransaccion(any());
        verifyNoMoreInteractions(repositorioRolFalso, gestorTransaccionalFalso);
    }

    @Test
    void deberiaActualizarDatosRolCorrectamente(){
        //ARRANGE
        int idRol = 1;
        String nombreNuevo = "   Nuevo   ";
        boolean activo = false;
        when(repositorioRolFalso.obtenerRol(idRol)).thenReturn(rolPruebas);
        //ACT
        Rol resultado = servicioRol.actualzarDatosRol(idRol, nombreNuevo, activo);
        //ASSERT
        assertEquals("Nuevo", resultado.getNombre());
        assertEquals(activo, resultado.isActivo());
        verify(repositorioRolFalso).obtenerRol(idRol);
        verify(repositorioRolFalso).actualizarDatosRol(rolPruebas);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
        verifyNoMoreInteractions(repositorioRolFalso, gestorTransaccionalFalso);
    }

    @Test
    void deberiaLanzarExcepcionSiElRolNoExisteAlActualizarDatosRol(){
        //ARRANGE
        int idRol = 1;
        String nombreNuevo = "   Nuevo   ";
        boolean activo = false;
        String mensajeEsperado = "El Rol de ID -" + idRol + "- NO Existe.";
        when(repositorioRolFalso.obtenerRol(idRol)).thenThrow(new RolNoEncontradoException(mensajeEsperado));
        //ACT AND ASSERT
        RolNoEncontradoException exception = assertThrows(
                RolNoEncontradoException.class,
                ()-> servicioRol.actualzarDatosRol(idRol, nombreNuevo, activo)
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(repositorioRolFalso).obtenerRol(idRol);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
        verifyNoMoreInteractions(repositorioRolFalso, gestorTransaccionalFalso);
    }

    @Test
    void deberiaActualizarPermisosDelRolCorrectamente(){
        //ARRANGE
        int idRol = 1;
        List<Permiso> permisosNuevos = List.of(permisoPruebas);
        when(repositorioRolFalso.obtenerRol(idRol)).thenReturn(rolPruebas);
        //ACT
        servicioRol.actualizarPermisosRol(idRol, permisosNuevos);
        //ASSERT
        assertEquals(permisosNuevos, rolPruebas.getPermisos().stream().toList());
        verify(gestorTransaccionalFalso).ejecutarEnTransaccion(any());
        verify(repositorioRolFalso).obtenerRol(idRol);
        verify(repositorioRolFalso).actualizarPermisosRol(rolPruebas);
        verifyNoMoreInteractions(repositorioRolFalso, gestorTransaccionalFalso);
    }

    @Test
    void deberiaRemoverTodosLosPermisosDelRolSiLaListaDePermisosEstaVaciaAlActualizarPermisosRol(){
        //ARRANGE
        int idRol = 1;
        List<Permiso> permisosNuevos = List.of();
        when(repositorioRolFalso.obtenerRol(idRol)).thenReturn(rolPruebas);
        //ACT
        servicioRol.actualizarPermisosRol(idRol, permisosNuevos);
        //ASSERT
        assertEquals(permisosNuevos, rolPruebas.getPermisos().stream().toList());
        verify(repositorioRolFalso).obtenerRol(idRol);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccion(any());
        verify(repositorioRolFalso).actualizarPermisosRol(rolPruebas);
        verifyNoMoreInteractions(repositorioRolFalso, gestorTransaccionalFalso);
    }

    @Test
    void deberiaLanzarExcepcionCuandoRolNoExisteAlActualizarPermisosRol(){
        //ARRANGE
        int idRol = 1;
        List<Permiso> permisosNuevos = List.of(permisoPruebas);
        String mensajeEsperado = "El Rol de ID -" + idRol + "- NO Existe.";
        when(repositorioRolFalso.obtenerRol(idRol)).thenThrow(new RolNoEncontradoException(mensajeEsperado));
        //ACT AND ASSERT
        RolNoEncontradoException exception = assertThrows(
                RolNoEncontradoException.class,
                ()-> servicioRol.actualizarPermisosRol(idRol, permisosNuevos)
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(repositorioRolFalso).obtenerRol(idRol);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccion(any());
        verifyNoMoreInteractions(repositorioRolFalso, gestorTransaccionalFalso);
    }

    @Test
    void deberiaObtenerTodosLosRolesCorrectamente(){
        // ARRANGE
        List<Rol> rolesEsperados = List.of(rolPruebas);
        when(repositorioRolFalso.obtenerTodosLosRoles()).thenReturn(rolesEsperados);
        // ACT
        List<Rol> resultado = servicioRol.obtenerTodosLosRoles();
        // ASSERT
        assertEquals(rolesEsperados, resultado);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionDeLectura(any());
        verify(repositorioRolFalso).obtenerTodosLosRoles();
        verifyNoMoreInteractions(repositorioRolFalso, gestorTransaccionalFalso);
    }

}//===================================================================================================================//


package RetailManagementSystem.infraestructura.seguridad;

import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.dominio.excepciones.autenticacionYSeguridad.AccesoDenegadoException;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ValidadorSeguridadTest {

    @Test
    void deberiaLanzarExcepcionCuandoElUsuarioEsNuloAlExigirPermiso() {
        //ACT AND ASSERT
        AccesoDenegadoException excepcion = assertThrows(
                AccesoDenegadoException.class,
                () -> ValidadorSeguridad.exigirPermiso(null, "CREAR_PRODUCTO")
        );
        assertEquals("NO hay una Sesión de Usuario Activa.", excepcion.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionCuandoElUsuarioNoTieneElPermisoRequeridoAlExigirPermiso() {
        //ARRANGE
        UsuarioDTOCompleto usuarioFalso = mock(UsuarioDTOCompleto.class);
        when(usuarioFalso.tienePermiso("ELIMINAR_VENTA")).thenReturn(false);
        //ACT AND ASSERT
        AccesoDenegadoException excepcion = assertThrows(
                AccesoDenegadoException.class,
                () -> ValidadorSeguridad.exigirPermiso(usuarioFalso, "eliminar_venta")
        );
        assertEquals(
                "Acceso Denegado: Se Requiere el Permiso [eliminar_venta] Para esta Acción.",
                excepcion.getMessage()
        );
        verify(usuarioFalso).tienePermiso("ELIMINAR_VENTA");
    }

    @Test
    void deberiaPasarCuandoElUsuarioTieneElPermisoAlExigirPermiso() {
        //ARRANGE
        UsuarioDTOCompleto usuarioFalso = mock(UsuarioDTOCompleto.class);
        when(usuarioFalso.tienePermiso("CREAR_PRODUCTO")).thenReturn(true);
        //ACT AND ASSERT
        assertDoesNotThrow(
                () -> ValidadorSeguridad.exigirPermiso(usuarioFalso, "crear_producto")
        );
        verify(usuarioFalso).tienePermiso("CREAR_PRODUCTO");
    }

    @Test
    void deberiaLanzarExcepcionCuandoElUsuarioEsNuloAlExigirAlgunPermiso() {
        //ARRANGE
        List<String> permisos = List.of("VER_REPORTES", "EXPORTAR_REPORTES");
        //ACT AND ASSERT
        AccesoDenegadoException excepcion = assertThrows(
                AccesoDenegadoException.class,
                () -> ValidadorSeguridad.exigirAlgunPermiso(null, permisos)
        );
        assertEquals("NO hay una Sesión de Usuario Activa.", excepcion.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionCuandoElUsuarioNoTieneNingunoDeLosPermisosAlExigirAlgunPermiso() {
        //ARRANGE
        UsuarioDTOCompleto usuarioFalso = mock(UsuarioDTOCompleto.class);
        List<String> permisosRequeridos = List.of("ver_reportes", "exportar_reportes");
        when(usuarioFalso.tienePermiso("VER_REPORTES")).thenReturn(false);
        when(usuarioFalso.tienePermiso("EXPORTAR_REPORTES")).thenReturn(false);
        //ACT AND ASSERT
        AccesoDenegadoException excepcion = assertThrows(
                AccesoDenegadoException.class,
                () -> ValidadorSeguridad.exigirAlgunPermiso(usuarioFalso, permisosRequeridos)
        );
        assertEquals("Acceso Denegado: NO tienes los Privilegios Necesarios.", excepcion.getMessage());
        verify(usuarioFalso).tienePermiso("VER_REPORTES");
        verify(usuarioFalso).tienePermiso("EXPORTAR_REPORTES");
    }

    @Test
    void deberiaLanzarExcepcionCuandoListaDePermisosEstaVaciaAlExigirAlgunPermiso() {
        //ARRANGE
        UsuarioDTOCompleto usuarioFalso = mock(UsuarioDTOCompleto.class);
        List<String> permisosVacios = Collections.emptyList();
        //ACT AND ASSERT
        AccesoDenegadoException excepcion = assertThrows(
                AccesoDenegadoException.class,
                () -> ValidadorSeguridad.exigirAlgunPermiso(usuarioFalso, permisosVacios)
        );
        assertEquals("Acceso Denegado: NO tienes los Privilegios Necesarios.", excepcion.getMessage());
        verify(usuarioFalso, never()).tienePermiso(anyString());
    }

    @Test
    void deberiaPasarCuandoElUsuarioTieneAlMenosUnPermisoAlExigirAlgunPermiso() {
        //ARRANGE
        UsuarioDTOCompleto usuarioFalso = mock(UsuarioDTOCompleto.class);
        List<String> permisosRequeridos = List.of("EDITAR_PRODUCTO", "CREAR_PRODUCTO", "ELIMINAR_PRODUCTO");
        when(usuarioFalso.tienePermiso("EDITAR_PRODUCTO")).thenReturn(false);
        when(usuarioFalso.tienePermiso("CREAR_PRODUCTO")).thenReturn(true);
        //ACT AND ASSERT
        assertDoesNotThrow(
                () -> ValidadorSeguridad.exigirAlgunPermiso(usuarioFalso, permisosRequeridos)
        );
        verify(usuarioFalso).tienePermiso("EDITAR_PRODUCTO");
        verify(usuarioFalso).tienePermiso("CREAR_PRODUCTO");
        verify(usuarioFalso, never()).tienePermiso("ELIMINAR_PRODUCTO");
    }

}//===================================================================================================================//


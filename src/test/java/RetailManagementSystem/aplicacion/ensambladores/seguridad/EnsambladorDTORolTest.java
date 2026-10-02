package RetailManagementSystem.aplicacion.ensambladores.seguridad;

import RetailManagementSystem.aplicacion.dto.seguridad.PermisoDTO;
import RetailManagementSystem.aplicacion.dto.seguridad.RolDTO;
import RetailManagementSystem.dominio.entidades.seguridad.Permiso;
import RetailManagementSystem.dominio.entidades.seguridad.Rol;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EnsambladorDTORolTest {

    @Mock
    private EnsambladorDTOPermiso ensambladorDTOPermisoFalso;

    @InjectMocks
    private EnsambladorDTORol ensambladorDTORol;

    @Captor
    private ArgumentCaptor<List<Permiso>> listaPermisosCaptor;

    //TESTS

    @Test
    void deberiaEnsamblarDatosRolCorrectamente() {
        //ARRANGE
        Rol rol = Rol.reconstruirDesdeBD(1, "ADMINISTRADOR", true);
        Permiso permiso = Permiso.reconstruirDesdeBD(
                10, "GESTION_PRODUCTOS", "Permite gestionar productos",
                "Gestion", true
        );
        rol.anadirPermiso(permiso);
        PermisoDTO dtoPermisoEsperado = new PermisoDTO(
                10, "GESTION_PRODUCTOS", "Permite gestionar productos",
                "Gestion", true
        );
        List<PermisoDTO> listaDtoPermisos = List.of(dtoPermisoEsperado);
        when(ensambladorDTOPermisoFalso.ensamblarDetallePermisos(anyList())).thenReturn(listaDtoPermisos);
        //ACT
        RolDTO recordResultado = ensambladorDTORol.ensamblarDatosRol(rol);
        //ASSERT
        assertNotNull(recordResultado);
        assertEquals(1, recordResultado.idRol());
        assertEquals("ADMINISTRADOR", recordResultado.nombre());
        assertTrue(recordResultado.activo());
        assertEquals(1, recordResultado.permisos().size());
        assertEquals(dtoPermisoEsperado, recordResultado.permisos().getFirst());
        verify(ensambladorDTOPermisoFalso).ensamblarDetallePermisos(listaPermisosCaptor.capture());
        List<Permiso> permisosCapturados = listaPermisosCaptor.getValue();
        assertEquals(1, permisosCapturados.size());
        assertTrue(permisosCapturados.contains(permiso));
        verifyNoMoreInteractions(ensambladorDTOPermisoFalso);
    }

    @Test
    void deberiaEnsamblarDetalleRolesCorrectamente() {
        //ARRANGE
        Rol rolAdmin = Rol.reconstruirDesdeBD(1, "ADMINISTRADOR", true);
        Rol rolCajero = Rol.reconstruirDesdeBD(2, "CAJERO", true);
        List<Rol> listaRoles = List.of(rolAdmin, rolCajero);
        PermisoDTO dtoPermiso1 = new PermisoDTO(
                10, "CREAR_VENTA", "Permite crear ventas", "Ventas", true
        );
        PermisoDTO dtoPermiso2 = new PermisoDTO(
                20, "ANULAR_VENTA", "Permite anular ventas", "Ventas", true
        );
        when(ensambladorDTOPermisoFalso.ensamblarDetallePermisos(anyList()))
                .thenReturn(List.of(dtoPermiso1))
                .thenReturn(List.of(dtoPermiso2));
        //ACT
        List<RolDTO> resultado = ensambladorDTORol.ensamblarDetalleRoles(listaRoles);
        //ASSERT
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(1, resultado.getFirst().idRol());
        assertEquals("ADMINISTRADOR", resultado.getFirst().nombre());
        assertEquals(1, resultado.get(0).permisos().size());
        assertEquals(dtoPermiso1, resultado.get(0).permisos().getFirst());
        assertEquals(2, resultado.get(1).idRol());
        assertEquals("CAJERO", resultado.get(1).nombre());
        assertEquals(1, resultado.get(1).permisos().size());
        assertEquals(dtoPermiso2, resultado.get(1).permisos().getFirst());
        verify(ensambladorDTOPermisoFalso, times(2)).ensamblarDetallePermisos(anyList());
        verifyNoMoreInteractions(ensambladorDTOPermisoFalso);
    }

}//===================================================================================================================//


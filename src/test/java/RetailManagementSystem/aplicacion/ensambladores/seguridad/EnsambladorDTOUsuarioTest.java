package RetailManagementSystem.aplicacion.ensambladores.seguridad;

import RetailManagementSystem.aplicacion.dto.seguridad.ResultadoRegistroDTO;
import RetailManagementSystem.aplicacion.dto.seguridad.RolDTO;
import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOBasico;
import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.dominio.entidades.seguridad.Permiso;
import RetailManagementSystem.dominio.entidades.seguridad.Rol;
import RetailManagementSystem.dominio.entidades.seguridad.Usuario;

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
public class EnsambladorDTOUsuarioTest {

    @Mock
    private EnsambladorDTORol ensambladorDTORolFalso;

    @InjectMocks
    private EnsambladorDTOUsuario ensambladorDTOUsuario;

    @Captor
    private ArgumentCaptor<List<Rol>> listaRolesCaptor;

    //TESTS

    @Test
    void deberiaEnsamblarDTOUsuarioCompletoCorrectamente() {
        // ARRANGE
        Usuario usuario = Usuario.reconstruirDesdeBD(
                1L,
                "Juan",
                "Pérez",
                "juan.perez@email.com",
                0,
                null,
                "$2a$10$hashEjemplo123",
                true,
                false
        );
        Rol rol = Rol.reconstruirDesdeBD(10, "ADMINISTRADOR", true);
        Permiso permiso = Permiso.reconstruirDesdeBD(
                100, "GESTION_USUARIOS", "Permite gestionar usuarios",
                "Seguridad", true
        );
        rol.anadirPermiso(permiso);
        usuario.anadirRol(rol);
        RolDTO dtoRolEsperado = mock(RolDTO.class);
        List<RolDTO> listaDtoRoles = List.of(dtoRolEsperado);
        when(ensambladorDTORolFalso.ensamblarDetalleRoles(anyList())).thenReturn(listaDtoRoles);
        //ACT
        UsuarioDTOCompleto resultado = ensambladorDTOUsuario.ensamblarDTOUsuarioCompleto(usuario);
        //ASSERT
        assertNotNull(resultado);
        assertEquals(1L, resultado.idUsuario());
        assertEquals("Juan", resultado.nombre());
        assertEquals("Pérez", resultado.apellido());
        assertEquals("Juan Pérez", resultado.getNombreCompleto());
        assertEquals("juan.perez@email.com", resultado.email());
        assertTrue(resultado.activo());
        assertFalse(resultado.debeCambiarContrasena());
        assertEquals(1, resultado.roles().size());
        assertSame(dtoRolEsperado, resultado.roles().getFirst());
        assertEquals(1, resultado.permisos().size());
        assertTrue(resultado.permisos().contains("GESTION_USUARIOS"));
        verify(ensambladorDTORolFalso).ensamblarDetalleRoles(listaRolesCaptor.capture());
        List<Rol> rolesCapturados = listaRolesCaptor.getValue();
        assertEquals(1, rolesCapturados.size());
        assertTrue(rolesCapturados.contains(rol));
        verifyNoMoreInteractions(ensambladorDTORolFalso);
    }

    @Test
    void deberiaEnsamblarDTOUsuarioBasicoCorrectamente() {
        //ARRANGE
        Usuario usuario = Usuario.reconstruirDesdeBD(
                2L,
                "María",
                "Gómez",
                "maria.gomez@email.com",
                0,
                null,
                "$2a$10$hashEjemplo456",
                true,
                true
        );
        //ACT
        UsuarioDTOBasico resultado = ensambladorDTOUsuario.ensamblarDTOUsuarioBasico(usuario);
        //ASSERT
        assertNotNull(resultado);
        assertEquals(2L, resultado.idUsuario());
        assertEquals("María", resultado.nombre());
        assertEquals("Gómez", resultado.apellido());
        assertEquals("María Gómez", resultado.getNombreCompleto());
        assertEquals("maria.gomez@email.com", resultado.email());
        assertTrue(resultado.activo());
        assertTrue(resultado.debeCambiarContrasena());
        verifyNoInteractions(ensambladorDTORolFalso);
    }

    @Test
    void deberiaEnsamblarDTOResultadoRegistroCorrectamente() {
        //ARRANGE
        UsuarioDTOBasico dtoBasico = new UsuarioDTOBasico(
                3L, "Carlos", "López", "carlos.lopez@email.com",
                true, true
        );
        char[] claveTemporal = "ClaveTemp123!".toCharArray();
        //ACT
        ResultadoRegistroDTO resultado =
                ensambladorDTOUsuario.ensamblarDTOResultadoRegistro(dtoBasico, claveTemporal);
        //ASSERT
        assertNotNull(resultado);
        assertSame(dtoBasico, resultado.usuario());
        assertArrayEquals(claveTemporal, resultado.claveTemporal());
        verifyNoInteractions(ensambladorDTORolFalso);
    }

    @Test
    void deberiaEnsamblarDetalleUsuariosCorrectamente() {
        //ARRANGE
        Usuario usuario1 = Usuario.reconstruirDesdeBD(
                1L,
                "Juan",
                "Pérez",
                "juan.perez@email.com",
                0,
                null,
                "$2a$10$hash1",
                true,
                false
        );
        Usuario usuario2 = Usuario.reconstruirDesdeBD(
                2L,
                "María",
                "Gómez",
                "maria.gomez@email.com",
                0,
                null,
                "$2a$10$hash2",
                true,
                true
        );
        List<Usuario> listaUsuarios = List.of(usuario1, usuario2);
        //ACT
        List<UsuarioDTOBasico> resultado = ensambladorDTOUsuario.ensamblarDetalleUsuarios(listaUsuarios);
        //ASSERT
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(1L, resultado.getFirst().idUsuario());
        assertEquals("Juan Pérez", resultado.get(0).getNombreCompleto());
        assertFalse(resultado.get(0).debeCambiarContrasena());
        assertEquals(2L, resultado.get(1).idUsuario());
        assertEquals("María Gómez", resultado.get(1).getNombreCompleto());
        assertTrue(resultado.get(1).debeCambiarContrasena());
        verifyNoInteractions(ensambladorDTORolFalso);
    }

}//===================================================================================================================//


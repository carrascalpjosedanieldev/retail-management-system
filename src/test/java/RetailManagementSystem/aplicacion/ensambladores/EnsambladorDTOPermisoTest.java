package RetailManagementSystem.aplicacion.ensambladores;

import RetailManagementSystem.aplicacion.dto.seguridad.PermisoDTO;
import RetailManagementSystem.dominio.entidades.seguridad.Permiso;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EnsambladorDTOPermisoTest {

    private EnsambladorDTOPermiso ensamblador;

    @BeforeEach
    void setUp() {
        this.ensamblador = new EnsambladorDTOPermiso();
    }

    //TESTS

    @Test
    void deberiaEnsamblarDatosPermisoCorrectamente() {
        //ARRANGE
        Permiso permiso = Permiso.reconstruirDesdeBD(
                1,
                "crear_usuario",
                "Permite la creación de usuarios en el sistema",
                "USUARIOS",
                true
        );
        //ACT
        PermisoDTO dto = ensamblador.ensamblarDatosPermiso(permiso);
        //ASSERT
        assertNotNull(dto);
        assertEquals(1, dto.idPermiso());
        assertEquals("CREAR_USUARIO", dto.nombre());
        assertEquals("Permite la creación de usuarios en el sistema", dto.descripcion());
        assertEquals("USUARIOS", dto.modulo());
        assertTrue(dto.activo());
    }

    @Test
    void deberiaEnsamblarDetallePermisosCorrectamente() {
        //ARRANGE
        Permiso permiso1 = Permiso.reconstruirDesdeBD(
                1,
                "crear_producto",
                "Permite crear productos",
                "INVENTARIO",
                true
        );
        Permiso permiso2 = Permiso.reconstruirDesdeBD(
                2,
                "eliminar_producto",
                "Permite eliminar productos",
                "INVENTARIO",
                false
        );
        List<Permiso> permisos = List.of(permiso1, permiso2);
        //ACT
        List<PermisoDTO> resultado = ensamblador.ensamblarDetallePermisos(permisos);
        //ASSERT
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(1, resultado.getFirst().idPermiso());
        assertEquals("CREAR_PRODUCTO", resultado.getFirst().nombre());
        assertEquals("Permite crear productos", resultado.getFirst().descripcion());
        assertEquals("INVENTARIO", resultado.get(0).modulo());
        assertTrue(resultado.get(0).activo());
        assertEquals(2, resultado.get(1).idPermiso());
        assertEquals("ELIMINAR_PRODUCTO", resultado.get(1).nombre());
        assertEquals("Permite eliminar productos", resultado.get(1).descripcion());
        assertEquals("INVENTARIO", resultado.get(1).modulo());
        assertFalse(resultado.get(1).activo());
    }

    @Test
    void deberiaRetornarListaVaciaCuandoLaListaDePermisosEstaVacia() {
        //ARRANGE
        List<Permiso> listaVacia = Collections.emptyList();
        //ACT
        List<PermisoDTO> resultado = ensamblador.ensamblarDetallePermisos(listaVacia);
        //ASSERT
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

}//===================================================================================================================//


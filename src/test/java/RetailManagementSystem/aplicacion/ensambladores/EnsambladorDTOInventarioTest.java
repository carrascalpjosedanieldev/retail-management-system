package RetailManagementSystem.aplicacion.ensambladores;

import RetailManagementSystem.aplicacion.dto.gestion.InventarioDTO;
import RetailManagementSystem.dominio.entidades.gestion.Inventario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EnsambladorDTOInventarioTest {

    private EnsambladorDTOInventario ensamblador;

    @BeforeEach
    void setUp() {
        ensamblador = new EnsambladorDTOInventario();
    }

    //TESTS

    @Test
    void deberiaEnsamblarDatosInventarioCorrectamente() {
        //ARRANGE
        Inventario inventario = Inventario.reconstruirDesdeBD(
                1, "Bodega Principal", 1000, 400
        );
        //ACT
        InventarioDTO dto = ensamblador.ensamblarDatosInventario(inventario);
        //ASSERT
        assertNotNull(dto);
        assertEquals(1, dto.idInventario());
        assertEquals("Bodega Principal", dto.nombre());
        assertEquals(1000, dto.capacidadMaxima());
        assertEquals(400, dto.capacidadOcupada());
        assertEquals(600, dto.capacidadLibre());
    }

    @Test
    void deberiaEnsamblarDetalleInventarioGeneralCorrectamente() {
        //ARRANGE
        Inventario inventario1 = Inventario.reconstruirDesdeBD(
                1, "Bodega Norte", 500, 500
        );
        Inventario inventario2 = Inventario.reconstruirDesdeBD(
                2, "Bodega Sur", 800, 200
        );
        List<Inventario> inventarios = List.of(inventario1, inventario2);
        //ACT
        List<InventarioDTO> resultado = ensamblador.ensamblarDetalleInventarioGeneral(inventarios);
        //ASSERT
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(1, resultado.getFirst().idInventario());
        assertEquals("Bodega Norte", resultado.getFirst().nombre());
        assertEquals(500, resultado.getFirst().capacidadMaxima());
        assertEquals(500, resultado.get(0).capacidadOcupada());
        assertEquals(0, resultado.get(0).capacidadLibre());
        assertEquals(2, resultado.get(1).idInventario());
        assertEquals("Bodega Sur", resultado.get(1).nombre());
        assertEquals(800, resultado.get(1).capacidadMaxima());
        assertEquals(200, resultado.get(1).capacidadOcupada());
        assertEquals(600, resultado.get(1).capacidadLibre());
    }

    @Test
    void deberiaRetornarListaVaciaCuandoRecibeListaDeInventariosVacia() {
        //ARRANGE
        List<Inventario> listaVacia = Collections.emptyList();
        //ACT
        List<InventarioDTO> resultado = ensamblador.ensamblarDetalleInventarioGeneral(listaVacia);
        //ASSERT
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

}//===================================================================================================================//


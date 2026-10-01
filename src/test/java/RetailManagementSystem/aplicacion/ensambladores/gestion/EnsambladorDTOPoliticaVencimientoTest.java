package RetailManagementSystem.aplicacion.ensambladores.gestion;

import RetailManagementSystem.aplicacion.dto.gestion.PoliticaVencimientoDTO;
import RetailManagementSystem.dominio.entidades.gestion.PoliticaVencimiento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EnsambladorDTOPoliticaVencimientoTest {

    private EnsambladorDTOPoliticaVencimiento ensamblador;

    @BeforeEach
    void setUp() {
        this.ensamblador = new EnsambladorDTOPoliticaVencimiento();
    }

    //TESTS

    @Test
    void deberiaEnsamblarDatosPoliticaVencimientoCorrectamente() {
        //ARRANGE
        BigDecimal porcentajeOriginal = new BigDecimal("35.555000");
        PoliticaVencimiento politica = PoliticaVencimiento.reconstruirDesdeBD(
                1,
                "Lácteos Próximos a Vencer",
                5,
                porcentajeOriginal,
                true
        );
        //ACT
        PoliticaVencimientoDTO dto = ensamblador.ensamblarDatosPoliticaVencimiento(politica);
        //ASSERT
        assertNotNull(dto);
        assertEquals(1, dto.idPoliticaVencimiento());
        assertEquals("Lácteos Próximos a Vencer", dto.nombrePolitica());
        assertEquals(5, dto.diasUmbral());
        assertEquals(new BigDecimal("35.56"), dto.porcentajeDescuento());
        assertTrue(dto.activo());
    }

    @Test
    void deberiaEnsamblarDetallePoliticasVencimientoCorrectamente() {
        //ARRANGE
        PoliticaVencimiento politica1 = PoliticaVencimiento.reconstruirDesdeBD(
                1, "Descuento Lácteos 5 Días", 5, new BigDecimal("30.00"), true
        );
        PoliticaVencimiento politica2 = PoliticaVencimiento.reconstruirDesdeBD(
                2, "Descuento Carnes 2 Días", 2, new BigDecimal("50.00"), false
        );
        List<PoliticaVencimiento> politicas = List.of(politica1, politica2);
        //ACT
        List<PoliticaVencimientoDTO> resultado = ensamblador.ensamblarDetallePoliticasVencimiento(politicas);
        //ASSERT
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(1, resultado.getFirst().idPoliticaVencimiento());
        assertEquals("Descuento Lácteos 5 Días", resultado.getFirst().nombrePolitica());
        assertEquals(5, resultado.getFirst().diasUmbral());
        assertEquals(new BigDecimal("30.00"), resultado.get(0).porcentajeDescuento());
        assertTrue(resultado.get(0).activo());
        assertEquals(2, resultado.get(1).idPoliticaVencimiento());
        assertEquals("Descuento Carnes 2 Días", resultado.get(1).nombrePolitica());
        assertEquals(2, resultado.get(1).diasUmbral());
        assertEquals(new BigDecimal("50.00"), resultado.get(1).porcentajeDescuento());
        assertFalse(resultado.get(1).activo());
    }

    @Test
    void deberiaRetornarListaVaciaCuandoLaListaDePoliticasEstaVacia() {
        //ARRANGE
        List<PoliticaVencimiento> listaVacia = Collections.emptyList();
        //ACT
        List<PoliticaVencimientoDTO> resultado = ensamblador.ensamblarDetallePoliticasVencimiento(listaVacia);
        //ASSERT
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

}//===================================================================================================================//


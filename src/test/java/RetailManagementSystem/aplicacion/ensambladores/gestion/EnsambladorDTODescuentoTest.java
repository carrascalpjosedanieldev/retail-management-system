package RetailManagementSystem.aplicacion.ensambladores.gestion;

import RetailManagementSystem.aplicacion.dto.gestion.DescuentoDTO;
import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EnsambladorDTODescuentoTest {

    private EnsambladorDTODescuento ensamblador;

    @BeforeEach
    void setUp() {
        this.ensamblador = new EnsambladorDTODescuento();
    }

    @Test
    void deberiaEnsamblarDatosDescuentoCorrectamenteConRedondeoADosDecimales() {
        // ARRANGE
        Descuento descuento = Descuento.reconstruirDesdeBD(
                1, "Black Friday", new BigDecimal("19.5555"), true
        );
        BigDecimal porcentajeEsperadoDTO = new BigDecimal("19.56");
        // ACT
        DescuentoDTO dto = ensamblador.ensamblarDatosDescuento(descuento);
        // ASSERT
        assertNotNull(dto);
        assertEquals(1, dto.idDescuento());
        assertEquals("Black Friday", dto.nombre());
        assertEquals(porcentajeEsperadoDTO, dto.porcentaje());
        assertTrue(dto.activo());
    }

    @Test
    void deberiaEnsamblarDetalleDescuentosCorrectamente() {
        // ARRANGE
        Descuento descuento1 = Descuento.reconstruirDesdeBD(
                1, "Promo Verano", new BigDecimal("10"), true
        );
        Descuento descuento2 = Descuento.reconstruirDesdeBD(
                2, "Promo Invierno", new BigDecimal("15.25"), false
        );
        List<Descuento> listaDescuentos = List.of(descuento1, descuento2);
        // ACT
        List<DescuentoDTO> resultado = ensamblador.ensamblarDetalleDescuentos(listaDescuentos);
        // ASSERT
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(1, resultado.getFirst().idDescuento());
        assertEquals("Promo Verano", resultado.getFirst().nombre());
        assertEquals(new BigDecimal("10.00"), resultado.get(0).porcentaje());
        assertTrue(resultado.get(0).activo());
        assertEquals(2, resultado.get(1).idDescuento());
        assertEquals("Promo Invierno", resultado.get(1).nombre());
        assertEquals(new BigDecimal("15.25"), resultado.get(1).porcentaje());
        assertFalse(resultado.get(1).activo());
    }

    @Test
    void deberiaRetornarListaVaciaCuandoLaListaDeDescuentosEstaVacia() {
        // ARRANGE
        List<Descuento> listaVacia = Collections.emptyList();
        // ACT
        List<DescuentoDTO> resultado = ensamblador.ensamblarDetalleDescuentos(listaVacia);
        // ASSERT
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

}//===================================================================================================================//


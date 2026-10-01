package RetailManagementSystem.aplicacion.ensambladores;

import RetailManagementSystem.aplicacion.dto.gestion.ImpuestoDTO;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EnsambladorDTOImpuestoTest {

    private EnsambladorDTOImpuesto ensamblador;

    @BeforeEach
    void setUp() {
        ensamblador = new EnsambladorDTOImpuesto();
    }

    //TESTS

    @Test
    void ensamblarDatosImpuesto_RetornaDTO_CuandoImpuestoEsValido() {
        //ARRANGE
        BigDecimal porcentajeOriginal = new BigDecimal("19.555");
        Impuesto impuesto = Impuesto.reconstruirDesdeBD(1, "IVA", porcentajeOriginal, true);
        //ACT
        ImpuestoDTO dto = ensamblador.ensamblarDatosImpuesto(impuesto);
        //ASSERT
        assertNotNull(dto);
        assertEquals(1, dto.idImpuesto());
        assertEquals("IVA", dto.nombre());
        assertEquals(new BigDecimal("19.56"), dto.porcentaje());
        assertTrue(dto.activo());
    }

    @Test
    void ensamblarDetalleImpuestos_RetornaListaDTOs_CuandoRecibeListaImpuestos() {
        //ARRANGE
        Impuesto impuesto1 = Impuesto.reconstruirDesdeBD(
                1, "IVA", new BigDecimal("19.00"), true
        );
        Impuesto impuesto2 = Impuesto.reconstruirDesdeBD(
                2, "ReteICA", new BigDecimal("9.66"), false
        );
        List<Impuesto> impuestos = List.of(impuesto1, impuesto2);
        //ACT
        List<ImpuestoDTO> listaFinal = ensamblador.ensamblarDetalleImpuestos(impuestos);
        //ASSERT
        assertNotNull(listaFinal);
        assertEquals(2, listaFinal.size());
        assertEquals(1, listaFinal.getFirst().idImpuesto());
        assertEquals("IVA", listaFinal.getFirst().nombre());
        assertEquals(new BigDecimal("19.00"), listaFinal.get(0).porcentaje());
        assertTrue(listaFinal.get(0).activo());
        assertEquals(2, listaFinal.get(1).idImpuesto());
        assertEquals("ReteICA", listaFinal.get(1).nombre());
        assertEquals(new BigDecimal("9.66"), listaFinal.get(1).porcentaje());
        assertFalse(listaFinal.get(1).activo());
    }

    @Test
    void ensamblarDetalleImpuestos_RetornaListaVacia_CuandoRecibeListaVacia() {
        //ARRANGE
        List<Impuesto> impuestosVacia = List.of();
        //ACT
        List<ImpuestoDTO> listaFinal = ensamblador.ensamblarDetalleImpuestos(impuestosVacia);
        //ASSERT
        assertNotNull(listaFinal);
        assertTrue(listaFinal.isEmpty());
    }

}//===================================================================================================================//


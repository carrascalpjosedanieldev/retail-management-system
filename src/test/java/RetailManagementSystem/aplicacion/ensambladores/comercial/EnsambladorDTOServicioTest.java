package RetailManagementSystem.aplicacion.ensambladores.comercial;

import RetailManagementSystem.aplicacion.dto.comercial.ServicioDTO;
import RetailManagementSystem.aplicacion.dto.gestion.DescuentoDTO;
import RetailManagementSystem.aplicacion.dto.gestion.ImpuestoDTO;
import RetailManagementSystem.aplicacion.ensambladores.gestion.EnsambladorDTODescuento;
import RetailManagementSystem.aplicacion.ensambladores.gestion.EnsambladorDTOImpuesto;
import RetailManagementSystem.dominio.entidades.comercial.Servicio;
import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.dominio.financiero.calculos.CalculadoraPrecios;
import RetailManagementSystem.dominio.financiero.calculos.ContextoEvaluacion;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EnsambladorDTOServicioTest {

    @Mock
    private CalculadoraPrecios calculadoraPreciosFalso;

    @Mock
    private EnsambladorDTOImpuesto ensambladorDTOImpuestoFalso;

    @Mock
    private EnsambladorDTODescuento ensambladorDTODescuentoFalso;

    @InjectMocks
    private EnsambladorDTOServicio ensambladorDTOServicio;

    //TESTS

    @Test
    void deberiaEnsamblarServicioCorrectamente() {
        //ARRANGE
        Impuesto impuesto = Impuesto.reconstruirDesdeBD(
                1, "IVA", new BigDecimal("19"), true
        );
        Descuento descuento = Descuento.reconstruirDesdeBD(
                1, "Cliente Frecuente", new BigDecimal("10"), true
        );
        Servicio servicio = Servicio.reconstruirDesdeBD(
                "SERV-001",
                "Mantenimiento Preventivo",
                new BigDecimal("150000"),
                impuesto,
                descuento,
                true
        );
        ContextoEvaluacion contexto = ContextoEvaluacion.crearNuevo(LocalDate.now());
        ImpuestoDTO dtoImpuestoEsperado =
                new ImpuestoDTO(1, "IVA", new BigDecimal("19"), true);
        DescuentoDTO dtoDescuentoEsperado =
                new DescuentoDTO(1, "Cliente Frecuente", new BigDecimal("10"), true);
        BigDecimal precioFinalEsperado = new BigDecimal("160500");
        when(ensambladorDTOImpuestoFalso.ensamblarDatosImpuesto(impuesto)).thenReturn(dtoImpuestoEsperado);
        when(ensambladorDTODescuentoFalso.ensamblarDatosDescuento(descuento)).thenReturn(dtoDescuentoEsperado);
        when(calculadoraPreciosFalso.calcularValorVenta(servicio, contexto)).thenReturn(precioFinalEsperado);
        //ACT
        ServicioDTO resultado = ensambladorDTOServicio.ensamblarServicio(servicio, contexto);
        //ASSERT
        assertNotNull(resultado);
        assertEquals("SERV-001", resultado.codigo());
        assertEquals("Mantenimiento Preventivo", resultado.nombre());
        assertEquals(new BigDecimal("150000.000000"), resultado.precioBase());
        assertEquals(precioFinalEsperado, resultado.precioFinal());
        assertTrue(resultado.activo());
        assertEquals(dtoImpuestoEsperado, resultado.datosImpuesto());
        assertEquals(dtoDescuentoEsperado, resultado.datosDescuento());
        verify(ensambladorDTOImpuestoFalso).ensamblarDatosImpuesto(impuesto);
        verify(ensambladorDTODescuentoFalso).ensamblarDatosDescuento(descuento);
        verify(calculadoraPreciosFalso).calcularValorVenta(servicio, contexto);
    }

    @Test
    void deberiaEnsamblarDatosCatalogoServiciosCorrectamente() {
        //ARRANGE
        Impuesto impuesto = Impuesto.reconstruirDesdeBD(
                1, "IVA", new BigDecimal("19"), true
        );
        Descuento descuento = Descuento.reconstruirDesdeBD(
                1, "Sin Descuento", BigDecimal.ZERO, true
        );
        Servicio servicio1 = Servicio.reconstruirDesdeBD(
                "SERV-001",
                "Instalación",
                new BigDecimal("50000"),
                impuesto,
                descuento,
                true
        );
        Servicio servicio2 = Servicio.reconstruirDesdeBD(
                "SERV-002",
                "Soporte Técnico",
                new BigDecimal("80000"),
                impuesto,
                descuento,
                true
        );
        List<Servicio> listaServicios = List.of(servicio1, servicio2);
        ContextoEvaluacion contexto = ContextoEvaluacion.crearNuevo(LocalDate.now());
        ImpuestoDTO dtoImpuestoEsperado =
                new ImpuestoDTO(1, "IVA", new BigDecimal("19"), true);
        DescuentoDTO dtoDescuentoEsperado =
                new DescuentoDTO(1, "Sin Descuento", BigDecimal.ZERO, true);
        when(ensambladorDTOImpuestoFalso.ensamblarDatosImpuesto(impuesto)).thenReturn(dtoImpuestoEsperado);
        when(ensambladorDTODescuentoFalso.ensamblarDatosDescuento(descuento)).thenReturn(dtoDescuentoEsperado);
        when(calculadoraPreciosFalso.calcularValorVenta(servicio1, contexto)).thenReturn(new BigDecimal("59500"));
        when(calculadoraPreciosFalso.calcularValorVenta(servicio2, contexto)).thenReturn(new BigDecimal("95200"));
        //ACT
        List<ServicioDTO> resultado =
                ensambladorDTOServicio.ensamblarDatosCatalogoServicios(listaServicios, contexto);
        //ASSERT
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("SERV-001", resultado.get(0).codigo());
        assertEquals(new BigDecimal("59500"), resultado.get(0).precioFinal());
        assertEquals("SERV-002", resultado.get(1).codigo());
        assertEquals(new BigDecimal("95200"), resultado.get(1).precioFinal());
        verify(ensambladorDTOImpuestoFalso, times(2)).ensamblarDatosImpuesto(impuesto);
        verify(ensambladorDTODescuentoFalso, times(2)).ensamblarDatosDescuento(descuento);
        verify(calculadoraPreciosFalso).calcularValorVenta(servicio1, contexto);
        verify(calculadoraPreciosFalso).calcularValorVenta(servicio2, contexto);
    }

}//===================================================================================================================//


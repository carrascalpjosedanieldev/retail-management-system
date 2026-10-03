package RetailManagementSystem.aplicacion.fabricas;

import RetailManagementSystem.aplicacion.dto.creacion.DatosGeneralesCreacionProductoDTO;
import RetailManagementSystem.aplicacion.dto.creacion.DetalleRopaDTO;
import RetailManagementSystem.dominio.entidades.comercial.ProductoRopa;
import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.dominio.enums.Talla;
import RetailManagementSystem.dominio.enums.TipoProducto;
import RetailManagementSystem.dominio.financiero.calculos.ContextoEvaluacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EstrategiaFabricarRopaTest {

    private EstrategiaFabricarRopa estrategia;

    private final Impuesto impuestoPruebas = Impuesto.reconstruirDesdeBD(
            1, "IVA", new BigDecimal("19"), true
    );

    private final Descuento descuentoPruebas = Descuento.reconstruirDesdeBD(
            1, "Descuento", new BigDecimal("10"), true
    );

    @BeforeEach
    void setUp() {
        estrategia = new EstrategiaFabricarRopa();
    }

    @Test
    void deberiaRetornarProductoRopaCreadoConLosDatosProporcionadosAlFabricarProducto() {
        //ARRANGE
        String nombreEsperado = "Camiseta Básica";
        BigDecimal valorCompraEsperado = new BigDecimal("25000");
        BigDecimal gananciaEsperada = new BigDecimal("40");
        int stockEsperado = 100;
        Talla tallaEsperada = Talla.M;
        DatosGeneralesCreacionProductoDTO datosGenerales = new DatosGeneralesCreacionProductoDTO(
                TipoProducto.ROPA, nombreEsperado, valorCompraEsperado, gananciaEsperada,
                stockEsperado, 1, 1
        );
        DetalleRopaDTO detalle = new DetalleRopaDTO(tallaEsperada);
        ContextoEvaluacion contexto = ContextoEvaluacion.crearNuevo(LocalDate.now());
        //ACT
        ProductoRopa resultado = estrategia.fabricarProducto(
                datosGenerales, detalle, contexto, impuestoPruebas, descuentoPruebas
        );
        //ASSERT
        assertNotNull(resultado, "El producto fabricado NO debe ser nulo");
        assertEquals(nombreEsperado, resultado.getNombre());
        assertEquals(0, valorCompraEsperado.compareTo(resultado.getValorCompra()));
        assertEquals(0, gananciaEsperada.compareTo(resultado.getPorcentajeGanancia()));
        assertEquals(stockEsperado, resultado.getStock());
        assertEquals(impuestoPruebas, resultado.getImpuesto());
        assertEquals(descuentoPruebas, resultado.getDescuento());
        assertEquals(tallaEsperada, resultado.getTalla());
    }

}//===================================================================================================================//


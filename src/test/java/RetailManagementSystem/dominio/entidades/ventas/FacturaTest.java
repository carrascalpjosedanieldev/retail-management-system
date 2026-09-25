package RetailManagementSystem.dominio.entidades.ventas;

import RetailManagementSystem.dominio.enums.TipoItem;
import RetailManagementSystem.dominio.excepciones.reglasDeNegocio.FacturaSinItemsException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FacturaTest {

    private static final String NUMERO_FACTURA_POR_DEFECTO = "FAC-2026-001";

    private static final LocalDateTime FECHA_EMISION_POR_DEFECTO =
            LocalDateTime.of(2026, 9, 25, 14, 30);

    private ItemVendido crearItemReal(
            String codigo, String nombre, int cantidad, String precioUnitario, String impuesto
    ) {
        return ItemVendido.crearNuevo(
                TipoItem.PRODUCTO,
                codigo,
                nombre,
                cantidad,
                new BigDecimal(precioUnitario),
                new BigDecimal(impuesto)
        );
    }

    //TESTS

    @Test
    void deberiaReconstruirDesdeBDCorrectamente() {
        // ARRANGE
        ItemVendido itemReal =
                crearItemReal("PROD-001", "Producto", 1, "119", "19");
        List<ItemVendido> items = List.of(itemReal);
        Integer idFactura = 100;
        BigDecimal totalGeneral = new BigDecimal("119.000000");
        BigDecimal totalImpuestos = new BigDecimal("19.000000");
        BigDecimal subTotal = new BigDecimal("100.000000");
        // ACT
        Factura factura = Factura.reconstruirDesdeBD(
                items,
                idFactura,
                NUMERO_FACTURA_POR_DEFECTO,
                FECHA_EMISION_POR_DEFECTO,
                totalGeneral,
                totalImpuestos,
                subTotal
        );
        // ASSERT
        assertEquals(idFactura, factura.getIdFactura());
        assertEquals(NUMERO_FACTURA_POR_DEFECTO, factura.getNumeroFactura());
        assertEquals(FECHA_EMISION_POR_DEFECTO, factura.getFechaHoraEmision());
        assertEquals(totalGeneral, factura.getTotalGeneral());
        assertEquals(totalImpuestos, factura.getTotalImpuestos());
        assertEquals(subTotal, factura.getSubTotal());
        assertEquals(1, factura.getItemsFinales().size());
        assertEquals(itemReal, factura.getItemsFinales().getFirst());
    }

    @Test
    void deberiaLanzarExcepcionSiLaListaDeItemsEsNulaAlReconstruirOCrear() {
        // ACT & ASSERT
        assertThrows(
                NullPointerException.class,
                () -> Factura.reconstruirDesdeBD(
                        null,
                        1,
                        NUMERO_FACTURA_POR_DEFECTO,
                        FECHA_EMISION_POR_DEFECTO,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO
                )
        );
    }

    @Test
    void deberiaCrearNuevaCorrectamenteYCalcularTotalesSumandoItemsReales() {
        // ARRANGE
        ItemVendido item1 =
                crearItemReal("PROD-001", "Camisa", 1, "119", "19");
        ItemVendido item2 =
                crearItemReal("PROD-002", "Gorra", 3, "15.55", "8.5");
        List<ItemVendido> itemsFacturar = List.of(item1, item2);
        BigDecimal subtotalEsperado = new BigDecimal("142.995392");
        BigDecimal impuestosEsperado = new BigDecimal("22.654608");
        BigDecimal totalGeneralEsperado = new BigDecimal("165.650000");
        // ACT
        Factura factura = Factura.crearNueva(itemsFacturar, NUMERO_FACTURA_POR_DEFECTO, FECHA_EMISION_POR_DEFECTO);
        // ASSERT
        assertNull(factura.getIdFactura());
        assertEquals(NUMERO_FACTURA_POR_DEFECTO, factura.getNumeroFactura());
        assertEquals(FECHA_EMISION_POR_DEFECTO, factura.getFechaHoraEmision());
        assertEquals(subtotalEsperado, factura.getSubTotal());
        assertEquals(impuestosEsperado, factura.getTotalImpuestos());
        assertEquals(totalGeneralEsperado, factura.getTotalGeneral());
    }

    @Test
    void deberiaLanzarExcepcionSiLaListaDeItemsFinalesEsNula() {
        // ACT AND ASSERT
        FacturaSinItemsException exception = assertThrows(
                FacturaSinItemsException.class,
                ()-> Factura.crearNueva(null, NUMERO_FACTURA_POR_DEFECTO, FECHA_EMISION_POR_DEFECTO)
        );
        assertEquals("NO se Puede Emitir una Factura sin Items Vendidos.", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiLaListaDeItemsFinalesEstaVacia(){
        //ACT AND ASSER
        FacturaSinItemsException exception = assertThrows(
                FacturaSinItemsException.class,
                ()-> Factura.crearNueva(List.of(), NUMERO_FACTURA_POR_DEFECTO, FECHA_EMISION_POR_DEFECTO)
        );
        assertEquals("NO se Puede Emitir una Factura sin Items Vendidos.", exception.getMessage());
    }

}//===================================================================================================================//


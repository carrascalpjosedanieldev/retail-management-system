package RetailManagementSystem.aplicacion.ensambladores.ventas;

import RetailManagementSystem.aplicacion.dto.ventas.FacturaDTO;
import RetailManagementSystem.aplicacion.dto.ventas.ItemVendidoFacturaDTO;
import RetailManagementSystem.dominio.entidades.ventas.Factura;
import RetailManagementSystem.dominio.entidades.ventas.ItemVendido;
import RetailManagementSystem.dominio.enums.TipoItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EnsambladorDTOFacturaTest {

    private EnsambladorDTOFactura ensamblador;

    @BeforeEach
    void setUp() {
        this.ensamblador = new EnsambladorDTOFactura();
    }

    //TESTS

    @Test
    void deberiaEnsamblarFacturaCorrectamenteConItems() {
        //ARRANGE
        LocalDateTime fechaEmision = LocalDateTime.of(2026, 10, 1, 10, 30);
        ItemVendido item1 = ItemVendido.crearNuevo(
                TipoItem.PRODUCTO,
                "PROD-001",
                "Camisa Manga Larga",
                2,
                new BigDecimal("50000"),
                new BigDecimal("19")
        );
        ItemVendido item2 = ItemVendido.crearNuevo(
                TipoItem.SERVICIO,
                "SERV-001",
                "Mantenimiento General",
                1,
                new BigDecimal("30000"),
                new BigDecimal("0")
        );
        Factura factura = Factura.crearNueva(
                List.of(item1, item2),
                "FACT-2026-0001",
                fechaEmision
        );
        //ACT
        FacturaDTO dto = ensamblador.ensamblarFactura(factura);
        //ASSERT
        assertNotNull(dto);
        assertEquals(factura.getNumeroFactura(), dto.numeroFactura());
        assertEquals(factura.getFechaHoraEmision(), dto.fechaEmision());
        assertEquals(factura.getSubTotal(), dto.subTotal());
        assertEquals(factura.getTotalImpuestos(), dto.totalImpuestos());
        assertEquals(factura.getTotalGeneral(), dto.totalGeneral());
        List<ItemVendidoFacturaDTO> itemsDTO = dto.listaItemsFinales();
        assertNotNull(itemsDTO);
        assertEquals(2, itemsDTO.size());
        ItemVendidoFacturaDTO dtoItem1 = itemsDTO.getFirst();
        assertEquals(item1.getTipoItem(), dtoItem1.tipoItem());
        assertEquals(item1.getCodigo(), dtoItem1.codigoReferencia());
        assertEquals(item1.getNombre(), dtoItem1.nombreItem());
        assertEquals(item1.getCantidad(), dtoItem1.cantidad());
        assertEquals(item1.getPrecioUnitario(), dtoItem1.precioUnitario());
        assertEquals(item1.getSubtotalNeto(), dtoItem1.subTotalNeto());
        assertEquals(item1.getPorcentajeImpuesto(), dtoItem1.porcentajeImpuestos());
        assertEquals(item1.getMontoImpuesto(), dtoItem1.montoImpuestos());
        assertEquals(item1.getTotalLinea(), dtoItem1.totalLinea());
        ItemVendidoFacturaDTO dtoItem2 = itemsDTO.get(1);
        assertEquals(item2.getTipoItem(), dtoItem2.tipoItem());
        assertEquals(item2.getCodigo(), dtoItem2.codigoReferencia());
        assertEquals(item2.getNombre(), dtoItem2.nombreItem());
        assertEquals(item2.getCantidad(), dtoItem2.cantidad());
        assertEquals(item2.getPrecioUnitario(), dtoItem2.precioUnitario());
        assertEquals(item2.getSubtotalNeto(), dtoItem2.subTotalNeto());
        assertEquals(item2.getPorcentajeImpuesto(), dtoItem2.porcentajeImpuestos());
        assertEquals(item2.getMontoImpuesto(), dtoItem2.montoImpuestos());
        assertEquals(item2.getTotalLinea(), dtoItem2.totalLinea());
    }

    @Test
    void deberiaEnsamblarFacturaReconstruidaSinItemsCorrectamente() {
        //ARRANGE
        LocalDateTime fechaEmision = LocalDateTime.of(2026, 10, 1, 11, 0);
        Factura facturaSinItems = Factura.reconstruirDesdeBD(
                Collections.emptyList(),
                100,
                "FACT-2026-0000",
                fechaEmision,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
        //ACT
        FacturaDTO dto = ensamblador.ensamblarFactura(facturaSinItems);
        //ASSERT
        assertNotNull(dto);
        assertEquals("FACT-2026-0000", dto.numeroFactura());
        assertEquals(fechaEmision, dto.fechaEmision());
        assertEquals(new BigDecimal("0.000000"), dto.subTotal());
        assertEquals(new BigDecimal("0.000000"), dto.totalImpuestos());
        assertEquals(new BigDecimal("0.000000"), dto.totalGeneral());
        assertNotNull(dto.listaItemsFinales());
        assertTrue(dto.listaItemsFinales().isEmpty());
    }

}//===================================================================================================================//


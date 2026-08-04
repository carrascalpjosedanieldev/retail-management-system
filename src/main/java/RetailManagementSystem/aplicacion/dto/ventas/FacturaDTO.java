package RetailManagementSystem.aplicacion.dto.ventas;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record FacturaDTO(String numeroFactura, List<ItemVendidoFacturaDTO> listaItemsFinales, LocalDateTime fechaEmision,
                         BigDecimal subTotal, BigDecimal totalImpuestos, BigDecimal totalGeneral) { }


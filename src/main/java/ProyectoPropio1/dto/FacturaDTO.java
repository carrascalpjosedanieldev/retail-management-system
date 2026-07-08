package ProyectoPropio1.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record FacturaDTO(String numeroFactura, List<DatosItemVendidoFacturaDTO> listaItemsFinales, LocalDateTime fechaEmision,
                         BigDecimal subTotal, BigDecimal totalImpuestos, BigDecimal totalGeneral) {
}


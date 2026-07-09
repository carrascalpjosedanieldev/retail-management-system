package ProyectoPropio1.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReporteRecaudoDTO(LocalDate fechaInicio, LocalDate fechaFin, int cantidadFacturasEmitidas,
                                BigDecimal subTotal, BigDecimal totalImpuestos, BigDecimal totalRecaudo) {
}


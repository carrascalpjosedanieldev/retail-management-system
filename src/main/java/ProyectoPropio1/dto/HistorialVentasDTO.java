package ProyectoPropio1.dto;

import java.math.BigDecimal;
import java.util.List;

public record HistorialVentasDTO(List<FacturaDTO> facturasRegistradas, BigDecimal recaudoTotal) {
}


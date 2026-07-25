package ProyectoPropio1.aplicacion.dto;

import java.math.BigDecimal;
import java.util.List;

public record VistaPreviaCarritoDTO(List<ItemCarritoDTO> carritoItems, BigDecimal totalAproximado) {
}


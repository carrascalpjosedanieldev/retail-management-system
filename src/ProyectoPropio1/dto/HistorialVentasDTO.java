package ProyectoPropio1.dto;

import java.util.List;

public record HistorialVentasDTO(List<FacturaDTO> facturasRegistradas, double recaudoTotal) {
}


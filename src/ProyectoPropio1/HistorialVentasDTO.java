package ProyectoPropio1;

import java.util.List;

public record HistorialVentasDTO(List<FacturaDTO> facturasRegistradas, double recaudoTotal) {
}


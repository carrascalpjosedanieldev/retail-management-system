package ProyectoPropio1.dto;

import java.util.List;

public record VistaPreviaCarritoDTO(List<ItemCarritoDTO> items, List<DatosServicioDTO> servicios, double totalAproximado) {
}


package ProyectoPropio1;

import java.util.List;

public record VistaPreviaCarritoDTO(List<ItemCarritoDTO> items,List<DatosServicioDTO> servicios, double totalAproximado) {
}


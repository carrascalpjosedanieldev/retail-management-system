package ProyectoPropio1.dto;

import java.util.List;

public record DetalleInventarioDTO(int id, String nombre, int capacidadMaxima, int capacidadOcupada, List<DatosTotalesProductoDTO> productos) {
}


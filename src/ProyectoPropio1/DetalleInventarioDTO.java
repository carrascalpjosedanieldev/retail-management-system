package ProyectoPropio1;

import java.util.List;

public record DetalleInventarioDTO(int id, String nombre, int capacidadMaxima, int capacidadOcupada, List<DatosTotalesProductoDTO> productos) {
}


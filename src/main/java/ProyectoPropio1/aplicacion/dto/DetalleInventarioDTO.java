package ProyectoPropio1.aplicacion.dto;

import java.util.List;

public record DetalleInventarioDTO(Integer id, String nombre, int capacidadMaxima, int capacidadOcupada, List<DatosTotalesProductoDTO> productos) {
}


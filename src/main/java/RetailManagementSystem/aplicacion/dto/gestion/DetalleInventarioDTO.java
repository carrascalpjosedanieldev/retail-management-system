package RetailManagementSystem.aplicacion.dto.gestion;

import RetailManagementSystem.aplicacion.dto.comercial.DatosTotalesProductoDTO;

import java.util.List;

public record DetalleInventarioDTO(Integer id, String nombre, int capacidadMaxima, int capacidadOcupada, List<DatosTotalesProductoDTO> productos) {
}


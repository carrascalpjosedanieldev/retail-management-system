package RetailManagementSystem.aplicacion.dto.gestion;

public record InventarioDTO(Integer idInventario, String nombre, int capacidadMaxima, int capacidadOcupada, int capacidadLibre) {
}


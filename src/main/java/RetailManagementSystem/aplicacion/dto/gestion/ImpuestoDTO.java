package RetailManagementSystem.aplicacion.dto.gestion;

import java.math.BigDecimal;

public record ImpuestoDTO(int idImpuesto, String nombre, BigDecimal porcentaje, boolean activo) {
}


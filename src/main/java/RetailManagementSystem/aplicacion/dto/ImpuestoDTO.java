package RetailManagementSystem.aplicacion.dto;

import java.math.BigDecimal;

public record ImpuestoDTO(int idImpuesto, String nombre, BigDecimal porcentaje, String estado) {
}


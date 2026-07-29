package RetailManagementSystem.aplicacion.dto;

import java.math.BigDecimal;

public record ResumenVentaDiaDTO(BigDecimal totalVentas, int cantidadFacturas, BigDecimal ultimaVenta) {

    public static ResumenVentaDiaDTO vacio() {
        return new ResumenVentaDiaDTO(BigDecimal.ZERO, 0, BigDecimal.ZERO);
    }

}

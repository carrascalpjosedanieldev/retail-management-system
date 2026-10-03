package RetailManagementSystem.aplicacion.dto.consultas;

import RetailManagementSystem.dominio.enums.Talla;

public record DetalleRopaDTO (
        Talla talla
) implements DetalleCreacionProductoDTO { }


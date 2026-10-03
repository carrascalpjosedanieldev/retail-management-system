package RetailManagementSystem.aplicacion.dto.creacion;

import RetailManagementSystem.dominio.enums.Talla;

public record DetalleRopaDTO (
        Talla talla
) implements DetalleCreacionProductoDTO { }


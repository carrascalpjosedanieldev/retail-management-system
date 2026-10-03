package RetailManagementSystem.aplicacion.dto.creacion;

import java.time.LocalDate;

public record DetallePerecederoDTO(
        LocalDate fechaVencimiento, int idPoliticaVencimiento
) implements DetalleCreacionProductoDTO { }


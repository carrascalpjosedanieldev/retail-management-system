package RetailManagementSystem.aplicacion.dto.consultas;

import java.time.LocalDate;

public record DetallePerecederoDTO(
        LocalDate fechaVencimiento, int idPoliticaVencimiento
) implements DetalleCreacionProductoDTO { }


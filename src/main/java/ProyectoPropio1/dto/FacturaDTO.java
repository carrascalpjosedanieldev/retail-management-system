package ProyectoPropio1.dto;

import java.util.List;

public record FacturaDTO(int idFactura, List<DatosLineaFacturaDTO> listaItemsFinales, double pagoTotal) {
}


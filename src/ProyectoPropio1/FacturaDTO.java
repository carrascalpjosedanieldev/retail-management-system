package ProyectoPropio1;

import java.util.List;

public record FacturaDTO(int idFactura, List<DatosLineaFacturaDTO> listaItemsFinales, double pagoTotal) {
}


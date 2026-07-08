package ProyectoPropio1.dto;

import java.math.BigDecimal;

public record DatosItemVendidoFacturaDTO(String tipoItem, String codigoReferencia, String nombreItem, int cantidad,
                                         BigDecimal precioUnitario, BigDecimal subTotalNeto, BigDecimal porcentajeImpuestos,
                                         BigDecimal montoImpuestos, BigDecimal totalLinea) {
}


package RetailManagementSystem.aplicacion.dto;

import java.math.BigDecimal;

public record ItemVendidoFacturaDTO(String tipoItem, String codigoReferencia, String nombreItem, int cantidad,
                                    BigDecimal precioUnitario, BigDecimal subTotalNeto, BigDecimal porcentajeImpuestos,
                                    BigDecimal montoImpuestos, BigDecimal totalLinea) { }


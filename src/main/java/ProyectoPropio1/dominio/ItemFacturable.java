package ProyectoPropio1.dominio;

import ProyectoPropio1.dominio.enums.TipoItem;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ItemFacturable {

    TipoItem getTipoItem();

    String getNombre();

    String getCodigo();

    BigDecimal getPorcentajeImpuesto();

    BigDecimal calcularImpuesto(BigDecimal precioFinalSinImpuesto, LocalDate fecha);

    BigDecimal getPorcentajeDescuento();

    BigDecimal calcularDescuento(BigDecimal valorVenta, LocalDate fecha);

    BigDecimal getValorVenta(LocalDate fecha);

}


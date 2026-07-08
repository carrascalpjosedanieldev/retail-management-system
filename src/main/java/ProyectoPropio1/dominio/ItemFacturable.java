package ProyectoPropio1.dominio;

import ProyectoPropio1.dominio.enums.TipoItem;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ItemFacturable {

    TipoItem getTipoItem();

    BigDecimal calcularImpuesto(LocalDate fecha);

    BigDecimal getPorcentajeImpuesto();

    String getNombre();

    String getCodigo();

    BigDecimal getValorVenta(LocalDate fecha);

}


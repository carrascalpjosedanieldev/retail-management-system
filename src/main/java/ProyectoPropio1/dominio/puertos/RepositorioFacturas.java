package ProyectoPropio1.dominio.puertos;

import ProyectoPropio1.dominio.entidades.Factura;
import ProyectoPropio1.dominio.entidades.ItemVendido;
import ProyectoPropio1.dominio.entidades.ReporteRecaudo;

import java.time.LocalDate;
import java.util.List;

public interface RepositorioFacturas {

    Factura insertarFactura(List<ItemVendido> items);

    ReporteRecaudo obtenerReporteRecaudo(LocalDate fechaInicio, LocalDate fechaFin);

}

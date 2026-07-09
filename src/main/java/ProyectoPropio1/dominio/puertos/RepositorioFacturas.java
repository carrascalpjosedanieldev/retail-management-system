package ProyectoPropio1.dominio.puertos;

import ProyectoPropio1.dominio.Factura;
import ProyectoPropio1.dominio.ItemVendido;
import ProyectoPropio1.dominio.ReporteRecaudo;

import java.time.LocalDate;
import java.util.List;

public interface RepositorioFacturas {

    Factura insertarFactura(List<ItemVendido> items);

    ReporteRecaudo obtenerReporteRecaudo(LocalDate fechaInicio, LocalDate fechaFin);

}

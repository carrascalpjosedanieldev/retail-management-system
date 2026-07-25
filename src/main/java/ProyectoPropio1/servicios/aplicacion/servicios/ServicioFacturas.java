package ProyectoPropio1.servicios.aplicacion.servicios;

import ProyectoPropio1.dominio.Factura;
import ProyectoPropio1.dominio.ItemVendido;
import ProyectoPropio1.dominio.ReporteRecaudo;
import ProyectoPropio1.dominio.puertos.RepositorioFacturas;

import java.time.LocalDate;
import java.util.List;

public class ServicioFacturas {

    private final RepositorioFacturas repositorioFacturas;

    public ServicioFacturas(RepositorioFacturas repositorioFacturas) {
        this.repositorioFacturas = repositorioFacturas;
    }

    public Factura registrarVentaYObtenerFactura(List<ItemVendido> itemsDelCarrito) {
        if (itemsDelCarrito == null || itemsDelCarrito.isEmpty()) {
            throw new IllegalArgumentException("No se puede registrar una venta vacía.");
        }
        return this.repositorioFacturas.insertarFactura(itemsDelCarrito);
    }

    public ReporteRecaudo obtenerReporteRecaudo(LocalDate fechaInicio, LocalDate fechaFin){
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas para el reporte no pueden estar vacías.");
        }
        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("Error de lógica: La fecha de inicio (" + fechaInicio + ")" +
                    " NO puede ser posterior a la fecha de fin (" + fechaFin + ").");
        }
        return this.repositorioFacturas.obtenerReporteRecaudo(fechaInicio, fechaFin);
    }

}


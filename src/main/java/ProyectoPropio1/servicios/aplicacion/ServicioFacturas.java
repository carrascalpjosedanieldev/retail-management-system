package ProyectoPropio1.servicios.aplicacion;

import ProyectoPropio1.dominio.Factura;
import ProyectoPropio1.dominio.ItemVendido;
import ProyectoPropio1.dominio.puertos.RepositorioFacturas;

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

}


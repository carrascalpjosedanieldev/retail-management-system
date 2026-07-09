package ProyectoPropio1.servicios.aplicacion;

import ProyectoPropio1.dominio.Descuento;
import ProyectoPropio1.dominio.Impuesto;
import ProyectoPropio1.dominio.Servicio;
import ProyectoPropio1.dominio.puertos.RepositorioDescuentos;
import ProyectoPropio1.dominio.puertos.RepositorioImpuestos;
import ProyectoPropio1.dominio.puertos.RepositorioServicio;

import java.math.BigDecimal;
import java.util.List;

public class ServicioServicios {

    private final RepositorioImpuestos repositorioImpuestos;

    private final RepositorioDescuentos repositorioDescuentos;

    private final RepositorioServicio repositorioServicio;

    public ServicioServicios(RepositorioImpuestos repositorioImpuestos, RepositorioDescuentos repositorioDescuentos,
                             RepositorioServicio repositorioServicio) {
        this.repositorioImpuestos = repositorioImpuestos;
        this.repositorioDescuentos = repositorioDescuentos;
        this.repositorioServicio = repositorioServicio;
    }

    public Servicio obtenerServicio(String codigoServicio){
        return this.repositorioServicio.obtenerServicio(codigoServicio);
    }

    public List<Servicio> obtenerServicios(){
        return this.repositorioServicio.obtenerServiciosActivos();
    }

    public String registrarServicioNuevo(String nombreServicio, BigDecimal precioBase, int idImpuesto, int idDescuento){
        Impuesto impuesto = this.repositorioImpuestos.obtenerImpuesto(idImpuesto);
        Descuento descuento = this.repositorioDescuentos.obtenerDescuento(idDescuento);
        Servicio servicio = Servicio.crearNuevo(nombreServicio, precioBase, impuesto, descuento);
        this.repositorioServicio.insertarServicio(servicio);
        return servicio.getCodigo();
    }

    public void desactivarServicio(String codigoServicio){
        this.repositorioServicio.desactivarServicio(codigoServicio);
    }

    public void activarServicio(String codigoServicio){
        this.repositorioServicio.activarServicio(codigoServicio);
    }

    public void cambiarNombreServicio(String codigoServicio, String nombreNuevo){
        Servicio servicio = this.repositorioServicio.obtenerServicio(codigoServicio);
        servicio.cambiarNombreServicio(nombreNuevo);
        this.repositorioServicio.actualizarServicio(servicio);
    }

    public void cambiarPrecioServicio(String codigoServicio, BigDecimal precioNuevo){
        Servicio servicio = this.obtenerServicio(codigoServicio);
        servicio.cambiarPrecioBase(precioNuevo);
        this.repositorioServicio.actualizarServicio(servicio);
    }

    public void cambiarImpuestoDeServicio(String codigoServicio, int idImpuesto){
        Servicio servicio = this.repositorioServicio.obtenerServicio(codigoServicio);
        this.repositorioServicio.actualizarImpuestoAServicio(servicio, idImpuesto);
    }

    public void cambiarDescuentoDeServicio(String codigoServicio, int idDescuento){
        Servicio servicio = this.obtenerServicio(codigoServicio);
        this.repositorioServicio.actualizarDescuentoAServicio(servicio, idDescuento);
    }

}

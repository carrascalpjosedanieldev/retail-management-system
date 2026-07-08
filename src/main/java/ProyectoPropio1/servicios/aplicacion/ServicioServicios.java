package ProyectoPropio1.servicios.aplicacion;

import ProyectoPropio1.dominio.Impuesto;
import ProyectoPropio1.dominio.Servicio;
import ProyectoPropio1.dominio.puertos.RepositorioImpuestos;
import ProyectoPropio1.dominio.puertos.RepositorioServicio;

import java.math.BigDecimal;
import java.util.List;

public class ServicioServicios {

    private final RepositorioImpuestos repositorioImpuestos;

    private final RepositorioServicio repositorioServicio;

    public ServicioServicios(RepositorioImpuestos repositorioImpuestos, RepositorioServicio repositorioServicio) {
        this.repositorioImpuestos = repositorioImpuestos;
        this.repositorioServicio = repositorioServicio;
    }

    public Servicio obtenerServicio(String codigoServicio){
        return this.repositorioServicio.obtenerServicio(codigoServicio);
    }

    public List<Servicio> obtenerServicios(){
        return this.repositorioServicio.obtenerServicios();
    }

    public String registrarServicioNuevo(String nombreServicio, BigDecimal precioBase, int idImpuesto){
        Impuesto impuesto = this.repositorioImpuestos.obtenerImpuesto(idImpuesto);
        Servicio servicio = new Servicio(nombreServicio, precioBase, impuesto);
        this.repositorioServicio.insertarServicio(servicio);
        return servicio.getCodigo();
    }

    public void eliminarServicio(String codigoServicio){
        this.repositorioServicio.eliminarServicio(codigoServicio);
    }

    public void cambiarNombreServicio(String codigoServicio, String nombreNuevo){
        Servicio servicio = this.repositorioServicio.obtenerServicio(codigoServicio);
        servicio.cambiarNombreServicio(nombreNuevo);
        this.repositorioServicio.actualizarServicio(servicio);
    }

    public void cambiarPrecioServicio(String codigoServicio, BigDecimal precioNuevo){
        Servicio servicio = this.repositorioServicio.obtenerServicio(codigoServicio);
        servicio.cambiarPrecioBase(precioNuevo);
        this.repositorioServicio.actualizarServicio(servicio);
    }

    public void cambiarImpuestoDeServicio(String codigoServicio, int idImpuesto){
        Servicio borrador = this.repositorioServicio.obtenerServicio(codigoServicio);
        Impuesto impuesto = Impuesto.reconstruirDesdeBD(idImpuesto, null, BigDecimal.ZERO, true);
        Servicio servicio = new Servicio(borrador.getCodigo(), borrador.getNombre(), borrador.getPrecioBase(), impuesto);
        this.repositorioServicio.actualizarServicio(servicio);
    }


}

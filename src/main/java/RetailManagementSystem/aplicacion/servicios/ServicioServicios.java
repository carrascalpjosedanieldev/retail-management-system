package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.dominio.entidades.Descuento;
import RetailManagementSystem.dominio.entidades.Impuesto;
import RetailManagementSystem.dominio.entidades.Servicio;
import RetailManagementSystem.dominio.puertos.RepositorioDescuentos;
import RetailManagementSystem.dominio.puertos.RepositorioImpuestos;
import RetailManagementSystem.dominio.puertos.RepositorioServicio;

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

    public Servicio obtenerServicoActivoParaLaVenta(String codigoServicio){
        return this.repositorioServicio.obtenerServicioActivoSoloPorCodigo(codigoServicio);
    }

    public boolean existeServicio(String codigoServicio){
        return this.repositorioServicio.existeServicio(codigoServicio);
    }

    public void registrarServicioNuevo(String nombreServicio, BigDecimal precioBase, int idImpuesto, int idDescuento){
        Impuesto impuesto = this.repositorioImpuestos.obtenerImpuesto(idImpuesto);
        Descuento descuento = this.repositorioDescuentos.obtenerDescuento(idDescuento);
        Servicio servicio = Servicio.crearNuevo(nombreServicio, precioBase, impuesto, descuento);
        this.repositorioServicio.insertarServicio(servicio);
    }

    public void actualizarServicio(String codigoServicio, String nombre, BigDecimal precioBase, int idImpuesto, int idDescuento){
        Servicio servicio = this.obtenerServicio(codigoServicio);
        servicio.cambiarNombreServicio(nombre);
        servicio.cambiarPrecioBase(precioBase);
        if (servicio.getIdImpuesto() != idImpuesto){
            Impuesto impuesto = this.repositorioImpuestos.obtenerImpuesto(idImpuesto);
            servicio.cambiarImpuesto(impuesto);
        }
        if (servicio.getIdDescuento() != idDescuento){
            Descuento descuento = this.repositorioDescuentos.obtenerDescuento(idDescuento);
            servicio.cambiarDescuento(descuento);
        }
        this.actualizarServicio(servicio);
    }

    public void cambiarEstadoServicio(String codigoServicio){
        Servicio servicio = this.obtenerServicio(codigoServicio);
        if (servicio.isActivo()){
            servicio.desactivarServicio();
        } else {
            servicio.activarServicio();
        }
        this.actualizarServicio(servicio);
    }

    private void actualizarServicio(Servicio servicio){
        this.repositorioServicio.actualizarServicio(servicio);
    }

    public List<Servicio> obtenerServiciosActivos(){
        return this.repositorioServicio.obtenerServiciosActivos();
    }

    public List<Servicio> obtenerServiciosInactivos(){
        return this.repositorioServicio.obtenerServiciosInactivos();
    }

}//===================================================================================================================//


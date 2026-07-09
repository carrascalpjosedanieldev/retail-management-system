package ProyectoPropio1.dominio.puertos;

import ProyectoPropio1.dominio.Servicio;

import java.util.List;

public interface RepositorioServicio {

    void insertarServicio(Servicio servicio);

    void activarServicio(String codigoServicio);

    void desactivarServicio(String codigoServicio);

    Servicio obtenerServicio(String codigoServicio);

    void actualizarServicio(Servicio servicio);

    void actualizarImpuestoAServicio(Servicio servicio, int idImpuesto);

    void actualizarDescuentoAServicio(Servicio servicio, int idDescuento);

    List<Servicio> obtenerServiciosActivos();

    List<Servicio> obtenerServiciosInactivos();

}

package ProyectoPropio1.dominio.puertos;

import ProyectoPropio1.dominio.entidades.Servicio;

import java.util.List;

public interface RepositorioServicio {

    void insertarServicio(Servicio servicio);

    Servicio obtenerServicio(String codigoServicio);

    void actualizarServicio(Servicio servicio);

    List<Servicio> obtenerServiciosActivos();

    List<Servicio> obtenerServiciosInactivos();

}

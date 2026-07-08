package ProyectoPropio1.dominio.puertos;

import ProyectoPropio1.dominio.Servicio;

import java.util.List;

public interface RepositorioServicio {

    void insertarServicio(Servicio servicio);

    void eliminarServicio(String codigoServicio);

    Servicio obtenerServicio(String codigoServicio);

    void actualizarServicio(Servicio servicio);

    List<Servicio> obtenerServicios();

}

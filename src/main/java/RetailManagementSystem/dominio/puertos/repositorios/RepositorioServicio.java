package RetailManagementSystem.dominio.puertos.repositorios;

import RetailManagementSystem.dominio.entidades.comercial.Servicio;

import java.util.List;

public interface RepositorioServicio {

    //CREATE:

    void insertarServicio(Servicio servicio);

    //READ:

    Servicio obtenerServicio(String codigoServicio);

    List<Servicio> obtenerServiciosActivos();

    List<Servicio> obtenerTodosLosServicios();

    Servicio obtenerServicioActivoSoloPorCodigo(String codigoServicio);

    boolean existeServicio(String codigoServicio);

    //UPDATE:

    void actualizarServicio(Servicio servicio);

}//===================================================================================================================//


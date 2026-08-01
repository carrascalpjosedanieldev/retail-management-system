package RetailManagementSystem.dominio.puertos;

import RetailManagementSystem.dominio.entidades.Servicio;

import java.util.List;

public interface RepositorioServicio {

    //CREATE:

    void insertarServicio(Servicio servicio);

    //READ:

    Servicio obtenerServicio(String codigoServicio);

    List<Servicio> obtenerServiciosActivos();

    List<Servicio> obtenerServiciosInactivos();

    Servicio obtenerServicioActivoSoloPorCodigo(String codigoServicio);

    boolean existeServicio(String codigoServicio);

    //UPDATE:

    void actualizarServicio(Servicio servicio);

}//===================================================================================================================//


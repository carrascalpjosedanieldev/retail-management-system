package RetailManagementSystem.dominio.puertos.repositorios;

import RetailManagementSystem.dominio.entidades.comercial.Servicio;

import java.util.List;

public interface RepositorioServicio {

    //CREATE:

    void insertarServicio(Servicio servicio);

    //READ:

    Servicio obtenerServicio(String codigoServicio);

    List<Servicio> obtenerTodosLosServicios();

    Servicio obtenerServicioActivoSoloPorCodigo(String codigoServicio);

    //UPDATE:

    void actualizarServicio(Servicio servicio);

}//===================================================================================================================//


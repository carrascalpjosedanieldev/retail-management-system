package RetailManagementSystem.dominio.puertos.repositorios;

import RetailManagementSystem.dominio.entidades.gestion.Descuento;

import java.util.List;

public interface RepositorioDescuentos {

    //CREATE:

    Descuento insertarDescuento(Descuento Descuento);

    //READ:

    Descuento obtenerDescuento(int idDescuento);

    List<Descuento> obtenerDescuentosActivos();

    List<Descuento> obtenerTodosLosDescuentos();

    //UPDATE:

    void actualizarDescuento(Descuento descuento);

}//===================================================================================================================//


package RetailManagementSystem.dominio.puertos;

import RetailManagementSystem.dominio.entidades.Descuento;

import java.util.List;

public interface RepositorioDescuentos {

    //CREATE:

    Descuento insertarDescuento(Descuento Descuento);

    //READ:

    Descuento obtenerDescuento(int idDescuento);

    List<Descuento> obtenerDescuentosActivos();

    List<Descuento> obtenerDescuentosInactivos();

    //UPDATE:

    void actualizarDescuento(Descuento descuento);

}//===================================================================================================================//


package RetailManagementSystem.dominio.puertos;

import RetailManagementSystem.dominio.entidades.Descuento;

import java.util.List;

public interface RepositorioDescuentos {

    Descuento insertarDescuento(Descuento Descuento);

    Descuento obtenerDescuento(int idDescuento);

    void actualizarDescuento(Descuento descuento);

    List<Descuento> obtenerDescuentosActivos();

    List<Descuento> obtenerDescuentosInactivos();

}

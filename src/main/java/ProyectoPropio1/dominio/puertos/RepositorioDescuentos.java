package ProyectoPropio1.dominio.puertos;

import ProyectoPropio1.dominio.Descuento;

import java.util.List;

public interface RepositorioDescuentos {

    Descuento insertarDescuento(Descuento Descuento);

    Descuento obtenerDescuento(int idDescuento);

    void actualizarDescuento(Descuento descuento);

    List<Descuento> obtenerDescuentosActivos();

    List<Descuento> obtenerDescuentosInactivos();

}

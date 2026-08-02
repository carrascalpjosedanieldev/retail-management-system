package RetailManagementSystem.dominio.puertos;

import RetailManagementSystem.dominio.entidades.comercial.Impuesto;

import java.util.List;

public interface RepositorioImpuestos {

    //CREATE:

    void insertarImpuesto(Impuesto impuesto);

    //READ:

    Impuesto obtenerImpuesto(int idImpuesto);

    List<Impuesto> obtenerImpuestosActivos();

    List<Impuesto> obtenerImpuestosInactivos();

    //UPDATE:

    void actualizarImpuesto(Impuesto impuesto);

}//===================================================================================================================//


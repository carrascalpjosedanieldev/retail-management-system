package RetailManagementSystem.dominio.puertos;

import RetailManagementSystem.dominio.entidades.gestion.Impuesto;

import java.util.List;

public interface RepositorioImpuestos {

    //CREATE:

    Impuesto insertarImpuesto(Impuesto impuesto);

    //READ:

    Impuesto obtenerImpuesto(int idImpuesto);

    List<Impuesto> obtenerImpuestosActivos();

    List<Impuesto> obtenerTodosLosImpuestos();

    //UPDATE:

    void actualizarImpuesto(Impuesto impuesto);

}//===================================================================================================================//


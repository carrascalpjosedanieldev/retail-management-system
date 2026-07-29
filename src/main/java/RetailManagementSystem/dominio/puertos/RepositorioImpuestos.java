package RetailManagementSystem.dominio.puertos;

import RetailManagementSystem.dominio.entidades.Impuesto;

import java.util.List;

public interface RepositorioImpuestos {

    Impuesto insertarImpuesto(Impuesto impuesto);

    Impuesto obtenerImpuesto(int idImpuesto);

    void actualizarImpuesto(Impuesto impuesto);

    List<Impuesto> obtenerImpuestosActivos();

    List<Impuesto> obtenerImpuestosInactivos();

}

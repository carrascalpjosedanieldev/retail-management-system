package ProyectoPropio1.dominio.puertos;

import ProyectoPropio1.dominio.Impuesto;

import java.util.List;

public interface RepositorioImpuestos {

    Impuesto insertarImpuesto(Impuesto impuesto);

    void desactivarImpuesto(int idImpuesto);

    void activarImpuesto(int idImpuesto);

    Impuesto obtenerImpuesto(int idImpuesto);

    void actualizarImpuesto(Impuesto impuesto);

    List<Impuesto> obtenerImpuestosActivos();

    List<Impuesto> obtenerImpuestosInactivos();

}

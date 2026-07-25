package ProyectoPropio1.dominio.puertos;

import ProyectoPropio1.dominio.entidades.PoliticaVencimiento;

import java.util.List;

public interface RepositorioPoliticaVencimiento {

    PoliticaVencimiento insertarPoliticaVencimiento(PoliticaVencimiento politicaVencimiento);

    PoliticaVencimiento obtenerPoliticaVencimiento(int idPoliticaVencimiento);

    void actualizarPoliticaVencimiento(PoliticaVencimiento politicaVencimiento);

    List<PoliticaVencimiento> obtenerPoliticasVencimientoActivas();

    List<PoliticaVencimiento> obtenerPoliticasVencimientoInactivas();

}

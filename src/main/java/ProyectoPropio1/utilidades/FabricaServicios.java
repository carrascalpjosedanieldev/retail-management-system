package ProyectoPropio1.utilidades;

import ProyectoPropio1.dominio.puertos.RepositorioDescuentos;
import ProyectoPropio1.dominio.puertos.RepositorioImpuestos;
import ProyectoPropio1.infraestructura.RepositorioDescuentosMySQL;
import ProyectoPropio1.infraestructura.RepositorioImpuestosMySQL;
import ProyectoPropio1.servicios.aplicacion.ServicioDescuentos;
import ProyectoPropio1.servicios.aplicacion.ServicioImpuestos;

public class FabricaServicios {

    private static ServicioDescuentos servicioDescuentos;

    public static ServicioDescuentos obtenerServicioDescuentos() {
        if (servicioDescuentos == null) {
            RepositorioDescuentos repositorio = new RepositorioDescuentosMySQL();
            servicioDescuentos = new ServicioDescuentos(repositorio);
        }
        return servicioDescuentos;
    }

    private static ServicioImpuestos servicioImpuestos;

    public static ServicioImpuestos obtenerServicioImpuestos() {
        if (servicioImpuestos == null) {
            RepositorioImpuestos repositorio = new RepositorioImpuestosMySQL();
            servicioImpuestos = new ServicioImpuestos(repositorio);
        }
        return servicioImpuestos;
    }

}


package ProyectoPropio1.utilidades;

import ProyectoPropio1.dominio.puertos.RepositorioDescuentos;
import ProyectoPropio1.infraestructura.RepositorioDescuentosMySQL;
import ProyectoPropio1.servicios.aplicacion.ServicioDescuentos;

public class FabricaServicios {

    private static ServicioDescuentos servicioDescuentos;

    public static ServicioDescuentos obtenerServicioDescuentos() {
        if (servicioDescuentos == null) {
            RepositorioDescuentos repositorio = new RepositorioDescuentosMySQL();
            servicioDescuentos = new ServicioDescuentos(repositorio);
        }
        return servicioDescuentos;
    }

}

package ProyectoPropio1.utilidades;

import ProyectoPropio1.dominio.puertos.RepositorioDescuentos;
import ProyectoPropio1.dominio.puertos.RepositorioImpuestos;
import ProyectoPropio1.dominio.puertos.RepositorioServicio;
import ProyectoPropio1.infraestructura.RepositorioDescuentosMySQL;
import ProyectoPropio1.infraestructura.RepositorioImpuestosMySQL;
import ProyectoPropio1.infraestructura.RepositorioServicioMySQL;
import ProyectoPropio1.servicios.aplicacion.ServicioDescuentos;
import ProyectoPropio1.servicios.aplicacion.ServicioImpuestos;
import ProyectoPropio1.servicios.aplicacion.ServicioServicios;

public class FabricaServicios {

    private static ServicioDescuentos servicioDescuentos;

    public static ServicioDescuentos obtenerServicioDescuentos() {
        if (servicioDescuentos == null) {
            RepositorioDescuentos repositorioDescuentos = new RepositorioDescuentosMySQL();
            servicioDescuentos = new ServicioDescuentos(repositorioDescuentos);
        }
        return servicioDescuentos;
    }

    private static ServicioImpuestos servicioImpuestos;

    public static ServicioImpuestos obtenerServicioImpuestos() {
        if (servicioImpuestos == null) {
            RepositorioImpuestos repositorioImpuestos = new RepositorioImpuestosMySQL();
            servicioImpuestos = new ServicioImpuestos(repositorioImpuestos);
        }
        return servicioImpuestos;
    }

    private static ServicioServicios servicioServicios;

    public static ServicioServicios obtenerServicioServicios() {
        if (servicioServicios == null){
            RepositorioImpuestos repositorioImpuestos = new RepositorioImpuestosMySQL();
            RepositorioDescuentos repositorioDescuentos = new RepositorioDescuentosMySQL();
            RepositorioServicio repositorioServicio = new RepositorioServicioMySQL();
            servicioServicios = new ServicioServicios(repositorioImpuestos, repositorioDescuentos, repositorioServicio);
        }
        return servicioServicios;
    }

}


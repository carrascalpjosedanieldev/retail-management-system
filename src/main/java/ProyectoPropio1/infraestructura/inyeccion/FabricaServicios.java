package ProyectoPropio1.infraestructura.inyeccion;

import ProyectoPropio1.aplicacion.servicios.*;
import ProyectoPropio1.dominio.puertos.*;
import ProyectoPropio1.infraestructura.persistencia.mysql.*;

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

    private static ServicioInventario servicioInventario;

    public static ServicioInventario obtenerServicioInventario() {
        if (servicioInventario == null){
            RepositorioInventario repositorioInventario = new RepositorioInventarioMySQL();
            servicioInventario = new ServicioInventario(repositorioInventario);
        }
        return servicioInventario;
    }

    private static ServicioProductos servicioProductos;

    public static ServicioProductos obtenerServicioProductos() {
        if (servicioProductos == null){
            RepositorioProducto repositorioProducto = new RepositorioProductoMySQL();
            RepositorioImpuestos repositorioImpuestos = new RepositorioImpuestosMySQL();
            RepositorioDescuentos repositorioDescuentos = new RepositorioDescuentosMySQL();
            RepositorioPoliticaVencimiento repositorioPoliticaVencimiento = new RepositorioPoliticaVencimientoMySQL();
            servicioProductos = new ServicioProductos(repositorioProducto, repositorioImpuestos,
                    repositorioDescuentos, repositorioPoliticaVencimiento);
        }
        return servicioProductos;
    }

    private static ServicioPoliticaVencimiento servicioPoliticaVencimiento;

    public static ServicioPoliticaVencimiento obtenerServicioPoliticas() {
        if (servicioPoliticaVencimiento == null){
            RepositorioPoliticaVencimiento repositorioPoliticaVencimiento = new RepositorioPoliticaVencimientoMySQL();
            servicioPoliticaVencimiento = new ServicioPoliticaVencimiento(repositorioPoliticaVencimiento);
        }
        return servicioPoliticaVencimiento;
    }

    private static ServicioConfiguraciones servicioConfiguraciones;

    public static ServicioConfiguraciones obtenerServicioConfiguraciones(){
        if (servicioConfiguraciones == null){
            RepositorioConfiguracion repositorioConfiguracion = new RepositorioConfiguracionMySQL();
            servicioConfiguraciones = new ServicioConfiguraciones(repositorioConfiguracion);
        }
        return servicioConfiguraciones;
    }

    private static ServicioFacturas servicioFacturas;

    public static ServicioFacturas obtenerServicioFacturas(){
        if (servicioFacturas == null){
            RepositorioFacturas repositorioFacturas = new RepositorioFacturasMySQL();
            servicioFacturas = new ServicioFacturas(repositorioFacturas);
        }
        return servicioFacturas;
    }

}


package ProyectoPropio1;

// PARA EXPORTAR EL PROYECTO FACILMENTE:
// Get-ChildItem -Recurse -Filter *.java | Get-Content | Out-File proyecto_completo.txt

import ProyectoPropio1.dominio.Tienda;
import ProyectoPropio1.dominio.puertos.*;
import ProyectoPropio1.infraestructura.*;
import ProyectoPropio1.servicios.aplicacion.*;
import ProyectoPropio1.servicios.controlador.ControladorTienda;
import ProyectoPropio1.servicios.controlador.GestorVentas;
import ProyectoPropio1.servicios.ensambladores.*;

import java.util.Scanner;

import static ProyectoPropio1.vistaConsola.MenuGestionarTienda.*;
import static ProyectoPropio1.vistaConsola.MenuUtilizarTienda.utilizarTienda;
import static ProyectoPropio1.vistaConsola.MetodosTienda.*;

public class Ejecutable {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        RepositorioConfiguracion repositorioConfiguracion = new RepositorioConfiguracionMySQL();
        String nombreTienda = repositorioConfiguracion.obtenerValorConfiguracion("NombreProyectoPropioOriginal");
        Tienda miTienda = Tienda.crearNueva(nombreTienda);

        EnsambladorDTOProducto ensambladorDTOProducto = new EnsambladorDTOProducto();
        EnsambladorDTOInventario ensambladorDTOInventario = new EnsambladorDTOInventario(ensambladorDTOProducto);
        EnsambladorDTOFactura ensambladorDTOFactura = new EnsambladorDTOFactura();
        EnsambladorDTOCarrito ensambladorDTOCarrito = new EnsambladorDTOCarrito();
        EnsambladorDTOServicio ensambladorDTOServicio = new EnsambladorDTOServicio();
        EnsambladorDTOImpuesto ensambladorDTOImpuesto = new EnsambladorDTOImpuesto();
        EnsambladorDTODescuento ensambladorDTODescuento = new EnsambladorDTODescuento();
        EnsambladorDTOPoliticaVencimiento ensambladorDTOPoliticaVencimiento = new EnsambladorDTOPoliticaVencimiento();

        RepositorioInventario repositorioInventario = new RepositorioInventarioMySQL();
        RepositorioProducto repositorioProducto = new RepositorioProductoMySQL();
        RepositorioImpuestos repositorioImpuestos = new RepositorioImpuestosMySQL();
        RepositorioServicio repositorioServicio = new RepositorioServicioMySQL();
        RepositorioFacturas repositorioFacturas = new RepositorioFacturasMySQL();
        RepositorioDescuentos repositorioDescuentos = new RepositorioDescuentosMySQL();
        RepositorioPoliticaVencimiento repositorioPoliticaVencimiento = new RepositorioPoliticaVencimientoMySQL();

        ServicioConfiguraciones servicioConfiguraciones = new ServicioConfiguraciones(repositorioConfiguracion);
        ServicioInventario servicioInventario = new ServicioInventario(repositorioInventario);
        ServicioProductos servicioProductos = new ServicioProductos(repositorioProducto);
        ServicioImpuestos servicioImpuestos = new ServicioImpuestos(repositorioImpuestos);
        ServicioServicios servicioServicios = new ServicioServicios(repositorioImpuestos, repositorioDescuentos,
                                                                    repositorioServicio);
        ServicioDescuentos servicioDescuentos = new ServicioDescuentos(repositorioDescuentos);
        ServicioFacturas servicioFacturas = new ServicioFacturas(repositorioFacturas);
        ServicioPoliticaVencimiento servicioPoliticaVencimiento = new ServicioPoliticaVencimiento(repositorioPoliticaVencimiento);

        FabricaProductos fabricaProductos = new FabricaProductos(servicioImpuestos, servicioDescuentos, servicioPoliticaVencimiento);

        GestorVentas gestorVentas = new GestorVentas(servicioProductos, servicioServicios, servicioFacturas);

        ControladorTienda miControladorTienda = new ControladorTienda(
                miTienda,
                ensambladorDTOProducto, ensambladorDTOInventario, ensambladorDTOFactura,
                ensambladorDTOCarrito, ensambladorDTOServicio, ensambladorDTOImpuesto,
                ensambladorDTODescuento, ensambladorDTOPoliticaVencimiento,
                gestorVentas,
                servicioFacturas, servicioImpuestos, servicioConfiguraciones, servicioInventario,
                servicioProductos, servicioServicios, servicioDescuentos,servicioPoliticaVencimiento,
                fabricaProductos
        );

        int opcionMenuPrincipal;
        do {
            menuPrincipal();
            opcionMenuPrincipal = pedirOpcion(sc,1,3);
            switch (opcionMenuPrincipal){
                case 1:
                    gestionarTienda(sc, miControladorTienda);
                    break;
                case 2:
                    utilizarTienda(sc, miControladorTienda);
                    break;
                case 3:
                    salirPrograma();
                    break;
            }
        } while (opcionMenuPrincipal!=3);

    }

}


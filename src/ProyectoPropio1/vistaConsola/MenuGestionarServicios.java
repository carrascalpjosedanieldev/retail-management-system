package ProyectoPropio1.vistaConsola;

import ProyectoPropio1.servicios.ControladorTienda;
import ProyectoPropio1.excepciones.ServicioNoEncontradoException;
import ProyectoPropio1.dto.DatosCatalogoServiciosDTO;
import ProyectoPropio1.dto.DatosServicioDTO;

import java.util.Scanner;

import static ProyectoPropio1.vistaConsola.MetodosTienda.*;

public class MenuGestionarServicios {

    private static void menuGestionarServicios(){
        System.out.print("""
                \n                             -MODIFICAR TIENDA-
                ---> SELECCIONA QUE QUIERES HACER:
                               * OPCION                              * ACCION
                                   1                            REGISTRAR SERVICIO
                                   2                         VER SERVICIOS DISPONIBLES
                                   3                            ELIMINAR SERVICIO
                                   4                         MODIFICAR NOMBRE SERVICIO
                                   5                         MODIFICAR PRECIO SERVICIO
                                   6                                  SALIR
                ---> Ingresa el numero segun tu eleccion:
                """ + "---> ");
    }

    public static void gestionarServicios(Scanner sc, ControladorTienda controladorTienda){
        System.out.println("\nHAS SELECCIONADO: -GESTIONAR SERVICIOS-");
        int opcionGestionarServicios;
        do {
            menuGestionarServicios();
            opcionGestionarServicios = pedirOpcion(sc,1,6);
            switch (opcionGestionarServicios){
                case 1:
                    registrarServicio(sc, controladorTienda);
                    break;
                case 2:
                    verServiciosDisponibles(controladorTienda);
                    break;
                case 3:
                    eliminarServicio(sc, controladorTienda);
                    break;
                case 4:
                    modificarNombreServicio(sc, controladorTienda);
                    break;
                case 5:
                    modificarPrecioServicio(sc, controladorTienda);
                    break;
                case 6:
                    System.out.println("\nSALIENDO . . .");
                    break;
            }
        } while (opcionGestionarServicios!=6);
    }

    private static void registrarServicio(Scanner sc, ControladorTienda controladorTienda){
        System.out.println("\nHAS SELECCIONADO: -REGISTRAR SERVICIO-");
        System.out.print("""
                    Escribe el Nombre del Servicio:
                    """ + "---> ");
        String nombreServicio = sc.nextLine();
        System.out.print("""
                    \nEscribe la Capacidad del inventario:
                    """ + "---> ");
        double precioServicio = leerDecimal(sc);
        try {
            controladorTienda.registrarServicioNuevo(nombreServicio, precioServicio);
            System.out.println("Servicio registrado con exito");
        } catch (RuntimeException e){
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }

    private static void verServiciosDisponibles(ControladorTienda controladorTienda){
        if (controladorTienda.tiendaNoTieneServicios()){
            System.out.println("""
                    \nACCION DENEGADA
                    TODAVIA NO HAY SERVICIOS DISPONIBLES
                    """);
            return;
        }
        DatosCatalogoServiciosDTO datosCatalogoServicios = controladorTienda.exportarCatalogoServicios();
        System.out.println("--------------------------------------------------------------------------------------------");
        System.out.println("---> SERVICIOS:");
        for (DatosServicioDTO datosServicio: datosCatalogoServicios.listaServicios()){
            System.out.println("Codigo:  " + datosServicio.codigo() + "   Servicio:  " + datosServicio.nombre() + "   Precio:  $" + datosServicio.precioFinal());
        }
        System.out.println("--------------------------------------------------------------------------------------------");
    }

    private static void eliminarServicio(Scanner sc, ControladorTienda controladorTienda){
        if (controladorTienda.tiendaNoTieneServicios()){
            System.out.println("""
                    \nACCION DENEGADA
                    TODAVIA NO HAY SERVICIOS DISPONIBLES
                    """);
            return;
        }
        System.out.print("""
                \nHAS SELECCIONADO: -ELIMINAR SERVICIO-
                Que Servicio vas a eliminar, escribe su codigo:
                """ + "---> ");
        int codigoServicio = leerEntero(sc);
        try {
            controladorTienda.eliminarServicioDeTienda(codigoServicio);
            System.out.println("El Servicio de codigo -" + codigoServicio + "- ha sido eliminado con exito");
        } catch (ServicioNoEncontradoException e){
            System.out.println("Proceso Interrumpido\nERROR:  " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }

    private static void modificarNombreServicio(Scanner sc, ControladorTienda controladorTienda){
        if (controladorTienda.tiendaNoTieneServicios()){
            System.out.println("""
                    \nACCION DENEGADA
                    TODAVIA NO HAY SERVICIOS DISPONIBLES
                    """);
            return;
        }
        System.out.print("""
                \nHAS SELECCIONADO: -MODIFICAR NOMBRE SERVICIO-
                A que Servicio le vas a cambiar el Nombre, escribe su Codigo:
                """ + "---> ");
        int codigoServicio = leerEntero(sc);
        System.out.print("Escribe el Nombre nuevo que le pondras:\n" +
                "---> ");
        String nombreNuevo = sc.nextLine();
        try {
            controladorTienda.cambiarNombreServicio(codigoServicio, nombreNuevo);
            System.out.println("El Servicio de codigo -" + codigoServicio + "- ahora se llama: " + nombreNuevo);
        } catch (ServicioNoEncontradoException e){
            System.out.println("Proceso Interrumpido\nERROR:  " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }

    private static void modificarPrecioServicio(Scanner sc, ControladorTienda controladorTienda){
        if (controladorTienda.tiendaNoTieneServicios()){
            System.out.println("""
                    \nACCION DENEGADA
                    TODAVIA NO HAY SERVICIOS DISPONIBLES
                    """);
            return;
        }
        System.out.print("""
                \nHAS SELECCIONADO: -MODIFICAR PRECIO SERVICIO-
                A que Servicio le vas a cambiar el Precio, escribe su Codigo:
                """ + "---> ");
        int codigoServicio = leerEntero(sc);
        System.out.print("Escribe el Precio nuevo que le pondras:\n" +
                "---> ");
        double precioNuevo = leerDecimal(sc);
        try {
            controladorTienda.cambiarPrecioServicio(codigoServicio, precioNuevo);
            System.out.println("El Servicio de codigo -" + codigoServicio + "- ahora vale: $" + precioNuevo);
        } catch (ServicioNoEncontradoException e) {
            System.out.println("Proceso Interrumpido\nERROR:  " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }

}


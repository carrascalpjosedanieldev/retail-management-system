package ProyectoPropio1.vistaConsola;

import ProyectoPropio1.dto.ServicioDTO;
import ProyectoPropio1.servicios.controlador.ControladorTienda;
import ProyectoPropio1.dto.DatosCatalogoServiciosDTO;

import java.math.BigDecimal;
import java.util.Scanner;

import static ProyectoPropio1.vistaConsola.MenuGestionarImpuestos.impuestosActivosParaRegistro;
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
                                   6                      MODIFICAR IMPUESTO DE SERVICIO
                                   7                                  SALIR
                ---> Ingresa el numero segun tu eleccion:
                """ + "---> ");
    }

    public static void gestionarServicios(Scanner sc, ControladorTienda controladorTienda){
        System.out.println("\nHAS SELECCIONADO: -GESTIONAR SERVICIOS-");
        int opcionGestionarServicios;
        do {
            menuGestionarServicios();
            opcionGestionarServicios = pedirOpcion(sc,1,7);
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
                    modifcarImpuestoDeServicio(sc, controladorTienda);
                    break;
                case 7:
                    System.out.println("\nSALIENDO . . .");
                    break;
            }
        } while (opcionGestionarServicios!=7);
    }

    private static void registrarServicio(Scanner sc, ControladorTienda controladorTienda){
        System.out.println("\nHAS SELECCIONADO: -REGISTRAR SERVICIO-");
        System.out.print("""
                    \nEscribe el Nombre del Servicio:
                    """ + "---> ");
        String nombreServicio = sc.nextLine();
        System.out.print("""
                    \nEscribe el Precio del Servicio:
                    """ + "---> ");
        BigDecimal precioServicio = leerDecimal(sc);
        try {
            String impuestosActivosParaRegistro = impuestosActivosParaRegistro(controladorTienda);
            System.out.print("\n" + impuestosActivosParaRegistro);
            System.out.print("""
                    Escribe el ID del Impuesto que le corresponda:
                    """ + "---> ");
            int idImpuesto = leerEntero(sc);
            String codigoServicio = controladorTienda.registrarServicioNuevo(nombreServicio, precioServicio, idImpuesto);
            System.out.println("\nServicio registrado con exito");
            ServicioDTO datosServicio = controladorTienda.obtenerDatosServicio(codigoServicio);
            System.out.println("Codigo:  " + datosServicio.codigo() + "   Servicio:  " + datosServicio.nombre() + "   Precio:  $" + datosServicio.precioFinal());
        } catch (RuntimeException e){
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }

    private static void verServiciosDisponibles(ControladorTienda controladorTienda){
        try {
            DatosCatalogoServiciosDTO datosCatalogoServicios = controladorTienda.obtenerCatalogoServicios();
            System.out.println("--------------------------------------------------------------------------------------------");
            System.out.println("---> SERVICIOS:");
            for (ServicioDTO datosServicio: datosCatalogoServicios.listaServicios()){
                System.out.println("Codigo:  " + datosServicio.codigo() + "   Servicio:  " + datosServicio.nombre() + "   Precio:  $" + datosServicio.precioFinal());
            }
            System.out.println("--------------------------------------------------------------------------------------------");
        } catch (RuntimeException e) {
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }

    private static void eliminarServicio(Scanner sc, ControladorTienda controladorTienda){
        System.out.print("""
                \nHAS SELECCIONADO: -ELIMINAR SERVICIO-
                Que Servicio vas a eliminar, escribe su codigo:
                """ + "---> ");
        String  codigoServicio = sc.nextLine();
        try {
            controladorTienda.eliminarServicioDeTienda(codigoServicio);
            System.out.println("El Servicio de codigo -" + codigoServicio + "- ha sido eliminado con exito");
        } catch (RuntimeException e) {
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }

    private static void modificarNombreServicio(Scanner sc, ControladorTienda controladorTienda){
        System.out.print("""
                \nHAS SELECCIONADO: -MODIFICAR NOMBRE SERVICIO-
                A que Servicio le vas a cambiar el Nombre, escribe su Codigo:
                """ + "---> ");
        String  codigoServicio = sc.nextLine();
        System.out.print("Escribe el Nombre nuevo que le pondras:\n" +
                "---> ");
        String nombreNuevo = sc.nextLine();
        try {
            controladorTienda.cambiarNombreServicio(codigoServicio, nombreNuevo);
            System.out.println("El Servicio de codigo -" + codigoServicio + "- ahora se llama: " + nombreNuevo);
        } catch (RuntimeException e) {
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }

    private static void modificarPrecioServicio(Scanner sc, ControladorTienda controladorTienda){
        System.out.print("""
                \nHAS SELECCIONADO: -MODIFICAR PRECIO SERVICIO-
                A que Servicio le vas a cambiar el Precio, escribe su Codigo:
                """ + "---> ");
        String codigoServicio = sc.nextLine();
        System.out.print("Escribe el Precio nuevo que le pondras:\n" +
                "---> ");
        BigDecimal precioNuevo = leerDecimal(sc);
        try {
            controladorTienda.cambiarPrecioServicio(codigoServicio, precioNuevo);
            System.out.println("El Servicio de codigo -" + codigoServicio + "- ahora vale: $" + precioNuevo);
        } catch (RuntimeException e) {
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }

    private static void modifcarImpuestoDeServicio(Scanner sc, ControladorTienda controladorTienda){
        System.out.println("""
                \nHAS SELECCIONADO: -MODIFICAR IMPUESTO DE SERVICIO-
                A que Servicio le vas a cambiar el Impuesto, escribe su Codigo:
                """ + "---> ");
        String codigoServicio = sc.nextLine();
        try {
            ServicioDTO datosServicio = controladorTienda.obtenerDatosServicio(codigoServicio);
            System.out.println("\nEl Servicio -" + datosServicio.nombre() + "- Tiene el Impuesto de ID -" + datosServicio.idImpuesto() + "-");
            String impuestosActivosParaRegistro = impuestosActivosParaRegistro(controladorTienda);
            System.out.println(impuestosActivosParaRegistro);
            System.out.println("""
                    \nEscribe el ID del Impuesto que le pondras:
                    """);
            int idImpuesto = leerEntero(sc);
            if (idImpuesto == datosServicio.idImpuesto()){
                throw new IllegalArgumentException("Impuestos Iguales");
            }
            controladorTienda.cambiarImpuestoDeServicio(codigoServicio, idImpuesto);
            System.out.println("El Servicio de Codigo -" + codigoServicio + "- ahora tiene el Impuesto de ID -" + idImpuesto + "-");
        } catch (RuntimeException e) {
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }

}


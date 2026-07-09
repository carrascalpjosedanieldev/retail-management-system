package ProyectoPropio1.vistaConsola;

import ProyectoPropio1.dto.DescuentoDTO;
import ProyectoPropio1.servicios.controlador.ControladorTienda;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static ProyectoPropio1.vistaConsola.MetodosTienda.*;

public class MenuGestionarDescuentos {

    private static void menuGestionarDescuentos(){
        System.out.print("""
                \n                           -GESTIONAR DESCUENTOS-
                ---> SELECCIONA QUE QUIERES HACER:
                               * OPCION                              * ACCION
                                   1                              REGISTRAR DESCUENTO
                                   2                                VER DESCUENTOS
                                   3                             DESACTIVAR DESCUENTO
                                   4                              ACTIVAR DESCUENTO
                                   5                                    SALIR
                ---> Ingresa el numero segun tu eleccion:
                """ + "---> ");
    }

    public static void gestionarDescuentos(Scanner sc, ControladorTienda controladorTienda){
        System.out.println("\nHAS SELECCIONADO: -GESTIONAR DESCUENTOS-");
        int opcionGestionarDescuentos;
        do {
            menuGestionarDescuentos();
            opcionGestionarDescuentos = pedirOpcion(sc,1,5);
            switch (opcionGestionarDescuentos){
                case 1:
                    registrarDescuento(sc, controladorTienda);
                    break;
                case 2:
                    verDescuentos(sc, controladorTienda);
                    break;
                case 3:
                    desactivarDescuento(sc, controladorTienda);
                    break;
                case 4:
                    activarDescuento(sc, controladorTienda);
                    break;
                case 5:
                    System.out.println("\nSALIENDO . . .");
                    break;
            }
        } while (opcionGestionarDescuentos !=5);
    }

    private static void registrarDescuento(Scanner sc, ControladorTienda controladorTienda){
        System.out.println("\nHAS SELECCIONADO: -REGISTRAR DESCUENTO-");
        System.out.print("""
                    Escribe el Nombre del Descuento:
                    """ + "---> ");
        String nombreDescuento = sc.nextLine();
        System.out.print("""
                    \nEscribe el Porcentaje del Descuento:
                    """ + "---> ");
        BigDecimal porcentajeDescuento = leerDecimal(sc);
        try {
            int idDescuento = controladorTienda.registrarDescuento(nombreDescuento, porcentajeDescuento);
            System.out.println("El Descuento de ID -" + idDescuento + "- ha sido Registrado con exito\n" +
                    "Este Descuento esta activo, si quieres desactivarlo puedes hacerlo en Gestionar Descuentos");
        } catch (RuntimeException e){
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }


    private static void verDescuentos(Scanner sc, ControladorTienda controladorTienda){
        System.out.println("\nHAS SELECCIONADO -VER DESCUENTOS-");
        System.out.println("""
                Deseas ver:
                1. Los Descuentos Activos
                2. Los Descuentos Inactivos
                """);
        int opcion = pedirOpcion(sc, 1, 2);
        try {
            List<DescuentoDTO> detalleDescuentos = new ArrayList<>();
            switch (opcion){
                case 1:
                    List<DescuentoDTO> detalleDescuentosActivos = controladorTienda.obtenerDetalleDescuentosActivos();
                    detalleDescuentos.addAll(detalleDescuentosActivos);
                    System.out.println("---> DESCUENTOS ACTIVOS:");
                    break;
                case 2:
                    List<DescuentoDTO> detalleDescuentosInactivos = controladorTienda.obtenerDetalleDescuentosInactivos();
                    detalleDescuentos.addAll(detalleDescuentosInactivos);
                    System.out.println("---> DESCUENTOS INACTIVOS:");
                    break;
            }
            System.out.println("------------------------------------------------------------------------------------------------------------");
            for (DescuentoDTO datosDescuento : detalleDescuentos){
                String informacion;
                informacion = String.format("ID DESCUENTO: %-5d NOMBRE: %-20s PORCENTAJE: %-8.2f ESTADO: %-8s",
                        datosDescuento.idDescuento(), datosDescuento.nombre(), datosDescuento.porcentaje(), datosDescuento.estado());
                System.out.println(informacion);
            }
            System.out.println("------------------------------------------------------------------------------------------------------------");
        } catch (RuntimeException e){
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }


    private static void desactivarDescuento(Scanner sc, ControladorTienda controladorTienda){
        System.out.println("\nHAS SELECCIONADO: -DESACTIVAR DESCUENTO-");
        System.out.print("""
                    \nEscribe el ID del Descuento:
                    """ + "---> ");
        int idDescuento = leerEntero(sc);
        try {
            controladorTienda.desactivarDescuento(idDescuento);
            System.out.println("El Descuento de ID -" + idDescuento + "- ha sido Desactivado con Exito");
        } catch (RuntimeException e){
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }


    private static void activarDescuento(Scanner sc, ControladorTienda controladorTienda){
        System.out.println("\nHAS SELECCIONADO: -ACTIVAR DESCUENTO-");
        System.out.print("""
                    \nEscribe el ID del Descuento:
                    """ + "---> ");
        int idDescuento = leerEntero(sc);
        try {
            controladorTienda.activarDescuento(idDescuento);
            System.out.println("El Descuento de ID -" + idDescuento + "- ha sido Activado con Exito");
        } catch (RuntimeException e){
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }


    public static String descuentosActivosParaRegistro(ControladorTienda controladorTienda){
        List<DescuentoDTO> detalleDescuentosActivos = controladorTienda.obtenerDetalleDescuentosActivos();
        StringBuilder descuentosParaRegistro = new StringBuilder();
        descuentosParaRegistro.append("---> DESCUENTOS ACTIVOS:");
        descuentosParaRegistro.append(System.lineSeparator());
        for (DescuentoDTO datosDescuento : detalleDescuentosActivos){
            String informacion;
            informacion = String.format("ID DESCUENTO: %-5d NOMBRE: %-20s PORCENTAJE: %-8.2f ESTADO: %-8s",
                    datosDescuento.idDescuento(), datosDescuento.nombre(), datosDescuento.porcentaje(), datosDescuento.estado());
            descuentosParaRegistro.append(informacion);
            descuentosParaRegistro.append(System.lineSeparator());
        }
        return descuentosParaRegistro.toString();
    }

}

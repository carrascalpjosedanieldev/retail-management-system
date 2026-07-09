package ProyectoPropio1.vistaConsola;

import ProyectoPropio1.dto.ImpuestoDTO;
import ProyectoPropio1.servicios.controlador.ControladorTienda;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static ProyectoPropio1.vistaConsola.MetodosTienda.*;

public class MenuGestionarImpuestos {

    private static void menuGestionarImpuestos(){
        System.out.print("""
                \n                           -GESTIONAR IMPUESTOS-
                ---> SELECCIONA QUE QUIERES HACER:
                               * OPCION                              * ACCION
                                   1                              REGISTRAR IMPUESTO
                                   2                                VER IMPUESTOS
                                   3                             DESACTIVAR IMPUESTO
                                   4                              ACTIVAR IMPUESTO
                                   5                                    SALIR
                ---> Ingresa el numero segun tu eleccion:
                """ + "---> ");
    }

    public static void gestionarImpuestos(Scanner sc, ControladorTienda controladorTienda){
        System.out.println("\nHAS SELECCIONADO: -GESTIONAR IMPUESTOS-");
        int opcionGestionarImpuestos;
        do {
            menuGestionarImpuestos();
            opcionGestionarImpuestos = pedirOpcion(sc,1,5);
            switch (opcionGestionarImpuestos){
                case 1:
                    registrarImpuesto(sc, controladorTienda);
                    break;
                case 2:
                    verImpuestos(sc, controladorTienda);
                    break;
                case 3:
                    desactivarImpuesto(sc, controladorTienda);
                    break;
                case 4:
                    activarImpuesto(sc, controladorTienda);
                    break;
                case 5:
                    System.out.println("\nSALIENDO . . .");
                    break;
            }
        } while (opcionGestionarImpuestos !=4);
    }

    private static void registrarImpuesto(Scanner sc, ControladorTienda controladorTienda){
        System.out.println("\nHAS SELECCIONADO: -REGISTRAR IMPUESTO-");
        System.out.print("""
                    Escribe el Nombre del Impuesto:
                    """ + "---> ");
        String nombreImpuesto = sc.nextLine();
        System.out.print("""
                    \nEscribe el Porcentaje del Impuesto:
                    """ + "---> ");
        BigDecimal porcentajeImpuesto = leerDecimal(sc);
        try {
            int idImpuesto = controladorTienda.registrarImpuesto(nombreImpuesto, porcentajeImpuesto);
            System.out.println("El Impuesto de ID -" + idImpuesto + "- ha sido registrado con exito\n" +
                    "Este Impuesto esta activo, si quieres desactivarlo puedes hacerlo en Gestionar Impuestos");
        } catch (RuntimeException e){
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }


    private static void verImpuestos(Scanner sc, ControladorTienda controladorTienda){
        System.out.println("\nHAS SELECCIONADO -VER IMPUESTOS-");
        System.out.println("""
                Deseas ver:
                1. Los Impuestos Activos
                2. Los Impuestos Inactivos
                """);
        int opcion = pedirOpcion(sc, 1, 2);
        try {
            List<ImpuestoDTO> detalleImpuestos = new ArrayList<>();
            switch (opcion){
                case 1:
                    List<ImpuestoDTO> detalleImpuestosActivos = controladorTienda.obtenerDetalleImpuestosActivos();
                    detalleImpuestos.addAll(detalleImpuestosActivos);
                    System.out.println("---> IMPUESTOS ACTIVOS:");
                    break;
                case 2:
                    List<ImpuestoDTO> detalleImpuestosInactivos = controladorTienda.obtenerDetalleImpuestosInactivos();
                    detalleImpuestos.addAll(detalleImpuestosInactivos);
                    System.out.println("---> IMPUESTOS INACTIVOS:");
                    break;
            }
            System.out.println("------------------------------------------------------------------------------------------------------------");
            for (ImpuestoDTO datosImpuesto: detalleImpuestos){
                String informacion;
                informacion = String.format("ID IMPUESTO: %-5d NOMBRE: %-20s PORCENTAJE: %-8.2f ESTADO: %-8s",
                        datosImpuesto.idImpuesto(), datosImpuesto.nombre(), datosImpuesto.porcentaje(), datosImpuesto.estado());
                System.out.println(informacion);
            }
            System.out.println("------------------------------------------------------------------------------------------------------------");
        } catch (RuntimeException e){
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }

    }


    private static void desactivarImpuesto(Scanner sc, ControladorTienda controladorTienda){
        System.out.println("\nHAS SELECCIONADO: -DESACTIVAR IMPUESTO-");
        System.out.print("""
                    \nEscribe el ID del Impuesto:
                    """ + "---> ");
        int idImpuesto = leerEntero(sc);
        try {
            controladorTienda.desactivarImpuesto(idImpuesto);
            System.out.println("El Impuesto de ID -" + idImpuesto + "- ha sido Desactivado con Exito");
        } catch (RuntimeException e){
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }

    private static void activarImpuesto(Scanner sc, ControladorTienda controladorTienda){
        System.out.println("\nHAS SELECCIONADO: -ACTIVAR IMPUESTO-");
        System.out.print("""
                    \nEscribe el ID del Impuesto:
                    """ + "---> ");
        int idImpuesto = leerEntero(sc);
        try {
            controladorTienda.activarImpuesto(idImpuesto);
            System.out.println("El Impuesto de ID -" + idImpuesto + "- ha sido Activado con Exito");
        } catch (RuntimeException e){
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }

    public static String impuestosActivosParaRegistro(ControladorTienda controladorTienda){
        List<ImpuestoDTO> detalleImpuestosActivos = controladorTienda.obtenerDetalleImpuestosActivos();
        StringBuilder impuestosParaRegistro = new StringBuilder();
        impuestosParaRegistro.append("---> IMPUESTOS ACTIVOS:");
        impuestosParaRegistro.append(System.lineSeparator());
        for (ImpuestoDTO datosImpuesto: detalleImpuestosActivos){
            String informacion;
            informacion = String.format("ID IMPUESTO: %-5d NOMBRE: %-20s PORCENTAJE: %-8.2f ESTADO: %-8s",
                    datosImpuesto.idImpuesto(), datosImpuesto.nombre(), datosImpuesto.porcentaje(), datosImpuesto.estado());
            impuestosParaRegistro.append(informacion);
            impuestosParaRegistro.append(System.lineSeparator());
        }
        return impuestosParaRegistro.toString();
    }

}

package ProyectoPropio1.vistaConsola;

import ProyectoPropio1.dto.PoliticaVencimientoDTO;
import ProyectoPropio1.excepciones.PoliticaVencimientoNoEncontradaException;
import ProyectoPropio1.servicios.controlador.ControladorTienda;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static ProyectoPropio1.vistaConsola.MetodosTienda.*;
import static ProyectoPropio1.vistaConsola.MetodosTienda.leerDecimal;
import static ProyectoPropio1.vistaConsola.MetodosTienda.leerEntero;

public class MenuGestionarPoliticasVencimiento {

    private static void menuGestionarPoliticasVencimiento(){
        System.out.print("""
                \n                    -GESTIONAR POLITICAS DE VENCIMIENTO-
                ---> SELECCIONA QUE QUIERES HACER:
                               * OPCION                              * ACCION
                                   1                      REGISTRAR POLITICA DE VENCIMIENTO
                                   2                        VER POLITICAS DE VENCIMIENTO
                                   3                      DESACTIVAR POLITICA DE VENCIMIENTO
                                   4                        ACTIVAR POLITICA DE VENCIMIENTO
                                   5                               CAMBIAR NOMBRE
                                   6                             CAMBIAR PORCENTAJE
                                   7                             CAMBIAR DIAS UMBRAL
                                   8                                    SALIR
                ---> Ingresa el numero segun tu eleccion:
                """ + "---> ");
    }

    public static void gestionarPoliticasDeVencimiento(Scanner sc, ControladorTienda controladorTienda){
        System.out.println("\nHAS SELECCIONADO: -GESTIONAR POLITICAS DE VENCIMIENTO-");
        int opcionGestionarPoliticasDeVencimiento;
        do {
            menuGestionarPoliticasVencimiento();
            opcionGestionarPoliticasDeVencimiento = pedirOpcion(sc,1,8);
            switch (opcionGestionarPoliticasDeVencimiento){
                case 1:
                    registrarPoliticaVencimiento(sc, controladorTienda);
                    break;
                case 2:
                    verPoliticasVencimiento(sc, controladorTienda);
                    break;
                case 3:
                    desactivarPoliticaVencimiento(sc, controladorTienda);
                    break;
                case 4:
                    activarPoliticaVencimiento(sc, controladorTienda);
                    break;
                case 5:
                    cambiarNombre(sc, controladorTienda);
                    break;
                case 6:
                    cambiarPorcentaje(sc, controladorTienda);
                    break;
                case 7:
                    cambiarDiasUmbral(sc, controladorTienda);
                    break;
                case 8:
                    System.out.println("\nSALIENDO . . .");
                    break;
            }
        } while (opcionGestionarPoliticasDeVencimiento !=8);
    }

    private static void registrarPoliticaVencimiento(Scanner sc, ControladorTienda controladorTienda){
        System.out.println("\nHAS SELECCIONADO: -REGISTRAR POLITICA DE VENCIMIENTO-");
        System.out.print("""
                    Escribe el Nombre de la Politica de Vencimiento:
                    """ + "---> ");
        String nombrePolitica = sc.nextLine();
        System.out.print("""
                    \nEscribe el Porcentaje de la Politica:
                    """ + "---> ");
        BigDecimal porcentajePolitica = leerDecimal(sc);
        System.out.print("""
                    \nEscribe los Dias Umbral de la Politica:
                    """ + "---> ");
        int diasUmbral = leerEntero(sc);
        try {
            int idPolitica = controladorTienda.registrarPoliticaVencimiento(nombrePolitica, diasUmbral, porcentajePolitica);
            System.out.println("La Politica de Vencimiento de ID -" + idPolitica + "- ha sido registrado con exito\n" +
                    "Esta Politica de Vencimiento esta activa, si quieres desactivarlo puedes hacerlo en Gestionar Politicas de Vencimiento");
        } catch (RuntimeException e){
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }


    private static void verPoliticasVencimiento(Scanner sc, ControladorTienda controladorTienda){
        System.out.println("\nHAS SELECCIONADO -VER POLITICA DE VENCIMIENTO-");
        System.out.println("""
                Deseas ver:
                1. Las Politicas Activas
                2. Las Politicas Inactivas
                """);
        int opcion = pedirOpcion(sc, 1, 2);
        try {
            List<PoliticaVencimientoDTO> detallePoliticas = new ArrayList<>();
            switch (opcion){
                case 1:
                    List<PoliticaVencimientoDTO> detallePoliticasActivos = controladorTienda.obtenerDetallePoliticasVencimientoActivas();
                    detallePoliticas.addAll(detallePoliticasActivos);
                    System.out.println("---> POLITICA DE VENCIMIENTO ACTIVAS:");
                    break;
                case 2:
                    List<PoliticaVencimientoDTO> detalleImpuestosInactivos = controladorTienda.obtenerDetallePoliticasVencimientoInactivas();
                    detallePoliticas.addAll(detalleImpuestosInactivos);
                    System.out.println("---> POLITICA DE VENCIMIENTO INACTIVAS:");
                    break;
            }
            System.out.println("------------------------------------------------------------------------------------------------------------");
            for (PoliticaVencimientoDTO datosPolitica : detallePoliticas){
                String informacion;
                informacion = String.format("ID POLITICA: %-5d NOMBRE: %-20s DIAS UMBRAL: %-5d PORCENTAJE: %-8.2f ESTADO: %-8s",
                        datosPolitica.idPoliticaVencimiento(), datosPolitica.nombrePolitica(),
                        datosPolitica.diasUmbral(), datosPolitica.porcentajeDescuento(), datosPolitica.estado());
                System.out.println(informacion);
            }
            System.out.println("------------------------------------------------------------------------------------------------------------");
        } catch (RuntimeException e){
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }

    }


    private static void desactivarPoliticaVencimiento(Scanner sc, ControladorTienda controladorTienda){
        System.out.println("\nHAS SELECCIONADO: -DESACTIVAR POLITICA DE VENCIMIENTO-");
        System.out.print("""
                    \nEscribe el ID de la Politica de Vencimiento:
                    """ + "---> ");
        int idPolitica = leerEntero(sc);
        try {
            controladorTienda.desactivarPoliticaVencimiento(idPolitica);
            System.out.println("La Politica de Vencimiento de ID -" + idPolitica + "- ha sido Desactivada con Exito");
        } catch (RuntimeException e){
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }

    private static void activarPoliticaVencimiento(Scanner sc, ControladorTienda controladorTienda){
        System.out.println("\nHAS SELECCIONADO: -ACTIVAR POLITICA DE VENCIMIENTO-");
        System.out.print("""
                    \nEscribe el ID de la Politica de Vencimiento:
                    """ + "---> ");
        int idPolitica = leerEntero(sc);
        try {
            controladorTienda.activarPoliticaVencimiento(idPolitica);
            System.out.println("La Politica de Vencimiento de ID -" + idPolitica + "- ha sido Activada con Exito");
        } catch (RuntimeException e){
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }

    public static String politicasVencimientoActivasParaRegistro(ControladorTienda controladorTienda){
        List<PoliticaVencimientoDTO> detallePoliticasActivos = controladorTienda.obtenerDetallePoliticasVencimientoActivas();
        StringBuilder politicasParaRegistro = new StringBuilder();
        politicasParaRegistro.append("---> POLITICAS DE VENCIMIENTO ACTIVAS:");
        politicasParaRegistro.append(System.lineSeparator());
        for (PoliticaVencimientoDTO datosPolitica : detallePoliticasActivos){
            String informacion;
            informacion = String.format("ID POLITICA: %-5d NOMBRE: %-20s DIAS UMBRAL: %-5d PORCENTAJE: %-8.2f ESTADO: %-8s",
                    datosPolitica.idPoliticaVencimiento(), datosPolitica.nombrePolitica(),
                    datosPolitica.diasUmbral(), datosPolitica.porcentajeDescuento(), datosPolitica.estado());
            politicasParaRegistro.append(informacion);
            politicasParaRegistro.append(System.lineSeparator());
        }
        return politicasParaRegistro.toString();
    }

    private static void cambiarNombre(Scanner sc, ControladorTienda controladorTienda){
        System.out.print("""
                \nHAS SELECCIONADO: -CAMBIAR NOMBRE POLITICA DE VENCIMIENTO-
                A que Politica de Vencimiento le cambiaras el Nombre, escribe el ID
                """ + "---> ");
        int idPolitica = leerEntero(sc);
        try {
            System.out.print("Escribe el Nombre nuevo que le pondras:\n" +
                    "---> ");
            String nombreNuevo = sc.nextLine();
            controladorTienda.cambiarNombrePoliticaVencimiento(idPolitica, nombreNuevo);
            System.out.println("La Politica de Vencimiento de ID -" + idPolitica + "- ahora se llama:  " + nombreNuevo);
        } catch (PoliticaVencimientoNoEncontradaException e) {
            System.out.println("""
                \nACCION DENEGADA:
                LA POLITICA DE VENCIMIENTO INGRESADA NO EXISTE
                """);
        } catch (RuntimeException e) {
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }

    private static void cambiarPorcentaje(Scanner sc, ControladorTienda controladorTienda){
        System.out.print("""
                \nHAS SELECCIONADO: -CAMBIAR PORCENTAJE POLITICA DE VENCIMIENTO-
                A que Politica de Vencimiento le cambiaras el Porcentaje, escribe el ID
                """ + "---> ");
        int idPolitica = leerEntero(sc);
        try {
            System.out.print("Escribe el Porcentaje nuevo que le pondras:\n" +
                    "---> ");
            BigDecimal porcentajeNuevo = leerDecimal(sc);
            controladorTienda.cambiarPorcentajePoliticaVencimiento(idPolitica, porcentajeNuevo);
            System.out.println("La Politica de Vencimiento de ID -" + idPolitica + "- ahora tendra el porcentaje:  " + porcentajeNuevo + "%");
        } catch (PoliticaVencimientoNoEncontradaException e) {
            System.out.println("""
                \nACCION DENEGADA:
                LA POLITICA DE VENCIMIENTO INGRESADA NO EXISTE
                """);
        } catch (RuntimeException e) {
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }

    private static void cambiarDiasUmbral(Scanner sc, ControladorTienda controladorTienda){
        System.out.print("""
                \nHAS SELECCIONADO: -CAMBIAR DIAS UMBRAL POLITICA DE VENCIMIENTO-
                A que Politica de Vencimiento le cambiaras loa Dias Umbral, escribe el ID
                """ + "---> ");
        int idPolitica = leerEntero(sc);
        try {
            System.out.print("Escribe los Dias Umbral nuevos que le pondras:\n" +
                    "---> ");
            int diasUmbral = leerEntero(sc);
            controladorTienda.cambiarDiasUmbralPoliticaVencimiento(idPolitica, diasUmbral);
            System.out.println("La Politica de Vencimiento de ID -" + idPolitica + "- ahora tendra los dias umbral:  " + diasUmbral);
        } catch (PoliticaVencimientoNoEncontradaException e) {
            System.out.println("""
                \nACCION DENEGADA:
                LA POLITICA DE VENCIMIENTO INGRESADA NO EXISTE
                """);
        } catch (RuntimeException e) {
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }

}

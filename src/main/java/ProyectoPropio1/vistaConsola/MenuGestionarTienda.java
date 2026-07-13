package ProyectoPropio1.vistaConsola;

import ProyectoPropio1.servicios.controlador.ControladorTienda;

import java.util.Scanner;

import static ProyectoPropio1.vistaConsola.MenuGestionarDescuentos.gestionarDescuentos;
import static ProyectoPropio1.vistaConsola.MenuGestionarInventarios.gestionarInventarios;
import static ProyectoPropio1.vistaConsola.MenuGestionarImpuestos.gestionarImpuestos;
import static ProyectoPropio1.vistaConsola.MenuGestionarPoliticasVencimiento.gestionarPoliticasDeVencimiento;
import static ProyectoPropio1.vistaConsola.MetodosTienda.*;
import static ProyectoPropio1.vistaConsola.MenuGestionarServicios.gestionarServicios;

public class MenuGestionarTienda {

    private static void menuGestionarTienda(){
        System.out.print("""
                \n                             -GESTIONAR TIENDA-
                ---> SELECCIONA QUE QUIERES HACER:
                               * OPCION                              * ACCION
                                   1                           CAMBIAR NOMBRE TIENDA
                                   2                           GESTIONAR INVENTARIOS
                                   3                            GESTIONAR SERVICIOS
                                   4                            GESTIONAR IMPUESTOS
                                   5                            GESTIONAR DESCUENTOS
                                   6                      GESTIONAR POLITICAS DE VENCIMIENTO
                                   7                                   SALIR
                ---> Ingresa el numero segun tu eleccion:
                """ + "---> ");
    }


    public static void gestionarTienda(Scanner sc, ControladorTienda controladorTienda){
        System.out.println("\nHAS SELECCIONADO: -GESTIONAR TIENDA-");
        int opcionGestionarTienda;
        do {
            menuGestionarTienda();
            opcionGestionarTienda = pedirOpcion(sc,1,7);
            switch (opcionGestionarTienda){
                case 1:
                    cambiarNombreTienda(sc,controladorTienda);
                    break;
                case 2:
                    gestionarInventarios(sc, controladorTienda);
                    break;
                case 3:
                    gestionarServicios(sc, controladorTienda);
                    break;
                case 4:
                    gestionarImpuestos(sc, controladorTienda);
                    break;
                case 5:
                    gestionarDescuentos(sc, controladorTienda);
                    break;
                case 6:
                    gestionarPoliticasDeVencimiento(sc, controladorTienda);
                    break;
                case 7:
                    System.out.println("\nSALIENDO . . .");
                    break;
            }
        } while (opcionGestionarTienda!=7);
    }

    private static void cambiarNombreTienda(Scanner sc, ControladorTienda controladorTienda){
        System.out.println("\nHAS SELECCIONADO: -CAMBIAR NOMBRE TIENDA-");
        try {
            System.out.print("La Tienda actualmente se llama -" + controladorTienda.obtenerNombreTienda() + "\n" +
                    "Escribe el nombre nuevo que le quieres colocar:\n" +
                    "---> ");
            String nuevoNombreTienda = sc.nextLine();
            controladorTienda.cambiarNombreTienda(nuevoNombreTienda);
            System.out.println("Nombre cambiado con Exito\n" +
                    "La Tienda ahora se llama -" + nuevoNombreTienda + "-");
        } catch (RuntimeException e){
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }

}


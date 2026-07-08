package ProyectoPropio1.vistaConsola;

import ProyectoPropio1.dto.*;
import ProyectoPropio1.servicios.controlador.ControladorTienda;

import java.util.Scanner;

import static ProyectoPropio1.vistaConsola.MenuVentas.vender;
import static ProyectoPropio1.vistaConsola.MetodosTienda.pedirOpcion;

public class MenuUtilizarTienda {

    private static void menuUtilizarTienda(){
        System.out.print("""
                \n                        BIENVENIDO -MENU UTILIZAR TIENDA-
                ---> SELECCIONA QUE QUIERES HACER:
                               * OPCION                              * ACCION
                                   1                                   VENDER
                                   2                             VER RECAUDO CLIENTES
                                   3                                   SALIR
                ---> Ingresa el numero segun tu eleccion:
                """ + "---> ");
    }

    public static void utilizarTienda(Scanner sc, ControladorTienda controladorTienda){
        System.out.println("\nHAS SELECCIONADO: -UTILIZAR TIENDA-");
        int opcionUtilizarTienda;
        do {
            menuUtilizarTienda();
            opcionUtilizarTienda = pedirOpcion(sc,1,3);
            switch (opcionUtilizarTienda){
                case 1:
                    vender(sc, controladorTienda);
                    break;
                case 2:
                    verRecaudoClientes(controladorTienda);
                    break;
                case 3:
                    System.out.println("\nSALIENDO . . .");
                    break;
            }
        } while (opcionUtilizarTienda!=3);
    }


    private static void verRecaudoClientes(ControladorTienda controladorTienda){
        System.out.print("""
                \nHAS SELECCIONADO -VER RECAUDO CLIENTES-
                """);
        try {
            System.out.println("En proceso");
        } catch (RuntimeException e){
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }

}


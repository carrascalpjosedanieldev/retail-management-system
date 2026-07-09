package ProyectoPropio1.vistaConsola;

import ProyectoPropio1.dto.*;
import ProyectoPropio1.servicios.controlador.ControladorTienda;

import java.time.LocalDate;
import java.util.Scanner;

import static ProyectoPropio1.vistaConsola.MenuVentas.vender;
import static ProyectoPropio1.vistaConsola.MetodosTienda.leerFecha;
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
                    verRecaudoClientes(sc, controladorTienda);
                    break;
                case 3:
                    System.out.println("\nSALIENDO . . .");
                    break;
            }
        } while (opcionUtilizarTienda!=3);
    }


    private static void verRecaudoClientes(Scanner sc, ControladorTienda controladorTienda){
        System.out.println("""
                \nHAS SELECCIONADO -VER RECAUDO CLIENTES-
                """);
        try {
            System.out.print("""
                    IMPORTANTE LEER:
                    Debes ingresar las fechas entre las cuales quieres ver el recuado
                    Si quieres ver el recaudo de un solo dia, ingresa la fecha en los dos campos
                    """);
            System.out.print("""
                            \nIngrese la fecha inicio (Formato DD/MM/AAAA):
                            """ + "---> ");
            LocalDate fechaInicio = leerFecha(sc);
            System.out.print("""
                            \nIngrese la fecha fin (Formato DD/MM/AAAA):
                            """ + "---> ");
            LocalDate fechaFin = leerFecha(sc);
            ReporteRecaudoDTO reporteRecaudo = controladorTienda.obtenerReporteRecaudo(fechaInicio, fechaFin);
            System.out.println("\n-------------------------------------------------------------------------------------");
            System.out.println("Recaudo entre: " + fechaInicio + "  Y  " + fechaFin);
            System.out.println("Facturas Emitidas:  " + reporteRecaudo.cantidadFacturasEmitidas());
            System.out.println("SubTotal Neto:  $" + reporteRecaudo.subTotal());
            System.out.println("Total Impuestos:  $" + reporteRecaudo.totalImpuestos());
            System.out.println("Total Recaudado:  $" + reporteRecaudo.totalRecaudo());
            System.out.println("-------------------------------------------------------------------------------------");
        } catch (RuntimeException e){
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }

}


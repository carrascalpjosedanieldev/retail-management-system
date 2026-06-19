package ProyectoPropio1;

import java.util.Scanner;

import static ProyectoPropio1.MenuModificarInventario.modificarInventario;
import static ProyectoPropio1.MetodosTienda.*;
import static ProyectoPropio1.MenuGestionarServicios.gestionarServicios;

public class MenuModificarTienda {

    private static void menuModificarTienda(){
        System.out.print("""
                \n                             -MODIFICAR TIENDA-
                ---> SELECCIONA QUE QUIERES HACER:
                               * OPCION                              * ACCION
                                   1                           CAMBIAR NOMBRE TIENDA
                                   2                             AGREGAR INVENTARIO
                                   3                               VER INVENTARIO
                                   4                            MODIFICAR INVENTARIO
                                   5                            VER STOCK INVENTARIO
                                   6                         ELIMINAR INVENTARIO VACIO
                                   7                            GESTIONAR SERVICIOS
                                   8                                   SALIR
                ---> Ingresa el numero segun tu eleccion:
                """ + "---> ");
    }

    public static void modificarTienda(Scanner sc, ControladorTienda controladorTienda){
        if (controladorTienda.noTieneTienda()){
            System.out.println("""
                                \nACCION DENEGADA
                                TODAVIA NO HAY TIENDA
                                """);
            return;
        }
        System.out.println("\nHAS SELECCIONADO: -MODIFICAR TIENDA-");
        int opcionModificarTienda;
        do {
            menuModificarTienda();
            opcionModificarTienda = pedirOpcion(sc,1,8);
            switch (opcionModificarTienda){
                case 1:
                    cambiarNombreTienda(sc,controladorTienda);
                    break;
                case 2:
                    agregarInventario(sc, controladorTienda);
                    break;
                case 3:
                    verInventario(sc,controladorTienda);
                    break;
                case 4:
                    modificarInventario(sc, controladorTienda);
                    break;
                case 5:
                    verStockInventario(sc, controladorTienda);
                    break;
                case 6:
                    eliminarInventarioVacio(sc, controladorTienda);
                    break;
                case 7:
                    gestionarServicios(sc, controladorTienda);
                    break;
                case 8:
                    System.out.println("\nSALIENDO . . .");
                    break;
            }
        } while (opcionModificarTienda!=8);
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
        } catch (IllegalArgumentException e){
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }

    private static void agregarInventario(Scanner sc, ControladorTienda controladorTienda){
        System.out.println("\nHAS SELECCIONADO: -AGREGAR INVENTARIO-");
        try {
            System.out.print("""
                    Escribe el Nombre del Inventario:
                    """ + "---> ");
            String nombreInventario = sc.nextLine();
            System.out.print("""
                    \nEscribe la Capacidad del inventario:
                    """ + "---> ");
            int capacidadInventario = leerEntero(sc);
            controladorTienda.agregarInventarioATienda(nombreInventario,capacidadInventario);
            System.out.println("Nuevo Inventario generado con Exito");
        } catch (IllegalArgumentException e){
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }

    private static void verInventario(Scanner sc, ControladorTienda controladorTienda){
        System.out.println("\nHAS SELECCIONADO: -VER INVENTARIO-");
        try {
            System.out.println(controladorTienda.mostrarInfoInventariosDeTienda());
            System.out.print("""
                    \nElige el Inventario por su numero identificador:
                    """ + "---> ");
            int id = leerEntero(sc);
            System.out.println(controladorTienda.obtenerDetalleInventarioDeTienda(id));
        } catch (IllegalArgumentException e) {
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }

    private static void verStockInventario(Scanner sc, ControladorTienda controladorTienda){
        if (controladorTienda.tiendaNoTieneInventarios()){
            System.out.println("""
                    \nACCION DENEGADA
                    TODAVIA NO HAY INVENTARIO
                    """);
            return;
        }
        System.out.println("""
                \nHAS SELECCIONADO: -VER STOCK INVENTARIO-
                ---> INVENTARIOS:
                ------------------------------------------------------------------------------------
                """);
        System.out.println(controladorTienda.mostraInfoInventarioDeTienda());
        System.out.print("""
                \n------------------------------------------------------------------------------------
                Elige el Inventario por su numero identificador:
                """ + "---> ");
        int numeroId = leerEntero(sc);
        if (controladorTienda.inventarioTieneProductos(numeroId)){
            System.out.println(controladorTienda.mostrarInfoStockInventario(numeroId));
            return;
        }
        System.out.println("""
                \nACCION DENEGADA
                EL INVENTARIO ESTA VACIO
                """);
    }

    private static void eliminarInventarioVacio(Scanner sc, ControladorTienda controladorTienda){
        if (controladorTienda.tiendaNoTieneInventarios()){
            System.out.println("""
                    \nACCION DENEGADA
                    TODAVIA NO HAY INVENTARIO
                    """);
            return;
        }
        System.out.println("""
                \nHAS SELECCIONADO: -ELIMINAR INVENTARIO VACIO-
                \n---> INVENTARIOS:
                ------------------------------------------------------------------------------------
                """);
        System.out.println(controladorTienda.mostrarInfoInventariosDeTienda());
        System.out.print("""
                \n------------------------------------------------------------------------------------
                Elige el Inventario que vas a Eliminar por su numero identificador:
                """ + "---> ");
        int numeroId = leerEntero(sc);
        try {
            controladorTienda.eliminarInventarioVacio(numeroId);
            System.out.println("Inventario eliminado con exito");
        } catch (IllegalArgumentException e) {
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }

}


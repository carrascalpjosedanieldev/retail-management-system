package ProyectoUniversidad1;

import java.util.Scanner;

import static ProyectoUniversidad1.MenuModificarProducto.modificarProductoAInventario;
import static ProyectoUniversidad1.MetodosTienda.*;

public class MenuModificarInventario {

    private static void menuModificarInventario(){
        System.out.print("""
                \n                           -MODIFICAR INVENTARIO-
                ---> SELECCIONA QUE QUIERES HACER:
                               * OPCION                              * ACCION
                                   1                         CAMBIAR NOMBRE INVENTARIO
                                   2                              AGREGAR PRODUCTO
                                   3                             MODIFICAR PRODUCTO
                                   4                              ELIMINAR PRODUCTO
                                   5                               BUSCAR PRODUCTO
                                   6                      MOVER PRODUCTO A OTRO INVENTARIO
                                   7                                   SALIR
                ---> Ingresa el numero segun tu eleccion:
                """ + "---> ");
    }

    public static void modificarInventario(Scanner sc, ControladorTienda controladorTienda){
        if (controladorTienda.tiendaNoTieneInventarios()){
            System.out.println("""
                    \nACCION DENEGADA
                    TODAVIA NO HAY INVENTARIO
                    """);
            return;
        }
        System.out.println("\nHAS SELECCIONADO: -MODIFICAR INVENTARIO-");
        int opcionModificarInventario;
        System.out.println("""
                \n---> INVENTARIOS:
                ------------------------------------------------------------------------------------
                """);
        System.out.println(controladorTienda.mostraInfoInventarioDeTienda());
        System.out.print("""
                \n------------------------------------------------------------------------------------
                Elige el Inventario por su numero identificador:
                """ + "---> ");
        int idInventario = leerEntero(sc);
        do {
            menuModificarInventario();
            opcionModificarInventario = pedirOpcion(sc,1,7);
            switch (opcionModificarInventario){
                case 1:
                    cambiarNombreInventario(sc, controladorTienda, idInventario);
                    break;
                case 2:
                    agregarProductoAInventario(sc, controladorTienda, idInventario);
                    break;
                case 3:
                    modificarProductoAInventario(sc, controladorTienda, idInventario);
                    break;
                case 4:
                    eliminarProductoAInventario(sc, controladorTienda, idInventario);
                    break;
                case 5:
                    buscarProductoAInventario(sc, controladorTienda, idInventario);
                    break;
                case 6:
                    moverProductoAInventario(sc, controladorTienda, idInventario);
                    break;
                case 7:
                    System.out.println("\nSALIENDO . . .");
                    break;
            }
        } while (opcionModificarInventario !=7);
    }

    private static void cambiarNombreInventario(Scanner sc, ControladorTienda controladorTienda, int idInventario) {
        System.out.print("""
                \nHAS SELECCIONADO: -CAMBIAR NOMBRE INVENTARIO-
                Escribe el nombre nuevo que le pondras:
                """ + "---> ");
        String nombreNuevoInv = sc.nextLine();
        try {
            controladorTienda.cambiarNombreAUnInventario(idInventario, nombreNuevoInv);
            System.out.println("El Inventario de ID -" + idInventario + "- ahora se llama: " + nombreNuevoInv);
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR:  " + e.getMessage());
        }
    }

    private static void agregarProductoAInventario(Scanner sc, ControladorTienda controladorTienda, int idInventario) {
        System.out.print("""
                \nHAS SELECCIONADO: -AGREGAR PRODUCTO-
                Escribe el nombre del producto nuevo:
                """ + "---> ");
        String nombre = sc.nextLine();
        System.out.print("""
                \nEscribe el valor de compra del producto nuevo:
                """ + "---> ");
        double valorC = leerDecimal(sc);
        String tieneStock;
        do {
            System.out.print("""
                    \nTiene Stock? (SI / NO):
                    """ + "---> ");
            tieneStock = sc.nextLine();
        } while (!tieneStock.equalsIgnoreCase("si") && !tieneStock.equalsIgnoreCase("no"));
        if (tieneStock.equalsIgnoreCase("si")){
            System.out.print("""
                    \nEscribe el stock del producto nuevo:
                    """ + "--->");
            int stock = leerEntero(sc);
            try {
                Producto producto = controladorTienda.agregarPorductoAInventario(idInventario, nombre, valorC, stock);
                System.out.println("Nuevo Producto:\n" + producto.describirProducto());
                System.out.println();
            } catch (IllegalArgumentException e){
                System.out.println("ERROR:  " + e.getMessage());
            }
            return;
        }
        try {
            Producto producto = controladorTienda.agregarPorductoAInventario(idInventario, nombre, valorC, 0);
            System.out.println("Nuevo Producto:\n" + producto.describirProducto());
            System.out.println();
        } catch (IllegalArgumentException e){
            System.out.println("ERROR:  " + e.getMessage());
        }

    }

    private static void eliminarProductoAInventario(Scanner sc, ControladorTienda controladorTienda, int idInventario) {
        System.out.print("""
                \nHAS SELECCIONADO: -ELIMINAR PRODUCTO-
                Que Producto vas a eliminar, escribe su codigo:
                """ + "---> ");
        int codigoProducto = leerEntero(sc);
        try {
            controladorTienda.eliminarProductoAInventario(idInventario, codigoProducto);
            System.out.println("El Producto de codigo -" + codigoProducto + "- ha sido eliminado con exito");
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR:  " + e.getMessage());
        }

    }

    private static void buscarProductoAInventario(Scanner sc, ControladorTienda controladorTienda, int idInventario) {
        System.out.print("""
                \nHAS SELECCIONADO: -BUSCAR PRODUCTO-
                Que Producto vas a buscar, escribe su codigo:
                """ + "---> ");
        int codigoProducto = leerEntero(sc);
        try {
            boolean esta = controladorTienda.buscarProductoAInventario(idInventario, codigoProducto);
            if (esta){
                System.out.println("El Producto de codigo -" + codigoProducto + "- NO esta en ese inventario");
                return;
            }
            System.out.println("El Producto de codigo -" + codigoProducto + "- SI esta en ese inventario");
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR:  " +e.getMessage());
        }
    }

    private static void moverProductoAInventario(Scanner sc, ControladorTienda controladorTienda, int idInventario){
        System.out.print("""
                \nHAS SELECCIONADO: -MOVER PRODUCTO A OTRO INVENTARIO-
                Que Producto vas a mover, escribe su codigo:
                """ + "---> ");
        int codigoProducto = leerEntero(sc);
        System.out.println("\nA que Inventario lo vas a mover, escribe su numero identificador:\n---> ");
        int numeroId2 = leerEntero(sc);
        try {
            controladorTienda.moverProductoAInventario(idInventario, numeroId2 , codigoProducto);
            System.out.println("El Producto de Codigo -" + codigoProducto + "- ha sido movido al Inventario de ID -" + numeroId2 + "- con exito");
        } catch (IllegalArgumentException e){
            System.out.println("ERROR:  " + e.getMessage());
        }
    }

}


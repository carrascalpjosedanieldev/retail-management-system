package ProyectoUniversidad1;

import java.util.Scanner;

import static ProyectoUniversidad1.MetodosTienda.leerEntero;
import static ProyectoUniversidad1.MetodosTienda.pedirOpcion;

public class MenuUtilizarTienda {

    private static void menuUtilizarTienda(){
        System.out.print("""
                \n                        BIENVENIDO -MENU UTILIZAR TIENDA-
                ---> SELECCIONA QUE QUIERES HACER:
                               * OPCION                              * ACCION
                                   1                          VER TODOS LOS PRODUCTOS
                                   2                              VENDER PRODUCTOS
                                   3                           VER RECAUDO A CLIENTES
                                   4                                   SALIR
                ---> Ingresa el numero segun tu eleccion:
                """ + "---> ");
    }

    public static void utilizarTienda(Scanner sc, ControladorTienda controladorTienda){
        if (!controladorTienda.tieneTienda()){
            System.out.println("""
                                \nACCION DENEGADA
                                TODAVIA NO HAY TIENDA
                                """);
            return;
        }
        System.out.println("\nHAS SELECCIONADO: -UTILIZAR TIENDA-");
        int opcionUtilizarTienda;
        do {
            menuUtilizarTienda();
            opcionUtilizarTienda = pedirOpcion(sc,1,4);
            switch (opcionUtilizarTienda){
                case 1:
                    verTodosLosProductos(sc ,controladorTienda);
                    break;
                case 2:
                    venderProductoDeInventario(sc, controladorTienda);
                    break;
                case 3:
                    verRecaudoClientes(sc, controladorTienda);
                    break;
                case 4:
                    System.out.println("\nSALIENDO . . .");
                    break;
            }
        } while (opcionUtilizarTienda!=4);
    }

    private static void verTodosLosProductos(Scanner sc, ControladorTienda controladorTienda){
        if (!controladorTienda.tiendaTieneInventarios()){
            System.out.println("""
                    \nACCION DENEGADA
                    TODAVIA NO HAY INVENTARIOS
                    """);
            return;
        }
        System.out.println("""
                \nHAS SELECCIONADO -VER TODOS LOS PRODUCTOS-
                """);
        try {
            System.out.println(controladorTienda.mostrarInventarioGeneralDeTienda());
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR:  " +e.getMessage());
        }
    }

    private static void venderProductoDeInventario(Scanner sc, ControladorTienda controladorTienda){
        System.out.print("""
                \nHAS SELECCIONADO -VENDER PRODUCTOS-
                """);
        String atenderCliente , pedirProducto;
        System.out.println("\nVAS A ATENDER UN CLIENTE (SI/NO):");
        atenderCliente = sc.nextLine();
        while (atenderCliente.equalsIgnoreCase("Si")){
            System.out.println("\nVAS A PEDIR UN PRODUCTO (SI/NO):");
            pedirProducto = sc.nextLine();
            controladorTienda.getMiGestorDeVentas().iniciarVenta();
            Carrito carrito = new Carrito();
            while (pedirProducto.equalsIgnoreCase("SI")){
                try{
                    System.out.println(controladorTienda.mostrarInventarioGeneralDeTienda());
                    System.out.println("ID del Inventario:");
                    int idInv = leerEntero(sc);
                    System.out.println("Codigo del Producto:");
                    int codigoProd = leerEntero(sc);
                    System.out.println("Cantidad:");
                    int cantidadVender = leerEntero(sc);
                    SolicitudItem solicitudItem = new SolicitudItem(idInv, codigoProd, cantidadVender);
                    carrito.agregarItem(solicitudItem);
                    System.out.println(carrito.mostrarCarrito());
                    System.out.println("Producto Agregado Con Exito Al Carrito");
                } catch (IllegalArgumentException e){
                    System.out.println("ERROR: " + e.getMessage());
                    System.out.println("No Se Agrego Este Producto, Pero El Carrito Sigue Intacto");
                }
                do {
                    System.out.println("VAS A PEDIR OTRO PRODUCTO (SI / NO):");
                    pedirProducto = sc.nextLine();
                } while (!pedirProducto.equalsIgnoreCase("SI") && !pedirProducto.equalsIgnoreCase("NO"));
            }
            try {
                controladorTienda.getMiGestorDeVentas().procesarVentaMultiproducto(carrito);
                controladorTienda.getMiGestorDeVentas().finalizarVenta();
            } catch (IllegalArgumentException e){
                System.out.println("ERROR:  " + e.getMessage());
            }
            do {
                System.out.println("\nVAS A ATENDER OTRO CLIENTE (SI/NO):");
                atenderCliente = sc.nextLine();
            } while (!atenderCliente.equalsIgnoreCase("si") && !atenderCliente.equalsIgnoreCase("no"));
        }
    }

    private static void verRecaudoClientes(Scanner sc, ControladorTienda controladorTienda){
        System.out.print("""
                \nHAS SELECCIONADO -VER RECAUDO CLIENTES-
                """);
        try {
            System.out.println(controladorTienda.getMiGestorDeVentas().obtenerHistorial());
        } catch (IllegalArgumentException e){
            System.out.println("ERROR:  " +e.getMessage());
        }
    }

}

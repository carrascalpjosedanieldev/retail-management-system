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
        if (controladorTienda.noTieneTienda()){
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
        if (controladorTienda.tiendaNoTieneInventarios()){
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
        System.out.println("\nVAS A ATENDER UN CLIENTE");
        atenderCliente = peticionSiNo(sc);
        while (atenderCliente.equalsIgnoreCase("Si")){
            controladorTienda.abrirCarritoSesion();
            System.out.println("\nVAS A PEDIR UN PRODUCTO");
            pedirProducto = peticionSiNo(sc);
            while (pedirProducto.equalsIgnoreCase("SI")){
                try{
                    System.out.println(controladorTienda.mostrarInventarioGeneralDeTienda());
                    SolicitudItem solicitudItem = capturarDatosDeCompra(sc);
                    controladorTienda.agregarItemASesion(solicitudItem.idInventario(), solicitudItem.codigoProducto(), solicitudItem.cantidad());
                    System.out.println(controladorTienda.obtenerVistaPreviaDelCarrito());
                    System.out.println("Producto Agregado Con Exito Al Carrito");
                } catch (IllegalArgumentException e){
                    System.out.println("ERROR: " + e.getMessage());
                    System.out.println("No Se Agrego Este Producto, Pero El Carrito Sigue Intacto");
                }
                System.out.println("VAS A PEDIR OTRO PRODUCTO");
                pedirProducto = peticionSiNo(sc);
            }
            try {
                Factura factura = controladorTienda.confirmarYProcesarVentaActual();
                String facturaGenerada = factura.generarFactura();
                System.out.println(facturaGenerada);
            } catch (IllegalArgumentException e){
                System.out.println("ERROR:  " + e.getMessage());
            }
            System.out.println("\nVAS A ATENDER OTRO CLIENTE");
            atenderCliente = peticionSiNo(sc);
        }
    }

    private static String peticionSiNo(Scanner sc){
        String peticion;
        do {
            System.out.println("(SI / NO):");
            peticion = sc.nextLine();
        } while (!peticion.equalsIgnoreCase("SI") && !peticion.equalsIgnoreCase("NO"));
        return peticion;
    }

    private static SolicitudItem capturarDatosDeCompra(Scanner sc) throws IllegalArgumentException{
        System.out.println("ID del Inventario:");
        int idInv = leerEntero(sc);
        System.out.println("Codigo del Producto:");
        int codigoProd = leerEntero(sc);
        System.out.println("Cantidad:");
        int cantidadVender = leerEntero(sc);
        return new SolicitudItem(idInv, codigoProd, cantidadVender);
    }

    private static void verRecaudoClientes(Scanner sc, ControladorTienda controladorTienda){
        System.out.print("""
                \nHAS SELECCIONADO -VER RECAUDO CLIENTES-
                """);
        try {
            System.out.println(controladorTienda.obtenerHistoralGestor());
        } catch (IllegalArgumentException e){
            System.out.println("ERROR:  " +e.getMessage());
        }

        System.out.println("\nTOTAL DE DINERO RECAUDADO A LOS CLIENTES:\n");
        System.out.println(controladorTienda.obtenerTotalRecaudoVentas());
    }

}


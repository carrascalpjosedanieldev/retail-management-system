package ProyectoPropio1;

import java.util.Scanner;

import static ProyectoPropio1.MetodosTienda.leerEntero;
import static ProyectoPropio1.MetodosTienda.pedirOpcion;

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
                    verRecaudoClientes(controladorTienda);
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
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }

    private static void venderProductoDeInventario(Scanner sc, ControladorTienda controladorTienda){
        System.out.print("""
                \nHAS SELECCIONADO -VENDER PRODUCTOS-
                """);
        String atenderCliente , pedirProducto, pedirServicio;
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
                    System.out.println("No se agrego este Producto, Pero el Carrito Sigue Intacto");
                }
                System.out.println("VAS A PEDIR OTRO PRODUCTO");
                pedirProducto = peticionSiNo(sc);
            }
            if (controladorTienda.tiendaNoTieneServicios()){
                System.out.println("\nNO HAY SERVICIOS DISPONIBLES EN LA TIENDA. GENERAREMOS TU FACTURA");
            } else {
                System.out.println("\nVAS A PEDIR UN SERVICIO");
                pedirServicio = peticionSiNo(sc);
                while (pedirServicio.equalsIgnoreCase("SI")){
                    try {
                        System.out.println(controladorTienda.mostrarServiciosDeLaTienda());
                        System.out.print("Codigo del Servicio:\n" +
                                "---> ");
                        int codigoServicio = leerEntero(sc);
                        Servicio servicio = controladorTienda.obtenerServicio(codigoServicio);
                        controladorTienda.agregarServicioAlCarrito(servicio);
                        System.out.println("Servicio agregado con exito, estos son los servicios que tienes:");
                        System.out.println(controladorTienda.mostrarDatosServiciosDelCarrito());
                    } catch (IllegalArgumentException e){
                        System.out.println("ERROR: " + e.getMessage());
                        System.out.println("No se agrego este Servicio, Pero el Carrito Sigue Intacto");
                    }
                    System.out.println("VAS A PEDIR OTRO SERVICIO");
                    pedirServicio = peticionSiNo(sc);
                }
            }
            try {
                Factura factura = controladorTienda.confirmarYProcesarVentaActual();
                String facturaGenerada = factura.generarFactura();
                System.out.println(facturaGenerada);
            } catch (IllegalArgumentException e){
                System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
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
        System.out.print("ID del Inventario:\n" +
                "---> ");
        int idInv = leerEntero(sc);
        System.out.print("Codigo del Producto:\n" +
                "---> ");
        int codigoProd = leerEntero(sc);
        System.out.print("Cantidad:\n" +
                "---> ");
        int cantidadVender = leerEntero(sc);
        return new SolicitudItem(idInv, codigoProd, cantidadVender);
    }

    private static void verRecaudoClientes(ControladorTienda controladorTienda){
        System.out.print("""
                \nHAS SELECCIONADO -VER RECAUDO CLIENTES-
                """);
        try {
            System.out.println(controladorTienda.obtenerHistoralGestor());
        } catch (IllegalArgumentException e){
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
        System.out.println("\nTOTAL DE DINERO RECAUDADO A LOS CLIENTES:\n");
        System.out.println(controladorTienda.obtenerTotalRecaudoVentas());
    }

}

